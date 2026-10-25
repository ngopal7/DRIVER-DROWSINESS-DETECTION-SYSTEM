# ADS1293 + ESP32 acquisition firmware

This ESP-IDF scaffold defines the acquisition boundary for the selected hardware. The ADS1293 is a three-channel ECG AFE; channel 1 is mapped to steering-wheel ECG and channel 2 to seatbelt ECG. Channel 3 is optionally another ECG lead/reference measurement. The analog ground/reference electrode is not a digital ECG channel.

Acquisition contract:

1. Configure the selected sample rate and electrode routing in the board-specific ADS1293 register profile.
2. Use the ADS1293 active-low DRDYB edge to latch an ESP32 monotonic timestamp and increment a frame sequence.
3. Read the 24-bit ECG channel registers over SPI from a task, not in the GPIO ISR.
4. Attach IMU samples timestamped from the same ESP32 monotonic clock. Keep packet arrival time separate from sample time.
5. Transport framed records over BLE, Wi-Fi, or serial. Preserve sequence numbers and timestamps across transport.

The board pin map, input routing, lead-off detection method, sample-rate/decimation registers, and analog protection must be completed for the actual PCB before enabling conversion. The provided profile intentionally does not invent electrode-routing register values. Confirm settings against the [TI ADS1293 datasheet](https://www.ti.com/lit/ds/symlink/ads1293.pdf) and bench-test with a safe ECG simulator before attaching electrodes. The ESP-IDF GPTimer/DRDY path should be selected and verified for the target ESP32 variant.

Directory structure follows ESP-IDF component conventions. `ECGFrame` in `data_collection/daq.py` and `frames_to_batch()` in `data_collection/ads1293_adapter.py` are the host-side frame contract. The C driver requires an explicit board register profile and pin map; `ads1293_start()` refuses conversion if the register profile is absent.

The ADS1293 DRDYB pin can be driven by one selected digital channel; configure it from the fastest-rate active ECG channel. If channel rates differ, read `DATA_STATUS` and discard or explicitly mark frames where both ECG channels were not updated. In this project, configure both ECG paths to the same effective output rate. The driver currently reads ECG data registers directly and records `DATA_STATUS`; validate data freshness behavior against the selected profile on an ECG simulator.
