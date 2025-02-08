package frc.robot.subsystems.vision;

import java.util.ArrayList;
import java.util.Optional;

import org.littletonrobotics.junction.Logger;

import frc.entech.subsystems.SubsystemOutput;

public class VisionOutput extends SubsystemOutput {
  private boolean hasTarget;
  private Optional<VisionTarget> bestTarget;
  private ArrayList<VisionTarget> targets;
  private long timestamp;

  @Override
  public void toLog() {
    Logger.recordOutput("VisionOutput/timestamp", timestamp);
    Logger.recordOutput("VisionOutput/hasTarget", hasTarget);
    if (bestTarget.isPresent()) {
      bestTarget.get().log("VisionOutput/bestTarget");
    }
    for (int i = 0; i < targets.size(); i++) {
      targets.get(i).log("VisionOutput/targets/target" + i);
    }
  }

  public boolean hasTarget() {
    return this.hasTarget;
  }

  public boolean getHasTarget() {
    return this.hasTarget;
  }

  public void setHasTarget(boolean hasTarget) {
    this.hasTarget = hasTarget;
  }

  public Optional<VisionTarget> getBestTarget() {
    return this.bestTarget;
  }

  public void setBestTarget(Optional<VisionTarget> bestTarget) {
    this.bestTarget = bestTarget;
  }

  public ArrayList<VisionTarget> getTargets() {
    return this.targets;
  }

  public void setTargets(ArrayList<VisionTarget> targets) {
    this.targets = targets;
  }

  public long getTimestamp() {
    return this.timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }
}