package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.vision_simulation.VisionSimulationSubsystem;

public class VisionSimulationCommand extends Command {
  private final VisionSimulationSubsystem visionSimulationSubsystem;

  public VisionSimulationCommand(VisionSimulationSubsystem visionSimulationSubsystem) {
    this.visionSimulationSubsystem = visionSimulationSubsystem;
    addRequirements(visionSimulationSubsystem);
  }

  @Override
  public void execute() {
    visionSimulationSubsystem.updateVisionSimulation();
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