package frc.robot.commands;

import java.util.List;
import java.util.Optional;

import edu.wpi.first.math.util.Units;
import frc.entech.commands.EntechCommand;
import frc.robot.RobotConstants;
import frc.robot.io.RobotIO;
import frc.robot.operation.UserPolicy;
import frc.robot.subsystems.drive.SwerveUtils;
import frc.robot.subsystems.vision.VisionTarget;

public class TeleFullAutoAlign extends EntechCommand {
    private static final double LATERAL_START_ANGLE = Units.degreesToRadians(22.5);

    @Override
    public void initialize() {
        UserPolicy.getInstance().setVisionPositionSetPoint(0);
    }

    @Override
    public void execute() {
        if (UserPolicy.getInstance().isAligningToAngle()) {
            Optional<VisionTarget> target = RobotIO.getInstance().getVisionOutput().getBestTarget();
            if (RobotIO.getInstance().getVisionOutput().hasTarget() && target.isPresent()) {
                UserPolicy.getInstance().setLaterallyAligning(SwerveUtils.angleDifference(RobotIO.getInstance().getOdometryPose().getRotation().getRadians(), Units.degreesToRadians(findTargetAngle(target.get().getTagID()))) < LATERAL_START_ANGLE);
                UserPolicy.getInstance().setTowardsAlignment(SwerveUtils.angleDifference(RobotIO.getInstance().getOdometryPose().getRotation().getRadians(), Units.degreesToRadians(findTargetAngle(target.get().getTagID()))) < LATERAL_START_ANGLE);
            } else {
                UserPolicy.getInstance().setLaterallyAligning(false);
                UserPolicy.getInstance().setTowardsAlignment(false);
            }
        } else {
            List<VisionTarget> foundTargets = RobotIO.getInstance().getVisionOutput().findSpecificTarget(UserPolicy.getInstance().getSelectedTargetLocations());
            if (!foundTargets.isEmpty()) {
                UserPolicy.getInstance().setAligningToAngle(true);
                UserPolicy.getInstance().setTargetAngle(findTargetAngle(foundTargets.get(0).getTagID()));
                UserPolicy.getInstance().setTargetTagID(foundTargets.get(0).getTagID());
            }
        }
    }

    private double findTargetAngle(int tagID) {
        if (RobotConstants.APRIL_TAG_DATA.TAG_ANGLES.containsKey(tagID)) {
            return RobotConstants.APRIL_TAG_DATA.TAG_ANGLES.get(tagID) - 180;
        } else {
            return 0;
        }
    }

    @Override
    public void end(boolean interrupted) {
        UserPolicy.getInstance().setAligningToAngle(false);
        UserPolicy.getInstance().setLaterallyAligning(false);
        UserPolicy.getInstance().setTowardsAlignment(false);
    }

    @Override
    public boolean runsWhenDisabled() {
        return false;
    }
}
