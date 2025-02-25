package frc.robot.commandCheker;

import frc.robot.io.RobotIO;

public class SafeMovementChecker {

    SafeCommandChecker checker;

    public SafeMovementChecker() {
        this.checker = new SafeCommandChecker();
    }
    
    public boolean isSafeElevatorMove(double targetElevator) {
        Move command = new Move(targetElevator, RobotIO.getInstance().getPivotOutput().getCurrentPosition());
        
        return checker.isSafe(command);
    }

    public boolean isSafeElevatorMove(double targetElevator, double currentPivot) {
        Move command = new Move(targetElevator, currentPivot);
        
        return checker.isSafe(command);
    }

    public boolean isSafePivotMove(double targetPivot) {
        Move command = new Move(RobotIO.getInstance().getElevatorOutput().getCurrentPosition(), targetPivot);
        
        return checker.isSafe(command);
    }

    public boolean isSafePivotMove(double targetPivot, double currentElevator) {
        Move command = new Move(currentElevator, targetPivot);
        
        return checker.isSafe(command);
    }
}
