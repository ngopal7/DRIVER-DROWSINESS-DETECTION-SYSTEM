"""Build model-ready rows only from the windowed HRV feature-table contract."""
def build_feature_table(windows: list[dict]) -> list[dict]:
    return [row.copy() for row in windows if row.get("valid", False)]
