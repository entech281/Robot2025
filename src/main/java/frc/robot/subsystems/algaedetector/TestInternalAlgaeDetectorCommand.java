package frc.robot.subsystems.algaedetector;

import org.littletonrobotics.junction.Logger;

import frc.entech.commands.EntechCommand;
import frc.robot.RobotConstants;

public class TestInternalAlgaeDetectorCommand extends EntechCommand {
  private final InternalAlgaeDetectorSubsystem detector;

  public TestInternalAlgaeDetectorCommand(
      InternalAlgaeDetectorSubsystem internalAlgaeDetectorSubsystem) {
    super(internalAlgaeDetectorSubsystem);
    detector = internalAlgaeDetectorSubsystem;
  }

  @Override
  public void execute() {
    Logger.recordOutput(RobotConstants.OperatorMessages.SUBSYSTEM_TEST, "Trigger the coral sensor.");
  }

  @Override
  public boolean isFinished() {
    return detector.getOutputs().hasAlgae();
  }

  @Override
  public void end(boolean interrupted) {
    Logger.recordOutput(RobotConstants.OperatorMessages.SUBSYSTEM_TEST, "No Current Test.");
  }

  //removed initialize but can be readded for purpose
  
}
