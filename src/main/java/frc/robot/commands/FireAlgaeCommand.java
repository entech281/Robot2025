package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.entech.util.StoppingCounter;
import frc.robot.livetuning.LiveTuningHandler;
import frc.robot.subsystems.coralmechanism.CoralMechanismInput;
import frc.robot.subsystems.coralmechanism.CoralMechanismSubsystem;

public class FireAlgaeCommand extends EntechCommand {
    private final CoralMechanismInput input = new CoralMechanismInput();
    private final CoralMechanismSubsystem intake;
    private final StoppingCounter counter = new StoppingCounter(0.15);

    public FireAlgaeCommand(CoralMechanismSubsystem algae) {
        super(algae);
        this.intake = algae;
        input.setBrakeMode(false);
    }

	@Override
	public void end(boolean interrupted) {
		input.setRequestedSpeed(0.0);
		intake.updateInputs(input);
	}

	@Override
	public void initialize() {
		input.setRequestedSpeed(LiveTuningHandler.getInstance().getValue("CoralMechanismSubsystem/AlgaeFireSpeed"));
		intake.updateInputs(input);
		counter.reset();
	}

	@Override
	public boolean isFinished() {
		return counter.isFinished(true);
	}
}
