package frc.robot.subsystems.vision;

import org.littletonrobotics.junction.Logger;

import frc.entech.subsystems.SubsystemOutput;

public class VisionOutput extends SubsystemOutput {
  private Boolean hasTarget;
  private int tagID;
  private int tagHeight;
  private int tagWidth;
  private double tagX;
  private double tagY;
  private long timestamp;

  @Override
  public void toLog() {
    Logger.recordOutput("VisionOutput/timestamp", timestamp);
    Logger.recordOutput("VisionOutput/hasTarget", hasTarget);
    Logger.recordOutput("VisionOutput/tagID", tagID);
    Logger.recordOutput("VisionOutput/tagHeight", tagHeight);
    Logger.recordOutput("VisionOutput/tagWidth", tagWidth);
    Logger.recordOutput("VisionOutput/tagX", tagX);
    Logger.recordOutput("VisionOutput/tagY", tagY);
  }

  public Boolean getHasTarget() {
    return this.hasTarget;
  }

  public void setHasTarget(Boolean hasTarget) {
    this.hasTarget = hasTarget;
  }

  public int getTagID() {
    return this.tagID;
  }

  public void setTagID(int tagID) {
    this.tagID = tagID;
  }

  public int getTagHeight() {
    return this.tagHeight;
  }

  public void setTagHeight(int tagHeight) {
    this.tagHeight = tagHeight;
  }

  public int getTagWidth() {
    return this.tagWidth;
  }

  public void setTagWidth(int tagWidth) {
    this.tagWidth = tagWidth;
  }

  public double getTagX() {
    return this.tagX;
  }

  public void setTagX(double tagX) {
    this.tagX = tagX;
  }

  public double getTagY() {
    return this.tagY;
  }

  public void setTagY(double tagY) {
    this.tagY = tagY;
  }

  public long getTimestamp() {
    return this.timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }
}