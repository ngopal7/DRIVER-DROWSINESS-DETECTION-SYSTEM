"""Load and validate the single pipeline configuration source."""
from pathlib import Path
import yaml

REQUIRED = {"sampling_rate_hz", "hrv", "quality", "filter", "r_peaks", "fusion", "labeling", "modeling", "android", "sensor_geometry"}

def load_config(path: str | Path = "config/pipeline_config.yaml") -> dict:
    with open(path, encoding="utf-8") as f: config = yaml.safe_load(f)
    missing = REQUIRED - set(config or {})
    if missing: raise ValueError(f"pipeline config missing: {sorted(missing)}")
    if config["hrv"]["overlap_seconds"] >= config["hrv"]["window_seconds"]: raise ValueError("overlap_seconds must be below window_seconds")
    return config
