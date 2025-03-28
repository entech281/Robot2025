package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.robot.io.RobotIO;
import frc.robot.operation.UserPolicy;

public class RotateToAngle extends EntechCommand {
    public static final double TOLERANCE = 0.5;
    private final double targetDeg;

    public RotateToAngle(double targetDeg) {
        this.targetDeg = targetDeg;
    }
    @Override
    public void end(boolean interrupted) {
        UserPolicy.getInstance().setAligningToAngle(false);
    }

    @Override
    public void execute() {
        UserPolicy.getInstance().setTargetAngle(targetDeg);
    }

    @Override
    public void initialize() {
        UserPolicy.getInstance().setAligningToAngle(true);
        UserPolicy.getInstance().setTargetAngle(RobotIO.getInstance().getOdometryPose().getRotation().getDegrees());
    }

    @Override
    public boolean isFinished() {
        return Math.abs(RobotIO.getInstance().getOdometryPose().getRotation().getDegrees() - targetDeg) <= TOLERANCE;
    }
}
