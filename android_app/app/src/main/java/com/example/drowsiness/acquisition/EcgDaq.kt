package com.example.drowsiness.acquisition

/**
 * Chip-neutral acquisition boundary matching Python's `ECGDAQ` ABC.
 *
 * Implementations wrap hardware-specific transports (BLE to ADS1292/AD8232)
 * or synthetic generators for integration testing.
 */
interface EcgDaq {
    /** Begin streaming. [hardwareTrigger] enables hardware-level sync when available. */
    fun start(hardwareTrigger: Boolean = false)

    /**
     * Return the next available frame of synchronized samples.
     * Blocking call — implementations manage their own buffering.
     */
    fun readSamples(): EcgFrame

    /** Stop streaming and release hardware resources. */
    fun stop()
}

/**
 * One frame of synchronized multi-channel ECG + IMU data.
 *
 * Bundles everything Python's `process_recording()` receives as separate arguments
 * into a single transport unit.
 *
 * @property steering   Steering-wheel electrode samples.
 * @property seatbelt   Seatbelt electrode samples.
 * @property ground     Ground/reference electrode samples (non-ECG).
 * @property imu        Flattened IMU: [ax,ay,az,gx,gy,gz] per sample (length = nSamples * 6).
 *                      Pass [ax,ay,az] only (length = nSamples * 3) if no gyroscope.
 * @property timestampsS  Monotonic timestamps in seconds for each sample.
 */
data class EcgFrame(
    val steering: FloatArray,
    val seatbelt: FloatArray,
    val ground: FloatArray,
    val imu: FloatArray,
    val timestampsS: DoubleArray,
) {
    /** Number of ECG samples in this frame. */
    val sampleCount: Int get() = steering.size

    /** IMU columns per sample (3 = accel-only, 6 = accel+gyro). */
    val imuColumns: Int get() = if (sampleCount > 0) imu.size / sampleCount else 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EcgFrame) return false
        return steering.contentEquals(other.steering) &&
            seatbelt.contentEquals(other.seatbelt) &&
            ground.contentEquals(other.ground) &&
            imu.contentEquals(other.imu) &&
            timestampsS.contentEquals(other.timestampsS)
    }

    override fun hashCode(): Int {
        var result = steering.contentHashCode()
        result = 31 * result + seatbelt.contentHashCode()
        result = 31 * result + ground.contentHashCode()
        result = 31 * result + imu.contentHashCode()
        result = 31 * result + timestampsS.contentHashCode()
        return result
    }
}
