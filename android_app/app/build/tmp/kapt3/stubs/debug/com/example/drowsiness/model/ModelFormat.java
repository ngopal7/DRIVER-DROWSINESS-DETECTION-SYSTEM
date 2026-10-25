package com.example.drowsiness.model;

/**
 * Model format selector. Auto-detects from file extension or explicit enum.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0005\b\u0086\u0081\u0002\u0018\u0000 \u00052\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001\u0005B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004\u00a8\u0006\u0006"}, d2 = {"Lcom/example/drowsiness/model/ModelFormat;", "", "(Ljava/lang/String;I)V", "TFLITE", "ONNX", "Companion", "app_debug"})
public enum ModelFormat {
    /*public static final*/ TFLITE /* = new TFLITE() */,
    /*public static final*/ ONNX /* = new ONNX() */;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.drowsiness.model.ModelFormat.Companion Companion = null;
    
    ModelFormat() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<com.example.drowsiness.model.ModelFormat> getEntries() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/example/drowsiness/model/ModelFormat$Companion;", "", "()V", "fromPath", "Lcom/example/drowsiness/model/ModelFormat;", "path", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        /**
         * Infer format from asset/file path extension.
         */
        @org.jetbrains.annotations.NotNull()
        public final com.example.drowsiness.model.ModelFormat fromPath(@org.jetbrains.annotations.NotNull()
        java.lang.String path) {
            return null;
        }
    }
}