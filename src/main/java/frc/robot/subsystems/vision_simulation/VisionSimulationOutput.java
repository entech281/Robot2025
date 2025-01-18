package frc.robot.subsystems.vision_simulation;

import org.littletonrobotics.junction.Logger;

import frc.entech.subsystems.SubsystemOutput;

public class VisionSimulationOutput extends SubsystemOutput {
  private boolean inFrame;
  private long timestamp;

  @Override
  public void toLog() {
    Logger.recordOutput("VisionSimulationOutput/inFrame", inFrame);
    Logger.recordOutput("VisionSimulationOutput/timestamp", timestamp);
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

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }
}