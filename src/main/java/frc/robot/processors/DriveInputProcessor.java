package frc.robot.processors;

import java.util.ArrayList;
import java.util.List;

import frc.robot.Position;
import frc.robot.RobotConstants;
import frc.robot.io.RobotIO;
import frc.robot.livetuning.LiveTuningHandler;
import frc.robot.processors.filters.AutoYawFilter;
import frc.robot.processors.filters.DriveFilterI;
import frc.robot.processors.filters.HoldYawFilter;
import frc.robot.processors.filters.LateralAlignFilter;
import frc.robot.processors.filters.MaxConstraintFilter;
import frc.robot.processors.filters.SpeedConstraintFilter;
import frc.robot.processors.filters.SquaringFilter;
import frc.robot.processors.filters.TowardsTargetFilter;
import frc.robot.processors.filters.TwistFilter;
import frc.robot.subsystems.drive.DriveInput;

public class DriveInputProcessor {
  private final List<DriveFilterI> driveFilters = new ArrayList<>();

  public DriveInputProcessor() {
    driveFilters.add(new TwistFilter());
    driveFilters.add(new MaxConstraintFilter());
    driveFilters.add(new SquaringFilter());
    driveFilters.add(new HoldYawFilter());
    driveFilters.add(new AutoYawFilter());
    driveFilters.add(new LateralAlignFilter());
    driveFilters.add(new TowardsTargetFilter());
    driveFilters.add(new SpeedConstraintFilter(RobotConstants.DrivetrainConstants.SPEED_LIMIT, LiveTuningHandler.getInstance().getValue(Position.L2.getElevatorKey())));
  }

  public DriveInput processInput(DriveInput input) {
    DriveInput processedInput = new DriveInput(input);

    for (DriveFilterI filter : driveFilters) {
      processedInput = filter.process(processedInput);
      processedInput.setKey(filter.getClass().getSimpleName());
      RobotIO.processInput(processedInput);
    }

    processedInput.setKey("final");
    RobotIO.processInput(processedInput);

    return processedInput;
  }
}
