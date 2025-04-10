package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.robot.subsystems.gamepiecehandler.GamePieceHandlerSubsystem;

public class AutoIntakeCoralCommand extends EntechCommand {
    private final GamePieceHandlerSubsystem intake;

    public AutoIntakeCoralCommand(GamePieceHandlerSubsystem coral) {
        super(coral);
        this.intake = coral;
    }

	@Override
	public void end(boolean interrupted) {
		if (interrupted) {
			intake.stop();
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
