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
    Logger.recordOutput(RobotConstants.OperatorMessages.SUBSYSTEM_TEST, "Trigger the coral sensor.");
  }

  @Override
  public boolean isFinished() {
    return detector.getOutputs().hasCoral();
  }

  @Override
  public void end(boolean interrupted) {
    Logger.recordOutput(RobotConstants.OperatorMessages.SUBSYSTEM_TEST, "No Current Test.");
  }

  //removed initialize but can be readded for purpose
  
}
