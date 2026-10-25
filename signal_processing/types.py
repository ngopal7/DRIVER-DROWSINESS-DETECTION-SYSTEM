from __future__ import annotations
from dataclasses import dataclass, field
from enum import Enum
from typing import NewType
import numpy as np

ECGStream = NewType("ECGStream", np.ndarray)
"""An ECG stream. GroundReference is deliberately a distinct, non-ECG type."""
GroundReference = NewType("GroundReference", np.ndarray)

class ECGChannel(str, Enum):
    STEERING_WHEEL = "steering_wheel"
    SEATBELT = "seatbelt"

@dataclass(frozen=True)
class QualityResult:
    passed: bool
    kurtosis: float
    template_correlation: float
    reason: str = ""
    segment_mask: np.ndarray | None = None
    valid_duration_seconds: float = 0.0

@dataclass
class PeakResult:
    indices: np.ndarray
    method: str
    fallback_used: bool = False
    confidence: np.ndarray | None = None
    confidence_components: dict[str, np.ndarray] = field(default_factory=dict)

@dataclass
class ChannelSeries:
    """One SQI-gated ECG channel's detected beats for fusion."""
    channel: ECGChannel
    peaks: PeakResult
    quality: QualityResult
    fs: float
    motion_artifact_estimate: np.ndarray | None = None

@dataclass
class RRSeries:
    """RR intervals never span a channel handoff or contact discontinuity."""
    peak_indices: np.ndarray
    rr_ms: np.ndarray
    beat_sources: np.ndarray
    valid: bool
    disagreement: bool = False
    disagreement_ms: float = float("nan")
    handoff_boundaries: np.ndarray = field(default_factory=lambda: np.array([], dtype=int))
    metadata: dict = field(default_factory=dict)

@dataclass
class FusionResult:
    selected_channel: ECGChannel | None
    peak_indices: np.ndarray
    valid: bool
    disagreement: bool
    reason: str
    metadata: dict = field(default_factory=dict)
