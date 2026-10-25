"""Pure parser for timestamped psychomotor vigilance task results."""
from __future__ import annotations

def parse_pvt(records: list[dict]) -> list[dict]:
    """Validate and normalize PVT records into {kind, time_s, value}."""
    return [{"kind": "pvt", "time_s": float(r["time_s"]), "value": float(r["value"])} for r in records]
