package frc.robot.subsystems.coralMechanism;

import org.littletonrobotics.junction.Logger;

import frc.entech.subsystems.SubsystemOutput;

public class CoralMechanismOutput extends SubsystemOutput {
  private boolean isRunning = false;
  private double currentSpeed = 0.0;
  private double encoderPosition = 0.0;
  private boolean isAtTargetPosition = false;

  @Override
  public void toLog() {
    Logger.recordOutput("CoralMechanismOutput/isRunning", isRunning);
    Logger.recordOutput("CoralMechanismOutput/currentSpeed", currentSpeed);
    Logger.recordOutput("CoralMechanismOutput/encoderPosition", encoderPosition);
    Logger.recordOutput("CoralMechanismOutput/isAtTargetPosition", isAtTargetPosition);
  }

  public boolean isRunning() {
    return this.isRunning;
  }

  public void setRunning(boolean isRunning) {
    this.isRunning = isRunning;
  }

  public double getCurrentSpeed() {
    return this.currentSpeed;
  }

  public void setCurrentSpeed(double currentSpeed) {
    this.currentSpeed = currentSpeed;
  }

  public double getEncoderPosition() {
    return this.encoderPosition;
  }

  public void setEncoderPosition(double encoderPosition) {
    this.encoderPosition = encoderPosition;
  }

  public boolean isAtTargetPosition() {
    return this.isAtTargetPosition;
  }

  public void setAtTargetPosition(boolean isAtTargetPosition) {
    this.isAtTargetPosition = isAtTargetPosition;
  }
}