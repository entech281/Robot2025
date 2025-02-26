package frc.robot.commandchecker;

import frc.robot.RobotConstants;

public class SafeCommandChecker {
    
    private SafeZone[] safeZones;

    public SafeCommandChecker() {
        this.safeZones = RobotConstants.SafeZones.SAFE_ZONES;
    }

    public boolean isSafe(Move command) {
        for (SafeZone zone : safeZones) {
            if (command.getTargetElevator() < zone.getElevatorEnd() && command.getTargetElevator() > zone.getElevatorStart() &&
                command.getTargetPivot() < zone.getPivotEnd() && command.getTargetPivot() > zone.getPivotStart()) {
                return true;
            }
        }
        return false;
    }

}