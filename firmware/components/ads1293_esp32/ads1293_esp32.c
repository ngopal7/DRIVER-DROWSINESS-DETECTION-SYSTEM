#include "ads1293_esp32.h"

#include <string.h>
#include "driver/gpio.h"
#include "driver/spi_master.h"
#include "esp_timer.h"
#include "freertos/FreeRTOS.h"
#include "freertos/queue.h"
#include "freertos/task.h"
#include "esp_log.h"

/* ADS1293 register addresses documented by TI. */
#define REG_CONFIG       0x00
#define REG_DATA_STATUS  0x30
#define REG_DATA_CH1_ECG 0x37
#define REG_DATA_CH2_ECG 0x3A
#define ECG_DATA_BYTES   3
#define DRDY_QUEUE_DEPTH 32
#define FRAME_QUEUE_DEPTH 64

typedef struct { int64_t timestamp_us; uint32_t sequence; } drdy_event_t;

static const char *TAG = "ads1293_esp32";
static ads1293_config_t s_config;
static spi_device_handle_t s_spi;
static QueueHandle_t s_drdy_queue, s_frame_queue;
static volatile uint32_t s_sequence;
static ads1293_imu_reader_t s_imu_reader;
static void *s_imu_context;
static bool s_initialized, s_running;

static void IRAM_ATTR drdy_isr(void *arg)
{
    drdy_event_t event = {.timestamp_us = esp_timer_get_time(), .sequence = s_sequence++};
    BaseType_t wake = pdFALSE;
    if (xQueueSendFromISR(s_drdy_queue, &event, &wake) != pdTRUE) {
        /* A queue overrun is surfaced by the sequence gap on the next frame. */
        s_sequence++;
    }
    if (wake) portYIELD_FROM_ISR();
}

static esp_err_t register_read(uint8_t address, uint8_t *value)
{
    /* 16-bit random-access protocol: command byte then one returned data byte. */
    spi_transaction_t t = {0};
    uint8_t tx[2] = {(uint8_t)(0x80u | (address & 0x7fu)), 0xff};
    uint8_t rx[2] = {0};
    t.length = 16; t.tx_buffer = tx; t.rx_buffer = rx;
    esp_err_t err = spi_device_transmit(s_spi, &t);
    if (err == ESP_OK) *value = rx[1];
    return err;
}

static esp_err_t register_write(uint8_t address, uint8_t value)
{
    spi_transaction_t t = {0};
    uint8_t tx[2] = {(uint8_t)(address & 0x7fu), value};
    t.length = 16; t.tx_buffer = tx;
    return spi_device_transmit(s_spi, &t);
}

static esp_err_t read_register_block(uint8_t first_address, uint8_t *data, size_t length)
{
    if (!length || length > 16) return ESP_ERR_INVALID_ARG;
    uint8_t tx[17] = {0}, rx[17] = {0};
    tx[0] = (uint8_t)(0x80u | (first_address & 0x7fu));
    memset(tx + 1, 0xff, length);
    spi_transaction_t t = {0}; t.length = 8 * (length + 1); t.tx_buffer = tx; t.rx_buffer = rx;
    esp_err_t err = spi_device_transmit(s_spi, &t);
    if (err == ESP_OK) memcpy(data, rx + 1, length);
    return err;
}

static int32_t signed_24(const uint8_t bytes[3])
{
    int32_t value = ((int32_t)bytes[0] << 16) | ((int32_t)bytes[1] << 8) | bytes[2];
    if (value & 0x800000) value |= (int32_t)0xff000000;
    return value;
}

static esp_err_t read_ecg_frame(const drdy_event_t *event, ads1293_ecg_frame_t *frame)
{
    uint8_t status, ch1[ECG_DATA_BYTES], ch2[ECG_DATA_BYTES];
    esp_err_t err = register_read(REG_DATA_STATUS, &status);
    if (err != ESP_OK) return err;
    if ((err = read_register_block(REG_DATA_CH1_ECG, ch1, sizeof(ch1))) != ESP_OK) return err;
    if ((err = read_register_block(REG_DATA_CH2_ECG, ch2, sizeof(ch2))) != ESP_OK) return err;
    memset(frame, 0, sizeof(*frame));
    frame->sequence = event->sequence;
    frame->timestamp_us = event->timestamp_us;
    frame->afe_status = status;
    frame->steering_ecg = signed_24(ch1);
    frame->seatbelt_ecg = signed_24(ch2);
    if (s_config.pin_steering_contact >= 0 && gpio_get_level(s_config.pin_steering_contact) == 0) frame->lead_off_mask |= 0x01;
    if (s_config.pin_seatbelt_contact >= 0 && gpio_get_level(s_config.pin_seatbelt_contact) == 0) frame->lead_off_mask |= 0x02;
    if (s_imu_reader) {
        int64_t imu_time = 0;
        if (s_imu_reader(event->timestamp_us, frame->accel_mps2, frame->gyro_rads, &imu_time, s_imu_context) == ESP_OK) {
            frame->imu_timestamp_us = imu_time;
            frame->imu_valid = true;
        }
    }
    return ESP_OK;
}

