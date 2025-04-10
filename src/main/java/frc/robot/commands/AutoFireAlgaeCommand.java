package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.entech.util.StoppingCounter;
import frc.robot.subsystems.gamepiecehandler.GamePieceHandlerInput;
import frc.robot.subsystems.gamepiecehandler.GamePieceHandlerSubsystem;

public class AutoFireAlgaeCommand extends EntechCommand {
    private final GamePieceHandlerInput input = new GamePieceHandlerInput();
    private final GamePieceHandlerSubsystem intake;
    private final double speed;
    private final StoppingCounter counter = new StoppingCounter(1);

    public AutoFireAlgaeCommand(GamePieceHandlerSubsystem coral, double speed) {
        super(coral);
        this.intake = coral;
        this.speed = speed;
        input.setBrakeMode(false);
    }

	@Override
	public void end(boolean interrupted) {
		input.setRequestedSpeed(0.0);
		intake.updateInputs(input);
	}

	@Override
	public void initialize() {
		input.setRequestedSpeed(speed);
		intake.updateInputs(input);
	}

    @Override
    public boolean isFinished() {
        return counter.isFinished(true);
    }
}
