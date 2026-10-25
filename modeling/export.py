"""Explicit model export boundary; validate feature parity before deployment."""
from pathlib import Path

def export_lstm_tflite(keras_model, output_path: str | Path) -> Path:
    import tensorflow as tf
    target = Path(output_path)
    converter = tf.lite.TFLiteConverter.from_keras_model(keras_model)
    target.write_bytes(converter.convert())
    return target

def export_sklearn_onnx(model, sample_features, output_path: str | Path) -> Path:
    try:
        from skl2onnx import to_onnx
    except ImportError as exc: raise RuntimeError("Install skl2onnx for Random Forest ONNX export") from exc
    target = Path(output_path); target.write_bytes(to_onnx(model, sample_features).SerializeToString()); return target
