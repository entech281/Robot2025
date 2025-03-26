package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.entech.util.StoppingCounter;
import frc.robot.io.RobotIO;
import frc.robot.subsystems.coralmechanism.CoralMechanismInput;
import frc.robot.subsystems.coralmechanism.CoralMechanismSubsystem;

public class FireCoralCommandAuto extends EntechCommand {
    private final CoralMechanismInput input = new CoralMechanismInput();
    private final CoralMechanismSubsystem intake;
    private final double speed;
	//TODO: Magic number
	private StoppingCounter counter = new StoppingCounter(0.02);

    public FireCoralCommandAuto(CoralMechanismSubsystem coral, double speed) {
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
