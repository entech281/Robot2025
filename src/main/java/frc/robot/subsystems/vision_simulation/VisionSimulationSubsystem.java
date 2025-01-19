package frc.robot.subsystems.vision_simulation;

import org.ejml.simple.UnsupportedOperation;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.entech.subsystems.EntechSubsystem;
import frc.robot.subsystems.vision.VisionOutput;
import frc.robot.subsystems.vision.VisionSubsystem;

public class VisionSimulationSubsystem extends EntechSubsystem<VisionSimulationInput, VisionSimulationOutput> {
  private static final boolean ENABLED = true;

  private final VisionSubsystem visionSubsystem;
  private final Joystick joystick;
  private boolean isEnabled = false;

  public VisionSimulationSubsystem(VisionSubsystem visionSubsystem, Joystick joystick) {
    this.visionSubsystem = visionSubsystem;
    this.joystick = joystick;
  }

  public void enable() {
    isEnabled = true;
  }

  public void disable() {
    isEnabled = false;
  }

  public boolean isEnabled() {
    return isEnabled;
  }

  public void updateVisionSimulation() {
    if (ENABLED && isEnabled) {
      VisionOutput visionOutput = visionSubsystem.toOutputs();

      boolean hasTarget = visionOutput.getHasTarget();
      double tagX = joystick.getX(); // Simulate tagX with joystick X-axis
      double tagY = joystick.getY(); // Simulate tagY with joystick Y-axis
      int tagID = visionOutput.getTagID(); // Get tag ID from VisionSubsystem

      // Check if the AprilTag is in frame based on joystick input
      boolean inFrame = hasTarget && tagX >= -1 && tagX <= 1 && tagY >= -1 && tagY <= 1;

      // Log the results
      VisionSimulationOutput output = new VisionSimulationOutput();
      output.setInFrame(inFrame);
      output.setTagID(tagID); // Set the tag ID
      output.setTimestamp(System.currentTimeMillis());
      output.toLog();
    }
  }

  @Override
  public VisionSimulationOutput toOutputs() {
    return new VisionSimulationOutput();
  }

  @Override
  public void initialize() {
    if (ENABLED) {
      // Initialization logic if needed
    }
  }

  @Override
  public void updateInputs(VisionSimulationInput input) {
    throw new UnsupportedOperation();
  }

  @Override
  public Command getTestCommand() {
    return Commands.none();
  }
}