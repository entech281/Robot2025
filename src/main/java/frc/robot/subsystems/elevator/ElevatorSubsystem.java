package frc.robot.subsystems.elevator;

import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.ClosedLoopConfig.FeedbackSensor;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import frc.entech.subsystems.EntechSubsystem;
import frc.entech.util.EntechUtils;
import frc.robot.RobotConstants;
import frc.robot.io.RobotIO;

public class ElevatorSubsystem extends EntechSubsystem<ElevatorInput, ElevatorOutput> {

  private static final boolean ENABLED = true;
  private static final boolean IS_INVERTED = false;
  
  private ElevatorInput currentInput = new ElevatorInput();

  private SparkMax leftElevator;
  private SparkMax rightElevator;

  public static double calculateMotorPositionFromDegrees(double degrees) {
    return degrees / RobotConstants.ELEVATOR.ELEVATOR_CONVERSION_FACTOR;
  }

  @Override
  public void initialize() {
    if (ENABLED) {
      
      SparkMaxConfig motorConfig = new SparkMaxConfig();

      leftElevator = new SparkMax(RobotConstants.PORTS.CAN.ELEVATOR_A, MotorType.kBrushless);
      leftElevator.getEncoder().setPosition(0.0);

      motorConfig.inverted(IS_INVERTED);

      motorConfig.idleMode(IdleMode.kBrake);

      motorConfig.closedLoop.feedbackSensor(FeedbackSensor.kPrimaryEncoder)
      .pid(0.2, 0, 0, ClosedLoopSlot.kSlot0)
      .pid(0.2, 0, 0, ClosedLoopSlot.kSlot1)
      .outputRange(-1, 1, ClosedLoopSlot.kSlot0)
      .outputRange(-1, 1, ClosedLoopSlot.kSlot1);

      motorConfig.closedLoop.maxMotion
          .maxVelocity(1000,ClosedLoopSlot.kSlot0)
          .maxAcceleration(1000,ClosedLoopSlot.kSlot0)
          .allowedClosedLoopError(1,ClosedLoopSlot.kSlot0)

          .maxAcceleration(500,ClosedLoopSlot.kSlot1)
          .maxVelocity(500,ClosedLoopSlot.kSlot1 )
          .allowedClosedLoopError(1,ClosedLoopSlot.kSlot1);

    leftElevator.configure(motorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);



    }
  }
  private double clampRequestedPosition(double position) {
    if (position < 0) {
      DriverStation.reportWarning("Pivot tried to go to " + currentInput.getRequestedPosition()
        + " value was changed to " + RobotConstants.ELEVATOR.LOWER_SOFT_LIMIT_DEG, false);
          return RobotConstants.ELEVATOR.LOWER_SOFT_LIMIT_DEG;
    } 
    else if (position > RobotConstants.ELEVATOR.UPPER_SOFT_LIMIT_DEG) {
      DriverStation.reportWarning("Pivot tried to go to " + currentInput.getRequestedPosition()
          + " value was changed to " + RobotConstants.ELEVATOR.UPPER_SOFT_LIMIT_DEG, false);
      return RobotConstants.ELEVATOR.UPPER_SOFT_LIMIT_DEG;
    } 
    else {
      return position;
    }
  }

  @Override
  public void periodic() {
    double clampedPosition = clampRequestedPosition(currentInput.getRequestedPosition());
    if (ENABLED) {
      if (currentInput.getActivate()) {
        if ((-leftElevator.getEncoder().getPosition() * RobotConstants.ELEVATOR.ELEVATOR_CONVERSION_FACTOR) - clampedPosition <= 0) {
          leftElevator.getClosedLoopController().setReference(-calculateMotorPositionFromDegrees(clampedPosition), ControlType.kMAXMotionPositionControl, ClosedLoopSlot.kSlot0);
        } 
        else {
          leftElevator.getClosedLoopController().setReference(-calculateMotorPositionFromDegrees(clampedPosition), ControlType.kMAXMotionPositionControl, ClosedLoopSlot.kSlot1);
        }
      } 
      else {
        leftElevator.getClosedLoopController().setReference(-calculateMotorPositionFromDegrees(RobotConstants.ELEVATOR.LOWER_SOFT_LIMIT_DEG), ControlType.kMAXMotionPositionControl, ClosedLoopSlot.kSlot1);
      }
    }
  }
  @Override
  public boolean isEnabled() {
    return ENABLED;
  }

  @Override
  public void updateInputs(ElevatorInput input) {
    RobotIO.processInput(input);
    this.currentInput = input;
  }

  @Override
  public ElevatorOutput toOutputs() {
    ElevatorOutput elevatorOutput = new ElevatorOutput();
    if (ENABLED) {
      elevatorOutput.setMoving(leftElevator.getEncoder().getVelocity() != 0);
      elevatorOutput.setLeftBrakeModeEnabled(true);
      elevatorOutput.setRightBrakeModeEnabled(true);
      elevatorOutput.setCurrentPosition(
          -leftElevator.getEncoder().getPosition() * RobotConstants.ELEVATOR.ELEVATOR_CONVERSION_FACTOR);
      elevatorOutput.setAtRequestedPosition(EntechUtils.isWithinTolerance(2,
          elevatorOutput.getCurrentPosition(), currentInput.getRequestedPosition()));
      elevatorOutput.setAtLowerLimit(
          leftElevator.getReverseLimitSwitch().isPressed());
      elevatorOutput.setAtUpperLimit(
          leftElevator.getForwardLimitSwitch().isPressed());
      elevatorOutput.setRequestedPosition(currentInput.getRequestedPosition());
    }
    return elevatorOutput;
  }

  @Override
  public Command getTestCommand() {
    // return new TestPivotCommand(this);
    return null;
  }

}
