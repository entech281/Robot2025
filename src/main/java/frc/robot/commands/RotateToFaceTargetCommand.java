package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.robot.io.RobotIO;
import frc.robot.operation.UserPolicy;

public class RotateToFaceTargetCommand extends EntechCommand {
    private static final double KP = 5.0;
    public static final double TOLERANCE = 0.1;

    @Override
    public void end(boolean interrupted) {
        UserPolicy.getInstance().setAligningToAngle(false);
    }

    @Override
    public void execute() {
        double change = KP * RobotIO.getInstance().getVisionOutput().getTagXP();
        UserPolicy.getInstance().setTargetAngle(RobotIO.getInstance().getOdometryPose().getRotation().getDegrees() - change);
    }

    @Override
    public void initialize() {
        UserPolicy.getInstance().setAligningToAngle(true);
        UserPolicy.getInstance().setTargetAngle(RobotIO.getInstance().getOdometryPose().getRotation().getDegrees());
    }

    @Override
    public boolean isFinished() {
        return Math.abs(RobotIO.getInstance().getVisionOutput().getTagXP()) <= TOLERANCE;
    }
}
