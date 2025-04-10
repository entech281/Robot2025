package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.robot.subsystems.gamepiecehandler.GamePieceHandlerSubsystem;

public class FireAlgaeCommand extends EntechCommand {
    private final GamePieceHandlerSubsystem intake;

    public FireAlgaeCommand(GamePieceHandlerSubsystem algae) {
        super(algae);
        this.intake = algae;
    }

	@Override
	public void end(boolean interrupted) {
		if (interrupted) {
			intake.stop();
		}
	}

	@Override
	public void initialize() {
		intake.shootAlgae();
	}

	@Override
	public boolean isFinished() {
		return intake.shotDone();
	}
}
