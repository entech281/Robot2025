package frc.robot.commands;

import java.util.Optional;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DriverStation;
import frc.entech.commands.EntechCommand;
import frc.robot.RobotConstants;
import frc.robot.io.RobotIO;
import frc.robot.operation.UserPolicy;
import frc.robot.processors.DriveInputProcessor;
import frc.robot.subsystems.drive.DriveInput;
import frc.robot.subsystems.drive.DriveSubsystem;
import frc.robot.subsystems.drive.SwerveUtils;
import frc.robot.subsystems.vision.VisionTarget;

public class AutoAlignToScoringLocationCommand extends EntechCommand {
    private static final double SPEED = 0.35;
    private static final double LATERAL_START_ANGLE = 22.5;
    private static final double STOPPING_DISTANCE = 0.8;
    private final DriveSubsystem drive;
    private final int tagID;
    private final DriveInputProcessor inputProcessor;
    public static final double TOLERANCE = 0.1;
    private static final double START_DISTANCE = 8.0;

    public AutoAlignToScoringLocationCommand(DriveSubsystem drive, int tagID) {
        super(drive);

        this.drive = drive;
        this.tagID = tagID;
        this.inputProcessor = new DriveInputProcessor();
    }

    @Override
    public void initialize() {
        Optional<VisionTarget> target = RobotIO.getInstance().getVisionOutput().getBestTarget();
        if (RobotIO.getInstance().getVisionOutput().hasTarget() && target.isPresent()) {
            UserPolicy.getInstance().setAligningToAngle(true);
            UserPolicy.getInstance().setTargetAngle(findTargetAngle(target.get().getTagID()));
            DriverStation.reportWarning("" + SwerveUtils.angleDifference(RobotIO.getInstance().getOdometryPose().getRotation().getRadians(), Units.degreesToRadians(findTargetAngle(target.get().getTagID()))), false);
            UserPolicy.getInstance().setLaterallyAligning(SwerveUtils.angleDifference(RobotIO.getInstance().getOdometryPose().getRotation().getRadians(), Units.degreesToRadians(findTargetAngle(target.get().getTagID()))) < LATERAL_START_ANGLE);
        }
        UserPolicy.getInstance().setVisionPositionSetPoint(0);
    }
    
    @Override
    public void execute() {
        Optional<VisionTarget> target = RobotIO.getInstance().getVisionOutput().getBestTarget();
        if (RobotIO.getInstance().getVisionOutput().hasTarget() && target.isPresent()) {
            UserPolicy.getInstance().setAligningToAngle(true);
            UserPolicy.getInstance().setTargetAngle(findTargetAngle(target.get().getTagID()));
            DriverStation.reportWarning("" + SwerveUtils.angleDifference(RobotIO.getInstance().getOdometryPose().getRotation().getRadians(), Units.degreesToRadians(findTargetAngle(target.get().getTagID()))), false);
            UserPolicy.getInstance().setLaterallyAligning(SwerveUtils.angleDifference(RobotIO.getInstance().getOdometryPose().getRotation().getRadians(), Units.degreesToRadians(findTargetAngle(target.get().getTagID()))) < LATERAL_START_ANGLE);
            double angle = Units.degreesToRadians(findTargetAngle(target.get().getTagID()));

            DriveInput input = inputProcessor.processInput(RobotIO.getInstance().getDriveInput());
        
        if (RobotIO.getInstance().getVisionOutput().hasTarget() && RobotIO.getInstance().getVisionOutput().getTargets().get(0).getDistance() > STOPPING_DISTANCE) {
            double ratio = MathUtil.clamp(RobotIO.getInstance().getVisionOutput().getTargets().get(0).getDistance() / START_DISTANCE, 0.0, 1.0);
            input.setXSpeed((Math.cos(angle) * SPEED * ratio) + input.getXSpeed());
            input.setYSpeed((Math.sin(angle) * SPEED * ratio) + input.getYSpeed());
        }

        drive.updateInputs(input);
        }
    }

    @Override
    public boolean isFinished() {
        return RobotIO.getInstance().getVisionOutput().hasTarget() &&
        (RobotIO.getInstance().getVisionOutput().getTargets().get(0).getDistance() <= STOPPING_DISTANCE) &&
        (Math.abs(RobotIO.getInstance().getVisionOutput().getTargets().get(0).getTagXW() - UserPolicy.getInstance().getVisionPositionSetPoint()) >= TOLERANCE);
    }

    @Override
    public void end(boolean interrupted) {
        UserPolicy.getInstance().setAligningToAngle(false);
        UserPolicy.getInstance().setLaterallyAligning(false);
        drive.updateInputs(RobotIO.getInstance().getDriveInput());
    }

    private double findTargetAngle(int tagID) {
        if (RobotConstants.APRIL_TAG_DATA.TAG_ANGLES.containsKey(tagID)) {
            return RobotConstants.APRIL_TAG_DATA.TAG_ANGLES.get(tagID);
        } else {
            return UserPolicy.getInstance().getTargetAngle();
        }
    }
}
