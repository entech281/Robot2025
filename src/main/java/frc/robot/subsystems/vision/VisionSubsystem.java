package frc.robot.subsystems.vision;

import java.util.ArrayList;
import java.util.Optional;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StringPublisher;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import frc.entech.subsystems.EntechSubsystem;
import frc.entech.util.AprilTagDistanceCalculator;
import frc.robot.RobotConstants;
import frc.robot.operation.UserPolicy;

public class VisionSubsystem extends EntechSubsystem<VisionInput, VisionOutput> {
  // NetworkTable instance
  private final NetworkTable networkTable;
  private final StringPublisher cameraSetter;

  public VisionSubsystem() {
    // Initialize the NetworkTable instance
    networkTable = NetworkTableInstance.getDefault().getTable("vision");
    cameraSetter = networkTable.getStringTopic("camera").publish();
  }

  @Override
  public VisionOutput toOutputs() {
    VisionOutput output = new VisionOutput();

    // Retrieve values from NetworkTables
    NetworkTableEntry hasTargetEntry = networkTable.getEntry("hasTarget");
    NetworkTableEntry idTagEntry = networkTable.getEntry("tagID");
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
    Number[] ids = idTagEntry.getNumberArray(new Number[] {});
    Number[] heights = tagHeightEntry.getNumberArray(new Number[] {});
    Number[] widths = tagWidthEntry.getNumberArray(new Number[] {});
    Number[] xs = tagXEntry.getNumberArray(new Number[] {});
    Number[] ys = tagYEntry.getNumberArray(new Number[] {});
    Number[] tagXWs = tagXWEntry.getNumberArray(new Number[] {});
    String[] cameraUsed = cameraUsedEntry.getStringArray(new String[] {});
    long numberOfTargets = numberOfTargetsEntry.getInteger(0);
    try {
      for (int i = 0; i < numberOfTargets; i++) {
        VisionTarget target = new VisionTarget();
        target.setTagID(ids[i].intValue());
        target.setTagHeight(heights[i].intValue());
        target.setTagWidth(widths[i].intValue());
        target.setTagX(xs[i].doubleValue());
        target.setTagY(ys[i].doubleValue());
        try {
          target.setDistance(AprilTagDistanceCalculator.calculateCurrentDistanceInches(RobotConstants.APRIL_TAG_DATA.CALIBRATION, widths[i].intValue()));
        } catch (Exception e) {
          DriverStation.reportWarning(e.getMessage(), false);
          continue;
        }
        target.setTagXW(tagXWs[i].doubleValue());
        target.setTimestamp(timestamp);
        target.setCameraName(cameraUsed[i]);
        targetList.add(target);
      }
    } catch (Exception e) {
      targetList = new ArrayList<>();
      DriverStation.reportWarning(e.getMessage(), false);
    }

    // Set values in VisionOutput
    output.setHasTarget(hasTargetEntry.getBoolean(false) && !targetList.isEmpty());
    output.setTimestamp(timestamp);
    output.setNumberOfTags(targetList.size());
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

    int selectedTag = UserPolicy.getInstance().getTargetTagID();
    double selectedTagWidth = 0;
    for (VisionTarget target : targetList) {
      if (target.getTagID() == selectedTag){
        selectedTagWidth = target.getTagWidth();
      }
    }

    output.setReefCloseness(VisionSubsystem.getCloseness(selectedTagWidth));

    
    return output;
  }
  
  public static final String getCloseness(double selectedTagWidth) {
    if (selectedTagWidth >= 200 && selectedTagWidth <= 250) {
      return "#00FF00";
    }
    else if (selectedTagWidth >= 144 && selectedTagWidth < 200) {
      return "#FFFF00";
    }
    else if (selectedTagWidth > 250) {
      return "#FF0000";
    }
    else {
      return "#000000";
    }
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
    cameraSetter.set(input.getCamera());
  }

   @Override
    public Command getTestCommand() {
    return new TestVisionCommand(this);
  }
}