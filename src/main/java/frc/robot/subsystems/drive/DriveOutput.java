package frc.robot.subsystems.drive;


import org.littletonrobotics.junction.Logger;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import frc.entech.subsystems.SparkMaxOutput;
import frc.entech.subsystems.SubsystemOutput;

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
    Logger.recordOutput("DriveOutput/modulePositions", modulePositions);
    Logger.recordOutput("DriveOutput/rawAbsoluteEncoders", rawAbsoluteEncoders);
    Logger.recordOutput("DriveOutput/virtualAbsoluteEncoders", virtualAbsoluteEncoders);
    Logger.recordOutput("DriveOutput/moduleStates", moduleStates);
    Logger.recordOutput("DriveOutput/chassisSpeed", speeds);

    frontLeftDrive.log("DriveOutput/frontLeft");
    frontRightDrive.log("DriveOutput/frontRight");
    rearLeftDrive.log("DriveOutput/rearLeft");
    rearRightDrive.log("DriveOutput/rearRight");
    frontRightTurn.log("DriveOutput/frontRightTurn");
    rearLeftTurn.log("DriveOutput/rearLeftTurn");
    rearRightTurn.log("DriveOutput/rearRightTurn");
    frontLeftTurn.log("DriveOutput/frontLeftTurn");
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
