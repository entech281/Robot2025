package frc.robot.processors.filters;

import org.littletonrobotics.junction.Logger;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import frc.entech.util.StoppingCounter;
import frc.robot.operation.UserPolicy;
import frc.robot.subsystems.drive.DriveInput;

public class HoldYawFilter implements DriveFilterI {
  private final static double RESET_MARGIN = 3.0;
  private final PIDController controller = new PIDController(0.0035, 0, 0.0);
  private final StoppingCounter stopCounter = new StoppingCounter(0.25);
  private Rotation2d holdAngle = new Rotation2d(0);

  public HoldYawFilter() {
    controller.enableContinuousInput(-180, 180);
  }

  @Override
  public DriveInput process(DriveInput input) {
    DriveInput filteredInput = new DriveInput(input);

    if (holdAngle != null)
      Logger.recordOutput("Hold Angle", holdAngle.getDegrees());

    if (!stopCounter.isFinished(input.getRotation() == 0.0) || (Math.abs(input.getLatestOdometryPose().getRotation().getDegrees() - holdAngle.getDegrees()) > RESET_MARGIN)) {
      holdAngle = input.getLatestOdometryPose().getRotation();
    } else if (holdAngle != null && !UserPolicy.getInstance().isTwistable()) {
      filteredInput.setRotation(controller.calculate(
          input.getLatestOdometryPose().getRotation().getDegrees(), holdAngle.getDegrees()));
    }

    return filteredInput;
  }
}
