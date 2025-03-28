package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.robot.io.RobotIO;
import frc.robot.operation.UserPolicy;

public class RotateToFaceTargetCommand extends EntechCommand {
    private static final double KP = 7.0;
    public static final double TOLERANCE = 2.5;

    @Override
    public void end(boolean interrupted) {
        UserPolicy.getInstance().setAligningToAngle(false);
    }

    @Override
    public void execute() {
        if (RobotIO.getInstance().getVisionOutput().hasTarget() && RobotIO.getInstance().getVisionOutput().getTargets().get(0).getTagXW() >= -99) {
            double change = KP * RobotIO.getInstance().getVisionOutput().getTargets().get(0).getTagXW();
            UserPolicy.getInstance().setTargetAngle(RobotIO.getInstance().getOdometryPose().getRotation().getDegrees() - change);
        }
    }

    @Override
    public void initialize() {
        UserPolicy.getInstance().setAligningToAngle(true);
        UserPolicy.getInstance().setTargetAngle(RobotIO.getInstance().getOdometryPose().getRotation().getDegrees());
    }

    @Override
    public boolean isFinished() {
        return RobotIO.getInstance().getVisionOutput().hasTarget() && Math.abs(RobotIO.getInstance().getVisionOutput().getTargets().get(0).getTagXW()) <= TOLERANCE;
    }
}
