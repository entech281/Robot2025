package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.entech.util.StoppingCounter;
import frc.robot.Position;
import frc.robot.livetuning.LiveTuningHandler;
import frc.robot.subsystems.gamepiecehandler.GamePieceHandlerInput;
import frc.robot.subsystems.gamepiecehandler.GamePieceHandlerSubsystem;
import frc.robot.subsystems.pivot.PivotSubsystem;

public class IntakeCoralCommand extends EntechCommand {
    private final GamePieceHandlerInput gphInput = new GamePieceHandlerInput();
    private final GamePieceHandlerSubsystem intake;
	private final PivotSubsystem pivot;
    private final StoppingCounter counter = new StoppingCounter(0.0);

    public IntakeCoralCommand(GamePieceHandlerSubsystem coralHandler, PivotSubsystem pivot) {
        super(coralHandler);
        this.intake = coralHandler;
		this.pivot = pivot;
    }

	@Override
	public void end(boolean interrupted) {
		gphInput.setRequestedSpeed(0.0);
		intake.updateInputs(gphInput);
		if (intake.hasCoral()) {
			new PivotMoveCommand(pivot, Position.SAFE_EXTEND).schedule();
		}
	}

	@Override
	public void execute() {
		if (intake.hasCoral()) {
			gphInput.setRequestedSpeed(LiveTuningHandler.getInstance().getValue("CoralMechanismSubsystem/SlowDownSpeed"));
			intake.updateInputs(gphInput);
		}
	}

	@Override
	public void initialize() {
		gphInput.setRequestedSpeed(LiveTuningHandler.getInstance().getValue("CoralMechanismSubsystem/StartSpeed"));
		intake.updateInputs(gphInput);
		counter.reset();
	}

	@Override
	public boolean isFinished() {
		return counter.isFinished(intake.hasCoral());
	}
}
