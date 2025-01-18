package frc.robot.operation;

public class UserPolicy {
  private static UserPolicy instance = new UserPolicy();

  private boolean twistable = false;
  private boolean aligningToAngle = false;
  private double targetAngle = 0.0;
  private double visionPositionSetPoint = 0.0;
  private boolean laterallyAligning = false;

  private UserPolicy() {}

  public static UserPolicy getInstance() {
    return instance;
  }

  public boolean isTwistable() {
    return this.twistable;
  }

  public void setIsTwistable(boolean twistable) {
    this.twistable = twistable;
  }

  public boolean isAligningToAngle() {
    return this.aligningToAngle;
  }

  public void setAligningToAngle(boolean aligningToAngle) {
    this.aligningToAngle = aligningToAngle;
  }

  public double getTargetAngle() {
    return this.targetAngle;
  }

  public void setTargetAngle(double targetAngle) {
    this.targetAngle = targetAngle;
  }

  public double getVisionPositionSetPoint() {
    return this.visionPositionSetPoint;
  }

  public void setVisionPositionSetPoint(double visionPositionSetPoint) {
    this.visionPositionSetPoint = visionPositionSetPoint;
  }

  public boolean isLaterallyAligning() {
    return this.laterallyAligning;
  }

  public void setLaterallyAligning(boolean laterallyAligning) {
    this.laterallyAligning = laterallyAligning;
  }
}
