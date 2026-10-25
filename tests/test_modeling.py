import numpy as np
from modeling.splits import subject_wise_split
from modeling.random_forest import train_random_forest
from modeling.lstm.sequence import prepare_sequences
from modeling.ensemble_compare import compare_prediction_sets
from modeling.train_random_forest import run_random_forest_training

def _rows():
    rows=[]
    for subject in range(6):
        for window in range(4):
            drowsy=int(window>=2); rows.append({"subject_id":f"s{subject}","session_id":f"drive{subject}","start_s":window*30,"valid":True,"rmssd_ms":20+30*drowsy+subject,"sdnn_ms":30+20*drowsy,"pnn50":.05+.3*drowsy,"drowsy":drowsy})
    return rows
def test_subject_wise_split_has_no_subject_leakage():
    X=np.zeros((24,2)); y=np.tile([0,1],12); subjects=np.repeat(np.arange(6),4)
    train,test=subject_wise_split(X,y,subjects,test_size=.33)
    assert not (set(subjects[train]) & set(subjects[test]))
def test_rf_feature_importances_and_sequence_padding_are_runnable():
    rows=_rows(); X=np.array([[r[k] for k in ("rmssd_ms","sdnn_ms","pnn50")] for r in rows]); y=np.array([r["drowsy"] for r in rows])
    model,importance=train_random_forest(X,y,["rmssd_ms","sdnn_ms","pnn50"],{"modeling":{"random_forest_trees":10}})
    assert model and list(importance)[0] in {"rmssd_ms","sdnn_ms","pnn50"}
    sequences,labels,subjects=prepare_sequences(rows,["rmssd_ms","sdnn_ms","pnn50"],"drowsy",sequence_length=5)
    assert sequences.shape == (24,5,3) and np.isnan(sequences[0,:4]).all() and len(labels)==len(subjects)
    report=run_random_forest_training(rows,{"modeling":{"random_forest_trees":10}},output_path=None)
    assert report["feature_importance"] and not (set(report["train_subjects"]) & set(report["test_subjects"]))
def test_ensemble_mode_can_be_fully_disabled_for_independent_benchmarks():
    y=np.array([0,0,1,1]); rf=np.array([.1,.3,.7,.9]); lstm=np.array([.2,.4,.6,.8])
    report=compare_prediction_sets(y,rf,lstm,"benchmark_independent")
    assert {"random_forest","lstm"}.issubset(report) and "ensemble" not in report
