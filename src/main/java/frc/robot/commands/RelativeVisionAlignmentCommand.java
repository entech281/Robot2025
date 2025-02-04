package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.robot.RobotConstants;
import frc.robot.io.RobotIO;
import frc.robot.operation.UserPolicy;

public class RelativeVisionAlignmentCommand extends EntechCommand {
    private static final double LATERAL_START_ANGLE = 22.5;
    @Override
    public void initialize() {
        UserPolicy.getInstance().setAligningToAngle(true);
        UserPolicy.getInstance().setLaterallyAligning(false);
        UserPolicy.getInstance().setTargetAngle(findTargetAngle(RobotIO.getInstance().getVisionOutput().getTagID()));
        UserPolicy.getInstance().setVisionPositionSetPoint(0);
    }

    @Override
    public void execute() {
        UserPolicy.getInstance().setAligningToAngle(true);
        UserPolicy.getInstance().setTargetAngle(findTargetAngle(RobotIO.getInstance().getVisionOutput().getTagID()));

        UserPolicy.getInstance().setLaterallyAligning(Math.abs(RobotIO.getInstance().getOdometryPose().getRotation().getDegrees()) - (findTargetAngle(RobotIO.getInstance().getVisionOutput().getTagID() - 180)) < LATERAL_START_ANGLE);
    }

    private double findTargetAngle(int tagID) {
        if (RobotConstants.APRIL_TAG_DATA.TAG_ANGLES.containsKey(tagID)) {
            return RobotConstants.APRIL_TAG_DATA.TAG_ANGLES.get(tagID);
        } else {
            return UserPolicy.getInstance().getTargetAngle();
        }
    }

    @Override
    public void end(boolean interrupted) {
        UserPolicy.getInstance().setAligningToAngle(false);
        UserPolicy.getInstance().setLaterallyAligning(false);
    }

    @Override
    public boolean runsWhenDisabled() {
        return false;
    }
}
