package frc.robot.processors.filters;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.util.Units;
import frc.robot.io.RobotIO;
import frc.robot.operation.UserPolicy;
import frc.robot.subsystems.drive.DriveInput;
import frc.robot.subsystems.drive.SwerveUtils;
import frc.robot.subsystems.vision.VisionTarget;

public class TowardsTargetFilter implements DriveFilterI {
    private static final double STOPPING_DISTANCE = 1.05;
    private static final double START_DISTANCE = 8.0;
    private static final double SPEED = 0.5;
    private static final double LATERAL_START_ANGLE = 22.5;

    @Override
    public DriveInput process(DriveInput input) {
        DriveInput processedInput = new DriveInput(input);

        if (RobotIO.getInstance().getVisionOutput().hasTarget() && UserPolicy.getInstance().isTowardsAlignment()) {
            for (VisionTarget t : RobotIO.getInstance().getVisionOutput().getTargets()) {
                if (t.getTagID() == UserPolicy.getInstance().getTargetTagID() && SwerveUtils.angleDifference(RobotIO.getInstance().getOdometryPose().getRotation().getRadians(), Units.degreesToRadians(UserPolicy.getInstance().getTargetAngle())) < LATERAL_START_ANGLE) {
                    UserPolicy.getInstance().setLaterallyAligning(true);
                    if (t.getDistance() > STOPPING_DISTANCE) {
                        double ratio = MathUtil.clamp(t.getDistance() / START_DISTANCE, 0.0, 1.0);
                        input.setXSpeed((ratio * Math.cos(Units.degreesToRadians(UserPolicy.getInstance().getTargetAngle())) * SPEED) + input.getXSpeed());
                        input.setYSpeed((Math.sin(Units.degreesToRadians(UserPolicy.getInstance().getTargetAngle())) * SPEED * ratio) + input.getYSpeed());
                    }
                }
            }
        }

        return processedInput;
    }
}
