import numpy as np
from signal_processing.r_peak_detection import detect_r_peaks
from signal_processing.contact_quality import assess_contact_quality
from signal_processing.artifact_rejection import lms_filter, IMUNoiseReference, reject_motion_artifact
from validation.cross_channel_validation import compare_rr_intervals, validate_channels, ArrayReferenceECGAdapter, LIMITATIONS
from signal_processing.pipeline import process_recording
from signal_processing.types import ECGStream, GroundReference
from signal_processing.contact_quality import contact_quality_mask
from signal_processing.types import ChannelSeries, ECGChannel, PeakResult, QualityResult
from signal_processing.channel_fusion import fuse

CFG={"r_peaks":{"wavelet":"db4","refractory_ms":250,"threshold_mad_multiplier":2.5,"allow_pan_tompkins_fallback":True},"quality":{"min_kurtosis":1,"max_kurtosis":100,"min_template_correlation":.2}}
def synthetic_ecg(fs=250, seconds=20):
    x=np.random.default_rng(7).normal(0,.02,fs*seconds); peaks=np.arange(fs,fs*seconds,fs)
    for p in peaks: x[p-3:p+4] += np.array([.1,.4,.8,1,.8,.4,.1])
    return x,peaks
def test_wavelet_peaks_near_injected_peaks():
    ecg, truth=synthetic_ecg(); result=detect_r_peaks(ecg,250,CFG)
    assert sum(np.min(abs(result.indices-t)) < 20 for t in truth) >= len(truth)-2
def test_quality_gate_rejects_flat_signal():
    q=assess_contact_quality(np.zeros(1000),np.array([],dtype=int),250,CFG); assert not q.passed
def test_timestamp_aware_rolling_mask_requires_minimum_contact_duration():
    ecg, peaks = synthetic_ecg(seconds=20); cfg={**CFG, "quality":{**CFG["quality"], "rolling_seconds":5}, "hrv":{"minimum_valid_contact_seconds":25}}
    q=contact_quality_mask(ecg, np.arange(len(ecg))/250, cfg, peaks)
    assert not q.passed and q.segment_mask.size == 4 and q.valid_duration_seconds == 20
def test_lms_reduces_imu_correlated_artifact():
    rng=np.random.default_rng(3); imu=rng.normal(size=1000); clean=rng.normal(0,.1,1000); out=lms_filter(clean+imu,imu,.05,4)
    assert abs(np.corrcoef(out,imu)[0,1]) < abs(np.corrcoef(clean+imu,imu)[0,1])
def test_imu_reference_and_quality_gate_are_enforced():
    imu=np.column_stack([np.ones((100,3)), np.arange(100)[:,None]*np.ones((1,3))])
    assert IMUNoiseReference().reference(imu).shape == (100,)
    cfg={"filter":{"enabled":True,"method":"lms","lms_step_size":.01,"taps":4}}
    from signal_processing.types import QualityResult
    try: reject_motion_artifact(np.ones(100), imu, cfg, QualityResult(False,0,0))
    except ValueError: pass
    else: assert False, "filter must reject failed-contact ECG"
def test_intermittent_steering_contact_dropout_is_rejected_by_rolling_sqi():
    ecg, peaks=synthetic_ecg(seconds=20)
    ecg[250*4:250*6]=0; ecg[250*12:250*14]=0  # one-handed contact losses
    cfg={**CFG,"quality":{**CFG["quality"],"rolling_seconds":2},"hrv":{"minimum_valid_contact_seconds":10}}
    q=contact_quality_mask(ecg,np.arange(len(ecg))/250,cfg,peaks)
    assert not q.passed and not q.segment_mask.all()
def test_compare_and_flag_marks_shared_motion_even_when_peaks_agree():
    q=QualityResult(True,3,.9); peaks=np.array([250,500,750,1000]); motion=np.zeros(1250); motion[500:700]=10
    a=ChannelSeries(ECGChannel.STEERING_WHEEL,PeakResult(peaks,"test",confidence=np.ones(4)),q,250,motion)
    b=ChannelSeries(ECGChannel.SEATBELT,PeakResult(peaks,"test",confidence=np.ones(4)),q,250,motion.copy())
    rr=fuse(a,b,"compare_and_flag",{"fusion":{"max_peak_disagreement_ms":80,"max_rr_disagreement_ms":100}})
    assert rr.disagreement and rr.metadata["shared_motion_artifact_risk"]
def test_reduced_amplitude_seatbelt_exposes_lower_confidence_instead_of_silent_pass():
    ecg,_=synthetic_ecg(); low=ecg*.15+np.random.default_rng(8).normal(0,.04,len(ecg))
    result=detect_r_peaks(low,250,CFG)
    assert len(result.indices)>0 and result.confidence is not None and np.all((result.confidence>=0)&(result.confidence<=1))
    assert np.any(result.confidence < .4)
def test_mid_window_channel_handoff_excludes_cross_channel_rr_interval():
    q=QualityResult(True,3,.9); a=np.array([250,500,750]); b=np.array([1000,1250,1500])
    steering=ChannelSeries(ECGChannel.STEERING_WHEEL,PeakResult(a,"test",confidence=np.ones(3)),q,250)
    seatbelt=ChannelSeries(ECGChannel.SEATBELT,PeakResult(b,"test",confidence=np.ones(3)),q,250)
    rr=fuse(steering,seatbelt,"priority_only",{"fusion":{"max_peak_disagreement_ms":80,"max_rr_disagreement_ms":100}})
    assert len(rr.handoff_boundaries)==1 and len(rr.rr_ms)==4 and set(rr.beat_sources)=={"steering_wheel","seatbelt"}
def test_cross_channel_limitations_exposed():
    report=compare_rr_intervals(np.array([0,250,500]),np.array([2,252,502]),250)
    assert report["comparable"] and report["concordant_beat_percentage"] == 100 and "No gold-standard" in LIMITATIONS
def test_optional_chest_strap_adapter_replaces_cross_channel_target():
    peaks=np.array([0,250,500,750]); adapter=ArrayReferenceECGAdapter(peaks)
    report=validate_channels(peaks,peaks+2,250,{"validation":{"beat_tolerance_ms":20}},adapter)
    assert report["mode"] == "against_reference" and report["steering_vs_reference"]["concordant_beat_percentage"] == 100
def test_integration_pipeline_hard_gates_invalid_windows():
    cfg={**CFG,"sampling_rate_hz":250,"filter":{"enabled":False,"method":"lms","lms_step_size":.01,"taps":4},"hrv":{"window_seconds":10,"overlap_seconds":0,"minimum_valid_contact_seconds":5,"include_lf_hf":False},"fusion":{"primary":"steering_wheel","secondary":"seatbelt","max_peak_disagreement_ms":80,"max_rr_disagreement_ms":100}}
    ecg,_=synthetic_ecg(seconds=20); flat=np.zeros_like(ecg); result=process_recording(ECGStream(ecg),ECGStream(flat),GroundReference(np.zeros_like(ecg)),np.zeros_like(ecg),cfg)
    assert len(result)==2 and all(row["valid"] for row in result)
    bad=process_recording(ECGStream(flat),ECGStream(flat),GroundReference(flat),np.zeros_like(flat),cfg)
    assert all(not row["valid"] and np.isnan(row["rmssd_ms"]) for row in bad)
