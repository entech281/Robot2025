// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.drive;

import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.config.ClosedLoopConfig.FeedbackSensor;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.util.WPIUtilJNI;
import edu.wpi.first.wpilibj2.command.Command;
import frc.entech.subsystems.EntechSubsystem;
import frc.robot.RobotConstants;
import frc.robot.RobotConstants.DrivetrainConstants;
import frc.robot.RobotConstants.SwerveModuleConstants;

/**
 * The {@code Drivetrain} class contains fields and methods pertaining to the function of the
 * drivetrain.
 */
public class DriveSubsystem extends EntechSubsystem<DriveInput, DriveOutput> {
  private static final boolean ENABLED = true;

  public static final int GYRO_ORIENTATION = 1; // might be able to merge with kGyroReversed

  private final SlewRateLimiter magLimiter = new SlewRateLimiter(DrivetrainConstants.MAGNITUDE_SLEW_RATE);
  private final SlewRateLimiter rotLimiter = new SlewRateLimiter(DrivetrainConstants.ROTATIONAL_SLEW_RATE);

  private SwerveModule frontLeft;
  private SwerveModule frontRight;
  private SwerveModule rearLeft;
  private SwerveModule rearRight;

  private double currentTranslationDir = 0.0;
  private double currentTranslationMag = 0.0;

  private double prevTime = WPIUtilJNI.now() * 1e-6;

  private ChassisSpeeds lastChassisSpeeds =
      ChassisSpeeds.fromRobotRelativeSpeeds(0.0, 0.0, 0.0, Rotation2d.fromDegrees(0.0));

  @Override
  public void updateInputs(DriveInput input) {
    if (ENABLED) {
      double xSpeedCommanded;
      double ySpeedCommanded;
      double currentRotation;

      if (RobotConstants.DrivetrainConstants.RATE_LIMITING) {
        double[] limitedInputs =
            calculateSlewRateLimiting(input.getXSpeed(), input.getYSpeed(), input.getRotation());

        xSpeedCommanded = limitedInputs[0];
        ySpeedCommanded = limitedInputs[1];
        currentRotation = limitedInputs[2];
      } else {
        xSpeedCommanded = input.getXSpeed();
        ySpeedCommanded = input.getYSpeed();
        currentRotation = input.getRotation();
      }

      // Convert the commanded speeds into the correct units for the drivetrain
      double xSpeedDelivered = xSpeedCommanded * DrivetrainConstants.MAX_SPEED_METERS_PER_SECOND;
      double ySpeedDelivered = ySpeedCommanded * DrivetrainConstants.MAX_SPEED_METERS_PER_SECOND;
      double rotDelivered =
          currentRotation * DrivetrainConstants.MAX_ANGULAR_SPEED_RADIANS_PER_SECOND;

      lastChassisSpeeds = ChassisSpeeds.fromFieldRelativeSpeeds(xSpeedDelivered, ySpeedDelivered,
          rotDelivered, input.getGyroAngle());

      SwerveModuleState[] swerveModuleStates =
          DrivetrainConstants.DRIVE_KINEMATICS.toSwerveModuleStates(lastChassisSpeeds);

      setModuleStates(swerveModuleStates);
    }
  }

  private double[] calculateSlewRateLimiting(double xSpeed, double ySpeed, double rotSpeed) {
    // Convert XY to polar for rate limiting
    double inputTranslationDir = Math.atan2(ySpeed, xSpeed);
    double inputTranslationMag = Math.sqrt(Math.pow(xSpeed, 2) + Math.pow(ySpeed, 2));

    // Calculate the direction slew rate based on an estimate of the lateral
    // acceleration
    double directionSlewRate;

    if (currentTranslationMag != 0.0) {
      directionSlewRate = Math.abs(DrivetrainConstants.DIRECTION_SLEW_RATE / currentTranslationMag);
    } else {
      directionSlewRate = 500.0; // some high number that means the slew rate is effectively
                                 // instantaneous
    }

    double currentTime = WPIUtilJNI.now() * 1e-6;
    double elapsedTime = currentTime - prevTime;
    double angleDif = SwerveUtils.angleDifference(inputTranslationDir, currentTranslationDir);

    if (angleDif < 0.45 * Math.PI) {
      currentTranslationDir = SwerveUtils.stepTowardsCircular(currentTranslationDir,
          inputTranslationDir, directionSlewRate * elapsedTime);
      currentTranslationMag = magLimiter.calculate(inputTranslationMag);
    } else if (angleDif > 0.85 * Math.PI) {
      if (currentTranslationMag > 1e-4) {
        currentTranslationMag = magLimiter.calculate(0.0);
      } else {
        currentTranslationDir = SwerveUtils.wrapAngle(currentTranslationDir + Math.PI);
        currentTranslationMag = magLimiter.calculate(inputTranslationMag);
      }
    } else {
      currentTranslationDir = SwerveUtils.stepTowardsCircular(currentTranslationDir,
          inputTranslationDir, directionSlewRate * elapsedTime);
      currentTranslationMag = magLimiter.calculate(0.0);
    }

    prevTime = currentTime;

    return new double[] {currentTranslationMag * Math.cos(currentTranslationDir),
        currentTranslationMag * Math.sin(currentTranslationDir), rotLimiter.calculate(rotSpeed)};
  }

