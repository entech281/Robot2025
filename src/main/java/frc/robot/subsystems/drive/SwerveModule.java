// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.drive;

import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.RelativeEncoder;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import frc.robot.sensors.ThriftyEncoder;

/**
 * The {@code SwerveModule} class contains fields and methods pertaining to the function of a swerve
 * module.
 */
public class SwerveModule {
  private final SparkMax drivingSparkMax;
  private final SparkMax turningSparkMax;

  private final RelativeEncoder drivingEncoder;
  private final RelativeEncoder turningEncoder;
  private final ThriftyEncoder turningAbsoluteEncoder;

  private final SparkClosedLoopController drivingPIDController;
  private final SparkClosedLoopController turningPIDController;

  private SwerveModuleState desiredState = new SwerveModuleState(0.0, new Rotation2d());

  /**
   * Constructs a SwerveModule and configures the driving and turning motor, encoder, and PID
   * controller.
   */
  public SwerveModule(int drivingCANId, int turningCANId, int turningAnalogPort, SparkMaxConfig turningConfig, SparkMaxConfig drivingConfig) {
    drivingSparkMax = new SparkMax(drivingCANId, MotorType.kBrushless);
    turningSparkMax = new SparkMax(turningCANId, MotorType.kBrushless);

    drivingSparkMax.configure(drivingConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    turningSparkMax.configure(turningConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    drivingEncoder = drivingSparkMax.getEncoder();
    turningEncoder = turningSparkMax.getEncoder();
    turningAbsoluteEncoder = new ThriftyEncoder(turningAnalogPort);

    drivingPIDController = drivingSparkMax.getClosedLoopController();
    turningPIDController = turningSparkMax.getClosedLoopController();

    desiredState.angle = new Rotation2d(turningEncoder.getPosition());
    drivingEncoder.setPosition(0);
  }

  /**
   * Returns the current state of the module.
   *
   * @return The current state of the module.
   */
  public SwerveModuleState getState() {
    return new SwerveModuleState(drivingEncoder.getVelocity(),
        new Rotation2d(turningEncoder.getPosition()));
  }

  /**
   * Returns the current position of the module.
   *
   * @return The current position of the module.
   */
  public SwerveModulePosition getPosition() {
    return new SwerveModulePosition(drivingEncoder.getPosition(),
        new Rotation2d(turningEncoder.getPosition()));
  }

  /**
   * Sets the desired state for the module.
   *
   * @param desiredState Desired state with speed and angle.
   */
  public void setDesiredState(SwerveModuleState desiredState) {
    // Apply chassis angular offset to the desired state.
    desiredState.optimize(new Rotation2d(turningEncoder.getPosition()));

    if (Math.abs(desiredState.speedMetersPerSecond) < 0.001 // less than 1 mm per sec
        && Math.abs(desiredState.angle.getRadians() - turningEncoder.getPosition()) < 0.1) // 10%
                                                                                                    // of
    // a
    // radian
    {
      drivingSparkMax.set(0); // no point in doing anything
      turningSparkMax.set(0);
    } else {
      // Command driving and turning SPARKS MAX towards their respective setpoints.
      drivingPIDController.setReference(desiredState.speedMetersPerSecond,
          ControlType.kVelocity);
      turningPIDController.setReference(desiredState.angle.getRadians(),
          ControlType.kPosition);
    }

    this.desiredState = desiredState;
  }

  /** Zeroes all the SwerveModule relative encoders. */
  public void resetEncoders() {

    drivingEncoder.setPosition(0); // arbitrarily set driving encoder to zero

    resetTurningEncoder();
  }

  public void resetTurningEncoder() {
    turningSparkMax.set(0); // no moving during reset of relative turning encoder

    turningEncoder.setPosition(turningAbsoluteEncoder.getVirtualPosition()); // set relative
                                                                             // position based
                                                                             // on
    // virtual absolute position
  }

  /**
   * Calibrates the virtual position (i.e. sets position offset) of the absolute encoder.
   */
  public void calibrateVirtualPosition(double angle) {
    turningAbsoluteEncoder.setPositionOffset(angle);
  }

  public RelativeEncoder getDrivingEncoder() {
    return drivingEncoder;
  }

  public RelativeEncoder getTurningEncoder() {
    return turningEncoder;
  }

  public ThriftyEncoder getTurningAbsoluteEncoder() {
    return turningAbsoluteEncoder;
  }

  public SwerveModuleState getDesiredState() {
    return desiredState;
  }

}
