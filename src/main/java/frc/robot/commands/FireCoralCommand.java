package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.robot.subsystems.gamepiecehandler.GamePieceHandlerSubsystem;

public class FireCoralCommand extends EntechCommand {
    private final GamePieceHandlerSubsystem intake;
    private final double speed;

    public FireCoralCommand(GamePieceHandlerSubsystem coral, double speed) {
        super(coral);
        this.intake = coral;
        this.speed = speed;
    }

	@Override
	public void initialize() {
		if (Math.abs(speed) > 0.5) {
			intake.shootCoralFast();
		} else {
			intake.shootCoralSlow();
		}
	}

	@Override
	public void execute() {
	}

	@Override
	public void end(boolean interrupted) {
		if (interrupted) {
			intake.stop();
		}
	}

	@Override
	public boolean isFinished() {
		return intake.shotDone();
	}
}