  public ChassisSpeeds getChassisSpeeds() {
    return lastChassisSpeeds;
  }

  @Override
  public DriveOutput toOutputs() {
    DriveOutput output = new DriveOutput();
    output.setModulePositions(getModulePositions());
    if (ENABLED) {
        output.setRawAbsoluteEncoders(new double[] {frontLeft.getTurningAbsoluteEncoder().getPosition(),
                frontRight.getTurningAbsoluteEncoder().getPosition(),
                rearLeft.getTurningAbsoluteEncoder().getPosition(),
                rearRight.getTurningAbsoluteEncoder().getPosition()});
        output.setVirtualAbsoluteEncoders(
            new double[] {frontLeft.getTurningAbsoluteEncoder().getVirtualPosition(),
                frontRight.getTurningAbsoluteEncoder().getVirtualPosition(),
                rearLeft.getTurningAbsoluteEncoder().getVirtualPosition(),
                rearRight.getTurningAbsoluteEncoder().getVirtualPosition()});
        output.setModuleStates(new SwerveModuleState[] {frontLeft.getState(), frontRight.getState(),
                rearLeft.getState(), rearRight.getState()});
        output.setSpeeds(lastChassisSpeeds);

      output.setFrontLeftDrive(frontLeft.getDriveOuput());
      output.setFrontRightDrive(frontRight.getDriveOuput());
      output.setRearLeftDrive(rearLeft.getDriveOuput());
      output.setRearRightDrive(rearRight.getDriveOuput());
      output.setFrontRightTurn(frontRight.getTurnOutput());
      output.setRearLeftTurn(rearLeft.getTurnOutput());
      output.setRearRightTurn(rearRight.getTurnOutput());
      output.setFrontLeftTurn(frontLeft.getTurnOutput());

    }
    return output;
  }

  private SwerveModulePosition[] getModulePositions() {
    if (ENABLED) {
      return new SwerveModulePosition[] {frontLeft.getPosition(), frontRight.getPosition(),
        rearLeft.getPosition(), rearRight.getPosition()};
    } else {
      return new SwerveModulePosition[] {
        new SwerveModulePosition(0.0,Rotation2d.fromDegrees(0.0)),
        new SwerveModulePosition(0.0,Rotation2d.fromDegrees(0.0)),
        new SwerveModulePosition(0.0,Rotation2d.fromDegrees(0.0)),
        new SwerveModulePosition(0.0,Rotation2d.fromDegrees(0.0)) };
    }
  }

  public void pathFollowDrive(ChassisSpeeds speeds) {
    SwerveModuleState[] swerveModuleStates =
        DrivetrainConstants.DRIVE_KINEMATICS.toSwerveModuleStates(speeds);

    setModuleStates(swerveModuleStates);
  }

