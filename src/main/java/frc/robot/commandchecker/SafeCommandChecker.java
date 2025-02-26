package frc.robot.commandcheker;

import frc.robot.RobotConstants;

public class SafeCommandChecker {
    
    private SafeZone[] safeZones;

    public SafeCommandChecker() {
        this.safeZones = RobotConstants.SafeZones.safeZones;
    }

    public boolean isSafe(Move command) {
        for (SafeZone zone : safeZones) {
            System.out.println(zone);
            System.out.println(command);
            if (command.getTargetElevator() < zone.getElevatorEnd() && command.getTargetElevator() > zone.getElevatorStart() &&
                command.getTargetPivot() < zone.getPivotEnd() && command.getTargetPivot() > zone.getPivotStart()) {
                return true;
            }
        }
        return false;
    }

}



