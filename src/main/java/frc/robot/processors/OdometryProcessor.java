package frc.robot.processors;

import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.RobotConstants;
import frc.robot.io.RobotIO;

public class OdometryProcessor {
  private SwerveDrivePoseEstimator estimator;
  private boolean integrateVision = false;
  private Field2d field = new Field2d();

  public Pose2d getEstimatedPose() {
    return estimator.getEstimatedPosition();
  }

  public void createEstimator() {
    estimator = new SwerveDrivePoseEstimator(RobotConstants.DrivetrainConstants.DRIVE_KINEMATICS,
        Rotation2d.fromDegrees(RobotIO.getInstance().getNavXOutput().getYaw()),
        RobotIO.getInstance().getDriveOutput().getModulePositions(),
        RobotConstants.ODOMETRY.INITIAL_POSE);

    estimator.setVisionMeasurementStdDevs(RobotConstants.Vision.VISION_STD_DEVS);
    field.setRobotPose(getEstimatedPose());
    SmartDashboard.putData(field);
  }

  public void update() {
    estimator.update(Rotation2d.fromDegrees(RobotIO.getInstance().getNavXOutput().getYaw()),
        RobotIO.getInstance().getDriveOutput().getModulePositions());

    RobotIO.getInstance().updateOdometryPose(getEstimatedPose());
    field.setRobotPose(getEstimatedPose());
  }

  public void addVisionEstimatedPose(Pose2d visionPose, double timeStamp, Rotation2d yaw) {
    Pose2d fixedVisionPose = new Pose2d(visionPose.getTranslation(), yaw);
    estimator.addVisionMeasurement(fixedVisionPose, timeStamp);
  }

  public double calculateDistanceFromTarget(Pose2d target) {
    double xDist = getEstimatedPose().getX() - target.getX();
    double yDist = getEstimatedPose().getY() - target.getY();

    return Math.sqrt(xDist * xDist + yDist * yDist);
  }

  /**
   * Resets the odometry to the specified pose.
   *
   * @param pose The pose to which to set the odometry.
   */
  public void resetOdometry(Pose2d pose) {
    estimator.resetPosition(Rotation2d.fromDegrees(RobotIO.getInstance().getNavXOutput().getYaw()),
        RobotIO.getInstance().getDriveOutput().getModulePositions(), pose);
  }

  /**
   * Resets the odometry to the specified pose.
   *
   * @param pose The pose to which to set the odometry.
   * @param gyroAngle the latest gyro angle.
   */
  public void resetOdometry(Pose2d pose, Rotation2d gyroAngle) {
    estimator.resetPosition(gyroAngle, RobotIO.getInstance().getDriveOutput().getModulePositions(),
        pose);
  }

  public boolean isIntegratingVision() {
    return this.integrateVision;
  }

  public void setIntegrateVision(boolean integrateVision) {
    this.integrateVision = integrateVision;
  }
}
