package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.robot.RobotConstants;
import frc.robot.io.RobotIO;
import frc.robot.operation.UserPolicy;

public class RotateToFaceTargetCommand extends EntechCommand {

    @Override
    public void end(boolean interrupted) {
        UserPolicy.getInstance().setAligningToAngle(false);
    }

    @Override
    public void execute() {
        if (RobotIO.getInstance().getVisionOutput().hasTarget() && RobotIO.getInstance().getVisionOutput().getTargets().get(0).getTagXW() >= -99) {
            double change = RobotConstants.DrivetrainConstants.ROTATE_TO_ANGLE_KP * RobotIO.getInstance().getVisionOutput().getTargets().get(0).getTagXW();
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
        return RobotIO.getInstance().getVisionOutput().hasTarget() && Math.abs(RobotIO.getInstance().getVisionOutput().getTargets().get(0).getTagXW()) <= RobotConstants.DrivetrainConstants.ROTATE_TO_ANGLE_TOLERANCE;
    }
}
