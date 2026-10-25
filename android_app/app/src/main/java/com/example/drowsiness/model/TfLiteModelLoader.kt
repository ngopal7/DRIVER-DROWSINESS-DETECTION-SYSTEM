package com.example.drowsiness.model

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.io.Closeable
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

/**
 * TFLite model loader implementing the [DrowsinessModel] contract
 * from `DrowsinessEngine.kt`.
 *
 * Loads a `.tflite` model exported via `modeling/export.py::export_lstm_tflite()`.
 * Input: FloatArray of HRV features. Output: single drowsiness probability [0, 1].
 */
class TfLiteModelLoader private constructor(
    private val interpreter: Interpreter,
) : Closeable {

    /**
     * Run inference. Implements `DrowsinessModel.infer(features)`.
     *
     * @param features Ordered HRV features matching training feature order
     *                 (rmssd_ms, sdnn_ms, pnn50 by default).
     * @return Drowsiness probability in [0, 1].
     */
    fun infer(features: FloatArray): Float {
        // TFLite expects [1, numFeatures] input shape
        val input = arrayOf(features)
        val output = Array(1) { FloatArray(1) }
        interpreter.run(input, output)
        return output[0][0]
    }

    override fun close() {
        interpreter.close()
    }

    companion object {
        /**
         * Load a TFLite model from Android assets.
         *
         * @param context Android context for asset access.
         * @param assetPath Path within `assets/`, e.g. `"drowsiness_lstm.tflite"`.
         * @param numThreads Interpreter thread count (default 2).
         */
        fun fromAsset(
            context: Context,
            assetPath: String,
            numThreads: Int = 2,
        ): TfLiteModelLoader {
            val buffer = loadModelFile(context, assetPath)
            val options = Interpreter.Options().apply {
                setNumThreads(numThreads)
            }
            return TfLiteModelLoader(Interpreter(buffer, options))
        }

        /**
         * Load a TFLite model from an absolute file path.
         *
         * @param filePath Absolute path to `.tflite` file.
         * @param numThreads Interpreter thread count.
         */
        fun fromFile(
            filePath: String,
            numThreads: Int = 2,
        ): TfLiteModelLoader {
            val options = Interpreter.Options().apply {
                setNumThreads(numThreads)
            }
            return TfLiteModelLoader(Interpreter(java.io.File(filePath), options))
        }

        private fun loadModelFile(context: Context, assetPath: String): MappedByteBuffer {
            val fd = context.assets.openFd(assetPath)
            val stream = FileInputStream(fd.fileDescriptor)
            val channel = stream.channel
            return channel.map(
                FileChannel.MapMode.READ_ONLY,
                fd.startOffset,
                fd.declaredLength
            )
        }
    }
}
