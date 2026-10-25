package com.example.drowsiness.model

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.content.Context
import java.io.Closeable
import java.nio.FloatBuffer

/**
 * ONNX Runtime model loader implementing the [DrowsinessModel] contract
 * from `DrowsinessEngine.kt`.
 *
 * Loads an `.onnx` model exported via `modeling/export.py::export_sklearn_onnx()`.
 * Input: FloatArray of HRV features. Output: single drowsiness probability [0, 1].
 */
class OnnxModelLoader private constructor(
    private val env: OrtEnvironment,
    private val session: OrtSession,
) : Closeable {

    /**
     * Run inference. Implements `DrowsinessModel.infer(features)`.
     *
     * @param features Ordered HRV features matching training feature order.
     * @return Drowsiness probability in [0, 1].
     */
    fun infer(features: FloatArray): Float {
        val inputName = session.inputNames.first()
        val shape = longArrayOf(1, features.size.toLong())
        val tensor = OnnxTensor.createTensor(
            env,
            FloatBuffer.wrap(features),
            shape
        )

        tensor.use { input ->
            val result = session.run(mapOf(inputName to input))
            result.use { output ->
                // ONNX RF models output probabilities as a sequence/map;
                // LSTM models output a single float tensor.
                // Try direct float tensor first, then fall back to probability map.
                val firstOutput = output.get(0)
                return when (val value = firstOutput?.value) {
                    is Array<*> -> {
                        // Typical shape: [[prob_class_0, prob_class_1]]
                        @Suppress("UNCHECKED_CAST")
                        val probs = value[0] as? FloatArray
                        probs?.lastOrNull() ?: 0f
                    }
                    is FloatArray -> value.lastOrNull() ?: 0f
                    is Float -> value
                    else -> {
                        // For sklearn RF ONNX: output may be a map of {label -> probability}
                        // Attempt second output which is typically the probability tensor
                        if (output.size() > 1) {
                            val probOutput = output.get(1)?.value
                            when (probOutput) {
                                is Array<*> -> {
                                    @Suppress("UNCHECKED_CAST")
                                    val map = probOutput[0] as? Map<*, *>
                                    (map?.get(1L) as? Float) ?: (map?.get(1) as? Float) ?: 0f
                                }
                                else -> 0f
                            }
                        } else 0f
                    }
                }
            }
        }
    }

    override fun close() {
        session.close()
        env.close()
    }

    companion object {
        /**
         * Load an ONNX model from Android assets.
         *
         * @param context Android context for asset access.
         * @param assetPath Path within `assets/`, e.g. `"drowsiness_rf.onnx"`.
         */
        fun fromAsset(context: Context, assetPath: String): OnnxModelLoader {
            val env = OrtEnvironment.getEnvironment()
            val bytes = context.assets.open(assetPath).use { it.readBytes() }
            val session = env.createSession(bytes)
            return OnnxModelLoader(env, session)
        }

        /**
         * Load an ONNX model from an absolute file path.
         *
         * @param filePath Absolute path to `.onnx` file.
         */
        fun fromFile(filePath: String): OnnxModelLoader {
            val env = OrtEnvironment.getEnvironment()
            val session = env.createSession(filePath)
            return OnnxModelLoader(env, session)
        }
    }
}
