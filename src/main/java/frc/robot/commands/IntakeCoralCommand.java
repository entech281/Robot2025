package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.robot.Position;
import frc.robot.subsystems.gamepiecehandler.GamePieceHandlerSubsystem;
import frc.robot.subsystems.pivot.PivotSubsystem;

public class IntakeCoralCommand extends EntechCommand {
    private final GamePieceHandlerSubsystem intake;
	private final PivotSubsystem pivot;

    public IntakeCoralCommand(GamePieceHandlerSubsystem coralHandler, PivotSubsystem pivot) {
        super(coralHandler, pivot);
        this.intake = coralHandler;
		this.pivot = pivot;
    }

	@Override
	public void end(boolean interrupted) {
		if (interrupted) {
			intake.stop();
		}
		if (intake.hasCoral()) {
			new PivotMoveCommand(pivot, Position.SAFE_EXTEND).schedule();
		}
	}

	@Override
	public void execute() {
	}

	@Override
	public void initialize() {
		intake.intakeCoral();
	}

	@Override
	public boolean isFinished() {
		return intake.intakeDone();
	}
}
