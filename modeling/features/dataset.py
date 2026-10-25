"""Convert persisted window rows into explicit numeric modeling inputs."""
from __future__ import annotations
import numpy as np

DEFAULT_FEATURES=("rmssd_ms","sdnn_ms","pnn50")
def rows_to_matrix(rows: list[dict], target_key: str, subject_key: str="subject_id", feature_names=DEFAULT_FEATURES):
    usable=[r for r in rows if r.get("valid",True) and np.isfinite([r.get(f,np.nan) for f in feature_names]).all() and target_key in r and subject_key in r]
    if not usable: raise ValueError("no valid rows with requested features, target, and subject ID")
    return np.asarray([[r[f] for f in feature_names] for r in usable],dtype=float),np.asarray([r[target_key] for r in usable]),np.asarray([r[subject_key] for r in usable]),list(feature_names),usable
