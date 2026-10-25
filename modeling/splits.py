from sklearn.model_selection import GroupShuffleSplit
import numpy as np

def subject_wise_split(X, y, subject_ids, test_size=.2, random_state=42):
    if len(X) != len(y) or len(y) != len(subject_ids): raise ValueError("X, y, and subject_ids must align")
    if len(np.unique(subject_ids)) < 2: raise ValueError("subject-wise splitting requires at least two subjects")
    train, test = next(GroupShuffleSplit(n_splits=1, test_size=test_size, random_state=random_state).split(X, y, subject_ids))
    if set(np.asarray(subject_ids)[train]) & set(np.asarray(subject_ids)[test]): raise RuntimeError("subject leakage detected")
    return train, test
