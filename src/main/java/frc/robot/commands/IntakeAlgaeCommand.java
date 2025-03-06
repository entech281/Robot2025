package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.robot.subsystems.coralmechanism.CoralMechanismInput;
import frc.robot.subsystems.coralmechanism.CoralMechanismSubsystem;

public class IntakeAlgaeCommand extends EntechCommand {
    private final CoralMechanismInput input = new CoralMechanismInput();
    private final CoralMechanismSubsystem intake;

    public IntakeAlgaeCommand(CoralMechanismSubsystem algae) {
        super(algae);
        this.intake = algae;
    }

	@Override
	public void end(boolean interrupted) {
		input.setRequestedSpeed(-0.05);
		intake.updateInputs(input);
	}

	@Override
	public void execute() {
    //Nothing to execute
	}

	@Override
	public void initialize() {
		input.setRequestedSpeed(-0.2);
		intake.updateInputs(input);
	}

	@Override
	public boolean isFinished() {
    return intake.toOutputs().getHasAlgae();
	}
}
