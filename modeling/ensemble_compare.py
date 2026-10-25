"""Benchmark RF, LSTM, and an optional combination on identical held-out subjects."""
from __future__ import annotations
import numpy as np
from .ensemble import Ensemble
from .evaluation import evaluate_binary
from .features.dataset import rows_to_matrix
from .splits import subject_wise_split
from .random_forest import train_random_forest
from .lstm.sequence import prepare_sequences
from .lstm import build_lstm

def compare_prediction_sets(y_true, rf_prob, lstm_prob, ensemble_mode="benchmark_independent", weight=.5):
    """Evaluate each model independently; disabled ensemble produces no fused metric."""
    report={"random_forest":evaluate_binary(y_true,rf_prob),"lstm":evaluate_binary(y_true,lstm_prob),"ensemble_mode":ensemble_mode}
    combined=Ensemble(ensemble_mode,weight).predict_proba(rf_prob,lstm_prob)
    if ensemble_mode != "benchmark_independent": report["ensemble"]=evaluate_binary(y_true,combined)
    return report

def run_table_comparison(rows, cfg, target_key="drowsy"):
    """Train RF/LSTM on the same subject-held-out synthetic/real table when TensorFlow is available."""
    X,y,subjects,names,usable=rows_to_matrix(rows,target_key)
    train,test=subject_wise_split(X,y,subjects)
    rf,_=train_random_forest(X[train],y[train],names,cfg); rf_prob=rf.predict_proba(X[test])[:,1]
    test_subjects=set(subjects[test]); seq_x,seq_y,seq_subjects=prepare_sequences(usable,names,target_key,sequence_length=cfg["modeling"]["sequence_length_windows"])
    # Every row becomes one padded sequence, so IDs preserve alignment with usable rows.
    mask=np.asarray([s in test_subjects for s in seq_subjects])
    try:
        model=build_lstm(seq_x.shape[1],seq_x.shape[2]); model.fit(seq_x[~mask],seq_y[~mask],epochs=20,verbose=0)
        lstm_prob=model.predict(seq_x[mask],verbose=0).reshape(-1)
    except RuntimeError as exc:
        return {"random_forest":evaluate_binary(y[test],rf_prob),"lstm_status":"unavailable","reason":str(exc),"ensemble_status":"skipped: requires aligned LSTM predictions"}
    if not np.array_equal(seq_y[mask],y[test]): raise RuntimeError("sequence/table test labels are not aligned")
    return compare_prediction_sets(y[test],rf_prob,lstm_prob,cfg["modeling"]["ensemble_mode"])
