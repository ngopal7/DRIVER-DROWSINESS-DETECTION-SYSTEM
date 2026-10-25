package com.example.drowsiness.acquisition;

/**
 * Chip-neutral acquisition boundary matching Python's `ECGDAQ` ABC.
 *
 * Implementations wrap hardware-specific transports (BLE to ADS1292/AD8232)
 * or synthetic generators for integration testing.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\u0012\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0007H&J\b\u0010\b\u001a\u00020\u0005H&\u00a8\u0006\t"}, d2 = {"Lcom/example/drowsiness/acquisition/EcgDaq;", "", "readSamples", "Lcom/example/drowsiness/acquisition/EcgFrame;", "start", "", "hardwareTrigger", "", "stop", "app_debug"})
public abstract interface EcgDaq {
    
    /**
     * Begin streaming. [hardwareTrigger] enables hardware-level sync when available.
     */
    public abstract void start(boolean hardwareTrigger);
    
    /**
     * Return the next available frame of synchronized samples.
     * Blocking call — implementations manage their own buffering.
     */
    @org.jetbrains.annotations.NotNull()
    public abstract com.example.drowsiness.acquisition.EcgFrame readSamples();
    
    /**
     * Stop streaming and release hardware resources.
     */
    public abstract void stop();
    
    /**
     * Chip-neutral acquisition boundary matching Python's `ECGDAQ` ABC.
     *
     * Implementations wrap hardware-specific transports (BLE to ADS1292/AD8232)
     * or synthetic generators for integration testing.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}