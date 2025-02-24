package frc.robot.subsystems.coraldetector;

import org.littletonrobotics.junction.Logger;

import frc.entech.commands.EntechCommand;
import frc.robot.RobotConstants;

public class TestInternalCoralDetectorCommand extends EntechCommand {
  private final InternalCoralDetectorSubsystem detector;

  public TestInternalCoralDetectorCommand(
      InternalCoralDetectorSubsystem internalCoralDetectorSubsystem) {
    super(internalCoralDetectorSubsystem);
    detector = internalCoralDetectorSubsystem;
  }

  @Override
  public void execute() {
        if (detector.getOutputs().sensorHasCoral()) {
        } else {
          Logger.recordOutput(RobotConstants.OperatorMessages.SUBSYSTEM_TEST,
              "Trigger the forward sensor.");
        }
  }

  @Override
  public void initialize() {
    //required comment so that sonar will be fine
  }

  @Override
  public boolean isFinished() {
    return detector.getOutputs().sensorHasCoral();
  }

  @Override
  public void end(boolean interrupted) {
    Logger.recordOutput(RobotConstants.OperatorMessages.SUBSYSTEM_TEST, "No Current Test.");
  }
}
