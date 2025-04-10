package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.entech.util.StoppingCounter;
import frc.robot.Position;
import frc.robot.io.RobotIO;
import frc.robot.livetuning.LiveTuningHandler;
import frc.robot.subsystems.gamepiecehandler.GamePieceHandlerInput;
import frc.robot.subsystems.gamepiecehandler.GamePieceHandlerSubsystem;
import frc.robot.subsystems.pivot.PivotSubsystem;

public class IntakeCoralCommand extends EntechCommand {
    private final GamePieceHandlerInput corInput = new GamePieceHandlerInput();
    private final GamePieceHandlerSubsystem intake;
	private final PivotSubsystem pivot;
    private final StoppingCounter counter = new StoppingCounter(0.0);

    public IntakeCoralCommand(GamePieceHandlerSubsystem coral, PivotSubsystem pivot) {
        super(coral);
        this.intake = coral;
		this.pivot = pivot;
    }

	@Override
	public void end(boolean interrupted) {
		corInput.setRequestedSpeed(0.0);
		intake.updateInputs(corInput);
		if (RobotIO.getInstance().getInternalCoralDetectorOutput().hasCoral()) {
			new PivotMoveCommand(pivot, Position.SAFE_EXTEND).schedule();
		}
	}

	@Override
	public void execute() {
		if (RobotIO.getInstance().getInternalCoralDetectorOutput().hasCoral()) {
			corInput.setRequestedSpeed(LiveTuningHandler.getInstance().getValue("CoralMechanismSubsystem/SlowDownSpeed"));
			intake.updateInputs(corInput);
		}
	}

	@Override
	public void initialize() {
		corInput.setRequestedSpeed(LiveTuningHandler.getInstance().getValue("CoralMechanismSubsystem/StartSpeed"));
		intake.updateInputs(corInput);
		counter.reset();
	}

	@Override
	public boolean isFinished() {
		return counter.isFinished(RobotIO.getInstance().getInternalCoralDetectorOutput().hasCoral());
	}
}
