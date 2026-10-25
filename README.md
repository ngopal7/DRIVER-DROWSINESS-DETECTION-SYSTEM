# HRV Driver-Drowsiness Stack

This research-oriented stack enforces quality gating before HRV, keeps ECG channel consistency separate from ground-truth validation, and treats KSS/PVT as independently aligned labels.

## Build order

1. Signal processing and synthetic tests
2. Cross-channel validation and labeling alignment
3. Subject-wise RF baseline, then sequential LSTM and ensemble comparison
4. Session protocol / DAQ tooling (can proceed in parallel)
5. Export to Android and perform numerical-parity plus real/semi-synthetic multichannel integration tests

Install with `pip install -e .[test,deep-learning]`, then run `pytest`. All open experimental/deployment parameters live in `config/pipeline_config.yaml`.

## ECGSYN synthetic sessions

`data_collection/ecgsyn_source.py` wraps an ECGSYN CLI and preserves its per-sample PQRST annotations (`3` marks an R peak). The supplied source set builds when `ran1.c` and `dfour1.c` are provided; these Numerical Recipes files are not copied into this repository. In this workspace, the source was built with WSL GCC in GNU89 mode as `.tools/ecgsyn/ecgsyn`, then exercised through the Python adapter. The config points to that local WSL executable. The built artifact is for local use and should not be redistributed with the supplied Numerical Recipes dependencies. `run_configured_synthetic_regression(...)` retimes ECGSYN cycles with a gradual fatigue-related RR-variability scale, injects the existing channel artifacts, runs the Python pipeline, and returns a model-ready table plus recovered-versus-known RMSSD/SDNN metrics. The `simple_qrs` source remains available for quick plumbing checks.

Safety note: this is a research stack, not a certified driver-safety device. A failed contact window yields no HRV-based decision; Android must log that decision and use its product safety policy.
