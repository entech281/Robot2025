package frc.robot.subsystems.coraldetector;

import java.util.function.BooleanSupplier;

import org.littletonrobotics.junction.Logger;

import frc.entech.subsystems.SubsystemOutput;

public class InternalCoralDetectorOutput extends SubsystemOutput  implements BooleanSupplier {

  private boolean coralSensor = false;

  public void setCoralSensor(boolean coralSensor) {
    this.coralSensor = coralSensor;
  }

  public boolean hasCoral() {
    return coralSensor;
  }

  public boolean getAsBoolean() {
    return hasCoral();
  }

  @Override
  public void toLog() {
    Logger.recordOutput("InternalCoralDetectorOutput/CoralSensor", coralSensor);
    Logger.recordOutput("InternalCoralDetectorOutput/HasCoral", hasCoral());
  }

}
