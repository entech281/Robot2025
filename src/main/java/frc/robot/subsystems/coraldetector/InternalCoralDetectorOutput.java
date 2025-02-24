package frc.robot.subsystems.coraldetector;

import org.littletonrobotics.junction.Logger;

import frc.entech.subsystems.SubsystemOutput;

public class InternalCoralDetectorOutput extends SubsystemOutput {

  private boolean coralSensor = false;

  public boolean sensorHasCoral() {
    return coralSensor;
  }

  public void setCoralSensor(boolean coralSensor) {
    this.coralSensor = coralSensor;
  }

  public boolean hasCoral() {
    return coralSensor;
  }

  @Override
  public void toLog() {
    Logger.recordOutput("InternalCoralDetectorOutput/CoralSensor", coralSensor);
    Logger.recordOutput("InternalCoralDetectorOutput/HasCoral", hasCoral());
  }

}
