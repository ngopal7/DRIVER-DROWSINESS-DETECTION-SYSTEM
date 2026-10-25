"""Chip-neutral, timestamp-preserving boundary from hardware into the pipeline."""
from abc import ABC, abstractmethod
from dataclasses import dataclass
import numpy as np

@dataclass(frozen=True)
class ECGFrame:
    """One simultaneous AFE frame; reference electrode is metadata, never an ECG channel."""
    sequence: int
    timestamp_us: int
    steering_ecg_counts: int
    seatbelt_ecg_counts: int
    lead_off_mask: int
    imu_timestamp_us: int | None = None
    imu_accel_gyro: tuple[float, float, float, float, float, float] | None = None
    status: int = 0

    @property
    def imu_synchronized(self) -> bool:
        return self.imu_timestamp_us == self.timestamp_us

class ECGDAQ(ABC):
    """Chip-neutral DAQ boundary; implementations may wrap ADS1293/ESP32 or another DAQ."""
    @abstractmethod
    def start(self, hardware_trigger): ...
    @abstractmethod
    def read_samples(self): ...
    @abstractmethod
    def stop(self): ...

    def read_frames(self, max_frames: int = 256) -> list[ECGFrame]:
        """Return frames in acquisition order with hardware monotonic timestamps."""
        raise NotImplementedError
