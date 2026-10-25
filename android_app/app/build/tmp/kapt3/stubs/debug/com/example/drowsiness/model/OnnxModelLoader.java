package com.example.drowsiness.model;

/**
 * ONNX Runtime model loader implementing the [DrowsinessModel] contract
 * from `DrowsinessEngine.kt`.
 *
 * Loads an `.onnx` model exported via `modeling/export.py::export_sklearn_onnx()`.
 * Input: FloatArray of HRV features. Output: single drowsiness probability [0, 1].
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u0014\n\u0002\b\u0002\u0018\u0000 \r2\u00020\u0001:\u0001\rB\u0017\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\b\u0010\u0007\u001a\u00020\bH\u0016J\u000e\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000e"}, d2 = {"Lcom/example/drowsiness/model/OnnxModelLoader;", "Ljava/io/Closeable;", "env", "Lai/onnxruntime/OrtEnvironment;", "session", "Lai/onnxruntime/OrtSession;", "(Lai/onnxruntime/OrtEnvironment;Lai/onnxruntime/OrtSession;)V", "close", "", "infer", "", "features", "", "Companion", "app_debug"})
public final class OnnxModelLoader implements java.io.Closeable {
    @org.jetbrains.annotations.NotNull()
    private final ai.onnxruntime.OrtEnvironment env = null;
    @org.jetbrains.annotations.NotNull()
    private final ai.onnxruntime.OrtSession session = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.drowsiness.model.OnnxModelLoader.Companion Companion = null;
    
    private OnnxModelLoader(ai.onnxruntime.OrtEnvironment env, ai.onnxruntime.OrtSession session) {
        super();
    }
    
    /**
     * Run inference. Implements `DrowsinessModel.infer(features)`.
     *
     * @param features Ordered HRV features matching training feature order.
     * @return Drowsiness probability in [0, 1].
     */
    public final float infer(@org.jetbrains.annotations.NotNull()
    float[] features) {
        return 0.0F;
    }
    
    @java.lang.Override()
    public void close() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bJ\u000e\u0010\t\u001a\u00020\u00042\u0006\u0010\n\u001a\u00020\b\u00a8\u0006\u000b"}, d2 = {"Lcom/example/drowsiness/model/OnnxModelLoader$Companion;", "", "()V", "fromAsset", "Lcom/example/drowsiness/model/OnnxModelLoader;", "context", "Landroid/content/Context;", "assetPath", "", "fromFile", "filePath", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        /**
         * Load an ONNX model from Android assets.
         *
         * @param context Android context for asset access.
         * @param assetPath Path within `assets/`, e.g. `"drowsiness_rf.onnx"`.
         */
        @org.jetbrains.annotations.NotNull()
        public final com.example.drowsiness.model.OnnxModelLoader fromAsset(@org.jetbrains.annotations.NotNull()
        android.content.Context context, @org.jetbrains.annotations.NotNull()
        java.lang.String assetPath) {
            return null;
        }
        
        /**
         * Load an ONNX model from an absolute file path.
         *
         * @param filePath Absolute path to `.onnx` file.
         */
        @org.jetbrains.annotations.NotNull()
        public final com.example.drowsiness.model.OnnxModelLoader fromFile(@org.jetbrains.annotations.NotNull()
        java.lang.String filePath) {
            return null;
        }
    }
}