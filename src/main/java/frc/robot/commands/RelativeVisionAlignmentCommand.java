package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.robot.operation.UserPolicy;

public class RelativeVisionAlignmentCommand extends EntechCommand {
    @Override
    public void initialize() {
        UserPolicy.getInstance().setAligningToAngle(true);
        UserPolicy.getInstance().setLaterallyAligning(true);
        UserPolicy.getInstance().setTargetAngle(-60);
        UserPolicy.getInstance().setVisionPositionSetPoint(0);
    }

    @Override
    public void execute() {
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
