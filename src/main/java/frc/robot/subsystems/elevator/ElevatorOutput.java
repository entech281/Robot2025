package frc.robot.subsystems.elevator;

import org.littletonrobotics.junction.Logger;

import frc.entech.subsystems.SparkMaxOutput;
import frc.entech.subsystems.SubsystemOutput;

public class ElevatorOutput extends SubsystemOutput {
  private boolean moving = false;
  private boolean leftBrakeModeEnabled = false;
  private boolean rightBrakeModeEnabled = false;
  private boolean isAtRequestedPosition = false;
  private double requestedPosition = 0.0;
  private boolean isAtUpperLimit = false;
  private boolean isAtLowerLimit = false;
  private double currentPosition = 0.0;

  private SparkMaxOutput leftMotor;
  private SparkMaxOutput rightMotor;

  @Override
  public void toLog() {
    Logger.recordOutput("ElevatorOutput/moving", moving);
    Logger.recordOutput("ElevatorOutput/leftBrakeModeEnabled", leftBrakeModeEnabled);
    Logger.recordOutput("ElevatorOutput/rightBrakeModeEnabled", rightBrakeModeEnabled);
    Logger.recordOutput("ElevatorOutput/requestedPosition", requestedPosition);
    Logger.recordOutput("ElevatorOutput/currentPosition", currentPosition);
    Logger.recordOutput("ElevatorOutput/isAtUpperLimit", isAtUpperLimit);
    Logger.recordOutput("ElevatorOutput/isAtLowerLimit", isAtLowerLimit);
    Logger.recordOutput("ElevatorOutput/isAtRequestedPosition", isAtRequestedPosition);

    leftMotor.log("ElevatorOutput/leftMotor");
    rightMotor.log("ElevatorOutput/rightMotor");
  }

  public boolean isMoving() {
    return this.moving;
  }

  public void setMoving(boolean moving) {
    this.moving = moving;
  }

  public boolean isLeftBrakeModeEnabled() {
    return this.leftBrakeModeEnabled;
  }

  public void setLeftBrakeModeEnabled(boolean leftBrakeModeEnabled) {
    this.leftBrakeModeEnabled = leftBrakeModeEnabled;
  }

  public boolean isRightBrakeModeEnabled() {
    return this.rightBrakeModeEnabled;
  }

  public void setRightBrakeModeEnabled(boolean rightBrakeModeEnabled) {
    this.rightBrakeModeEnabled = rightBrakeModeEnabled;
  }

  public boolean isAtRequestedPosition() {
    return this.isAtRequestedPosition;
  }

  public void setAtRequestedPosition(boolean isAtRequestedPosition) {
    this.isAtRequestedPosition = isAtRequestedPosition;
  }

  public double getCurrentPosition() {
    return this.currentPosition;
  }

  public void setCurrentPosition(double currentPosition) {
    this.currentPosition = currentPosition;
  }

  public boolean isAtUpperLimit() {
    return this.isAtUpperLimit;
  }

  public void setAtUpperLimit(boolean isAtUpperLimit) {
    this.isAtUpperLimit = isAtUpperLimit;
  }

  public boolean isAtLowerLimit() {
    return this.isAtLowerLimit;
  }

  public void setAtLowerLimit(boolean isAtLowerLimit) {
    this.isAtLowerLimit = isAtLowerLimit;
  }

  public double getRequestedPosition() {
    return this.requestedPosition;
  }

  public void setRequestedPosition(double requestedPosition) {
    this.requestedPosition = requestedPosition;
  }

  public SparkMaxOutput getLeftMotor() {
    return this.leftMotor;
  }

  public void setLeftMotor(SparkMaxOutput leftMotor) {
    this.leftMotor = leftMotor;
  }

  public SparkMaxOutput getRightMotor() {
    return this.rightMotor;
  }

  public void setRightMotor(SparkMaxOutput rightMotor) {
    this.rightMotor = rightMotor;
  }
}