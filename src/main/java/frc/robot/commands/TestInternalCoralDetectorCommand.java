package frc.robot.commands;

import org.littletonrobotics.junction.Logger;
import frc.entech.commands.EntechCommand;
import frc.robot.RobotConstants;
import frc.robot.subsystems.coraldetector.InternalCoralDetectorSubsystem;

public class TestInternalCoralDetectorCommand extends EntechCommand {
  private final InternalCoralDetectorSubsystem detector;

  private int stage = 0;

  public TestInternalCoralDetectorCommand(
      InternalCoralDetectorSubsystem internalCoralDetectorSubsystem) {
    super(internalCoralDetectorSubsystem);
    detector = internalCoralDetectorSubsystem;
  }

  @Override
  public void execute() {
    switch (stage) {
      case 0:
        if (detector.getOutputs().forwardSensorHasCoral()) {
          stage++;
        } else {
          Logger.recordOutput(RobotConstants.OperatorMessages.SUBSYSTEM_TEST,
              "Trigger the forward sensor.");
        }
        break;
      case 1:
        if (detector.getOutputs().rearSensorHasCoral()) {
          stage++;
        } else {
          Logger.recordOutput(RobotConstants.OperatorMessages.SUBSYSTEM_TEST,
              "Trigger the rear sensor.");
        }
        break;
      default:
        break;
    }
  }

  @Override
  public void initialize() {
    stage = 0;
  }

  @Override
  public boolean isFinished() {
    return stage >= 2;
  }

  @Override
  public void end(boolean interrupted) {
    Logger.recordOutput(RobotConstants.OperatorMessages.SUBSYSTEM_TEST, "No Current Test.");
  }
}