  /**
   * Sets the wheels into an X formation to prevent movement.
   */
  public void setX() {
    if (ENABLED) {
      frontLeft.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(45)));
      frontRight.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(-45)));
      rearLeft.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(-45)));
      rearRight.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(45)));
    }
  }

  /**
   * Sets the swerve ModuleStates.
   *
   * @param desiredStates The desired SwerveModule states.
   */
  public void setModuleStates(SwerveModuleState[] desiredStates) {
    if (ENABLED) {
      SwerveDriveKinematics.desaturateWheelSpeeds(desiredStates,
          DrivetrainConstants.MAX_SPEED_METERS_PER_SECOND);

      frontLeft.setDesiredState(desiredStates[0]);
      frontRight.setDesiredState(desiredStates[1]);
      rearLeft.setDesiredState(desiredStates[2]);
      rearRight.setDesiredState(desiredStates[3]);
    }
  }

  /**
   * Resets the drive encoders to currently read a position of 0 and seeds the turn encoders using
   * the absolute encoders.
   */
  public void resetEncoders() {
    if (ENABLED) {
      frontLeft.resetEncoders();
      rearLeft.resetEncoders();
      frontRight.resetEncoders();
      rearRight.resetEncoders();
    }
  }

  public void resetTurningEncoders() {
    if (ENABLED) {
      frontLeft.resetTurningEncoder();
      rearLeft.resetTurningEncoder();
      frontRight.resetTurningEncoder();
      rearRight.resetTurningEncoder();
    }
  }

  @Override
  public boolean isEnabled() {
    return ENABLED;
  }

  @Override
  public void initialize() {
    if (ENABLED) {
      SparkMaxConfig drivingConfig = new SparkMaxConfig();
      SparkMaxConfig turningConfig = new SparkMaxConfig();

      drivingConfig.smartCurrentLimit(RobotConstants.SwerveModuleConstants.DRIVING_MOTOR_CURRENT_LIMIT_AMPS);
      turningConfig.smartCurrentLimit(RobotConstants.SwerveModuleConstants.TURNING_MOTOR_CURRENT_LIMIT_AMPS);

      drivingConfig.inverted(false);
      turningConfig.inverted(true);

      drivingConfig.encoder
        .positionConversionFactor(RobotConstants.SwerveModuleConstants.DRIVING_ENCODER_POSITION_FACTOR_METERS_PER_ROTATION)
        .velocityConversionFactor(RobotConstants.SwerveModuleConstants.DRIVING_ENCODER_VELOCITY_FACTOR_METERS_PER_SECOND_PER_RPM);
      turningConfig.encoder
        .positionConversionFactor(RobotConstants.SwerveModuleConstants.TURNING_ENCODER_POSITION_FACTOR_RADIANS_PER_ROTATION)
        .velocityConversionFactor(RobotConstants.SwerveModuleConstants.TURNING_ENCODER_VELOCITY_FACTOR_RADIANS_PER_SECOND_PER_RPM);

      turningConfig.closedLoop
        .positionWrappingEnabled(true)
        .positionWrappingMaxInput(SwerveModuleConstants.TURNING_ENCODER_POSITION_PID_MAX_INPUT_RADIANS)
        .positionWrappingMinInput(SwerveModuleConstants.TURNING_ENCODER_POSITION_PID_MIN_INPUT_RADIANS);

      drivingConfig.idleMode(SwerveModuleConstants.DRIVING_MOTOR_IDLE_MODE);
      turningConfig.idleMode(SwerveModuleConstants.TURNING_MOTOR_IDLE_MODE);

      drivingConfig.closedLoop
        .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
        .pidf(SwerveModuleConstants.DRIVING_P, SwerveModuleConstants.DRIVING_I, SwerveModuleConstants.DRIVING_D, SwerveModuleConstants.DRIVING_FF, ClosedLoopSlot.kSlot0)
        .outputRange(SwerveModuleConstants.DRIVING_MIN_OUTPUT_NORMALIZED, SwerveModuleConstants.DRIVING_MAX_OUTPUT_NORMALIZED, ClosedLoopSlot.kSlot0);
      turningConfig.closedLoop
        .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
        .pidf(SwerveModuleConstants.TURNING_P, SwerveModuleConstants.TURNING_I, SwerveModuleConstants.TURNING_D, SwerveModuleConstants.TURNING_FF, ClosedLoopSlot.kSlot0)
        .outputRange(SwerveModuleConstants.TURNING_MIN_OUTPUT_NORMALIZED, SwerveModuleConstants.TURNING_MAX_OUTPUT_NORMALIZED, ClosedLoopSlot.kSlot0);

      frontLeft = new SwerveModule(RobotConstants.PORTS.CAN.FRONT_LEFT_DRIVING,
          RobotConstants.PORTS.CAN.FRONT_LEFT_TURNING,
          RobotConstants.PORTS.ANALOG.FRONT_LEFT_TURNING_ABSOLUTE_ENCODER, turningConfig, drivingConfig);

      frontRight = new SwerveModule(RobotConstants.PORTS.CAN.FRONT_RIGHT_DRIVING,
          RobotConstants.PORTS.CAN.FRONT_RIGHT_TURNING,
          RobotConstants.PORTS.ANALOG.FRONT_RIGHT_TURNING_ABSOLUTE_ENCODER, turningConfig, drivingConfig);

      rearLeft = new SwerveModule(RobotConstants.PORTS.CAN.REAR_LEFT_DRIVING,
          RobotConstants.PORTS.CAN.REAR_LEFT_TURNING,
          RobotConstants.PORTS.ANALOG.REAR_LEFT_TURNING_ABSOLUTE_ENCODER, turningConfig, drivingConfig);

      rearRight = new SwerveModule(RobotConstants.PORTS.CAN.REAR_RIGHT_DRIVING,
          RobotConstants.PORTS.CAN.REAR_RIGHT_TURNING,
          RobotConstants.PORTS.ANALOG.REAR_RIGHT_TURNING_ABSOLUTE_ENCODER, turningConfig, drivingConfig);

      frontLeft.calibrateVirtualPosition(RobotConstants.SwerveModuleConstants.FRONT_LEFT_VIRTUAL_OFFSET_RADIANS);
      frontRight.calibrateVirtualPosition(RobotConstants.SwerveModuleConstants.FRONT_RIGHT_VIRTUAL_OFFSET_RADIANS);
      rearLeft.calibrateVirtualPosition(RobotConstants.SwerveModuleConstants.REAR_LEFT_VIRTUAL_OFFSET_RADIANS);
      rearRight.calibrateVirtualPosition(RobotConstants.SwerveModuleConstants.REAR_RIGHT_VIRTUAL_OFFSET_RADIANS);

      resetEncoders();
    }
  }

  @Override
  public Command getTestCommand() {
    return new TestDriveCommand(this);
  }
}
