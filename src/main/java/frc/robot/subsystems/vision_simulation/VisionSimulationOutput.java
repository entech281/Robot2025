package frc.robot.subsystems.vision_simulation;

import org.littletonrobotics.junction.Logger;

import frc.entech.subsystems.SubsystemOutput;

public class VisionSimulationOutput extends SubsystemOutput {
  private boolean inFrame;
  private long timestamp;
  private int tagID;

  @Override
  public void toLog() {
    Logger.recordOutput("VisionSimulationOutput/inFrame", inFrame);
    Logger.recordOutput("VisionSimulationOutput/timestamp", timestamp);
    Logger.recordOutput("VisionSimulationOutput/tagID", tagID);

  }

  public boolean isInFrame() {
    return inFrame;
  }

  public void setInFrame(boolean inFrame) {
    this.inFrame = inFrame;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public int getTagID() {
    return this.tagID;
  }

  public void setTagID(int tagID) {
    this.tagID = tagID;
  }

  
  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }
}