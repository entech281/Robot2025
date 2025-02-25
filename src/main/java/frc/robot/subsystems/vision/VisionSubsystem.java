package frc.robot.subsystems.vision;

import java.util.ArrayList;
import java.util.Optional;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.entech.subsystems.EntechSubsystem;
import frc.entech.util.AprilTagDistanceCalculator;
import frc.robot.RobotConstants;

public class VisionSubsystem extends EntechSubsystem<VisionInput, VisionOutput> {
  // NetworkTable instance
  private final NetworkTable networkTable;

  public VisionSubsystem() {
    // Initialize the NetworkTable instance
    networkTable = NetworkTableInstance.getDefault().getTable("vision");
  }

  @Override
  public VisionOutput toOutputs() {
    VisionOutput output = new VisionOutput();

    // Retrieve values from NetworkTables
    NetworkTableEntry hasTargetEntry = networkTable.getEntry("hasTarget");
    NetworkTableEntry idTagEntry = networkTable.getEntry("idTag");
    NetworkTableEntry tagHeightEntry = networkTable.getEntry("tagHeight");
    NetworkTableEntry tagWidthEntry = networkTable.getEntry("tagWidth");
    NetworkTableEntry tagXEntry = networkTable.getEntry("tagX");
    NetworkTableEntry tagYEntry = networkTable.getEntry("tagY");
    NetworkTableEntry timestampEntry = networkTable.getEntry("timestamp");
    NetworkTableEntry tagXWEntry = networkTable.getEntry("tagXWidths");
    NetworkTableEntry cameraUsedEntry = networkTable.getEntry("cameraUsed");
    NetworkTableEntry numberOfTargetsEntry = networkTable.getEntry("numberOfTargets");

    ArrayList<VisionTarget> targetList = new ArrayList<>();
    
    long timestamp = timestampEntry.getInteger(0);
    long[] ids = idTagEntry.getIntegerArray(new long[] {});
    long[] heights = tagHeightEntry.getIntegerArray(new long[] {});
    long[] widths = tagWidthEntry.getIntegerArray(new long[] {});
    double[] xs = tagXEntry.getDoubleArray(new double[] {});
    double[] ys = tagYEntry.getDoubleArray(new double[] {});
    double[] tagXWs = tagXWEntry.getDoubleArray(new double[] {});
    String[] cameraUsed = cameraUsedEntry.getStringArray(new String[] {});
    long numberOfTargets = numberOfTargetsEntry.getInteger(0);

    for (int i = 0; i < numberOfTargets; i++) {
      VisionTarget target = new VisionTarget();
      target.setTagID((int) ids[i]);
      target.setTagHeight((int) heights[i]);
      target.setTagWidth((int) widths[i]);
      target.setTagX(xs[i]);
      target.setTagY(ys[i]);
      target.setDistance(AprilTagDistanceCalculator.calculateCurrentDistanceInches(RobotConstants.APRIL_TAG_DATA.CALIBRATION, widths[i]));
      target.setTagXW(tagXWs[i]);
      target.setTimestamp(timestamp);
      target.setCameraName(cameraUsed[i]);
      targetList.add(target);
    }


    // Set values in VisionOutput
    output.setHasTarget(hasTargetEntry.getBoolean(false));
    output.setTimestamp(timestamp);
    output.setNumberOfTags((int) numberOfTargets);
    if (output.hasTarget()) {
      output.setTargets(targetList);
      double closest = 999;
      VisionTarget bestTarget = null;
      for (VisionTarget target : targetList) {
        if (target.getDistance() < closest) {
          closest = target.getDistance();
          bestTarget = target;
        }
      }
      if (bestTarget != null) {
        output.setBestTarget(Optional.of(bestTarget));
      } else {
        output.setBestTarget(Optional.empty());
      }
    } else {
      output.setTargets(new ArrayList<>());
      output.setBestTarget(Optional.empty());
    }

    return output;
  }

  @Override
  public void initialize() {
    // Initialization logic if needed
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public void updateInputs(VisionInput input) {
    try (NetworkTableEntry entry = new NetworkTableEntry(NetworkTableInstance.getDefault(), 0)) {
      entry.setString(input.getCamera());
      networkTable.putValue("camera", entry.getValue());
    }
  }

   @Override
    public Command getTestCommand() {
    return Commands.none();
  }
}