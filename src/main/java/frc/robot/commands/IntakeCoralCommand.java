package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.entech.util.StoppingCounter;
import frc.robot.io.RobotIO;
import frc.robot.livetuning.LiveTuningHandler;
import frc.robot.subsystems.coralmechanism.CoralMechanismInput;
import frc.robot.subsystems.coralmechanism.CoralMechanismSubsystem;

public class IntakeCoralCommand extends EntechCommand {
    private final CoralMechanismInput input = new CoralMechanismInput();
    private final CoralMechanismSubsystem intake;
    private final StoppingCounter counter = new StoppingCounter(0.02);

    public IntakeCoralCommand(CoralMechanismSubsystem coral) {
        super(coral);
        this.intake = coral;
    }

	@Override
	public void end(boolean interrupted) {
		input.setRequestedSpeed(0.0);
		intake.updateInputs(input);
	}

	@Override
	public void execute() {
		if (RobotIO.getInstance().getInternalCoralDetectorOutput().hasCoral()) {
			input.setRequestedSpeed(LiveTuningHandler.getInstance().getValue("CoralMechanismSubsystem/SlowDownSpeed"));
			intake.updateInputs(input);
		}
	}

	@Override
	public void initialize() {
		input.setRequestedSpeed(LiveTuningHandler.getInstance().getValue("CoralMechanismSubsystem/StartSpeed"));
		intake.updateInputs(input);
		counter.reset();
	}

	@Override
	public boolean isFinished() {
		return counter.isFinished(RobotIO.getInstance().getInternalCoralDetectorOutput().hasCoral());
	}
}
