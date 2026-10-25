import json
import numpy as np
from data_collection.synthetic_ecg import Dropout, MotionSpike, generate_synthetic_session
from data_collection.sync import ClockDriftModel, detect_clock_drift, resample_to_reference_clock
from modeling.features.synthetic_workflow import build_synthetic_modeling_table, evaluate_synthetic_recovery

def _cfg():
    return {"sampling_rate_hz":250,"quality":{"rolling_seconds":5,"min_kurtosis":1,"max_kurtosis":100,"min_template_correlation":.15},"filter":{"enabled":False,"method":"lms","lms_step_size":.01,"taps":4},"r_peaks":{"wavelet":"db4","refractory_ms":250,"threshold_mad_multiplier":2.5,"allow_pan_tompkins_fallback":True,"confidence":{"wavelet_amplitude_weight":.5,"rr_plausibility_weight":.3,"motion_artifact_weight":.2}},"hrv":{"window_seconds":30,"overlap_seconds":0,"minimum_valid_contact_seconds":10,"include_lf_hf":False},"fusion_strategy":"compare_and_flag","fusion":{"strategy":"compare_and_flag","primary":"steering_wheel","secondary":"seatbelt","max_peak_disagreement_ms":80,"max_rr_disagreement_ms":100},"labeling":{"strategy":"nearest","max_alignment_seconds":120}}

def test_synthetic_multichannel_regression_workflow(tmp_path):
    session=generate_synthetic_session(session_seconds=90,dropouts=(Dropout("steering_wheel",20,25),),motion_spikes=(MotionSpike(40,45,2),),seatbelt_amplitude_scale=.35,handoff_s=65,seatbelt_clock_drift=ClockDriftModel(.02,30))
    target=tmp_path/"synthetic_feature_table.json"; rows=build_synthetic_modeling_table(session,_cfg(),target)
    assert len(rows)==3 and target.exists()
    assert all("kss" in row and "pvt" in row and "true_rmssd_ms" in row for row in rows)
    assert np.isclose(rows[0]["clock_drift_ppm_corrected"],30,atol=.01)
    assert json.loads(target.read_text())[0]["synthetic_fatigue_trend"] >= 0
    recovery=evaluate_synthetic_recovery(rows)
    assert recovery["n_valid_windows"] > 0 and "rmssd_ms_mae_ms" in recovery

def test_drift_detection_and_resampling_use_injected_generator_clock_model():
    session=generate_synthetic_session(session_seconds=90,seatbelt_clock_drift=ClockDriftModel(.02,30))
    report=detect_clock_drift(session.shared_timestamps_s,session.seatbelt_timestamps_s,20,.5)
    restored=resample_to_reference_clock(session.seatbelt_ecg,session.seatbelt_timestamps_s,session.shared_timestamps_s,report.model)
    assert report.drift_detected and np.isclose(report.model.drift_ppm,30,atol=.01)
    assert np.allclose(restored,session.seatbelt_ecg,atol=1e-8)
