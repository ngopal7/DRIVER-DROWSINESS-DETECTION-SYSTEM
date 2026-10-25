from sklearn.metrics import roc_auc_score, average_precision_score, roc_curve, precision_recall_curve, confusion_matrix

def evaluate_binary(y_true, probabilities):
    fpr, tpr, thresholds = roc_curve(y_true, probabilities); precision, recall, pr_thresholds = precision_recall_curve(y_true, probabilities)
    tradeoffs = []
    for t in thresholds:
        tn, fp, fn, tp = confusion_matrix(y_true, probabilities >= t, labels=[0,1]).ravel()
        tradeoffs.append({"threshold":float(t), "false_positive_rate":fp/(fp+tn) if fp+tn else 0, "false_negative_rate":fn/(fn+tp) if fn+tp else 0})
    return {"roc_auc":roc_auc_score(y_true, probabilities), "pr_auc":average_precision_score(y_true, probabilities), "roc":{"fpr":fpr.tolist(),"tpr":tpr.tolist(),"thresholds":thresholds.tolist()}, "pr":{"precision":precision.tolist(),"recall":recall.tolist(),"thresholds":pr_thresholds.tolist()}, "false_positive_false_negative_tradeoffs":tradeoffs}
