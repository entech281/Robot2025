package frc.robot;

public enum Position {
    HOME,
    SAFE_EXTEND,
    L1,
    L2,
    L3,
    L4,
    AUTO_L1,
    AUTO_L2,
    AUTO_L3,
    AUTO_L4,
    ALGAE_L2,
    ALGAE_L3,
    ALGAE_GROUND,
    ALGAE_HOME,
    BARGE,
    FLICK_LEVEL;

    public String getElevatorKey() {
        return "ElevatorSubsystem/" + toString();
    }

    public String getPivotKey() {
        return "PivotSubsystem/" + toString();
    }
}
