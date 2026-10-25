"""Optional TensorFlow training entry point for padded HRV session sequences."""
from __future__ import annotations
from .lstm import build_lstm
from .lstm.sequence import prepare_sequences
from .splits import subject_wise_split

def run_lstm_training(rows, feature_names, cfg, target_key="drowsy", epochs=20):
    X,y,subjects=prepare_sequences(rows,feature_names,target_key,sequence_length=cfg["modeling"]["sequence_length_windows"])
    train,test=subject_wise_split(X,y,subjects); model=build_lstm(X.shape[1],X.shape[2]); model.fit(X[train],y[train],epochs=epochs,verbose=0)
    return model, {"test_indices":test,"test_probabilities":model.predict(X[test],verbose=0).reshape(-1),"test_labels":y[test],"test_subjects":subjects[test]}
