package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.entech.util.StoppingCounter;
import frc.robot.io.RobotIO;
import frc.robot.subsystems.gamepiecehandler.GamePieceHandlerInput;
import frc.robot.subsystems.gamepiecehandler.GamePieceHandlerSubsystem;

public class FireCoralCommandAuto extends EntechCommand {
    private final GamePieceHandlerInput input = new GamePieceHandlerInput();
    private final GamePieceHandlerSubsystem intake;
    private final double speed;
	//TODO: Magic number
	private StoppingCounter counter = new StoppingCounter(0.02);

    public FireCoralCommandAuto(GamePieceHandlerSubsystem coral, double speed) {
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
		counter.reset();
	}

	@Override
	public boolean isFinished() {
		return counter.isFinished(!RobotIO.getInstance().getInternalCoralDetectorOutput().hasCoral());
	}
}
