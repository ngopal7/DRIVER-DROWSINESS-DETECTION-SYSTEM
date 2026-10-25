"""Hard ECG contact gate; downstream HRV must not use a failed window."""
from __future__ import annotations
import numpy as np
from scipy.stats import kurtosis
from .types import QualityResult

def _mask_sample_ranges(mask: np.ndarray, timestamps_s: np.ndarray, rolling_seconds: float, minimum_seconds: float) -> list[tuple[int, int]]:
    """Map runs of valid rolling bins to sample-index ranges meeting minimum duration."""
    if not len(mask): return []
    padded=np.r_[False,mask,False].astype(np.int8); edges=np.diff(padded)
    starts=np.flatnonzero(edges==1); ends=np.flatnonzero(edges==-1); ranges=[]
    for first,last in zip(starts,ends):
        start_time=timestamps_s[0]+first*rolling_seconds
        end_time=min(timestamps_s[-1]+np.median(np.diff(timestamps_s)), timestamps_s[0]+last*rolling_seconds)
        if end_time-start_time >= minimum_seconds:
            a=int(np.searchsorted(timestamps_s,start_time,side="left")); b=int(np.searchsorted(timestamps_s,end_time,side="left"))
            if b-a >= 2: ranges.append((a,b))
    return ranges

def valid_sample_ranges(quality: QualityResult, timestamps_s: np.ndarray, config: dict) -> list[tuple[int, int]]:
    """Return all contiguous SQI-valid sample runs long enough for HRV."""
    if quality.segment_mask is None: return []
    q=config["quality"]; minimum=float(config.get("hrv",{}).get("minimum_valid_contact_seconds",q.get("rolling_seconds",10)))
    return _mask_sample_ranges(np.asarray(quality.segment_mask,bool),np.asarray(timestamps_s,float),float(q.get("rolling_seconds",10)),minimum)

def _median_template_correlation(signal: np.ndarray, peaks: np.ndarray, fs: float) -> float:
    half = max(1, round(0.10 * fs))
    beats = [signal[p-half:p+half+1] for p in peaks if p-half >= 0 and p+half < len(signal)]
    if len(beats) < 3:
        return 0.0
    template = np.median(np.asarray(beats), axis=0)
    norm_t = np.linalg.norm(template)
    if not norm_t:
        return 0.0
    scores = []
    for beat in beats:
        denom = np.linalg.norm(beat) * norm_t
        if denom:
            scores.append(abs(float(np.dot(beat, template) / denom)))
    return float(np.median(scores)) if scores else 0.0

def contact_quality_mask(
    samples: np.ndarray, timestamps_s: np.ndarray, config: dict, peak_indices: np.ndarray | None = None,
) -> QualityResult:
    """Return a timestamp-aware rolling contact mask.

    Each mask item represents one configurable rolling interval. The result
    passes when at least one contiguous good-contact run meets
    ``hrv.minimum_valid_contact_seconds``; downstream selects a single such run.
    Failed intervals are therefore never bridged in HRV calculations.
    """
    q = config["quality"]
    x, ts = np.asarray(samples, dtype=float), np.asarray(timestamps_s, dtype=float)
    if x.ndim != 1 or ts.ndim != 1 or len(x) != len(ts) or len(x) < 4:
        return QualityResult(False, float("nan"), 0.0, "samples/timestamps invalid or too short", np.array([], dtype=bool), 0.0)
    if np.any(~np.isfinite(ts)) or np.any(np.diff(ts) <= 0):
        return QualityResult(False, float("nan"), 0.0, "timestamps must be finite and strictly increasing", np.array([], dtype=bool), 0.0)
    rolling_s = float(q.get("rolling_seconds", 10.0)); starts = np.arange(ts[0], ts[-1], rolling_s)
    masks, ks, corrs = [], [], []
    peaks = np.asarray(peak_indices if peak_indices is not None else [], dtype=int)
    fs = 1 / np.median(np.diff(ts))
    for start in starts:
        indices = np.flatnonzero((ts >= start) & (ts < start + rolling_s))
        part = x[indices]
        local_peaks = np.searchsorted(indices, peaks[(peaks >= indices[0]) & (peaks <= indices[-1])]) if len(indices) else np.array([], dtype=int)
        k = float(kurtosis(part, fisher=False, bias=False)) if len(part) > 3 else float("nan")
        corr = _median_template_correlation(part, local_peaks, fs) if peak_indices is not None else 1.0
        masks.append(np.isfinite(k) and q["min_kurtosis"] <= k <= q["max_kurtosis"] and corr >= q["min_template_correlation"])
        ks.append(k); corrs.append(corr)
    mask = np.asarray(masks, dtype=bool)
    minimum = float(config.get("hrv", {}).get("minimum_valid_contact_seconds", rolling_s))
    ranges=_mask_sample_ranges(mask,ts,rolling_s,minimum)
    duration=max(((ts[min(b-1,len(ts)-1)]-ts[a]+np.median(np.diff(ts))) for a,b in ranges),default=0.0)
    passed=bool(ranges)
    reason="" if passed else "no continuous SQI-valid segment meets minimum duration"
    median_k = float(np.nanmedian(ks)) if np.isfinite(ks).any() else float("nan")
    median_corr = float(np.nanmedian(corrs)) if np.isfinite(corrs).any() else 0.0
    return QualityResult(passed, median_k, median_corr, reason, mask, duration)

def assess_contact_quality(signal: np.ndarray, peak_indices: np.ndarray, fs: float, config: dict, timestamps_s: np.ndarray | None = None) -> QualityResult:
    """Compatibility wrapper; use :func:`contact_quality_mask` for raw ECG + timestamps."""
    timestamps = np.asarray(timestamps_s) if timestamps_s is not None else np.arange(len(signal), dtype=float) / fs
    return contact_quality_mask(signal, timestamps, config, peak_indices)
