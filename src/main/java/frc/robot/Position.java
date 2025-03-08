package frc.robot;

public enum Position {
    HOME,
    SAFE_EXTEND,
    L1,
    L2,
    L3,
    L4,
    ALGAE_L2,
    ALGAE_L3,
    ALGAE_GROUND,
    ALGAE_HOME,
    BARGE;

    public String getElevatorKey() {
        return "ElevatorSubsystem/" + toString();
    }

    public String getPivotKey() {
        return "PivotSubsystem/" + toString();
    }
}
