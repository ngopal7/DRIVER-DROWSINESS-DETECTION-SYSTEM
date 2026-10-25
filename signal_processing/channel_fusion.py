"""Pure strategy-based fusion of SQI-gated channel beat series."""
from __future__ import annotations
import numpy as np
from .types import ECGChannel, ECGStream, GroundReference, ChannelSeries, FusionResult, PeakResult, QualityResult, RRSeries

def _confidence(result: PeakResult) -> np.ndarray:
    return result.confidence if result.confidence is not None and len(result.confidence) == len(result.indices) else np.ones(len(result.indices))

def _as_rr(peaks: np.ndarray, sources: np.ndarray, fs: float, **kwargs) -> RRSeries:
    # Exclude the interval crossing a source handoff: it is not continuous ECG.
    same = sources[1:] == sources[:-1]
    rr = np.diff(peaks) * 1000 / fs
    handoffs = np.flatnonzero(~same) + 1
    return RRSeries(peaks, rr[same], sources, len(peaks) > 1, handoff_boundaries=handoffs, **kwargs)

def _valid(series: ChannelSeries) -> bool: return series.quality.passed and len(series.peaks.indices) > 1

def _priority(a: ChannelSeries, b: ChannelSeries) -> RRSeries:
    primary, secondary = (a, b) if a.channel == ECGChannel.STEERING_WHEEL else (b, a)
    if _valid(primary):
        if not _valid(secondary): return _as_rr(primary.peaks.indices, np.full(len(primary.peaks.indices), primary.channel.value), primary.fs, metadata={"strategy":"priority_only"})
        # Seatbelt continuity beats are admitted only outside primary coverage or
        # into an abnormally long primary RR gap; simultaneous beats stay primary.
        pp, sp = primary.peaks.indices, secondary.peaks.indices
        expected = np.median(np.diff(pp)) if len(pp) > 1 else np.inf
        fill = []
        for p in sp:
            pos = np.searchsorted(pp, p)
            if pos == 0 or pos == len(pp): fill.append(p)
            elif pp[pos] - pp[pos-1] > 1.5*expected: fill.append(p)
        peaks=np.concatenate([pp,np.asarray(fill,dtype=int)]); sources=np.concatenate([np.full(len(pp),primary.channel.value),np.full(len(fill),secondary.channel.value)])
        order=np.argsort(peaks)
        return _as_rr(peaks[order], sources[order], primary.fs, metadata={"strategy":"priority_only","seatbelt_continuity_beats":len(fill)})
    if _valid(secondary): return _as_rr(secondary.peaks.indices, np.full(len(secondary.peaks.indices), secondary.channel.value), secondary.fs, metadata={"strategy":"priority_only", "primary_failed":True})
    return RRSeries(np.array([],dtype=int),np.array([],dtype=float),np.array([],dtype=str),False,metadata={"strategy":"priority_only"})

def _disagreement(a: ChannelSeries, b: ChannelSeries, cfg: dict) -> tuple[bool, float, dict]:
    if not (_valid(a) and _valid(b)): return False, float("nan"), {"comparison_coverage":"single_channel"}
    nearest = np.min(abs(a.peaks.indices[:,None] - b.peaks.indices[None,:]), axis=1) * 1000 / a.fs
    timing = float(np.median(nearest)); n=min(len(a.peaks.indices),len(b.peaks.indices))
    rr_delta = abs(np.diff(a.peaks.indices)[:max(0,n-1)] - np.diff(b.peaks.indices)[:max(0,n-1)]) * 1000/a.fs
    rr = float(np.median(rr_delta)) if len(rr_delta) else float("nan")
    f=cfg.get("fusion", cfg); flagged=timing > f["max_peak_disagreement_ms"] or (np.isfinite(rr) and rr > f["max_rr_disagreement_ms"])
    # Agreement under coincident high motion is explicitly not evidence of correctness.
    shared_motion = False
    ma, mb = getattr(a, "motion_artifact_estimate", None), getattr(b, "motion_artifact_estimate", None)
    if ma is not None and mb is not None:
        ma, mb = np.asarray(ma), np.asarray(mb)
        if len(ma) and len(mb): shared_motion = np.corrcoef(ma, mb)[0,1] > .8 and np.percentile(abs(ma),95) > 0
    return bool(flagged or shared_motion), max(timing, rr if np.isfinite(rr) else 0), {"timing_disagreement_ms":timing,"rr_disagreement_ms":rr,"shared_motion_artifact_risk":bool(shared_motion)}

