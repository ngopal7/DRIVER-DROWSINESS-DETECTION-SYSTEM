from __future__ import annotations
import numpy as np
from .types import ECGStream, GroundReference, PeakResult
from .artifact_rejection import reject_motion_artifact
from .artifact_rejection import IMUNoiseReference
from .r_peak_detection import detect_r_peaks
from .contact_quality import assess_contact_quality, valid_sample_ranges
from .channel_fusion import fuse_channels
from .hrv_features import compute_hrv_features

def process_recording(steering: ECGStream, seatbelt: ECGStream, ground: GroundReference, imu: np.ndarray, cfg: dict, timestamps_s: np.ndarray | None = None) -> list[dict]:
    fs = cfg["sampling_rate_hz"]; n = len(steering); window = int(cfg["hrv"]["window_seconds"] * fs); step = int((cfg["hrv"]["window_seconds"] - cfg["hrv"]["overlap_seconds"]) * fs)
    if step <= 0: raise ValueError("overlap must be smaller than window length")
    timestamps = np.asarray(timestamps_s) if timestamps_s is not None else np.arange(n) / fs
    if len(timestamps) != n: raise ValueError("timestamps_s must match ECG sample count")
    table = []
    for start in range(0, max(0, n-window+1), step):
        end = start + window; s, b, g, m, ts = steering[start:end], seatbelt[start:end], ground[start:end], imu[start:end], timestamps[start:end]
        motion = IMUNoiseReference().reference(m)
        raw_sp, raw_bp = detect_r_peaks(s, fs, cfg, motion), detect_r_peaks(b, fs, cfg, motion)
        sq, bq = assess_contact_quality(s, raw_sp.indices, fs, cfg, ts), assess_contact_quality(b, raw_bp.indices, fs, cfg, ts)
        sf = reject_motion_artifact(s, m, cfg, sq) if sq.passed else np.asarray(s, dtype=float)
        bf = reject_motion_artifact(b, m, cfg, bq) if bq.passed else np.asarray(b, dtype=float)
        sp = detect_r_peaks(sf, fs, cfg, motion) if sq.passed else raw_sp
        bp = detect_r_peaks(bf, fs, cfg, motion) if bq.passed else raw_bp
        # HRV must come from one uninterrupted valid-contact run. Select the
        # longest eligible primary-channel run; use seatbelt only if steering
        # has no eligible run. Never let fusion bridge a quality failure.
        sranges = valid_sample_ranges(sq, ts, cfg)
        branges = valid_sample_ranges(bq, ts, cfg)
        selected_channel = "steering_wheel" if sranges else ("seatbelt" if branges else None)
        ranges = sranges if selected_channel == "steering_wheel" else branges
        if ranges:
            seg_start, seg_end = max(ranges, key=lambda pair: pair[1] - pair[0])
            sp_valid = sp.indices[(sp.indices >= seg_start) & (sp.indices < seg_end)] if selected_channel == "steering_wheel" else np.array([], dtype=int)
            bp_valid = bp.indices[(bp.indices >= seg_start) & (bp.indices < seg_end)] if selected_channel == "seatbelt" else np.array([], dtype=int)
            failed = type(sq)(False, sq.kurtosis, sq.template_correlation, "excluded to preserve single-channel continuous segment", np.zeros_like(sq.segment_mask), 0.0) if selected_channel == "seatbelt" else type(bq)(False, bq.kurtosis, bq.template_correlation, "excluded to preserve single-channel continuous segment", np.zeros_like(bq.segment_mask), 0.0)
            effective_sq, effective_bq = (sq, failed) if selected_channel == "steering_wheel" else (failed, bq)
            sm = (sp.indices >= seg_start) & (sp.indices < seg_end)
            bm = (bp.indices >= seg_start) & (bp.indices < seg_end)
            sp_conf = sp.confidence[sm] if sp.confidence is not None and len(sp.confidence) == len(sm) else None
            bp_conf = bp.confidence[bm] if bp.confidence is not None and len(bp.confidence) == len(bm) else None
            sp_result = PeakResult(sp_valid, sp.method, sp.fallback_used, sp_conf)
            bp_result = PeakResult(bp_valid, bp.method, bp.fallback_used, bp_conf)
            fusion = fuse_channels(ECGStream(sf), ECGStream(bf), GroundReference(g), effective_sq, effective_bq, sp_valid, bp_valid, fs, cfg, sp_result, bp_result, motion)
            fusion.metadata["valid_segment_samples"] = [int(seg_start), int(seg_end)]
            fusion.metadata["valid_segment_seconds"] = float(ts[seg_end - 1] - ts[seg_start] + 1 / fs)
        else:
            fusion = fuse_channels(ECGStream(sf), ECGStream(bf), GroundReference(g), sq, bq, np.array([], dtype=int), np.array([], dtype=int), fs, cfg, sp, bp, motion)
        row = {"start_s": float(ts[0]), "end_s": float(ts[-1]), "valid": fusion.valid, "selected_channel": fusion.selected_channel.value if fusion.selected_channel else None, "valid_segment_seconds": fusion.metadata.get("valid_segment_seconds", 0.0), "valid_segment_samples": fusion.metadata.get("valid_segment_samples"), "fusion_disagreement": fusion.disagreement, "fusion_reason": fusion.reason, "steering_quality": sq.passed, "seatbelt_quality": bq.passed, "steering_contact_mask": sq.segment_mask, "seatbelt_contact_mask": bq.segment_mask, "steering_kurtosis": sq.kurtosis, "seatbelt_kurtosis": bq.kurtosis, "steering_template_correlation": sq.template_correlation, "seatbelt_template_correlation": bq.template_correlation, "steering_valid_contact_seconds": sq.valid_duration_seconds, "seatbelt_valid_contact_seconds": bq.valid_duration_seconds, "steering_detector": sp.method, "seatbelt_detector": bp.method}
        # Hard gate: no HRV values for invalid contact windows.
        continuous = not len(fusion.metadata.get("handoff_boundaries", []))
        row["valid"] = bool(fusion.valid and continuous)
        row["continuous_contact"] = continuous
        row.update(compute_hrv_features(fusion.peak_indices, fs, cfg) if row["valid"] else compute_hrv_features(np.array([]), fs, cfg))
        table.append(row)
    return table
