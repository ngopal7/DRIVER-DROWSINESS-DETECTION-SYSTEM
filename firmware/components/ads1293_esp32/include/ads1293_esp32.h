#pragma once

#include <stdbool.h>
#include <stddef.h>
#include <stdint.h>
#include "esp_err.h"

typedef struct {
    uint32_t sequence;
    int64_t timestamp_us;       // ESP32 monotonic time captured at DRDYB edge
    int32_t steering_ecg;       // signed 24-bit ADC code, channel 1
    int32_t seatbelt_ecg;       // signed 24-bit ADC code, channel 2
    uint8_t afe_status;
    uint8_t lead_off_mask;      // board-level contact inputs; bit 0=steering, bit 1=seatbelt
    int64_t imu_timestamp_us;   // same ESP32 monotonic clock
    float accel_mps2[3];
    float gyro_rads[3];
    bool imu_valid;
} ads1293_ecg_frame_t;

typedef struct { uint8_t address; uint8_t value; } ads1293_register_write_t;

typedef struct {
    int spi_host;
    int pin_mosi, pin_miso, pin_sclk, pin_cs;
    int pin_drdyb;
    int pin_steering_contact;   // optional external contact sensor; -1 if unavailable
    int pin_seatbelt_contact;   // optional external contact sensor; -1 if unavailable
    int spi_clock_hz;
    uint32_t expected_sample_rate_hz;
    const ads1293_register_write_t *register_profile;
    size_t register_profile_length;
    uint8_t conversion_start_value;
} ads1293_config_t;

#define ADS1293_CONFIG_DEFAULT() { \
    .spi_host = 2, .pin_mosi = -1, .pin_miso = -1, .pin_sclk = -1, .pin_cs = -1, \
    .pin_drdyb = -1, .pin_steering_contact = -1, .pin_seatbelt_contact = -1, \
    .spi_clock_hz = 1000000, .expected_sample_rate_hz = 250, \
    .register_profile = NULL, .register_profile_length = 0, .conversion_start_value = 0x01 \
}

typedef esp_err_t (*ads1293_frame_sink_t)(const ads1293_ecg_frame_t *frame, void *context);
typedef esp_err_t (*ads1293_imu_reader_t)(int64_t target_timestamp_us, float accel_mps2[3], float gyro_rads[3], int64_t *sample_timestamp_us, void *context);

esp_err_t ads1293_esp32_init(const ads1293_config_t *config);
esp_err_t ads1293_start(void);
esp_err_t ads1293_stop(void);
esp_err_t ads1293_read_frame(ads1293_ecg_frame_t *frame, int timeout_ms);
void ads1293_set_imu_reader(ads1293_imu_reader_t reader, void *context);
esp_err_t ads1293_capture_task_start(ads1293_frame_sink_t sink, void *context);
