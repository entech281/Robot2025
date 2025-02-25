package frc.robot.commands;

import edu.wpi.first.math.geometry.Pose2d;
import frc.entech.commands.EntechCommand;
import frc.robot.processors.OdometryProcessor;

public class ResetOdometryCommand extends EntechCommand {
  private final OdometryProcessor odometry;

  public ResetOdometryCommand(OdometryProcessor odometry) {
    this.odometry = odometry;
  }

  @Override
  public void initialize() {
    odometry.resetOdometry(new Pose2d(15.17, 5.53, odometry.getEstimatedPose().getRotation()));
  }

  @Override
  public boolean runsWhenDisabled() {
    return true;
  }

  @Override
  public boolean isFinished() {
    return true;
  }
}
