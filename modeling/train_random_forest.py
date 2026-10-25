"""Train/evaluate the independent RF baseline and emit ranked feature importances."""
from __future__ import annotations
import json
from pathlib import Path
from .features.dataset import rows_to_matrix
from .splits import subject_wise_split
from .random_forest import train_random_forest
from .evaluation import evaluate_binary

def run_random_forest_training(rows: list[dict], cfg: dict, target_key="drowsy", output_path: str | Path | None=None) -> dict:
    X,y,subjects,names,_=rows_to_matrix(rows,target_key); train,test=subject_wise_split(X,y,subjects)
    model,importance=train_random_forest(X[train],y[train],names,cfg); report={"feature_importance":importance,"evaluation":evaluate_binary(y[test],model.predict_proba(X[test])[:,1]),"train_subjects":sorted(set(subjects[train])),"test_subjects":sorted(set(subjects[test]))}
    if output_path: Path(output_path).write_text(json.dumps(report,indent=2),encoding="utf-8")
    return report
