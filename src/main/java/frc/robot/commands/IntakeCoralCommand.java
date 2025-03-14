package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.entech.util.StoppingCounter;
import frc.robot.Position;
import frc.robot.io.RobotIO;
import frc.robot.livetuning.LiveTuningHandler;
import frc.robot.subsystems.coralmechanism.CoralMechanismInput;
import frc.robot.subsystems.coralmechanism.CoralMechanismSubsystem;
import frc.robot.subsystems.pivot.PivotSubsystem;

public class IntakeCoralCommand extends EntechCommand {
    private final CoralMechanismInput corInput = new CoralMechanismInput();
    private final CoralMechanismSubsystem intake;
	private final PivotSubsystem pivot;
    private final StoppingCounter counter = new StoppingCounter(0.0);

    public IntakeCoralCommand(CoralMechanismSubsystem coral, PivotSubsystem pivot) {
        super(coral);
        this.intake = coral;
		this.pivot = pivot;
    }

	@Override
	public void end(boolean interrupted) {
		corInput.setRequestedSpeed(0.0);
		intake.updateInputs(corInput);
		new PivotMoveCommand(pivot, Position.SAFE_EXTEND).schedule();
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
