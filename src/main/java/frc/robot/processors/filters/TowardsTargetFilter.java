package frc.robot.processors.filters;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import frc.robot.io.RobotIO;
import frc.robot.livetuning.LiveTuningHandler;
import frc.robot.operation.UserPolicy;
import frc.robot.subsystems.drive.DriveInput;
import frc.robot.subsystems.vision.VisionTarget;

public class TowardsTargetFilter implements DriveFilterI {
    private final PIDController moveController = new PIDController(
        LiveTuningHandler.getInstance().getValue("AutoAlign/StopP"),
        LiveTuningHandler.getInstance().getValue("AutoAlign/StopI"),
        LiveTuningHandler.getInstance().getValue("AutoAlign/StopD")
    );

    public TowardsTargetFilter() {
        moveController.setTolerance(0.02);
    }

    @Override
    public DriveInput process(DriveInput input) {
        DriveInput processedInput = new DriveInput(input);

        if (RobotIO.getInstance().getVisionOutput().hasTarget() && UserPolicy.getInstance().isTowardsAlignment()) {
            for (VisionTarget t : RobotIO.getInstance().getVisionOutput().getTargets()) {
                if (t.getTagID() == UserPolicy.getInstance().getTargetTagID()) {
                    moveController.setP(LiveTuningHandler.getInstance().getValue("AutoAlign/StopP"));
                    moveController.setI(LiveTuningHandler.getInstance().getValue("AutoAlign/StopI"));
                    moveController.setD(LiveTuningHandler.getInstance().getValue("AutoAlign/StopD"));
                    double ratio = MathUtil.clamp(-moveController.calculate(t.getDistance(), LiveTuningHandler.getInstance().getValue("AutoAlign/Stop")), -1.0, 1.0);
                    double mag = MathUtil.clamp(ratio * LiveTuningHandler.getInstance().getValue("AutoAlign/Speed"), -LiveTuningHandler.getInstance().getValue("AutoAlign/MaxSpeed"), LiveTuningHandler.getInstance().getValue("AutoAlign/MaxSpeed"));
                    if (DriverStation.getAlliance().isPresent() && DriverStation.getAlliance().get() == Alliance.Blue) {
                        processedInput.setXSpeed((Math.cos(Units.degreesToRadians(UserPolicy.getInstance().getTargetAngle())) * mag) + input.getXSpeed());
                        processedInput.setYSpeed((Math.sin(Units.degreesToRadians(UserPolicy.getInstance().getTargetAngle())) * mag) + input.getYSpeed());
                    } else {
                        processedInput.setXSpeed((Math.cos(Units.degreesToRadians(UserPolicy.getInstance().getTargetAngle())) * -mag) + input.getXSpeed());
                        processedInput.setYSpeed((Math.sin(Units.degreesToRadians(UserPolicy.getInstance().getTargetAngle())) * -mag) + input.getYSpeed());
                    }
                }
            }
        }

        return processedInput;
    }
}
