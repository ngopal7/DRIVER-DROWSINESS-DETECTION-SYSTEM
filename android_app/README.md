# Android deployment boundary

`DrowsinessEngine.kt` is the runtime policy around an exported TFLite or ONNX model: the ECG feature extractor must be the validated, platform-equivalent port of `signal_processing`. Configure update/inference cadence and alert hysteresis from the deployed copy of `pipeline_config.yaml`; do not bake study thresholds into code. Every decision is emitted to the supplied local logger.

Export options: TensorFlow LSTM: `tf.lite.TFLiteConverter.from_keras_model(model)`. A scikit-learn RF needs conversion/serving through a supported ONNX/TFLite path and numerical parity testing before use.
