package com.example.drowsiness.model

/**
 * Model format selector. Auto-detects from file extension or explicit enum.
 */
enum class ModelFormat {
    TFLITE,
    ONNX;

    companion object {
        /** Infer format from asset/file path extension. */
        fun fromPath(path: String): ModelFormat = when {
            path.endsWith(".tflite", ignoreCase = true) -> TFLITE
            path.endsWith(".onnx", ignoreCase = true) -> ONNX
            else -> throw IllegalArgumentException(
                "Cannot infer model format from path: $path. Use .tflite or .onnx extension."
            )
        }
    }
}
