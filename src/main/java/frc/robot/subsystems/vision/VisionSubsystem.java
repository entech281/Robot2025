package frc.robot.subsystems.vision;

import org.ejml.simple.UnsupportedOperation;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.entech.subsystems.EntechSubsystem;

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
    NetworkTableEntry timeStampEntry = table.getEntry("timeStamp");

    // Set values in VisionOutput
    output.setHasTarget(hasTargetEntry.getBoolean(false));
    output.setTagID((int) idTagEntry.getDouble(0));
    output.setTagHeight((int) tagHeightEntry.getInteger(0));
    output.setTagWidth((int) tagWidthEntry.getInteger(0));
    output.setTagX(tagXEntry.getDouble(0));
    output.setTagY(tagYEntry.getDouble(0));
    output.setTimestamp(timeStampEntry.getInteger(0));


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