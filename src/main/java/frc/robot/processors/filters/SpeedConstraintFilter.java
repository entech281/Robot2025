package frc.robot.processors.filters;

import edu.wpi.first.math.MathUtil;
import frc.entech.util.EntechUtils;
import frc.robot.RobotConstants;
import frc.robot.io.RobotIO;
import frc.robot.subsystems.drive.DriveInput;

public class SpeedConstraintFilter implements DriveFilterI {

  public static double ELEVATOR_HEIGHT_AT_TOP = 20.0;
  public static double ELEVATOR_HEIGHT_FOR_FULL_SPEED =
      RobotConstants.DrivetrainConstants.SPEED_LIMIT;
  public static double FULL_SPEED = 1.0;
  public static double SLOWEST_SPEED = 0.2;

  @Override
  public DriveInput process(DriveInput input) {

    double currentElevatorPosition = RobotIO.getInstance().getElevatorOutput().getCurrentPosition();
    boolean intakeHasCoral = RobotIO.getInstance().getInternalCoralDetectorOutput().hasCoral();
    boolean intakeRunning = RobotIO.getInstance().getCoralMechanismOutput().isRunning();

    double overallMaxSpeed =
        computeMaxSpeed(currentElevatorPosition, intakeHasCoral, intakeRunning);

    return getConstrainedInput(input, overallMaxSpeed);
  }



  public static double computeMaxSpeedFromElevatorHeight(double currentElevatorHeight) {

    double calculated = EntechUtils.map(currentElevatorHeight, ELEVATOR_HEIGHT_FOR_FULL_SPEED,
        ELEVATOR_HEIGHT_AT_TOP, FULL_SPEED, SLOWEST_SPEED);

    //make sure this calculation never returns more than full speed or less than zero speed
    return MathUtil.clamp(calculated, SLOWEST_SPEED, FULL_SPEED);
  }

  public static double computeMaxSpeedFromCoralIntake(boolean hasCoral, boolean intakeRunning) {
    if (!hasCoral && intakeRunning) {
      return SLOWEST_SPEED;
    } else {
      return FULL_SPEED;
    }
  }

  public static double computeMaxSpeed(double currentElevatorHeight, boolean hasCoral,
      boolean intakeRunning) {

    return Math.min(computeMaxSpeedFromElevatorHeight(currentElevatorHeight),
        computeMaxSpeedFromCoralIntake(hasCoral, intakeRunning));
  }



  private DriveInput getConstrainedInput(DriveInput input, double maxSpeed) {
    DriveInput processedInput = new DriveInput(input);

    processedInput.setXSpeed(MathUtil.clamp(processedInput.getXSpeed(), -maxSpeed, maxSpeed));
    processedInput.setYSpeed(MathUtil.clamp(processedInput.getYSpeed(), -maxSpeed, maxSpeed));

    return processedInput;
  }
}
