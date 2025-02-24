package frc.robot.operation;

import org.littletonrobotics.junction.Logger;

public class UserPolicy {
  private static final UserPolicy instance = new UserPolicy();

  private boolean twistable = false;
  private boolean aligningToAngle = false;
  private double targetAngle = 0.0;
  private double visionPositionSetPoint = 0.0;
  private boolean laterallyAligning = false;

  private UserPolicy() {
    Logger.recordOutput("UserPolicy/twistable", twistable);
    Logger.recordOutput("UserPolicy/aligningToAngle", aligningToAngle);
    Logger.recordOutput("UserPolicy/targetAngle", targetAngle);
    Logger.recordOutput("UserPolicy/visionPositionSetPoint", visionPositionSetPoint);
    Logger.recordOutput("UserPolicy/laterallyAligning", laterallyAligning);
  }

  public static UserPolicy getInstance() {
    return instance;
  }

  public boolean isTwistable() {
    return this.twistable;
  }

  public void setIsTwistable(boolean twistable) {
    this.twistable = twistable;
    Logger.recordOutput("UserPolicy/twistable", twistable);
  }

  public boolean isAligningToAngle() {
    return this.aligningToAngle;
  }

  public void setAligningToAngle(boolean aligningToAngle) {
    this.aligningToAngle = aligningToAngle;
    Logger.recordOutput("UserPolicy/aligningToAngle", aligningToAngle);
  }

  public double getTargetAngle() {
    return this.targetAngle;
  }

  public void setTargetAngle(double targetAngle) {
    this.targetAngle = targetAngle;
    Logger.recordOutput("UserPolicy/targetAngle", targetAngle);
  }

  public double getVisionPositionSetPoint() {
    return this.visionPositionSetPoint;
  }

  public void setVisionPositionSetPoint(double visionPositionSetPoint) {
    this.visionPositionSetPoint = visionPositionSetPoint;
    Logger.recordOutput("UserPolicy/visionPositionSetPoint", visionPositionSetPoint);
  }

  public boolean isLaterallyAligning() {
    return this.laterallyAligning;
  }

  public void setLaterallyAligning(boolean laterallyAligning) {
    this.laterallyAligning = laterallyAligning;
    Logger.recordOutput("UserPolicy/laterallyAligning", laterallyAligning);
  }
}
