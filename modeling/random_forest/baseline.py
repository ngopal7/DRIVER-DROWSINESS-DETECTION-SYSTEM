"""Pure training function for the tabular HRV baseline."""
from sklearn.ensemble import RandomForestClassifier

def train_random_forest(X_train, y_train, feature_names, cfg):
    model = RandomForestClassifier(n_estimators=cfg["modeling"]["random_forest_trees"], class_weight="balanced", random_state=42, n_jobs=-1).fit(X_train, y_train)
    importance = dict(zip(feature_names, model.feature_importances_))
    return model, dict(sorted(importance.items(), key=lambda x: x[1], reverse=True))
