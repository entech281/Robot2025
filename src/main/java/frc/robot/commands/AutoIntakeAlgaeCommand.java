package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.entech.util.StoppingCounter;
import frc.robot.livetuning.LiveTuningHandler;
import frc.robot.operation.UserPolicy;
import frc.robot.subsystems.gamepiecehandler.GamePieceHandlerInput;
import frc.robot.subsystems.gamepiecehandler.GamePieceHandlerSubsystem;

public class AutoIntakeAlgaeCommand extends EntechCommand {
    private final GamePieceHandlerInput input = new GamePieceHandlerInput();
    private final GamePieceHandlerSubsystem intake;
	private final StoppingCounter counter = new StoppingCounter(3);

    public AutoIntakeAlgaeCommand(GamePieceHandlerSubsystem algae) {
        super(algae);
        this.intake = algae;
    }

	@Override
	public void end(boolean interrupted) {
		input.setRequestedSpeed(0.0);
		intake.updateInputs(input);
		UserPolicy.getInstance().setAlgaeMode(true);
	}

	@Override
	public void execute() {
    //Nothing to execute
	}

	@Override
	public void initialize() {
		input.setRequestedSpeed(-LiveTuningHandler.getInstance().getValue("CoralMechanismSubsystem/AlgaeIntakeSpeed"));
		intake.updateInputs(input);
	}

	@Override
	public boolean isFinished() {
    	return counter.isFinished(true);
	}
}
