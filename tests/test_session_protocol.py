from data_collection.session_protocol import DriveSession, SessionPhase

def test_session_phase_transitions_are_timestamped_and_structured():
    session=DriveSession(); session.validate()
    for timestamp in (300.,301.,2700.,2701.,2710.,2720.): session.advance(timestamp)
    log=session.structured_log()
    assert session.phase == SessionPhase.COMPLETE and len(log) == 6
    assert log[0] == {"timestamp_s":300.,"from_phase":"baseline_rest","to_phase":"pre_drive_kss","event":"phase_advance"}
