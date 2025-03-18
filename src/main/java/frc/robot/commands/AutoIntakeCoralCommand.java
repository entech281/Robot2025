package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.entech.util.StoppingCounter;
import frc.robot.io.RobotIO;
import frc.robot.livetuning.LiveTuningHandler;
import frc.robot.subsystems.coralmechanism.CoralMechanismInput;
import frc.robot.subsystems.coralmechanism.CoralMechanismSubsystem;

public class AutoIntakeCoralCommand extends EntechCommand {
    private final CoralMechanismInput corInput = new CoralMechanismInput();
    private final CoralMechanismSubsystem intake;
    private final StoppingCounter counter = new StoppingCounter(0.0);

    public AutoIntakeCoralCommand(CoralMechanismSubsystem coral) {
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
