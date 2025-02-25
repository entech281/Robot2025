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
  public boolean isFinished() {
    return detector.getOutputs().sensorHasCoral();
  }

  @Override
  public void end(boolean interrupted) {
    Logger.recordOutput(RobotConstants.OperatorMessages.SUBSYSTEM_TEST, "No Current Test.");
  }

  //removed initialize but can be readded for purpose
  
}
