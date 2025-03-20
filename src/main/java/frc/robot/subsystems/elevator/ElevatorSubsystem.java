package frc.robot.subsystems.elevator;

import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.ClosedLoopConfig.FeedbackSensor;
import com.revrobotics.spark.config.MAXMotionConfig.MAXMotionPositionMode;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.entech.subsystems.EntechSubsystem;
import frc.entech.subsystems.SparkMaxOutput;
import frc.entech.util.EntechUtils;
import frc.robot.RobotConstants;
import frc.robot.io.RobotIO;

public class ElevatorSubsystem extends EntechSubsystem<ElevatorInput, ElevatorOutput> {

  private static final boolean ENABLED = true;
  private static final boolean IS_INVERTED = false;
  
  private ElevatorInput currentInput = new ElevatorInput();

  private SparkMax leftElevator;
  private SparkMax rightElevator;

  private double lastPosition;

  public static double calculateMotorPositionFromInches(double inches) {
    return -inches * RobotConstants.ELEVATOR.ELEVATOR_CONVERSION_FACTOR;
  }

  public static double calculateInchesFromMotorPosition(double motorPosition) {
    return -motorPosition / RobotConstants.ELEVATOR.ELEVATOR_CONVERSION_FACTOR;
  }

  @Override
  public void initialize() {
    if (ENABLED) {
      SparkMaxConfig motorConfig = new SparkMaxConfig();

      leftElevator = new SparkMax(RobotConstants.PORTS.CAN.ELEVATOR_A, MotorType.kBrushless);
      rightElevator = new SparkMax(RobotConstants.PORTS.CAN.ELEVATOR_B, MotorType.kBrushless);
      leftElevator.getEncoder().setPosition(0.0);
      rightElevator.getEncoder().setPosition(0.0);

      motorConfig.inverted(IS_INVERTED);

      motorConfig.idleMode(IdleMode.kBrake);

      motorConfig.closedLoop.feedbackSensor(FeedbackSensor.kPrimaryEncoder)
      .pidf(0.375, 0.0, 0.0, 0.0, ClosedLoopSlot.kSlot0)
      .pidf(0.25, 0.0, 0.0, 0.0, ClosedLoopSlot.kSlot1)
      .outputRange(-1.0, 1.0, ClosedLoopSlot.kSlot0)
      .outputRange(-1.0, 1.0, ClosedLoopSlot.kSlot1);

      motorConfig.closedLoop.maxMotion
          .maxAcceleration(RobotConstants.ELEVATOR.SLOT1_MAX_ACCELERATION,ClosedLoopSlot.kSlot1)
          .maxVelocity(RobotConstants.ELEVATOR.SLOT1_MAX_VELOCITY,ClosedLoopSlot.kSlot1)
          .allowedClosedLoopError(RobotConstants.ELEVATOR.SLOT1_ALLOWED_ERROR,ClosedLoopSlot.kSlot1)
          .positionMode(MAXMotionPositionMode.kMAXMotionTrapezoidal, ClosedLoopSlot.kSlot1);

      // motorConfig.secondaryCurrentLimit(40);

      SparkMaxConfig followerConfig = new SparkMaxConfig();
      followerConfig.apply(motorConfig).follow(leftElevator);


      leftElevator.configure(motorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
      rightElevator.configure(followerConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

      lastPosition = RobotConstants.ELEVATOR.UPPER_SOFT_LIMIT_DEG;
    }
  }
  private double clampRequestedPosition(double position) {
    if (position < 0) {
      DriverStation.reportWarning("Lelevator tried to go to " + currentInput.getRequestedPosition()
        + " value was changed to " + RobotConstants.ELEVATOR.LOWER_SOFT_LIMIT_DEG, false);
          return RobotConstants.ELEVATOR.LOWER_SOFT_LIMIT_DEG;
    } 
    else if (position > RobotConstants.ELEVATOR.UPPER_SOFT_LIMIT_DEG) {
      DriverStation.reportWarning("Lelevator tried to go to " + currentInput.getRequestedPosition()
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
    double kg = 0.15;
    if ((ENABLED) && (clampedPosition != lastPosition)) {
      if (currentInput.getActivate()) {
        if ((calculateInchesFromMotorPosition(leftElevator.getEncoder().getPosition())) - clampedPosition <= 0) {
          leftElevator.getClosedLoopController().setReference(calculateMotorPositionFromInches(clampedPosition), ControlType.kPosition, ClosedLoopSlot.kSlot0, -kg);
        } 
        else {
          leftElevator.getClosedLoopController().setReference(calculateMotorPositionFromInches(clampedPosition), ControlType.kMAXMotionPositionControl, ClosedLoopSlot.kSlot1, kg);
        }
      } 
      else {
        leftElevator.getClosedLoopController().setReference(calculateMotorPositionFromInches(clampedPosition), ControlType.kMAXMotionPositionControl, ClosedLoopSlot.kSlot0, clampedPosition != 0 ? RobotIO.getInstance().getElevatorOutput().getCurrentPosition() < clampedPosition ? -kg : kg : 0.0);
      }
    }
    lastPosition = clampedPosition;
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
      elevatorOutput.setCurrentPosition(calculateInchesFromMotorPosition(leftElevator.getEncoder().getPosition()));
      elevatorOutput.setAtRequestedPosition(EntechUtils.isWithinTolerance(0.15,
          elevatorOutput.getCurrentPosition(), currentInput.getRequestedPosition()));
      elevatorOutput.setAtLowerLimit(
          leftElevator.getReverseLimitSwitch().isPressed());
      elevatorOutput.setAtUpperLimit(
          leftElevator.getForwardLimitSwitch().isPressed());
      elevatorOutput.setRequestedPosition(currentInput.getRequestedPosition());

      SparkMaxOutput sm = SparkMaxOutput.createOutput(leftElevator);
      elevatorOutput.setLeftMotor(sm);
      SparkMaxOutput sm2 = SparkMaxOutput.createOutput(rightElevator);
      elevatorOutput.setRightMotor(sm2);
    }
    return elevatorOutput;
  }

  @Override
  public Command getTestCommand() {
    return Commands.none();
  }

  @Override
  public boolean isEnabled() {
    return ENABLED;
  }
}