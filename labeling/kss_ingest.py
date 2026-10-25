"""Pure parser for timestamped Karolinska Sleepiness Scale records."""
from __future__ import annotations

def parse_kss(records: list[dict]) -> list[dict]:
    """Validate and normalize KSS records into {kind, time_s, value}."""
    return [{"kind": "kss", "time_s": float(r["time_s"]), "value": float(r["value"])} for r in records]
