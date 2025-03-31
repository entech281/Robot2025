package frc.robot.subsystems.vision;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.littletonrobotics.junction.Logger;

import frc.entech.subsystems.SubsystemOutput;

public class VisionOutput extends SubsystemOutput {
  private boolean hasTarget;
  private Optional<VisionTarget> bestTarget;
  private List<VisionTarget> targets;
  private long timestamp;
  private int numberOfTags;
  private String reefCloseness;
  private boolean cameraGood;


  @Override
  public void toLog() {
    Logger.recordOutput("VisionOutput/timestamp", timestamp);
    Logger.recordOutput("VisionOutput/hasTarget", hasTarget);
    Logger.recordOutput("VisionOutput/numberOfTags", numberOfTags);
    Logger.recordOutput("VisionOutput/reefCloseness", reefCloseness);
    Logger.recordOutput("VisionOutput/cameraGood", cameraGood);
    if (bestTarget.isPresent()) {
      bestTarget.get().log("VisionOutput/bestTarget");
    }
    for (int i = 0; i < targets.size(); i++) {
      targets.get(i).log("VisionOutput/targets/target" + i);
    }
  }

  public List<VisionTarget> findSpecificTarget(Set<TargetLocation> query) {
    List<VisionTarget> foundTargets = new ArrayList<>();
    if (!targets.isEmpty()) {
      for (VisionTarget t : targets) {
        if (query.contains(new TargetLocation(t.getTagID(), t.getCameraName().equals(VisionInput.Camera.SIDE.label) ? VisionInput.Camera.SIDE : VisionInput.Camera.TOP))) {
          foundTargets.add(t);
        }
      }
    }
    return foundTargets;
  }

  public String getReefCloseness() {
    return reefCloseness;
  }

  public void setReefCloseness(String reefCloseness) {
    this.reefCloseness = reefCloseness;
  }

  public boolean hasTarget() {
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

  public List<VisionTarget> getTargets() {
    return this.targets;
  }

  public void setTargets(List<VisionTarget> targets) {
    this.targets = targets;
  }

  public long getTimestamp() {
    return this.timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public int getNumberOfTags() {
    return this.numberOfTags;
  }

  public void setNumberOfTags(int numberOfTags) {
    this.numberOfTags = numberOfTags;
  }

  public boolean isCameraGood() {
    return this.cameraGood;
  }

  public void setCameraGood(boolean cameraGood) {
    this.cameraGood = cameraGood;
  }
}