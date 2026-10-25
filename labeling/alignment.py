from __future__ import annotations
import numpy as np

def align_assessments(windows: list[dict], assessments: list[dict], strategy: str, max_seconds: float) -> list[dict]:
    """KSS and PVT remain independent outcome streams; caller supplies kind per record."""
    for w in windows:
        center = (w["start_s"] + w["end_s"]) / 2
        for kind in ("kss", "pvt"):
            points = sorted((a for a in assessments if a["kind"] == kind), key=lambda a:a["time_s"])
            if not points: w[kind] = np.nan; continue
            times = np.array([p["time_s"] for p in points]); values = np.array([p["value"] for p in points], dtype=float)
            if strategy == "windowed":
                inside = values[(times >= w["start_s"]) & (times <= w["end_s"])]; w[kind] = float(np.mean(inside)) if len(inside) else np.nan
            elif strategy == "interpolated": w[kind] = float(np.interp(center, times, values)) if times[0] <= center <= times[-1] else np.nan
            else:
                idx = int(np.argmin(abs(times-center))); w[kind] = float(values[idx]) if abs(times[idx]-center) <= max_seconds else np.nan
    return windows
