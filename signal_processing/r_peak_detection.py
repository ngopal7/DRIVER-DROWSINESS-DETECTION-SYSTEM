from __future__ import annotations
import numpy as np
import pywt
from scipy.signal import butter, find_peaks, sosfiltfilt
from .types import PeakResult

def _adaptive_peaks(energy: np.ndarray, fs: float, cfg: dict) -> np.ndarray:
    r = cfg["r_peaks"]; distance = max(1, int(fs * r["refractory_ms"] / 1000))
    # Rolling robust threshold permits amplitude drift over a long drive.
    block = max(int(8 * fs), 1); candidates = []
    for start in range(0, len(energy), block):
        part = energy[start:start+block]
        med = np.median(part); threshold = med + r["threshold_mad_multiplier"] * np.median(abs(part-med))
        p, _ = find_peaks(part, height=threshold, distance=distance)
        candidates.extend(start + p)
    return np.asarray(candidates, dtype=int)

def _confidence(peaks: np.ndarray, wavelet_energy: np.ndarray, motion: np.ndarray | None, fs: float, cfg: dict) -> tuple[np.ndarray, dict]:
    """Combine morphology strength, RR plausibility, and low-motion agreement."""
    c = cfg["r_peaks"].get("confidence", {})
    weights = np.asarray([c.get("wavelet_amplitude_weight", .5), c.get("rr_plausibility_weight", .3), c.get("motion_artifact_weight", .2)], dtype=float)
    if np.any(weights < 0) or weights.sum() == 0: raise ValueError("R-peak confidence weights must be non-negative with positive sum")
    weights /= weights.sum()
    amplitudes, rr = wavelet_energy[peaks], np.diff(peaks) * 1000 / fs
    amplitude_score = np.empty(len(peaks))
    for i, p in enumerate(peaks):
        radius = max(1, int(4*fs)); local = wavelet_energy[max(0,p-radius):min(len(wavelet_energy),p+radius)]
        floor = np.median(local) + np.median(abs(local-np.median(local))) + 1e-12
        amplitude_score[i] = np.clip(amplitudes[i] / (amplitudes[i] + floor), 0, 1)
    rr_score = np.ones(len(peaks))
    if len(rr):
        for i, value in enumerate(rr, start=1):
            nearby = rr[max(0,i-5):min(len(rr),i+4)]; median = np.median(nearby)
            rr_score[i] = np.exp(-abs(value-median)/(0.20*median + 1e-12))
        rr_score[0] = rr_score[1] if len(peaks) > 1 else 1.
    if motion is None: motion_score = np.ones(len(peaks))
    else:
        m = np.asarray(motion, dtype=float).reshape(-1)
        if len(m) != len(wavelet_energy): raise ValueError("motion-artifact estimate must align with ECG samples")
        baseline = np.median(abs(m)) + np.median(abs(abs(m)-np.median(abs(m)))) + 1e-12
        motion_score = np.exp(-abs(m[peaks]) / baseline)
    combined = np.clip(weights[0]*amplitude_score + weights[1]*rr_score + weights[2]*motion_score, 0, 1)
    return combined, {"wavelet_amplitude": amplitude_score, "rr_plausibility": rr_score, "motion_artifact_agreement": motion_score}

def detect_r_peaks(ecg: np.ndarray, fs: float, cfg: dict, motion_artifact_estimate: np.ndarray | None = None) -> PeakResult:
    wavelet = cfg["r_peaks"]["wavelet"]
    try:
        coeff = pywt.wavedec(ecg, wavelet, level=min(4, pywt.dwt_max_level(len(ecg), pywt.Wavelet(wavelet).dec_len)))
        # Reconstruct only the level-1 detail.  `upcoef` changes alignment/length
        # for multilevel decompositions, while waverec preserves sample timing.
        isolated = [np.zeros_like(c) for c in coeff]
        isolated[-2] = coeff[-2]
        detail = pywt.waverec(isolated, wavelet)[:len(ecg)]
        candidates = _adaptive_peaks(detail ** 2, fs, cfg)
        # DWT marks candidate QRS neighborhoods. Squared wavelet detail can
        # produce several lobes per beat, so confirm candidates against one
        # prominent QRS-band extremum and apply a physiological RR floor.
        settings = cfg["r_peaks"]
        low, high = settings.get("qrs_band_hz", [5.0, 20.0])
        filtered = sosfiltfilt(butter(3, [low, high], btype="bandpass", fs=fs, output="sos"), np.asarray(ecg, dtype=float))
        magnitude = np.abs(filtered)
        noise = np.median(np.abs(magnitude - np.median(magnitude)))
        prominence = max(float(settings.get("minimum_prominence", .04)), float(settings.get("prominence_noise_multiplier", 3.0)) * noise)
        minimum_rr = float(settings.get("minimum_rr_ms", 450.0))
        distance = max(int(fs * settings["refractory_ms"] / 1000), int(fs * minimum_rr / 1000))
        qrs, _ = find_peaks(magnitude, distance=distance, prominence=prominence)
        confirm_samples = int(float(settings.get("wavelet_confirmation_ms", 100.0)) * fs / 1000)
        if len(candidates) and len(qrs):
            confirmed = [p for p in qrs if np.min(np.abs(candidates - p)) <= confirm_samples]
        else:
            confirmed = []
        peaks = np.asarray(confirmed, dtype=int)
        if len(peaks) >= 2:
            confidence, components = _confidence(peaks, detail**2, motion_artifact_estimate, fs, cfg)
            return PeakResult(peaks, "dwt_" + wavelet, False, confidence, components)
    except Exception:
        peaks = np.array([], dtype=int)
    if not cfg["r_peaks"]["allow_pan_tompkins_fallback"]: return PeakResult(peaks, "dwt_failed", False, np.array([]))
    derivative = np.diff(ecg, prepend=ecg[0]); peaks = _adaptive_peaks(derivative**2, fs, cfg)
    confidence, components = _confidence(peaks, derivative**2, motion_artifact_estimate, fs, cfg) if len(peaks) else (np.array([]), {})
    return PeakResult(peaks, "pan_tompkins_benchmark", True, confidence, components)
