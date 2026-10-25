import numpy as np

class Ensemble:
    """Swappable policy; benchmark_independent deliberately does not fuse predictions."""
    def __init__(self, mode="benchmark_independent", weight=.5): self.mode, self.weight = mode, weight
    def predict_proba(self, rf_prob, lstm_prob):
        if self.mode == "benchmark_independent": return {"random_forest": rf_prob, "lstm": lstm_prob}
        if self.mode == "weighted_average": return self.weight*np.asarray(rf_prob)+(1-self.weight)*np.asarray(lstm_prob)
        raise NotImplementedError("stacked ensemble requires validation-set meta-model fitting")
