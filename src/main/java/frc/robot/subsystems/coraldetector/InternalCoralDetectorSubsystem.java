package frc.robot.subsystems.coraldetector;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.Command;
import frc.entech.subsystems.EntechSubsystem;
import frc.robot.RobotConstants;
import frc.robot.commands.TestInternalCoralDetectorCommand;
import frc.robot.io.RobotIO;

public class InternalCoralDetectorSubsystem
    extends EntechSubsystem<InternalCoralDetectorInput, InternalCoralDetectorOutput> {

  private static final boolean ENABLED = true;

  private DigitalInput internalSensorF;
  private DigitalInput internalSensorR;

  @Override
  public void initialize() {
    if (ENABLED) {
      internalSensorF = new DigitalInput(RobotConstants.PORTS.HAS_CORAL.INTERNAL_SENSOR_FORWARD);
      internalSensorR = new DigitalInput(RobotConstants.PORTS.HAS_CORAL.INTERNAL_SENSOR_REAR);
    }
  }

  @Override
  public boolean isEnabled() {
    return ENABLED;
  }

  @Override
  public void updateInputs(InternalCoralDetectorInput input) {
    RobotIO.processInput(input);
  }

  @Override
  public Command getTestCommand() {
    return new TestInternalCoralDetectorCommand(this);
  }

  @Override
  public InternalCoralDetectorOutput toOutputs() {
    InternalCoralDetectorOutput output = new InternalCoralDetectorOutput();
    if (ENABLED) {
      output.setForwardCoralSensor(internalSensorF.get());
      output.setRearCorealSensor(internalSensorR.get());
    }
    return output;
  }

}
