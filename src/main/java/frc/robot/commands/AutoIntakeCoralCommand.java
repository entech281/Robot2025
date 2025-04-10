package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.entech.util.StoppingCounter;
import frc.robot.io.RobotIO;
import frc.robot.livetuning.LiveTuningHandler;
import frc.robot.subsystems.gamepiecehandler.GamePieceHandlerInput;
import frc.robot.subsystems.gamepiecehandler.GamePieceHandlerSubsystem;

public class AutoIntakeCoralCommand extends EntechCommand {
    private final GamePieceHandlerInput corInput = new GamePieceHandlerInput();
    private final GamePieceHandlerSubsystem intake;
    private final StoppingCounter counter = new StoppingCounter(0.0);

    public AutoIntakeCoralCommand(GamePieceHandlerSubsystem coral) {
        super(coral);
        this.intake = coral;
    }

	@Override
	public void end(boolean interrupted) {
		corInput.setRequestedSpeed(0.0);
		intake.updateInputs(corInput);
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
