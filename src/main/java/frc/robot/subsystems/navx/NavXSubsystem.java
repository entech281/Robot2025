package frc.robot.subsystems.navx;

import org.ejml.simple.UnsupportedOperation;

import com.studica.frc.AHRS;
import com.studica.frc.AHRS.NavXComType;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.entech.subsystems.EntechSubsystem;
import frc.entech.util.StoppingCounter;

public class NavXSubsystem extends EntechSubsystem<NavXInput, NavXOutput> {
  private static final boolean ENABLED = true;
  private AHRS gyro;
  private final StoppingCounter faultCounter = new StoppingCounter(3.5);
  private boolean faultDetected = false;

  @Override
  public NavXOutput toOutputs() {
    NavXOutput output = new NavXOutput();

    if (ENABLED) {
      output.setYaw(gyro.getAngle());
      output.setPitch(gyro.getPitch());
      output.setRoll(gyro.getRoll());
      output.setYawRate(gyro.getRate());
      output.setChassisSpeeds(getChassisSpeeds());
      output.setZVelocity(gyro.getVelocityZ());
      output.setTemperature(gyro.getTempC());
      output.setAngleAdjustment(gyro.getAngleAdjustment());
      output.setCompassHeading(gyro.getCompassHeading());
      output.setIsCalibrating(gyro.isCalibrating());
      output.setIsMagneticDisturbance(gyro.isMagneticDisturbance());
      output.setIsMagnetometerCalibrated(gyro.isMagnetometerCalibrated());
      output.setIsMoving(gyro.isMoving());
      output.setIsRotating(gyro.isRotating());
      output.setIsFaultDetected(faultDetected);
    }
    return output;
  }

  @Override
  public void periodic() {
    if (ENABLED) {
      SmartDashboard.putData(gyro);
      faultDetected = faultCounter.isFinished(gyro.isCalibrating());
    }
  }

  @Override
  public void initialize() {
    if (ENABLED) {
      gyro = new AHRS(NavXComType.kMXP_SPI);

      gyro.reset();

      while (gyro.isCalibrating()) {
        ;
      }

      gyro.zeroYaw();
    }
  }

  private ChassisSpeeds getChassisSpeeds() {
    if (ENABLED) {
      double radiansPerSecond = Units.degreesToRadians(gyro.getRate());
      return ChassisSpeeds.fromRobotRelativeSpeeds(gyro.getVelocityX(), gyro.getVelocityY(),
          radiansPerSecond, gyro.getRotation2d());
    } else {
        return ChassisSpeeds.fromRobotRelativeSpeeds(0.0, 0.0, 0.0, new Rotation2d(0.0));
    }
  }

  @Override
  public boolean isEnabled() {
    return ENABLED;
  }

  @Override
  public void updateInputs(NavXInput input) {
    throw new UnsupportedOperation();
  }

  public void zeroYaw() {
      if (ENABLED) {
          gyro.zeroYaw();
      }
  }

  @Override
  public Command getTestCommand() {
    return Commands.none();
  }

  public void setAngleAdjustment(double angleAdjustment) {
      if (ENABLED) {
          gyro.setAngleAdjustment(angleAdjustment);
      }
  }
}
