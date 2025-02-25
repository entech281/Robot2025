package frc.robot.subsystems.coralmechanism;

import org.littletonrobotics.junction.LogTable;

import frc.entech.subsystems.SubsystemInput;

public class CoralMechanismInput implements SubsystemInput {
  private boolean activate = true;
  private double requestedSpeed = 0.0;
  private boolean brakeMode = true;

  @Override
  public void toLog(LogTable table) {
    table.put("Activate", activate);
    table.put("Requested Speed", requestedSpeed);
    table.put("Brake Mode", brakeMode);
  }

  @Override
  public void fromLog(LogTable table) {
    activate = table.get("Activate", activate);
    requestedSpeed = table.get("Requested Speed", 0.0);
    brakeMode = table.get("Brake Mode", true);
  }

  public boolean getActivate() {
    return this.activate;
  }

  public void setActivate(boolean activate) {
    this.activate = activate;
  }

  public double getRequestedSpeed() {
    return this.requestedSpeed;
  }

  public void setRequestedSpeed(double requestedSpeed) {
    this.requestedSpeed = requestedSpeed;
  }

  public boolean getBrakeMode() {
    return this.brakeMode;
  }

  public void setBrakeMode(boolean brakeMode) {
    this.brakeMode = brakeMode;
  }
}