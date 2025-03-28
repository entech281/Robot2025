package frc.robot.operation;

import frc.robot.subsystems.vision.TargetLocation;
import org.littletonrobotics.junction.Logger;

import java.util.HashSet;
import java.util.Set;

public class UserPolicy {
  private static final UserPolicy instance = new UserPolicy();

  private boolean twistable = false;
  private boolean aligningToAngle = false;
  private double targetAngle = 0.0;
  private double visionPositionSetPoint = 0.0;
  private boolean laterallyAligning = false;
  private boolean algaeMode = false;
  private int targetTagID = 7;
  private boolean towardsAlignment = false;
  private Set<TargetLocation> selectedTargetLocations = new HashSet<>();

  private UserPolicy() {
    Logger.recordOutput("UserPolicy/twistable", twistable);
    Logger.recordOutput("UserPolicy/aligningToAngle", aligningToAngle);
    Logger.recordOutput("UserPolicy/targetAngle", targetAngle);
    Logger.recordOutput("UserPolicy/visionPositionSetPoint", visionPositionSetPoint);
    Logger.recordOutput("UserPolicy/laterallyAligning", laterallyAligning);
    Logger.recordOutput("UserPolicy/algaeMode", algaeMode);
    Logger.recordOutput("UserPolicy/targetTagID", targetTagID);
    Logger.recordOutput("UserPolicy/towardsAlignment", towardsAlignment);
    Logger.recordOutput("UserPolicy/selectedTargets", selectedTargetLocations + "");
  }

  public void setTargetLocations(TargetLocation t1, TargetLocation t2){
    selectedTargetLocations.clear();
    selectedTargetLocations.add(t1);
    selectedTargetLocations.add(t2);
    Logger.recordOutput("UserPolicy/selectedTargets", selectedTargetLocations + "");
  }

  public void clearTargetLocations() {
    selectedTargetLocations.clear();
    Logger.recordOutput("UserPolicy/selectedTargets", selectedTargetLocations + "");
  }

  public Set<TargetLocation> getSelectedTargetLocations(){
      return selectedTargetLocations;
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

  public boolean isAlgaeMode() {
    return this.algaeMode;
  }

  public void setAlgaeMode(boolean algaeMode) {
    this.algaeMode = algaeMode;
    Logger.recordOutput("UserPolicy/algaeMode", algaeMode);
  }

  public int getTargetTagID() {
    return this.targetTagID;
  }

  public void setTargetTagID(int targetTagID) {
    this.targetTagID = targetTagID;
    Logger.recordOutput("UserPolicy/targetTagID", targetTagID);
  }

  public boolean isTowardsAlignment() {
    return towardsAlignment;
  }

  public void setTowardsAlignment(boolean towardsAlignment) {
    this.towardsAlignment = towardsAlignment;
    Logger.recordOutput("UserPolicy/towardsAlignment", towardsAlignment);
  }
}
