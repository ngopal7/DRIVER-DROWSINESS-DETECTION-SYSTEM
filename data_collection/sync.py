"""Pure shared-clock/hardware-trigger safety assessment for acquisition streams."""
from dataclasses import dataclass
import numpy as np
@dataclass(frozen=True)
class SyncStatus:
    hardware_trigger: bool
    shared_clock: bool
    safe_for_long_session: bool
    reason: str
def assess_sync(hardware_trigger: bool, shared_clock: bool) -> SyncStatus:
    safe = hardware_trigger and shared_clock
    return SyncStatus(hardware_trigger, shared_clock, safe, "hardware-triggered shared clock" if safe else "UNSAFE: software timestamps alone drift in long sessions")

@dataclass(frozen=True)
class ClockDriftModel:
    """Affine device-clock drift used for simulation and visible correction tests."""
    offset_seconds: float = 0.0
    drift_ppm: float = 0.0
    def apply(self, shared_clock_seconds: np.ndarray) -> np.ndarray:
        return np.asarray(shared_clock_seconds, dtype=float) * (1 + self.drift_ppm * 1e-6) + self.offset_seconds
    def correct(self, device_clock_seconds: np.ndarray) -> np.ndarray:
        return (np.asarray(device_clock_seconds, dtype=float) - self.offset_seconds) / (1 + self.drift_ppm * 1e-6)

def estimate_affine_drift(shared_clock_seconds: np.ndarray, device_clock_seconds: np.ndarray) -> ClockDriftModel:
    """Estimate drift from hardware trigger/checkpoint pairs; never infer it silently."""
    shared, device = np.asarray(shared_clock_seconds), np.asarray(device_clock_seconds)
    if len(shared) < 2 or len(shared) != len(device): raise ValueError("at least two matched sync checkpoints are required")
    slope, offset = np.polyfit(shared, device, 1)
    return ClockDriftModel(float(offset), float((slope - 1) * 1e6))

@dataclass(frozen=True)
class DriftReport:
    checkpoint_reference_seconds: np.ndarray
    offsets_ms: np.ndarray
    model: ClockDriftModel
    drift_detected: bool

def detect_clock_drift(reference_timestamps_s: np.ndarray, device_timestamps_s: np.ndarray, check_interval_seconds: float = 60., threshold_ms: float = 5.) -> DriftReport:
    """Perform periodic offset checks on matched sample clocks and estimate affine drift."""
    reference, device=np.asarray(reference_timestamps_s,float),np.asarray(device_timestamps_s,float)
    if len(reference)!=len(device) or len(reference)<2: raise ValueError("matched reference/device timestamp arrays are required")
    if check_interval_seconds <= 0: raise ValueError("check_interval_seconds must be positive")
    checkpoints=np.arange(reference[0],reference[-1]+check_interval_seconds,check_interval_seconds); checkpoints=np.clip(checkpoints,reference[0],reference[-1]); checkpoints=np.unique(checkpoints)
    observed=np.interp(checkpoints,reference,device); offsets_ms=(observed-checkpoints)*1000
    model=estimate_affine_drift(checkpoints,observed)
    return DriftReport(checkpoints,offsets_ms,model,bool(np.max(abs(offsets_ms)) > threshold_ms))

def resample_to_reference_clock(samples: np.ndarray, device_timestamps_s: np.ndarray, reference_timestamps_s: np.ndarray, model: ClockDriftModel | None = None) -> np.ndarray:
    """Interpolate device samples onto the shared reference clock after visible drift correction."""
    values=np.asarray(samples,float); device=np.asarray(device_timestamps_s,float); reference=np.asarray(reference_timestamps_s,float)
    if len(values)!=len(device): raise ValueError("sample and device timestamp counts must match")
    # The target's device-clock coordinate is affine(reference); this makes
    # interpolation correct both clock offset and long-session rate drift.
    correction=model or estimate_affine_drift(reference,device)
    target_device_time=correction.apply(reference)
    if values.ndim == 1: return np.interp(target_device_time,device,values)
    if values.ndim == 2: return np.column_stack([np.interp(target_device_time,device,values[:,i]) for i in range(values.shape[1])])
    raise ValueError("samples must be one- or two-dimensional")
