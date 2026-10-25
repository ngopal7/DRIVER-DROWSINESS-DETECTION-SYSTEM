"""Public chip-neutral acquisition interface and timestamped sample contract."""
from .daq import ECGDAQ, ECGFrame
__all__ = ["ECGDAQ", "ECGFrame"]
