package frc.robot.commands;

import java.util.Optional;

import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DriverStation;
import frc.entech.commands.EntechCommand;
import frc.robot.RobotConstants;
import frc.robot.io.RobotIO;
import frc.robot.operation.UserPolicy;
import frc.robot.subsystems.drive.SwerveUtils;
import frc.robot.subsystems.vision.VisionInput;
import frc.robot.subsystems.vision.VisionInput.Camera;
import frc.robot.subsystems.vision.VisionSubsystem;
import frc.robot.subsystems.vision.VisionTarget;

public class TeleFullAutoAlign extends EntechCommand {
    private static final double LATERAL_START_ANGLE = Units.degreesToRadians(22.5);
    public final VisionSubsystem vision;
    public final VisionInput input = new VisionInput();
    public final Camera camera;

    public TeleFullAutoAlign(VisionSubsystem vision, Camera camera) {
        super(vision);
        this.vision = vision;
        this.camera = camera;
        input.setCamera(camera);
    }

    @Override
    public void initialize() {
        UserPolicy.getInstance().setVisionPositionSetPoint(0);
        vision.updateInputs(input);
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
            Optional<VisionTarget> target = RobotIO.getInstance().getVisionOutput().getBestTarget();
            if (RobotIO.getInstance().getVisionOutput().hasTarget() && target.isPresent()) {
                UserPolicy.getInstance().setAligningToAngle(true);
                UserPolicy.getInstance().setTargetAngle(findTargetAngle(target.get().getTagID()));
                UserPolicy.getInstance().setTargetTagID(target.get().getTagID());
            }
        }
        vision.updateInputs(input);
    }

    private double findTargetAngle(int tagID) {
        if (RobotConstants.APRIL_TAG_DATA.TAG_ANGLES.containsKey(tagID)) {
            if (DriverStation.getAlliance().isPresent() && DriverStation.getAlliance().get() == DriverStation.Alliance.Blue) {
                return RobotConstants.APRIL_TAG_DATA.TAG_ANGLES.get(tagID) - 180;
            } else {
                return RobotConstants.APRIL_TAG_DATA.TAG_ANGLES.get(tagID);
            }
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
