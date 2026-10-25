"""Deterministic multi-channel ECG sessions with known HRV, artifacts, and clock drift."""
from __future__ import annotations
from dataclasses import dataclass, field
from pathlib import Path
import numpy as np
from .sync import ClockDriftModel
from .ecgsyn_source import generate_ecgsyn

@dataclass(frozen=True)
class Dropout:
    channel: str
    start_s: float
    end_s: float

@dataclass(frozen=True)
class MotionSpike:
    start_s: float
    end_s: float
    amplitude: float = 1.0

@dataclass
class SyntheticSession:
    steering_ecg: np.ndarray
    seatbelt_ecg: np.ndarray
    ground: np.ndarray
    imu: np.ndarray
    shared_timestamps_s: np.ndarray
    steering_timestamps_s: np.ndarray
    seatbelt_timestamps_s: np.ndarray
    true_peak_indices: np.ndarray
    true_peak_times_s: np.ndarray
    fatigue_trend: np.ndarray
    metadata: dict = field(default_factory=dict)

def _qrs_template(fs: float) -> np.ndarray:
    width=max(3, round(.025*fs)); x=np.arange(-width,width+1)/width
    return np.exp(-x*x*5) - .15*np.exp(-((x+.8)/.35)**2)

def generate_synthetic_session(
    session_seconds: float = 180, fs: float = 250, seed: int = 7,
    dropouts: tuple[Dropout, ...] = (), motion_spikes: tuple[MotionSpike, ...] = (),
    seatbelt_amplitude_scale: float = .65, handoff_s: float | None = None,
    seatbelt_clock_drift: ClockDriftModel = ClockDriftModel(),
    ecgsyn_executable: str | Path | None = None, ecgsyn_settings: dict | None = None,
) -> SyntheticSession:
    """Generate true beat timings whose variability increases gradually with fatigue.

    The channels share physiological truth but have independent noise. Motion is
    deliberately correlated across channels, and contact losses are literal
    signal loss rather than interpolated ECG.
    """
    rng=np.random.default_rng(seed); n=int(session_seconds*fs); t=np.arange(n)/fs
    fatigue=np.clip(t/session_seconds,0,1)
    if ecgsyn_executable is not None:
        settings={"seed":seed,**(ecgsyn_settings or {})}
        clean=generate_ecgsyn(ecgsyn_executable,session_seconds,round(fs),settings)
        source_peak_times=clean.time_s[clean.r_peak_indices]
        if len(source_peak_times)<3: raise RuntimeError("ECGSYN output contained fewer than three annotated R peaks")
        source_rr=np.diff(source_peak_times); center=float(np.median(source_rr))
        # Slowly scale beat-to-beat RR excursions to create a gradual HRV drift
        # while preserving each ECGSYN PQRST cycle and its morphology.
        local_fatigue=np.clip(source_peak_times/session_seconds,0,1)
        gain=float(settings.get("fatigue_variability_gain",1.5))
        target_rr=np.maximum(.45,center+(source_rr-center)*(1+gain*local_fatigue[1:]))
        peak_times=np.concatenate([[source_peak_times[0]],source_peak_times[0]+np.cumsum(target_rr)])
        keep=peak_times<session_seconds; peak_times=peak_times[keep]
        peaks=np.rint(peak_times*fs).astype(int)
        # Piecewise-affine time warp retimes clean ECGSYN cycles to the known
        # fatigue RR trajectory. This preserves morphology and exact event labels.
        src_knots=np.concatenate([[0.],source_peak_times,[source_peak_times[-1]+source_rr[-1]]])
        dst_knots=np.concatenate([[0.],np.concatenate([[source_peak_times[0]],source_peak_times[0]+np.cumsum(target_rr)]),[source_peak_times[0]+np.sum(target_rr)+target_rr[-1]]])
        mapped=np.interp(t,dst_knots,src_knots)
        base=np.interp(mapped,clean.time_s,clean.ecg_mv)
        true_rr=np.diff(peak_times)*1000
        waveform_source="ecgsyn_cli"
    else:
        rr=[]; times=[]; current=.8
        while current < session_seconds-.5:
            variability=.012 + .055*float(np.interp(current,t,fatigue))
            delta=float(rng.normal(0,variability)); rr.append(.8+delta); times.append(current); current += max(.45,.8+delta)
        peak_times=np.asarray(times); peaks=np.rint(peak_times*fs).astype(int)
        base=np.zeros(n); template=_qrs_template(fs); half=len(template)//2
        for p in peaks:
            if half <= p < n-half: base[p-half:p+half+1] += template
        true_rr=np.diff(peak_times)*1000; waveform_source="simple_qrs"
    steering=base + rng.normal(0,.018,n); seatbelt=seatbelt_amplitude_scale*base + rng.normal(0,.028,n)
    imu=rng.normal(0,.015,n)
    for spike in motion_spikes:
        mask=(t>=spike.start_s)&(t<spike.end_s); shape=spike.amplitude*np.sin(np.linspace(0,10*np.pi,mask.sum()))
        imu[mask] += shape; steering[mask] += .35*shape; seatbelt[mask] += .25*shape
    effective=list(dropouts)
    if handoff_s is not None: effective.append(Dropout("steering_wheel",handoff_s,session_seconds))
    for dropout in effective:
        mask=(t>=dropout.start_s)&(t<dropout.end_s)
        target=steering if dropout.channel == "steering_wheel" else seatbelt
        target[mask]=rng.normal(0,.002,mask.sum())
    return SyntheticSession(steering,seatbelt,np.zeros(n),imu,t,t,seatbelt_clock_drift.apply(t),peaks,peak_times,fatigue,{"true_rr_ms":true_rr,"dropouts":effective,"motion_spikes":motion_spikes,"seatbelt_clock_drift":seatbelt_clock_drift,"waveform_source":waveform_source})

def generate_configured_session(config: dict, **overrides) -> SyntheticSession:
    """Build a session from the centralized `synthetic` config without silent source fallback."""
    settings=config["synthetic"]
    source=settings.get("waveform_source","simple_qrs")
    executable=overrides.pop("ecgsyn_executable",None) or settings.get("ecgsyn_executable")
    if source == "ecgsyn_cli" and not executable: raise ValueError("synthetic.ecgsyn_executable is required when waveform_source is ecgsyn_cli")
    if source not in ("ecgsyn_cli","simple_qrs"): raise ValueError("waveform_source must be ecgsyn_cli or simple_qrs")
    return generate_synthetic_session(session_seconds=settings["session_seconds"],fs=config["sampling_rate_hz"],seed=settings["seed"],seatbelt_amplitude_scale=settings["seatbelt_amplitude_scale"],seatbelt_clock_drift=ClockDriftModel(0.,settings["seatbelt_clock_drift_ppm"]),ecgsyn_executable=executable if source == "ecgsyn_cli" else None,ecgsyn_settings=settings.get("ecgsyn",{}),**overrides)
