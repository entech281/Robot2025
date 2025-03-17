package frc.robot.subsystems.algaedetector;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.Command;
import frc.entech.subsystems.EntechSubsystem;
import frc.robot.RobotConstants;
import frc.robot.io.RobotIO;

public class InternalAlgaeDetectorSubsystem extends EntechSubsystem<InternalAlgaeDetectorInput, InternalAlgaeDetectorOutput> {

  private static final boolean ENABLED = true;

  private DigitalInput internalSensor;

  @Override
  public void initialize() {
    if (ENABLED) {
      internalSensor = new DigitalInput(RobotConstants.PORTS.HAS_ALGAE.INTERNAL_ALGAE_SENSOR);
    }
  }

  @Override
  public boolean isEnabled() {
    return ENABLED;
  }

  @Override
  public void updateInputs(InternalAlgaeDetectorInput input) {
    RobotIO.processInput(input);
  }

  @Override
  public Command getTestCommand() {
    return new TestInternalAlgaeDetectorCommand(this);
  }

  @Override
  public InternalAlgaeDetectorOutput toOutputs() {
    InternalAlgaeDetectorOutput output = new InternalAlgaeDetectorOutput();
    if (ENABLED) {
      output.setAlgaeSensor(internalSensor.get());
    }
    return output;
  }

}

    

