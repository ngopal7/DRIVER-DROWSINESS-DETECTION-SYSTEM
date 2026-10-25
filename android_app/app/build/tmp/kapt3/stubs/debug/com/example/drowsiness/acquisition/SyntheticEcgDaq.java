package com.example.drowsiness.acquisition;

/**
 * Deterministic synthetic ECG source for pipeline integration testing without hardware.
 * Mirrors `data_collection/synthetic_ecg.py`: sine-based QRS approximation + noise.
 *
 * @param fs               Sampling rate in Hz (must match pipeline_config.yaml).
 * @param frameSamples      Number of samples per [readSamples] call.
 * @param seed              Deterministic seed for reproducibility.
 * @param seatbeltScale     Amplitude scaling for the seatbelt channel.
 * @param seatbeltDriftPpm  Simulated clock drift in ppm for seatbelt.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B7\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\b\u00a2\u0006\u0002\u0010\nJ\b\u0010\u0011\u001a\u00020\u0012H\u0016J\u0010\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u000eH\u0016J\b\u0010\u0016\u001a\u00020\u0014H\u0016R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0017"}, d2 = {"Lcom/example/drowsiness/acquisition/SyntheticEcgDaq;", "Lcom/example/drowsiness/acquisition/EcgDaq;", "fs", "", "frameSamples", "", "seed", "seatbeltScale", "", "seatbeltDriftPpm", "(DIIFF)V", "rng", "Ljava/util/Random;", "running", "", "sampleIndex", "", "readSamples", "Lcom/example/drowsiness/acquisition/EcgFrame;", "start", "", "hardwareTrigger", "stop", "app_debug"})
public final class SyntheticEcgDaq implements com.example.drowsiness.acquisition.EcgDaq {
    private final double fs = 0.0;
    private final int frameSamples = 0;
    private final int seed = 0;
    private final float seatbeltScale = 0.0F;
    private final float seatbeltDriftPpm = 0.0F;
    private boolean running = false;
    private long sampleIndex = 0L;
    private java.util.Random rng;
    
    public SyntheticEcgDaq(double fs, int frameSamples, int seed, float seatbeltScale, float seatbeltDriftPpm) {
        super();
    }
    
    @java.lang.Override()
    public void start(boolean hardwareTrigger) {
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public com.example.drowsiness.acquisition.EcgFrame readSamples() {
        return null;
    }
    
    @java.lang.Override()
    public void stop() {
    }
    
    public SyntheticEcgDaq() {
        super();
    }
}