"""Convert validated ESP32/ADS1293 transport frames into synchronized arrays."""
from __future__ import annotations
from dataclasses import dataclass
import numpy as np
from .daq import ECGFrame

@dataclass(frozen=True)
class FrameBatch:
    timestamps_s: np.ndarray
    imu_timestamps_s: np.ndarray
    steering_ecg_counts: np.ndarray
    seatbelt_ecg_counts: np.ndarray
    imu: np.ndarray
    steering_contact_valid: np.ndarray
    seatbelt_contact_valid: np.ndarray
    sequence_gaps: tuple[int, ...]

def frames_to_batch(frames: list[ECGFrame], sample_rate_hz: float) -> FrameBatch:
    """Preserve hardware time and flag sequence/contact gaps; never interpolate missing ECG."""
    if sample_rate_hz <= 0: raise ValueError("sample_rate_hz must be positive")
    if not frames: raise ValueError("at least one ECGFrame is required")
    ordered=sorted(frames,key=lambda f:f.sequence)
    if any(b.timestamp_us <= a.timestamp_us for a,b in zip(ordered,ordered[1:])): raise ValueError("frame timestamps must increase with sequence")
    gaps=tuple(b.sequence-a.sequence-1 for a,b in zip(ordered,ordered[1:]) if b.sequence-a.sequence>1)
    times=np.asarray([f.timestamp_us for f in ordered],float)*1e-6
    imu=np.full((len(ordered),6),np.nan); imu_times=np.full(len(ordered),np.nan)
    for i,frame in enumerate(ordered):
        if frame.imu_accel_gyro is not None and frame.imu_timestamp_us is not None:
            imu[i]=frame.imu_accel_gyro
            # Preserve true IMU time; downstream sync correction/resampling decides alignment.
            imu_times[i]=frame.imu_timestamp_us*1e-6
    return FrameBatch(times,imu_times,np.asarray([f.steering_ecg_counts for f in ordered]),np.asarray([f.seatbelt_ecg_counts for f in ordered]),imu,
        np.asarray([not (f.lead_off_mask&1) for f in ordered]),np.asarray([not (f.lead_off_mask&2) for f in ordered]),gaps)