static void capture_task(void *arg)
{
    drdy_event_t event;
    while (s_running) {
        if (xQueueReceive(s_drdy_queue, &event, pdMS_TO_TICKS(100)) != pdTRUE) continue;
        ads1293_ecg_frame_t frame;
        if (read_ecg_frame(&event, &frame) == ESP_OK) xQueueSend(s_frame_queue, &frame, 0);
    }
    vTaskDelete(NULL);
}

esp_err_t ads1293_esp32_init(const ads1293_config_t *config)
{
    if (!config || config->pin_mosi < 0 || config->pin_miso < 0 || config->pin_sclk < 0 || config->pin_cs < 0 || config->pin_drdyb < 0) return ESP_ERR_INVALID_ARG;
    s_config = *config;
    spi_bus_config_t bus = {.mosi_io_num=config->pin_mosi,.miso_io_num=config->pin_miso,.sclk_io_num=config->pin_sclk,.quadwp_io_num=-1,.quadhd_io_num=-1,.max_transfer_sz=32};
    esp_err_t err = spi_bus_initialize(config->spi_host, &bus, SPI_DMA_CH_AUTO);
    if (err != ESP_OK && err != ESP_ERR_INVALID_STATE) return err;
    spi_device_interface_config_t dev = {.clock_speed_hz=config->spi_clock_hz,.mode=0,.spics_io_num=config->pin_cs,.queue_size=2};
    if ((err = spi_bus_add_device(config->spi_host, &dev, &s_spi)) != ESP_OK) return err;
    s_drdy_queue=xQueueCreate(DRDY_QUEUE_DEPTH,sizeof(drdy_event_t)); s_frame_queue=xQueueCreate(FRAME_QUEUE_DEPTH,sizeof(ads1293_ecg_frame_t));
    if (!s_drdy_queue || !s_frame_queue) return ESP_ERR_NO_MEM;
    gpio_config_t io={.pin_bit_mask=1ULL<<config->pin_drdyb,.mode=GPIO_MODE_INPUT,.intr_type=GPIO_INTR_NEGEDGE};
    if ((err=gpio_config(&io)) != ESP_OK) return err;
    if ((err=gpio_install_isr_service(ESP_INTR_FLAG_IRAM)) != ESP_OK && err != ESP_ERR_INVALID_STATE) return err;
    if ((err=gpio_isr_handler_add(config->pin_drdyb,drdy_isr,NULL)) != ESP_OK) return err;
    s_initialized=true;
    return ESP_OK;
}

esp_err_t ads1293_start(void)
{
    if (!s_initialized) return ESP_ERR_INVALID_STATE;
    /* Profile must set ECG routing, identical ECG decimation, fastest ECG
       channel as DRDYB source, streaming registers, and lead-off status. */
    if (!s_config.register_profile || !s_config.register_profile_length) return ESP_ERR_NOT_SUPPORTED;
    esp_err_t err=register_write(REG_CONFIG,0x00);
    if (err != ESP_OK) return err;
    for (size_t i=0;i<s_config.register_profile_length;i++) {
        const ads1293_register_write_t *w=&s_config.register_profile[i];
        if ((err=register_write(w->address,w->value)) != ESP_OK) return err;
    }
    s_running=true;
    if ((err=register_write(REG_CONFIG,s_config.conversion_start_value)) != ESP_OK) { s_running=false; return err; }
    return ESP_OK;
}

esp_err_t ads1293_stop(void)
{
    if (!s_initialized) return ESP_ERR_INVALID_STATE;
    s_running=false;
    return register_write(REG_CONFIG,0x00);
}

esp_err_t ads1293_read_frame(ads1293_ecg_frame_t *frame, int timeout_ms)
{
    if (!s_initialized || !frame || timeout_ms < 0) return ESP_ERR_INVALID_ARG;
    return xQueueReceive(s_frame_queue,frame,pdMS_TO_TICKS(timeout_ms)) == pdTRUE ? ESP_OK : ESP_ERR_TIMEOUT;
}

void ads1293_set_imu_reader(ads1293_imu_reader_t reader, void *context) { s_imu_reader=reader; s_imu_context=context; }

esp_err_t ads1293_capture_task_start(ads1293_frame_sink_t sink, void *context)
{
    if (!s_initialized || !sink || s_running) return ESP_ERR_INVALID_STATE;
    s_running=true;
    if (xTaskCreate(capture_task,"ads1293_capture",4096,NULL,configMAX_PRIORITIES-3,NULL) != pdPASS) { s_running=false; return ESP_ERR_NO_MEM; }
    ads1293_ecg_frame_t frame;
    while (s_running) {
        if (ads1293_read_frame(&frame,100)==ESP_OK) sink(&frame,context);
    }
    return ESP_OK;
}
