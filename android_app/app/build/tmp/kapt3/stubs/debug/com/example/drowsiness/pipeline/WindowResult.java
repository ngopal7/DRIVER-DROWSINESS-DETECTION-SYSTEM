package com.example.drowsiness.pipeline;

/**
 * Complete window result carrying everything the decision logger needs.
 * Extends beyond Python's pipeline output to include metadata the
 * Android logging layer requires.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\b\u001b\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B[\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0006\u0012\b\u0010\b\u001a\u0004\u0018\u00010\t\u0012\u0006\u0010\n\u001a\u00020\u0006\u0012\u0006\u0010\u000b\u001a\u00020\u0006\u0012\u0006\u0010\f\u001a\u00020\u0006\u0012\u0012\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00030\u000e\u00a2\u0006\u0002\u0010\u0010J\t\u0010\u001e\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010 \u001a\u00020\u0006H\u00c6\u0003J\t\u0010!\u001a\u00020\u0006H\u00c6\u0003J\u000b\u0010\"\u001a\u0004\u0018\u00010\tH\u00c6\u0003J\t\u0010#\u001a\u00020\u0006H\u00c6\u0003J\t\u0010$\u001a\u00020\u0006H\u00c6\u0003J\t\u0010%\u001a\u00020\u0006H\u00c6\u0003J\u0015\u0010&\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00030\u000eH\u00c6\u0003Jq\u0010\'\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u00062\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t2\b\b\u0002\u0010\n\u001a\u00020\u00062\b\b\u0002\u0010\u000b\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\u00062\u0014\b\u0002\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00030\u000eH\u00c6\u0001J\u0013\u0010(\u001a\u00020\u00062\b\u0010)\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010*\u001a\u00020+H\u00d6\u0001J\t\u0010,\u001a\u00020\u000fH\u00d6\u0001R\u0011\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u001d\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00030\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\n\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0012R\u0011\u0010\f\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0012R\u0013\u0010\b\u001a\u0004\u0018\u00010\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0014R\u0011\u0010\u000b\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0012R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0012\u00a8\u0006-"}, d2 = {"Lcom/example/drowsiness/pipeline/WindowResult;", "", "startS", "", "endS", "valid", "", "continuousContact", "selectedChannel", "Lcom/example/drowsiness/pipeline/EcgChannel;", "fusionDisagreement", "steeringQualityPassed", "seatbeltQualityPassed", "features", "", "", "(DDZZLcom/example/drowsiness/pipeline/EcgChannel;ZZZLjava/util/Map;)V", "getContinuousContact", "()Z", "getEndS", "()D", "getFeatures", "()Ljava/util/Map;", "getFusionDisagreement", "getSeatbeltQualityPassed", "getSelectedChannel", "()Lcom/example/drowsiness/pipeline/EcgChannel;", "getStartS", "getSteeringQualityPassed", "getValid", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "", "toString", "app_debug"})
public final class WindowResult {
    private final double startS = 0.0;
    private final double endS = 0.0;
    private final boolean valid = false;
    private final boolean continuousContact = false;
    @org.jetbrains.annotations.Nullable()
    private final com.example.drowsiness.pipeline.EcgChannel selectedChannel = null;
    private final boolean fusionDisagreement = false;
    private final boolean steeringQualityPassed = false;
    private final boolean seatbeltQualityPassed = false;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<java.lang.String, java.lang.Double> features = null;
    
    public WindowResult(double startS, double endS, boolean valid, boolean continuousContact, @org.jetbrains.annotations.Nullable()
    com.example.drowsiness.pipeline.EcgChannel selectedChannel, boolean fusionDisagreement, boolean steeringQualityPassed, boolean seatbeltQualityPassed, @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.Double> features) {
        super();
    }
    
    public final double getStartS() {
        return 0.0;
    }
    
    public final double getEndS() {
        return 0.0;
    }
    
    public final boolean getValid() {
        return false;
    }
    
    public final boolean getContinuousContact() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.example.drowsiness.pipeline.EcgChannel getSelectedChannel() {
        return null;
    }
    
    public final boolean getFusionDisagreement() {
        return false;
    }
    
    public final boolean getSteeringQualityPassed() {
        return false;
    }
    
    public final boolean getSeatbeltQualityPassed() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Map<java.lang.String, java.lang.Double> getFeatures() {
        return null;
    }
    
    public final double component1() {
        return 0.0;
    }
    
    public final double component2() {
        return 0.0;
    }
    
    public final boolean component3() {
        return false;
    }
    
    public final boolean component4() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.example.drowsiness.pipeline.EcgChannel component5() {
        return null;
    }
    
    public final boolean component6() {
        return false;
    }
    
    public final boolean component7() {
        return false;
    }
    
    public final boolean component8() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Map<java.lang.String, java.lang.Double> component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.drowsiness.pipeline.WindowResult copy(double startS, double endS, boolean valid, boolean continuousContact, @org.jetbrains.annotations.Nullable()
    com.example.drowsiness.pipeline.EcgChannel selectedChannel, boolean fusionDisagreement, boolean steeringQualityPassed, boolean seatbeltQualityPassed, @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.Double> features) {
        return null;
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
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}