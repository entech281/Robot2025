package frc.robot.commands;

import java.util.Optional;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.util.Units;
import frc.entech.commands.EntechCommand;
import frc.robot.RobotConstants;
import frc.robot.io.RobotIO;
import frc.robot.operation.UserPolicy;
import frc.robot.processors.DriveInputProcessor;
import frc.robot.processors.filters.AutoYawFilter;
import frc.robot.processors.filters.LateralAlignFilter;
import frc.robot.subsystems.drive.DriveInput;
import frc.robot.subsystems.drive.DriveSubsystem;
import frc.robot.subsystems.drive.SwerveUtils;
import frc.robot.subsystems.vision.VisionTarget;

public class AutoAlignToScoringLocationCommand extends EntechCommand {
    private static final double SPEED = 0.5;
    private static final double LATERAL_START_ANGLE = 22.5;
    private static final double STOPPING_DISTANCE = 0.8;
    private final DriveSubsystem drive;
    private final int tagID;
    private final DriveInputProcessor inputProcessor;
    public static final double TOLERANCE = 0.1;
    private static final double START_DISTANCE = 8.0;

    private final LateralAlignFilter lateralFilter = new LateralAlignFilter();
    private final AutoYawFilter yawFilter = new AutoYawFilter();

    public AutoAlignToScoringLocationCommand(DriveSubsystem drive, int tagID) {
        super(drive);

        this.drive = drive;
        this.tagID = tagID;
        this.inputProcessor = new DriveInputProcessor();
    }

    @Override
    public void initialize() {
        UserPolicy.getInstance().setVisionPositionSetPoint(0);
    }
    
    @Override
    public void execute() {
        DriveInput input = RobotIO.getInstance().getDriveInput();
        if (UserPolicy.getInstance().isAligningToAngle()) {
            if (RobotIO.getInstance().getVisionOutput().hasTarget()) {
                for (VisionTarget t : RobotIO.getInstance().getVisionOutput().getTargets()) {
                    if (t.getTagID() == tagID && SwerveUtils.angleDifference(RobotIO.getInstance().getOdometryPose().getRotation().getRadians(), Units.degreesToRadians(UserPolicy.getInstance().getTargetAngle())) < LATERAL_START_ANGLE) {
                        UserPolicy.getInstance().setLaterallyAligning(true);
                        if (t.getDistance() > STOPPING_DISTANCE) {
                            // double ratio = -1.0;
                            double ratio = -MathUtil.clamp(t.getDistance() / START_DISTANCE, 0.0, 1.0);
                            input.setXSpeed((ratio * Math.cos(UserPolicy.getInstance().getTargetAngle()) * SPEED) + input.getXSpeed());
                            input.setYSpeed((Math.sin(UserPolicy.getInstance().getTargetAngle()) * SPEED * ratio) + input.getYSpeed());
                        }
                    }
                }
            } else {
                UserPolicy.getInstance().setLaterallyAligning(false);
            }
        } else {
            Optional<VisionTarget> target = RobotIO.getInstance().getVisionOutput().getBestTarget();
            if (RobotIO.getInstance().getVisionOutput().hasTarget() && target.isPresent()) {
                UserPolicy.getInstance().setAligningToAngle(true);
                UserPolicy.getInstance().setTargetAngle(findTargetAngle(tagID));
                UserPolicy.getInstance().setTargetTagID(tagID);
            }
        }
        input = lateralFilter.process(input);
        input = yawFilter.process(input);
        drive.updateInputs(input);
    }

    @Override
    public boolean isFinished() {
        if (RobotIO.getInstance().getVisionOutput().hasTarget()) {
            for (VisionTarget t : RobotIO.getInstance().getVisionOutput().getTargets()) {
                if (t.getTagID() == tagID) {
                    return(t.getDistance() <= STOPPING_DISTANCE) && (Math.abs(t.getTagXW() - UserPolicy.getInstance().getVisionPositionSetPoint()) >= TOLERANCE);
                }
            }
        }
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        UserPolicy.getInstance().setAligningToAngle(false);
        UserPolicy.getInstance().setLaterallyAligning(false);
        drive.updateInputs(RobotIO.getInstance().getDriveInput());
    }

    private double findTargetAngle(int tagID) {
        if (RobotConstants.APRIL_TAG_DATA.TAG_ANGLES.containsKey(tagID)) {
            return RobotConstants.APRIL_TAG_DATA.TAG_ANGLES.get(tagID) - 180;
        } else {
            return 0;
        }
    }
}
