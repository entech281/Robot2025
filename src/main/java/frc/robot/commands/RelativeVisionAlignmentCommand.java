package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.robot.RobotConstants;
import frc.robot.io.RobotIO;
import frc.robot.operation.UserPolicy;

public class RelativeVisionAlignmentCommand extends EntechCommand {
    @Override
    public void initialize() {
        UserPolicy.getInstance().setAligningToAngle(true);
        UserPolicy.getInstance().setLaterallyAligning(true);
        UserPolicy.getInstance().setTargetAngle(findTargetAngle(RobotIO.getInstance().getVisionOutput().getTagID()));
        UserPolicy.getInstance().setVisionPositionSetPoint(0);
    }

    @Override
    public void execute() {
        UserPolicy.getInstance().setAligningToAngle(true);
        UserPolicy.getInstance().setLaterallyAligning(true);
        UserPolicy.getInstance().setTargetAngle(RobotIO.getInstance().getVisionOutput().getTagID());
    }

    private double findTargetAngle(int tagID) {
        if (RobotConstants.APRIL_TAG_DATA.TAG_ANGLES.containsKey(tagID)) {
            return RobotConstants.APRIL_TAG_DATA.TAG_ANGLES.get(tagID);
        } else {
            UserPolicy.getInstance().setAligningToAngle(false);
            UserPolicy.getInstance().setLaterallyAligning(false);
            return 0.0;
        }
    }

    @Override
    public void end(boolean interrupted) {
        UserPolicy.getInstance().setAligningToAngle(false);
        UserPolicy.getInstance().setLaterallyAligning(false);
    }

    @Override
    public boolean runsWhenDisabled() {
        return true;
    }
}
