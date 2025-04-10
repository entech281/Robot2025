package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.robot.livetuning.LiveTuningHandler;
import frc.robot.operation.UserPolicy;
import frc.robot.subsystems.gamepiecehandler.GamePieceHandlerInput;
import frc.robot.subsystems.gamepiecehandler.GamePieceHandlerSubsystem;

public class AlgaeHoldCommand extends EntechCommand {
    private final GamePieceHandlerSubsystem coral;
    private final GamePieceHandlerInput input = new GamePieceHandlerInput();

    public AlgaeHoldCommand(GamePieceHandlerSubsystem coral) {
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
