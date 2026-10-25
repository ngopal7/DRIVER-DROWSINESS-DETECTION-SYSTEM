from __future__ import annotations
import numpy as np

def compute_hrv_features(peaks: np.ndarray, fs: float, cfg: dict) -> dict:
    """Only invoke after a passed continuous-contact gate."""
    rr_ms = np.diff(peaks) * 1000 / fs
    if len(rr_ms) < 2: return {"rmssd_ms": np.nan, "sdnn_ms": np.nan, "pnn50": np.nan, "n_rr": len(rr_ms)}
    d = np.diff(rr_ms)
    values = {"rmssd_ms": float(np.sqrt(np.mean(d*d))), "sdnn_ms": float(np.std(rr_ms, ddof=1)), "pnn50": float(np.mean(abs(d) > 50)), "n_rr": len(rr_ms)}
    if cfg["hrv"]["include_lf_hf"]: values["lf_hf"] = np.nan  # reserved, requires validated spectral preprocessing
    return values
