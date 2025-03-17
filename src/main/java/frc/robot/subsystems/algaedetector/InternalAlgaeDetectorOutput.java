package frc.robot.subsystems.algaedetector;

import org.littletonrobotics.junction.Logger;

import frc.entech.subsystems.SubsystemOutput;

public class InternalAlgaeDetectorOutput extends SubsystemOutput {

  private boolean algaeSensor = false;

  public void setAlgaeSensor(boolean algaeSensor) {
    this.algaeSensor = algaeSensor;
  }

  public boolean hasAlgae() {
    return algaeSensor;
  }

  @Override
  public void toLog() {
    Logger.recordOutput("InternalAlgaeDetectorOutput/CoralSensor", algaeSensor);
    Logger.recordOutput("InternalAlgaeDetectorOutput/HasCoral", hasAlgae());
  }

}
