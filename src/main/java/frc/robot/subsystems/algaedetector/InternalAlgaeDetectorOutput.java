package frc.robot.subsystems.algaedetector;

import java.util.function.BooleanSupplier;

import org.littletonrobotics.junction.Logger;

import frc.entech.subsystems.SubsystemOutput;

public class InternalAlgaeDetectorOutput extends SubsystemOutput implements BooleanSupplier {

  private boolean algaeSensor = false;

  public void setAlgaeSensor(boolean algaeSensor) {
    this.algaeSensor = algaeSensor;
  }

  public boolean hasAlgae() {
    return algaeSensor;
  }

  public boolean getAsBoolean() {
    return hasAlgae();
  }

  @Override
  public void toLog() {
    Logger.recordOutput("InternalAlgaeDetectorOutput/AlgaeSensor", algaeSensor);
    Logger.recordOutput("InternalAlgaeDetectorOutput/HasAlgae", hasAlgae());
  }

}
