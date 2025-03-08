package frc.robot.processors.filters;

import edu.wpi.first.math.controller.PIDController;
import frc.robot.operation.UserPolicy;
import frc.robot.subsystems.drive.DriveInput;

public class AutoYawFilter implements DriveFilterI {
  private final PIDController controller = new PIDController(0.005, 0, 0.0);

  public AutoYawFilter() {
    controller.enableContinuousInput(-180, 180);
  }

  @Override
  public DriveInput process(DriveInput input) {
    DriveInput processedInput = new DriveInput(input);

    if (UserPolicy.getInstance().isAligningToAngle() && !UserPolicy.getInstance().isTwistable()) {
      processedInput.setRotation(controller.calculate(
        input.getLatestOdometryPose().getRotation().getDegrees(),
        UserPolicy.getInstance().getTargetAngle()
      ));
    }

    return processedInput;
  }
}
