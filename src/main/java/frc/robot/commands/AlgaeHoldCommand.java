package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.robot.livetuning.LiveTuningHandler;
import frc.robot.operation.UserPolicy;
import frc.robot.subsystems.coralmechanism.CoralMechanismInput;
import frc.robot.subsystems.coralmechanism.CoralMechanismSubsystem;

public class AlgaeHoldCommand extends EntechCommand {
    private final CoralMechanismSubsystem coral;
    private final CoralMechanismInput input = new CoralMechanismInput();

    public AlgaeHoldCommand(CoralMechanismSubsystem coral) {
        super(coral);
        this.coral = coral;
    }

    @Override
    public void end(boolean interrupted) {
        input.setRequestedSpeed(0);
        coral.updateInputs(input);
    }

    @Override
    public void initialize() {
        if (UserPolicy.getInstance().isAlgaeMode()) {
            input.setActivate(true);
            input.setActivate(true);
            input.setRequestedSpeed(-LiveTuningHandler.getInstance().getValue("CoralMechanismSubsystem/AlgaeHoldSpeed"));
            coral.updateInputs(input);
        }
    }

    @Override
    public boolean isFinished() {
        return !UserPolicy.getInstance().isAlgaeMode();
    }
}
