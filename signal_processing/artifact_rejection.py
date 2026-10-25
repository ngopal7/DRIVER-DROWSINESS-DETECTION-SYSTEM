"""Adaptive, motion-reference artifact removal for quality-gated ECG."""
from __future__ import annotations
from typing import Protocol
import numpy as np
from .types import QualityResult

class NoiseReferenceSource(Protocol):
    """Swappable source: IMU today, another synchronized proxy later."""
    def reference(self, samples: np.ndarray) -> np.ndarray: ...

class IMUNoiseReference:
    """Build one reference from scalar IMU or [ax,ay,az,gx,gy,gz] samples."""
    def reference(self, samples: np.ndarray) -> np.ndarray:
        imu = np.asarray(samples, dtype=float)
        if imu.ndim == 1: return imu
        if imu.ndim != 2 or imu.shape[1] not in (3, 6):
            raise ValueError("IMU must be (n,), (n,3) accelerometer, or (n,6) accel+gyro")
        accel = np.linalg.norm(imu[:, :3], axis=1)
        if imu.shape[1] == 3: return accel
        gyro = np.linalg.norm(imu[:, 3:], axis=1)
        return (accel-np.median(accel))/(np.std(accel)+1e-8) + (gyro-np.median(gyro))/(np.std(gyro)+1e-8)

class IdentityNoiseReference:
    """Intentional no-reference mode for sessions without IMU."""
    def reference(self, samples: np.ndarray) -> np.ndarray: return np.zeros(len(samples), dtype=float)

def lms_filter(ecg: np.ndarray, noise_reference: np.ndarray, step_size: float = .01, taps: int = 8) -> np.ndarray:
    """Normalized LMS subtracting the component correlated with the reference."""
    x, d = np.asarray(noise_reference, dtype=float).reshape(-1), np.asarray(ecg, dtype=float).reshape(-1)
    if len(x) != len(d): raise ValueError("ECG and noise reference must be synchronized and equal length")
    w, out = np.zeros(taps), np.empty_like(d); padded = np.pad(x, (taps - 1, 0))
    for i, desired in enumerate(d):
        v = padded[i:i+taps][::-1]; error = desired - float(w @ v)
        w += step_size * error * v / (1e-8 + v @ v); out[i] = error
    return out

def kalman_filter(ecg: np.ndarray, noise_reference: np.ndarray, process_noise: float = 1e-4, measurement_noise: float = 1e-2) -> np.ndarray:
    if len(ecg) != len(noise_reference): raise ValueError("ECG and noise reference must be synchronized and equal length")
    state, covariance, out = 0.0, 1.0, np.empty(len(ecg)); scale = np.std(ecg)/(np.std(noise_reference)+1e-8)
    for i, (sample, motion) in enumerate(zip(ecg, noise_reference)):
        covariance += process_noise*(1+abs(motion)); gain = covariance/(covariance+measurement_noise)
        state += gain*(motion*scale-state); covariance *= 1-gain; out[i] = sample-state
    return out

def reject_motion_artifact(gated_ecg: np.ndarray, imu: np.ndarray | None, cfg: dict, quality: QualityResult | None = None, reference_source: NoiseReferenceSource | None = None) -> np.ndarray:
    """Filter passed-contact ECG only; failed gates raise instead of silently cleaning."""
    if quality is not None and not quality.passed: raise ValueError("refusing artifact rejection: ECG failed contact-quality gate")
    f = cfg["filter"]
    if not f["enabled"] or imu is None: return np.asarray(gated_ecg, dtype=float).copy()
    reference = (reference_source or IMUNoiseReference()).reference(imu)
    # An adaptive canceller with a near-quiet reference can learn incidental
    # ECG/noise correlation and distort QRS timing. Treat that as no usable
    # motion reference; the threshold is tunable for the installed IMU scale.
    if np.std(reference) < float(f.get("minimum_reference_rms", 0.05)):
        return np.asarray(gated_ecg, dtype=float).copy()
    if f["method"] == "lms": return lms_filter(gated_ecg, reference, f["lms_step_size"], f["taps"])
    if f["method"] == "kalman": return kalman_filter(gated_ecg, reference)
    raise ValueError("filter.method must be 'lms' or 'kalman'")
