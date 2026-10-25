package com.example.drowsiness.acquisition;

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
 *                     Pass [ax,ay,az] only (length = nSamples * 3) if no gyroscope.
 * @property timestampsS  Monotonic timestamps in seconds for each sample.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0014\n\u0002\b\u0004\n\u0002\u0010\u0013\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B-\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\b\u00a2\u0006\u0002\u0010\tJ\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\bH\u00c6\u0003J;\u0010\u001c\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\bH\u00c6\u0001J\u0013\u0010\u001d\u001a\u00020\u001e2\b\u0010\u001f\u001a\u0004\u0018\u00010\u0001H\u0096\u0002J\b\u0010 \u001a\u00020\u000eH\u0016J\t\u0010!\u001a\u00020\"H\u00d6\u0001R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000bR\u0011\u0010\r\u001a\u00020\u000e8F\u00a2\u0006\u0006\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0011\u001a\u00020\u000e8F\u00a2\u0006\u0006\u001a\u0004\b\u0012\u0010\u0010R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u000bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u000bR\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016\u00a8\u0006#"}, d2 = {"Lcom/example/drowsiness/acquisition/EcgFrame;", "", "steering", "", "seatbelt", "ground", "imu", "timestampsS", "", "([F[F[F[F[D)V", "getGround", "()[F", "getImu", "imuColumns", "", "getImuColumns", "()I", "sampleCount", "getSampleCount", "getSeatbelt", "getSteering", "getTimestampsS", "()[D", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "toString", "", "app_debug"})
public final class EcgFrame {
    @org.jetbrains.annotations.NotNull()
    private final float[] steering = null;
    @org.jetbrains.annotations.NotNull()
    private final float[] seatbelt = null;
    @org.jetbrains.annotations.NotNull()
    private final float[] ground = null;
    @org.jetbrains.annotations.NotNull()
    private final float[] imu = null;
    @org.jetbrains.annotations.NotNull()
    private final double[] timestampsS = null;
    
    public EcgFrame(@org.jetbrains.annotations.NotNull()
    float[] steering, @org.jetbrains.annotations.NotNull()
    float[] seatbelt, @org.jetbrains.annotations.NotNull()
    float[] ground, @org.jetbrains.annotations.NotNull()
    float[] imu, @org.jetbrains.annotations.NotNull()
    double[] timestampsS) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final float[] getSteering() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final float[] getSeatbelt() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final float[] getGround() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final float[] getImu() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final double[] getTimestampsS() {
        return null;
    }
    
    public final int getSampleCount() {
        return 0;
    }
    
    public final int getImuColumns() {
        return 0;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final float[] component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final float[] component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final float[] component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final float[] component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final double[] component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.drowsiness.acquisition.EcgFrame copy(@org.jetbrains.annotations.NotNull()
    float[] steering, @org.jetbrains.annotations.NotNull()
    float[] seatbelt, @org.jetbrains.annotations.NotNull()
    float[] ground, @org.jetbrains.annotations.NotNull()
    float[] imu, @org.jetbrains.annotations.NotNull()
    double[] timestampsS) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}