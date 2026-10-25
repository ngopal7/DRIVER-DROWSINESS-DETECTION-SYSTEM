package com.example.drowsiness.engine;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0014\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u00e6\u0080\u0001\u0018\u00002\u00020\u0001J,\u0010\u0002\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00050\u00032\u0006\u0010\u0006\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\u0004H&\u00a8\u0006\t"}, d2 = {"Lcom/example/drowsiness/engine/OnDeviceFeaturePipeline;", "", "update", "Lkotlin/Pair;", "", "", "ecgSteering", "ecgSeatbelt", "imu", "app_debug"})
public abstract interface OnDeviceFeaturePipeline {
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlin.Pair<float[], java.lang.Boolean> update(@org.jetbrains.annotations.NotNull()
    float[] ecgSteering, @org.jetbrains.annotations.NotNull()
    float[] ecgSeatbelt, @org.jetbrains.annotations.NotNull()
    float[] imu);
}