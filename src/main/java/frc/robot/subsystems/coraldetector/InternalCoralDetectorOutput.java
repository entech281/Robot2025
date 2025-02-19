package frc.robot.subsystems.coraldetector;

import org.littletonrobotics.junction.Logger;
import frc.entech.subsystems.SubsystemOutput;

public class InternalCoralDetectorOutput extends SubsystemOutput {

  private boolean forwardCoralSensor = false;
  private boolean rearCoralSensor = false;

  public boolean forwardSensorHasCoral() {
    return forwardCoralSensor;
  }

  public void setForwardCoralSensor(boolean forwardCoralSensor) {
    this.forwardCoralSensor = forwardCoralSensor;
  }

  public boolean rearSensorHasCoral() {
    return rearCoralSensor;
  }

  public void setRearCorealSensor(boolean rearCoralSensor) {
    this.rearCoralSensor = rearCoralSensor;
  }

  public boolean hasCoral() {
    //return forwardCoralSensor || rearCoralSensor;
    return rearCoralSensor && forwardCoralSensor;
  }

  @Override
  public void toLog() {
    Logger.recordOutput("InternalCoralDetectorOutput/ForwardInternalSensor", forwardCoralSensor);
    Logger.recordOutput("InternalCoralDetectorOutput/RearInternalSensor", rearCoralSensor);
    Logger.recordOutput("InternalCoralDetectorOutput/HasCoral", hasCoral());
  }

}
