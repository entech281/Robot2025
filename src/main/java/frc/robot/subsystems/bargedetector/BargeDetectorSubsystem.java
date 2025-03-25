package frc.robot.subsystems.bargedetector;

import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj2.command.Command;
import frc.entech.subsystems.EntechSubsystem;
import frc.robot.io.RobotIO;
public class BargeDetectorSubsystem extends EntechSubsystem<BargeDetectorInput, BargeDetectorOutput> {

  private static final boolean ENABLED = true;

  private I2C.Port sensorPort;
  private ColorSensorV3 sensor;

  @Override
  public void initialize() {
    if (ENABLED) {
      // Use the REV Color Sensor V3
      sensorPort = I2C.Port.kOnboard;
      sensor = new ColorSensorV3(sensorPort);
    }
  }

  @Override
  public boolean isEnabled() {
    return ENABLED;
  }

  @Override
  public void updateInputs(BargeDetectorInput input) {
    RobotIO.processInput(input);
  }

  @Override
  public BargeDetectorOutput toOutputs() {
    BargeDetectorOutput output = new BargeDetectorOutput();
    if (ENABLED) {
      output.setColor(sensor.getColor());
    }
    return output;
  }

  @Override
  public Command getTestCommand() {
    throw new UnsupportedOperationException("Unimplemented method 'getTestCommand'");
  }

}

    

