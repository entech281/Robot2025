package frc.robot.commandCheker;

public class Move {
    
    private double targetElevator;
    private double targetPivot;

    public Move (double targetElevator, double targetPivot) {
        this.targetElevator = targetElevator;
        this.targetPivot = targetPivot;
    }

    public double getTargetElevator() {
        return targetElevator;
    }

    public double getTargetPivot() {        
        return targetPivot;
    }

    public String toString() {
        return "targetElevator: " + targetElevator + " targetPivot: " + targetPivot;
    }
}
