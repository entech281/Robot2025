package frc.robot.subsystems.vision;

import java.util.ArrayList;
import java.util.Optional;

import org.ejml.simple.UnsupportedOperation;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.entech.subsystems.EntechSubsystem;
import frc.entech.util.AprilTagDistanceCalculator;
import frc.robot.RobotConstants;

public class VisionSubsystem extends EntechSubsystem<VisionInput, VisionOutput> {
  private static final boolean ENABLED = true;

  // NetworkTable instance
  private final NetworkTable table;

  public VisionSubsystem() {
    // Initialize the NetworkTable instance
    table = NetworkTableInstance.getDefault().getTable("vision");
  }

  @Override
  public VisionOutput toOutputs() {
    VisionOutput output = new VisionOutput();

    // Retrieve values from NetworkTables
    NetworkTableEntry hasTargetEntry = table.getEntry("hasTarget");
    NetworkTableEntry idTagEntry = table.getEntry("idTag");
    NetworkTableEntry tagHeightEntry = table.getEntry("tagHeight");
    NetworkTableEntry tagWidthEntry = table.getEntry("tagWidth");
    NetworkTableEntry tagXEntry = table.getEntry("tagX");
    NetworkTableEntry tagYEntry = table.getEntry("tagY");
    NetworkTableEntry timestampEntry = table.getEntry("timestamp");
    NetworkTableEntry tagXWEntry = table.getEntry("tagXWidths");
    NetworkTableEntry cameraUsedEntry = table.getEntry("cameraUsed");

    ArrayList<VisionTarget> targetList = new ArrayList<>();
    
    long timestamp = timestampEntry.getInteger(0);
    long[] ids = idTagEntry.getIntegerArray(new long[] {});
    long[] heights = tagHeightEntry.getIntegerArray(new long[] {});
    long[] widths = tagWidthEntry.getIntegerArray(new long[] {});
    double[] xs = tagXEntry.getDoubleArray(new double[] {});
    double[] ys = tagYEntry.getDoubleArray(new double[] {});
    double[] tagXWs = tagXWEntry.getDoubleArray(new double[] {});
    String[] cameraUsed = cameraUsedEntry.getStringArray(new String[] {});

    for (int i = 0; i < ids.length; i++) {
      targetList.add(
        new VisionTarget(
          (int) ids[i],
          (int) heights[i],
          (int) widths[i],
          xs[i],
          ys[i],
          AprilTagDistanceCalculator.calculateCurrentDistanceInches(RobotConstants.APRIL_TAG_DATA.CALIBRATION, widths[i]),
          tagXWs[i],
          timestamp,
          cameraUsed[i]
        )
      );
    }


    // Set values in VisionOutput
    output.setHasTarget(hasTargetEntry.getBoolean(false));
    output.setTimestamp(timestampEntry.getInteger(0));
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
    return ENABLED;
  }

  @Override
  public void updateInputs(VisionInput input) {
    throw new UnsupportedOperation();
  }

   @Override
    public Command getTestCommand() {
    return Commands.none();
  }
}