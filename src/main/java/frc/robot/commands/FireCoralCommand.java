package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.robot.subsystems.coralmechanism.CoralMechanismInput;
import frc.robot.subsystems.coralmechanism.CoralMechanismSubsystem;

public class FireCoralCommand extends EntechCommand {
    private final CoralMechanismInput input = new CoralMechanismInput();
    private final CoralMechanismSubsystem intake;
    private final double speed;

    public FireCoralCommand(CoralMechanismSubsystem coral, double speed) {
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
}
