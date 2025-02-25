package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.entech.util.StoppingCounter;
import frc.robot.io.RobotIO;
import frc.robot.subsystems.coral.CoralMechanismInput;
import frc.robot.subsystems.coral.CoralMechanismSubsystem;

public class IntakeCoralCommand extends EntechCommand {
    private final CoralMechanismInput input = new CoralMechanismInput();
    private final CoralMechanismSubsystem intake;
    private final StoppingCounter counter = new StoppingCounter(0.15);

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
		if (RobotIO.getInstance().getInternalCoralDetectorOutput().forwardSensorHasCoral()) {
			input.setRequestedSpeed(0.1);
			intake.updateInputs(input);
		}
	}

	@Override
	public void initialize() {
		input.setRequestedSpeed(0.2);
		intake.updateInputs(input);
		counter.reset();
	}

	@Override
	public boolean isFinished() {
		return counter.isFinished(RobotIO.getInstance().getInternalCoralDetectorOutput().forwardSensorHasCoral());
	}
}
