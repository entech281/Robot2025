package frc.robot.subsystems.drive;

import org.littletonrobotics.junction.Logger;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.RobotBase;
import frc.entech.subsystems.SparkMaxOutput;
import frc.entech.subsystems.SubsystemOutput;
import frc.robot.io.RobotIO;
import frc.robot.subsystems.drive.DriveInput;

public class DriveOutput extends SubsystemOutput {
  private SwerveModulePosition[] modulePositions;
  private double[] rawAbsoluteEncoders;
  private double[] virtualAbsoluteEncoders;
  private SwerveModuleState[] moduleStates;
  private ChassisSpeeds speeds;

  private SparkMaxOutput frontLeftDrive;
  private SparkMaxOutput frontRightDrive;
  private SparkMaxOutput rearLeftDrive;
  private SparkMaxOutput rearRightDrive;
  private SparkMaxOutput frontLeftTurn;
  private SparkMaxOutput frontRightTurn;
  private SparkMaxOutput rearLeftTurn;
  private SparkMaxOutput rearRightTurn;

  @Override
  public void toLog() {
    if (RobotBase.isSimulation()) {
      // In simulation, grab the drive inputs (or simulated values) and log those.
      DriveInput simInput = RobotIO.getInstance().getDriveInput();
      Logger.recordOutput("DriveOutput/simulatedXSpeed", simInput.getXSpeed());
      Logger.recordOutput("DriveOutput/simulatedYSpeed", simInput.getYSpeed());
      Logger.recordOutput("DriveOutput/simulatedRotation", simInput.getRotation());
      // You might also log the simulated gyro angle or pose, for example:
      Logger.recordOutput("DriveOutput/simulatedGyroAngle", simInput.getGyroAngle().getDegrees());
      Logger.recordOutput("DriveOutput/simulatedOdometryPose", simInput.getLatestOdometryPose());
    } else {
      Logger.recordOutput("DriveOutput/virtualAbsoluteEncoders", virtualAbsoluteEncoders);
      Logger.recordOutput("DriveOutput/moduleStates", moduleStates);
      Logger.recordOutput("DriveOutput/chassisSpeed", speeds);
      
      // Log drive motor speeds
      // Logger.recordOutput("DriveOutput/frontLeftSpeed", frontLeftDrive.get);
      // Logger.recordOutput("DriveOutput/frontRightSpeed", frontRightDrive.getCurrentSpeed());
      // Logger.recordOutput("DriveOutput/rearLeftSpeed", rearLeftDrive.getCurrentSpeed());
      // Logger.recordOutput("DriveOutput/rearRightSpeed", rearRightDrive.getCurrentSpeed());

      frontLeftDrive.log("DriveOutput/frontLeft");
      frontRightDrive.log("DriveOutput/frontRight");
      rearLeftDrive.log("DriveOutput/rearLeft");
      rearRightDrive.log("DriveOutput/rearRight");
      frontLeftTurn.log("DriveOutput/frontLeftTurn");
      frontRightTurn.log("DriveOutput/frontRightTurn");
      rearLeftTurn.log("DriveOutput/rearLeftTurn");
      rearRightTurn.log("DriveOutput/rearRightTurn");
    }
  }

  public SwerveModulePosition[] getModulePositions() {
    return this.modulePositions;
  }

  public void setModulePositions(SwerveModulePosition[] modulePositions) {
    this.modulePositions = modulePositions;
  }

  public double[] getRawAbsoluteEncoders() {
    return this.rawAbsoluteEncoders;
  }

  public void setRawAbsoluteEncoders(double[] rawAbsoluteEncoders) {
    this.rawAbsoluteEncoders = rawAbsoluteEncoders;
  }

  public double[] getVirtualAbsoluteEncoders() {
    return this.virtualAbsoluteEncoders;
  }

  public void setVirtualAbsoluteEncoders(double[] virtualAbsoluteEncoders) {
    this.virtualAbsoluteEncoders = virtualAbsoluteEncoders;
  }

  public SwerveModuleState[] getModuleStates() {
    return this.moduleStates;
  }

  public void setModuleStates(SwerveModuleState[] moduleStates) {
    this.moduleStates = moduleStates;
  }


  public ChassisSpeeds getSpeeds() {
    return this.speeds;
  }

  public void setSpeeds(ChassisSpeeds speeds) {
    this.speeds = speeds;
  }

   public SparkMaxOutput getFrontLeftDrive() {
    return frontLeftDrive;
  }

  public void setFrontLeftDrive(SparkMaxOutput frontLeftDrive) {
    this.frontLeftDrive = frontLeftDrive;
  }

  public SparkMaxOutput getFrontRightDrive() {
    return frontRightDrive;
  }

  public void setFrontRightDrive(SparkMaxOutput frontRightDrive) {
    this.frontRightDrive = frontRightDrive;
  }

  public SparkMaxOutput getRearLeftDrive() {
    return rearLeftDrive;
  }

  public void setRearLeftDrive(SparkMaxOutput rearLeftDrive) {
    this.rearLeftDrive = rearLeftDrive;
  }

  public SparkMaxOutput getRearRightDrive() {
    return rearRightDrive;
  }

  public void setRearRightDrive(SparkMaxOutput rearRightDrive) {
    this.rearRightDrive = rearRightDrive;
  }

  public SparkMaxOutput getFrontLeftTurn() {
    return frontLeftTurn;
  }

  public void setFrontLeftTurn(SparkMaxOutput frontLeftTurn) {
    this.frontLeftTurn = frontLeftTurn;
  }

  public SparkMaxOutput getFrontRightTurn() {
    return frontRightTurn;
  }

  public void setFrontRightTurn(SparkMaxOutput frontRightTurn) {
    this.frontRightTurn = frontRightTurn;
  }

  public SparkMaxOutput getRearLeftTurn() {
    return rearLeftTurn;
  }

  public void setRearLeftTurn(SparkMaxOutput rearLeftTurn) {
    this.rearLeftTurn = rearLeftTurn;
  }

  public SparkMaxOutput getRearRightTurn() {
    return rearRightTurn;
  }

  public void setRearRightTurn(SparkMaxOutput rearRightTurn) {
    this.rearRightTurn = rearRightTurn;
  }
}
