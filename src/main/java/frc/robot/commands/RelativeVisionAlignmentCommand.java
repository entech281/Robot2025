package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.robot.operation.UserPolicy;

public class RelativeVisionAlignmentCommand extends EntechCommand {
    @Override
    public void initialize() {
        UserPolicy.getInstance().setAligningToAngle(true);
        UserPolicy.getInstance().setLaterallyAligning(false);
        UserPolicy.getInstance().setTargetAngle(0);
        UserPolicy.getInstance().setVisionPositionSetPoint(0);
    }

    @Override
    public void execute() {
        // TODO: Implement this method
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