def _full_fusion(a: ChannelSeries, b: ChannelSeries, cfg: dict) -> RRSeries:
    if not _valid(a): return _priority(a,b)
    if not _valid(b): return _priority(a,b)
    tolerance = cfg.get("fusion",cfg)["max_peak_disagreement_ms"] * a.fs / 1000
    pa,pb,ca,cb=a.peaks.indices,b.peaks.indices,_confidence(a.peaks),_confidence(b.peaks)
    used_b=set(); peaks=[]; sources=[]
    for i,p in enumerate(pa):
        candidates=np.flatnonzero(abs(pb-p)<=tolerance); candidates=np.array([j for j in candidates if j not in used_b])
        if len(candidates):
            j=candidates[np.argmin(abs(pb[candidates]-p))]; used_b.add(int(j)); total=ca[i]+cb[j]
            peaks.append(int(round((ca[i]*p+cb[j]*pb[j])/total))); sources.append("confidence_fused")
        else: peaks.append(int(p)); sources.append(a.channel.value)
    for j,p in enumerate(pb):
        if j not in used_b: peaks.append(int(p)); sources.append(b.channel.value)
    order=np.argsort(peaks); return _as_rr(np.asarray(peaks)[order],np.asarray(sources)[order],a.fs,metadata={"strategy":"full_fusion"})

def fuse(channel_a: ChannelSeries, channel_b: ChannelSeries, strategy: str | None, config: dict) -> RRSeries:
    """Select a named strategy; never silently treats agreement as gold-standard truth."""
    strategy = strategy or config.get("fusion_strategy") or config.get("fusion",{}).get("strategy", "priority_only")
    if channel_a.fs != channel_b.fs: raise ValueError("channel series must share a sampling rate")
    if strategy == "priority_only": return _priority(channel_a,channel_b)
    if strategy == "compare_and_flag":
        base=_priority(channel_a,channel_b); flag,magnitude,meta=_disagreement(channel_a,channel_b,config)
        base.disagreement,base.disagreement_ms,base.metadata=flag,magnitude,{**base.metadata,**meta,"strategy":"compare_and_flag"}; return base
    if strategy == "full_fusion": return _full_fusion(channel_a,channel_b,config)
    raise ValueError("fusion strategy must be priority_only, compare_and_flag, or full_fusion")

def fuse_channels(steering: ECGStream, seatbelt: ECGStream, ground: GroundReference, steering_quality: QualityResult, seatbelt_quality: QualityResult, steering_peaks: np.ndarray, seatbelt_peaks: np.ndarray, fs: float, cfg: dict, steering_result: PeakResult | None = None, seatbelt_result: PeakResult | None = None, motion_artifact_estimate: np.ndarray | None = None) -> FusionResult:
    """Compatibility adapter; ground cannot enter the ChannelSeries interface."""
    if len(ground) != len(steering) or len(steering) != len(seatbelt): raise ValueError("All synchronized streams must have equal sample length")
    a=ChannelSeries(ECGChannel.STEERING_WHEEL, steering_result or PeakResult(steering_peaks,"legacy"), steering_quality, fs, motion_artifact_estimate)
    b=ChannelSeries(ECGChannel.SEATBELT, seatbelt_result or PeakResult(seatbelt_peaks,"legacy"), seatbelt_quality, fs, motion_artifact_estimate)
    rr=fuse(a,b,cfg.get("fusion_strategy"),cfg)
    selected = ECGChannel.STEERING_WHEEL if len(rr.beat_sources) and rr.beat_sources[0] == ECGChannel.STEERING_WHEEL.value else (ECGChannel.SEATBELT if len(rr.beat_sources) and rr.beat_sources[0] == ECGChannel.SEATBELT.value else None)
    return FusionResult(selected,rr.peak_indices,rr.valid,rr.disagreement,"fusion strategy applied",{**rr.metadata,"rr_series":rr,"handoff_boundaries":rr.handoff_boundaries,"disagreement_ms":rr.disagreement_ms})
