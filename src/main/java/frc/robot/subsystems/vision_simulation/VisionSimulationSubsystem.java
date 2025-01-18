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

  public VisionSimulationSubsystem(VisionSubsystem visionSubsystem, Joystick joystick) {
    this.visionSubsystem = visionSubsystem;
    this.joystick = joystick;
  }

  @Override
  public VisionSimulationOutput toOutputs() {
    VisionSimulationOutput output = new VisionSimulationOutput();

    if (ENABLED) {
      VisionOutput visionOutput = visionSubsystem.toOutputs();

      boolean hasTarget = visionOutput.getHasTarget();
      double tagX = joystick.getX(); // Simulate tagX with joystick X-axis
      double tagY = joystick.getY(); // Simulate tagY with joystick Y-axis

      // Check if the AprilTag is in frame based on joystick input
      boolean inFrame = hasTarget && tagX >= -1 && tagX <= 1 && tagY >= -1 && tagY <= 1;

      // Log the results
      output.setInFrame(inFrame);
      output.setTimestamp(System.currentTimeMillis());
    }

    return output;
  }

  @Override
  public void initialize() {
    if (ENABLED) {
      // Initialization logic if needed
    }
  }

  @Override
  public boolean isEnabled() {
        return ENABLED;
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