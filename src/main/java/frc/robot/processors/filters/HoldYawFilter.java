package frc.robot.processors.filters;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import frc.entech.util.StoppingCounter;
import frc.robot.subsystems.drive.DriveInput;

public class HoldYawFilter implements DriveFilterI {
  private final PIDController controller = new PIDController(0.0075, 0, 0.0);
  private final StoppingCounter stopCounter = new StoppingCounter(0.15);
  private Rotation2d holdAngle;

  public HoldYawFilter() {
    controller.enableContinuousInput(-180, 180);
  }

  @Override
  public DriveInput process(DriveInput input) {
    DriveInput filteredInput = new DriveInput(input);

    if (!stopCounter.isFinished(input.getRotation() != 0.0)) {
      holdAngle = input.getLatestOdometryPose().getRotation();
    } else if (holdAngle != null) {
      filteredInput.setRotation(controller.calculate(
          input.getLatestOdometryPose().getRotation().getDegrees(), holdAngle.getDegrees()));
    }

    return filteredInput;
  }
}
