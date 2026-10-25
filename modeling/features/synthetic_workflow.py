"""Run known synthetic physiology through production pipeline into model-table rows."""
from __future__ import annotations
import json
from pathlib import Path
import numpy as np
from data_collection.synthetic_ecg import SyntheticSession, generate_configured_session
from data_collection.sync import detect_clock_drift, resample_to_reference_clock
from signal_processing.pipeline import process_recording
from signal_processing.types import ECGStream, GroundReference
from labeling.alignment import align_assessments

def _truth_for_window(session: SyntheticSession, start_s: float, end_s: float) -> dict:
    rr=session.metadata["true_rr_ms"]
    times=session.true_peak_times_s[1:]
    selected=rr[(times>=start_s)&(times<=end_s)]
    if len(selected)<2: return {"true_rmssd_ms":np.nan,"true_sdnn_ms":np.nan}
    return {"true_rmssd_ms":float(np.sqrt(np.mean(np.diff(selected)**2))),"true_sdnn_ms":float(np.std(selected,ddof=1))}

def build_synthetic_modeling_table(session: SyntheticSession, cfg: dict, output_path: str | Path | None = None, subject_id="synthetic_subject", session_id="synthetic_session") -> list[dict]:
    """Correct declared clock drift, process ECG, independently align KSS/PVT proxies, and optionally save JSON."""
    drift_report=detect_clock_drift(session.shared_timestamps_s,session.seatbelt_timestamps_s)
    estimated=drift_report.model
    corrected_seatbelt_ts=estimated.correct(session.seatbelt_timestamps_s)
    if not np.allclose(corrected_seatbelt_ts,session.shared_timestamps_s,atol=1e-8): raise ValueError("clock-drift correction did not recover shared timeline")
    aligned_seatbelt=resample_to_reference_clock(session.seatbelt_ecg,session.seatbelt_timestamps_s,session.shared_timestamps_s,estimated)
    rows=process_recording(ECGStream(session.steering_ecg),ECGStream(aligned_seatbelt),GroundReference(session.ground),session.imu,cfg,session.shared_timestamps_s)
    labels=[]
    for time_s in np.arange(0,session.shared_timestamps_s[-1]+1,60):
        trend=float(np.interp(time_s,session.shared_timestamps_s,session.fatigue_trend))
        labels.extend([{"kind":"kss","time_s":float(time_s),"value":1+8*trend},{"kind":"pvt","time_s":float(time_s),"value":250+250*trend}])
    rows=align_assessments(rows,labels,cfg["labeling"]["strategy"],cfg["labeling"]["max_alignment_seconds"])
    for row in rows:
        row.update(_truth_for_window(session,row["start_s"],row["end_s"])); row["synthetic_fatigue_trend"]=float(np.interp((row["start_s"]+row["end_s"])/2,session.shared_timestamps_s,session.fatigue_trend)); row["drowsy"]=int(row["synthetic_fatigue_trend"] >= .5); row["subject_id"]=subject_id; row["session_id"]=session_id; row["clock_drift_ppm_corrected"]=estimated.drift_ppm
        # Convert ndarray metadata into JSON-safe feature-table values.
        for key,value in list(row.items()):
            if isinstance(value,np.ndarray): row[key]=value.tolist()
    if output_path is not None: Path(output_path).write_text(json.dumps(rows,allow_nan=True,indent=2),encoding="utf-8")
    return rows

def evaluate_synthetic_recovery(rows: list[dict]) -> dict:
    """Regression metrics comparing recovered HRV to generator truth on valid windows."""
    report={"n_valid_windows":0}
    for feature in ("rmssd_ms","sdnn_ms"):
        pairs=[(r[feature],r["true_"+feature]) for r in rows if r.get("valid") and np.isfinite(r[feature]) and np.isfinite(r["true_"+feature])]
        if pairs:
            recovered,truth=np.asarray(pairs).T; report[feature+"_mae_ms"]=float(np.mean(abs(recovered-truth))); report[feature+"_correlation"]=float(np.corrcoef(recovered,truth)[0,1]) if len(pairs)>1 and np.std(recovered)>0 and np.std(truth)>0 else np.nan
            report["n_valid_windows"]=len(pairs)
        else: report[feature+"_mae_ms"]=np.nan; report[feature+"_correlation"]=np.nan
    return report

def run_configured_synthetic_regression(config: dict, ecgsyn_executable: str | Path | None = None, output_path: str | Path | None = None, **session_overrides):
    """Generate ECGSYN physiology, inject project artifacts, run the full pipeline, and report recovery."""
    session=generate_configured_session(config,ecgsyn_executable=ecgsyn_executable,**session_overrides)
    rows=build_synthetic_modeling_table(session,config,output_path)
    return {"session":session,"feature_table":rows,"recovery":evaluate_synthetic_recovery(rows)}
