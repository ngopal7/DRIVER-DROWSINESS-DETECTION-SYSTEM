#include "esp_log.h"
#include "ads1293_esp32.h"

static const char *TAG = "hrv_acq";

void app_main(void)
{
    ads1293_config_t config = ADS1293_CONFIG_DEFAULT();
    esp_err_t err = ads1293_esp32_init(&config);
    if (err != ESP_OK) {
        ESP_LOGE(TAG, "ADS1293 acquisition init failed: %s", esp_err_to_name(err));
        return;
    }
    ESP_LOGW(TAG, "Board-specific ADS1293 register profile must be validated before ECG capture");
}
