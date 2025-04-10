package frc.robot.subsystems.gamepiecehandler;

import org.littletonrobotics.junction.Logger;

import frc.entech.subsystems.SparkMaxOutput;
import frc.entech.subsystems.SubsystemOutput;

public class GamePieceHandlerOutput extends SubsystemOutput {
  private boolean isRunning = false;
  private double currentSpeed = 0.0;
  private double encoderPosition = 0.0;
  private boolean isAtTargetPosition = false;
  private boolean brakeModeEnabled = false;
  private boolean hasAlgae = false;
  private boolean hasCoral = false;

  private SparkMaxOutput motor;

  @Override
  public void toLog() {
    Logger.recordOutput("GamePieceHandlerOutput/isRunning", isRunning);
    Logger.recordOutput("GamePieceHandlerOutput/currentSpeed", currentSpeed);
    Logger.recordOutput("GamePieceHandlerOutput/encoderPosition", encoderPosition);
    Logger.recordOutput("GamePieceHandlerOutput/isAtTargetPosition", isAtTargetPosition);
    Logger.recordOutput("GamePieceHandlerOutput/brakeModeEnabled", brakeModeEnabled);

    Logger.recordOutput("GamePieceHandlerOutput/HasCoral", hasCoral);
    Logger.recordOutput("GamePieceHandlerOutput/HasAlgae", hasAlgae);

    motor.log("GamePieceHandlerOutput/motor");
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
  
  public boolean isBrakeModeEnabled() {
    return this.brakeModeEnabled;
  }

  public void setBrakeModeEnabled(boolean brakeModeEnabled) {
        this.brakeModeEnabled = brakeModeEnabled;
   }

  public boolean getHasCoral() {
    return this.hasCoral;
  }

  public void setHasCoral(boolean hasCoral) {
    this.hasCoral = hasCoral;
  }

  public boolean getHasAlgae() {
    return this.hasAlgae;
  }

  public void setHasAlgae(boolean hasAlgae) {
    this.hasAlgae = hasAlgae;
  }

  public SparkMaxOutput getMotor() {
    return this.motor;
  }

  public void setMotor(SparkMaxOutput motor) {
    this.motor = motor;
  }
}