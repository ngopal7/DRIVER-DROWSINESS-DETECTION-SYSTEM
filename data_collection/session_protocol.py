"""Explicit, timestamp-loggable six-phase participant session state machine."""
from enum import Enum
from dataclasses import dataclass, field

class SessionPhase(str, Enum):
    BASELINE_REST="baseline_rest"; PRE_DRIVE_KSS="pre_drive_kss"; DRIVE="drive"; PERIODIC_KSS="periodic_kss"; PVT="pvt"; POST_DRIVE_KSS="post_drive_kss"; COMPLETE="complete"

_NEXT={SessionPhase.BASELINE_REST:SessionPhase.PRE_DRIVE_KSS,SessionPhase.PRE_DRIVE_KSS:SessionPhase.DRIVE,SessionPhase.DRIVE:SessionPhase.PERIODIC_KSS,SessionPhase.PERIODIC_KSS:SessionPhase.PVT,SessionPhase.PVT:SessionPhase.POST_DRIVE_KSS,SessionPhase.POST_DRIVE_KSS:SessionPhase.COMPLETE}
@dataclass(frozen=True)
class PhaseTransition:
    timestamp_s: float
    from_phase: SessionPhase
    to_phase: SessionPhase
    event: str = "phase_advance"

@dataclass
class DriveSession:
    phase: SessionPhase=SessionPhase.BASELINE_REST
    drive_minutes: int=45
    kss_interval_minutes: int=10
    transition_log: list[PhaseTransition] = field(default_factory=list)
    def advance(self, timestamp_s: float):
        """Advance only through the approved protocol and append an immutable audit event."""
        if self.phase == SessionPhase.COMPLETE: raise RuntimeError("session already complete")
        if timestamp_s < 0: raise ValueError("transition timestamp must be non-negative")
        previous=self.phase; self.phase = _NEXT[self.phase]
        self.transition_log.append(PhaseTransition(float(timestamp_s),previous,self.phase))
        return self.phase
    def structured_log(self) -> list[dict]:
        return [{"timestamp_s":e.timestamp_s,"from_phase":e.from_phase.value,"to_phase":e.to_phase.value,"event":e.event} for e in self.transition_log]
    def validate(self):
        if not 45 <= self.drive_minutes <= 90: raise ValueError("drive must be 45–90 minutes")
        if not 5 <= self.kss_interval_minutes <= 10: raise ValueError("KSS interval must be 5–10 minutes")
