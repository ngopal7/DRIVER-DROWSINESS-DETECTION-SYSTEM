"""Cross-channel consistency checks, not clinical validation."""
from __future__ import annotations
from typing import Protocol
import numpy as np

LIMITATIONS = ("No gold-standard ECG reference is present; artifacts shared by both channels can agree while wrong; "
               "agreement coverage is weak whenever only one channel passes contact quality.")

class ReferenceECGAdapter(Protocol):
    """Optional chest-strap/reference bridge; implementations return aligned peak samples."""
    def r_peak_indices(self, sampling_rate_hz: float) -> np.ndarray: ...

class ArrayReferenceECGAdapter:
    """Minimal adapter for a pre-detected chest-strap R-peak array."""
    def __init__(self, peak_indices: np.ndarray): self.peak_indices = np.asarray(peak_indices, dtype=int)
    def r_peak_indices(self, sampling_rate_hz: float) -> np.ndarray: return self.peak_indices

def compare_rr_intervals(steering_peaks: np.ndarray, seatbelt_peaks: np.ndarray, fs: float, beat_tolerance_ms: float = 80.) -> dict:
    """Report RR plus nearest-neighbour beat concordance; inputs need not be equal length."""
    steering_peaks, seatbelt_peaks = np.asarray(steering_peaks), np.asarray(seatbelt_peaks)
    if len(steering_peaks) and len(seatbelt_peaks):
        nearest_ms=np.min(abs(steering_peaks[:,None]-seatbelt_peaks[None,:]),axis=1)*1000/fs
        concordant=int(np.sum(nearest_ms <= beat_tolerance_ms))
        beat_metrics={"n_source_beats":int(len(steering_peaks)),"n_concordant_beats":concordant,"concordant_beat_percentage":100*concordant/len(steering_peaks),"beat_tolerance_ms":beat_tolerance_ms,"median_peak_timing_error_ms":float(np.median(nearest_ms))}
    else: beat_metrics={"n_source_beats":int(len(steering_peaks)),"n_concordant_beats":0,"concordant_beat_percentage":0.,"beat_tolerance_ms":beat_tolerance_ms,"median_peak_timing_error_ms":np.nan}
    a, b = np.diff(steering_peaks)*1000/fs, np.diff(seatbelt_peaks)*1000/fs
    n = min(len(a), len(b))
    if n == 0: return {"comparable": False, "limitations": LIMITATIONS, "coverage": 0.0, **beat_metrics}
    delta = a[:n] - b[:n]
    correlation = np.nan if n <= 1 or np.std(a[:n]) == 0 or np.std(b[:n]) == 0 else float(np.corrcoef(a[:n], b[:n])[0,1])
    return {"comparable": True, "n_rr_pairs": n, "mean_abs_rr_error_ms": float(np.mean(abs(delta))), "bias_ms": float(np.mean(delta)), "rr_correlation": correlation, "coverage": 1.0, "limitations": LIMITATIONS, **beat_metrics}

def validate_channels(steering_peaks: np.ndarray, seatbelt_peaks: np.ndarray, fs: float, config: dict | None = None, reference: ReferenceECGAdapter | None = None) -> dict:
    """Use a reference when supplied; otherwise preserve cross-channel-only validation."""
    tolerance=(config or {}).get("validation",{}).get("beat_tolerance_ms",80.)
    if reference is None:
        return {"mode":"cross_channel","steering_vs_seatbelt":compare_rr_intervals(steering_peaks,seatbelt_peaks,fs,tolerance)}
    ref_peaks=reference.r_peak_indices(fs)
    return {"mode":"against_reference","reference_present":True,"steering_vs_reference":compare_rr_intervals(steering_peaks,ref_peaks,fs,tolerance),"seatbelt_vs_reference":compare_rr_intervals(seatbelt_peaks,ref_peaks,fs,tolerance),"limitations":"Reference comparison depends on adapter synchronization and reference-sensor quality."}
