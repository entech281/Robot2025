package frc.robot.commandCheker;

public class SafeZone {

    private double elevatorStart;
    private double elevatorEnd;
    private double pivotStart;
    private double pivotEnd;

    public SafeZone(double elevatorStart, double elevatorEnd, double pivotStart, double pivotEnd) {
        this.elevatorStart = elevatorStart;
        this.elevatorEnd = elevatorEnd;
        this.pivotStart = pivotStart;
        this.pivotEnd = pivotEnd;
    }

    public double getElevatorStart() {
        return elevatorStart;
    }

    public double getElevatorEnd() {
        return elevatorEnd;
    }

    public double getPivotStart() {
        return pivotStart;
    } 

    public double getPivotEnd() {
        return pivotEnd;
    }

    public String toString() {
        return "(" + elevatorStart + ", " + elevatorEnd + ", " + pivotStart + ", " + pivotEnd + ")";
    }
    
}
