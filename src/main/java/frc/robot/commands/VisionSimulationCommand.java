package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.robot.subsystems.vision_simulation.VisionSimulationSubsystem;

public class VisionSimulationCommand extends EntechCommand {
  private final VisionSimulationSubsystem visionSimulationSubsystem;

  public VisionSimulationCommand(VisionSimulationSubsystem visionSimulationSubsystem) {
    this.visionSimulationSubsystem = visionSimulationSubsystem;
    addRequirements(visionSimulationSubsystem);
  }

  @Override
  public void execute() {
    visionSimulationSubsystem.toOutputs().toLog();
  }

  @Override
  public boolean isFinished() {
    return false; // Run until interrupted
  }

  @Override
  public void end(boolean interrupted) {
    // Optionally handle cleanup when command ends
  }
}