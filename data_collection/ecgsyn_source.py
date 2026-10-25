"""Adapter for an externally built ECGSYN executable; keeps GPL source out of this package."""
from __future__ import annotations
from dataclasses import dataclass
from pathlib import Path
import math
import subprocess
import tempfile
import re
import numpy as np

@dataclass(frozen=True)
class ECGSYNOutput:
    time_s: np.ndarray
    ecg_mv: np.ndarray
    annotations: np.ndarray

    @property
    def r_peak_indices(self) -> np.ndarray:
        return np.flatnonzero(self.annotations == 3)

def generate_ecgsyn(executable: str | Path, duration_s: float, sampling_rate_hz: int, settings: dict) -> ECGSYNOutput:
    """Run ECGSYN CLI and parse its time, mV signal, and PQRST marker columns.

    The executable must come from a complete, separately built ECGSYN package.
    ECGSYN requires its internal rate to be an integer multiple of ECG rate.
    """
    runtime=settings.get("runtime","native")
    if runtime == "wsl":
        wsl_binary=str(executable)
        match=re.match(r"^/mnt/([a-zA-Z])/(.*)$",wsl_binary)
        local_binary=Path(f"{match.group(1).upper()}:/"+match.group(2).replace("/","\\")) if match else None
        if local_binary is None or not local_binary.is_file(): raise FileNotFoundError(f"WSL ECGSYN executable is not available at {wsl_binary}")
        distribution=settings.get("wsl_distribution","Ubuntu")
        command_prefix=["wsl.exe","-d",distribution,"--",wsl_binary]
    elif runtime == "native":
        binary=Path(executable).expanduser().resolve()
        if not binary.is_file(): raise FileNotFoundError(f"ECGSYN executable not found: {binary}")
        command_prefix=[str(binary)]
    else: raise ValueError("ECGSYN runtime must be native or wsl")
    if duration_s <= 0 or sampling_rate_hz <= 0: raise ValueError("duration and sampling rate must be positive")
    internal=int(settings.get("internal_sampling_hz",sampling_rate_hz*2))
    if internal % sampling_rate_hz: raise ValueError("ECGSYN internal_sampling_hz must be an integer multiple of sampling_rate_hz")
    hr_mean=float(settings.get("hr_mean_bpm",70)); hr_std=float(settings.get("hr_std_bpm",2))
    if hr_mean <= 0 or hr_std < 0: raise ValueError("invalid ECGSYN heart-rate parameters")
    beats=max(3,math.ceil(duration_s*hr_mean/60*1.25)+8)
    workspace_temp_root=Path(__file__).resolve().parents[1]
    with tempfile.TemporaryDirectory(prefix=".ecgsyn-",dir=workspace_temp_root) as temporary:
        output=Path(temporary)/"ecgsyn.dat"
        output_arg="ecgsyn.dat"
        if runtime == "wsl":
            resolved=output.resolve()
            drive=resolved.drive.rstrip(":").lower(); relative=resolved.as_posix().split(":",1)[-1].lstrip("/")
            output_arg=f"/mnt/{drive}/{relative}"
        # C stores the output path in a 100-byte array; use short paths.
        args=command_prefix+["-O",output_arg,"-n",str(beats),"-s",str(sampling_rate_hz),"-S",str(internal),"-a","0","-h",str(hr_mean),"-H",str(hr_std),"-f",str(settings.get("lf_hz",.1)),"-F",str(settings.get("hf_hz",.25)),"-v",str(settings.get("lf_std_hz",.01)),"-V",str(settings.get("hf_std_hz",.01)),"-q",str(settings.get("lf_hf_ratio",.5)),"-R",str(int(settings.get("seed",7)))]
        try:
            completed=subprocess.run(args,cwd=temporary,capture_output=True,text=True,timeout=int(settings.get("timeout_seconds",120)),check=False)
        except OSError as exc:
            raise RuntimeError(f"could not start ECGSYN executable {binary}: {exc}") from exc
        if completed.returncode != 0: raise RuntimeError(f"ECGSYN exited {completed.returncode}: {completed.stderr[-2000:]} {completed.stdout[-1000:]}")
        if not output.is_file(): raise RuntimeError("ECGSYN completed without creating its requested output file")
        data=np.loadtxt(output,ndmin=2)
    if data.shape[1] < 3: raise ValueError("ECGSYN output must contain time, ECG mV, and PQRST annotation columns")
    valid=data[:,0] < duration_s
    data=data[valid,:3]
    expected=round(duration_s*sampling_rate_hz)
    if len(data) < expected: raise RuntimeError(f"ECGSYN returned {len(data)} samples; {expected} required for requested duration")
    data=data[:expected]
    return ECGSYNOutput(data[:,0],data[:,1],data[:,2].astype(int))
