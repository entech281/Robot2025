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
import jdk.jfr.Enabled;

public class ElevatorSubsystem extends EntechSubsystem<ElevatorInput, ElevatorOutput> {

  private static final boolean ENABLED = true;
  private static final boolean IS_INVERTED = false;
  
  private ElevatorInput currentInput = new ElevatorInput();

  private SparkMax leftElevator;
  private SparkMax rightElevator;

  public static double calculateMotorPositionFromDegrees(double degrees) {
    // return degrees / RobotConstants.ELEVATOR.ELEVATOR_CONVERSION_FACTOR;
    return degrees;
  }

  @Override
  public void initialize() {
    if (ENABLED) {
      
      SparkMaxConfig lelevator = new SparkMaxConfig();
      // SparkMaxConfig relevator = new SparkMaxConfig();

      leftElevator = new SparkMax(RobotConstants.PORTS.CAN.ELEVATOR_A, MotorType.kBrushless);
      // rightElevator = new SparkMax(RobotConstants.PORTS.CAN.ELEVATOR_B, MotorType.kBrushless);

      // relevator.follow(leftElevator);
      leftElevator.getEncoder().setPosition(0.0);

      lelevator.inverted(IS_INVERTED);
      // relevator.inverted(IS_INVERTED);

      lelevator.idleMode(IdleMode.kBrake);
      // relevator.idleMode(IdleMode.kBrake);

      lelevator.closedLoop.feedbackSensor(FeedbackSensor.kPrimaryEncoder);
      lelevator.closedLoop.pidf(0.2, 0, 0, 0);
      lelevator.closedLoop.outputRange(-0.2, 0.2);

      leftElevator.configure(lelevator, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
      // rightElevator.configure(relevator, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
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
    // double clampedPosition = currentInput.getRequestedPosition();
    // if (ENABLED) {
    //   if (currentInput.getActivate()) {
    //     if ((leftElevator.getEncoder().getPosition() * RobotConstants.ELEVATOR.ELEVATOR_CONVERSION_FACTOR) - clampedPosition <= 0) {
    //       leftElevator.getClosedLoopController().setReference(calculateMotorPositionFromDegrees(clampedPosition), ControlType.kPosition);
    //     } 
    //     else {
    //       leftElevator.getClosedLoopController().setReference(calculateMotorPositionFromDegrees(clampedPosition), ControlType.kPosition);
    //     }
    //   } 
    //   else {
    //     leftElevator.getClosedLoopController().setReference(calculateMotorPositionFromDegrees(RobotConstants.ELEVATOR.LOWER_SOFT_LIMIT_DEG), ControlType.kMAXMotionPositionControl);
    //   }
    // }
    leftElevator.getClosedLoopController().setReference(currentInput.getRequestedPosition(), ControlType.kPosition);
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
          leftElevator.getEncoder().getPosition());
      elevatorOutput.setAtRequestedPosition(EntechUtils.isWithinTolerance(2,
          elevatorOutput.getCurrentPosition(), currentInput.getRequestedPosition()));
      elevatorOutput.setAtLowerLimit(
          leftElevator.getReverseLimitSwitch().isPressed());
      elevatorOutput.setRequestedPosition(currentInput.getRequestedPosition());
    }
    return elevatorOutput;
  }

  @Override
  public Command getTestCommand() {
    // return new TestPivotCommand(this);
    return null;
  }

  @Override
  public boolean isEnabled() {
    return ENABLED;
  }

}