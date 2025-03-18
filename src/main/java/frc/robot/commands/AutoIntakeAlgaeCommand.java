package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.entech.util.StoppingCounter;
import frc.robot.livetuning.LiveTuningHandler;
import frc.robot.subsystems.coralmechanism.CoralMechanismInput;
import frc.robot.subsystems.coralmechanism.CoralMechanismSubsystem;

public class AutoIntakeAlgaeCommand extends EntechCommand {
    private final CoralMechanismInput input = new CoralMechanismInput();
    private final CoralMechanismSubsystem intake;
	private final StoppingCounter counter = new StoppingCounter(3);

    public AutoIntakeAlgaeCommand(CoralMechanismSubsystem algae) {
        super(algae);
        this.intake = algae;
    }

	@Override
	public void end(boolean interrupted) {
		input.setRequestedSpeed(0.0);
		intake.updateInputs(input);
	}

	@Override
	public void execute() {
    //Nothing to execute
	}

	@Override
	public void initialize() {
		input.setRequestedSpeed(-LiveTuningHandler.getInstance().getValue("CoralMechanismSubsystem/AlgaeIntakeSpeed"));
		intake.updateInputs(input);
	}

	@Override
	public boolean isFinished() {
    	return counter.isFinished(true);
	}
}
