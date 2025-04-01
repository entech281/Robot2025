package frc.robot.commands;

import java.util.Optional;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import frc.entech.commands.EntechCommand;
import frc.robot.RobotConstants;
import frc.robot.io.RobotIO;
import frc.robot.livetuning.LiveTuningHandler;
import frc.robot.operation.UserPolicy;
import frc.robot.processors.filters.AutoYawFilter;
import frc.robot.processors.filters.LateralAlignFilter;
import frc.robot.subsystems.drive.DriveInput;
import frc.robot.subsystems.drive.DriveSubsystem;
import frc.robot.subsystems.drive.SwerveUtils;
import frc.robot.subsystems.vision.VisionTarget;

public class AutoAlignToScoringLocationCommand extends EntechCommand {
    private static final double LATERAL_START_ANGLE = 22.5;
    private final DriveSubsystem drive;
    private final int tagID;
    public static final double TOLERANCE = 0.12;
    public static final double DIST_TOLERANCE = 0.05;

    private final LateralAlignFilter lateralFilter = new LateralAlignFilter();
    private final AutoYawFilter yawFilter = new AutoYawFilter();

    private final PIDController moveController = new PIDController(
        LiveTuningHandler.getInstance().getValue("AutoAlign/StopP"),
        LiveTuningHandler.getInstance().getValue("AutoAlign/StopI"),
        LiveTuningHandler.getInstance().getValue("AutoAlign/StopD")
    );

    public AutoAlignToScoringLocationCommand(DriveSubsystem drive, int tagID) {
        super(drive);

        this.drive = drive;
        this.tagID = tagID;
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
                        double ratio = MathUtil.clamp(-moveController.calculate(t.getDistance(), LiveTuningHandler.getInstance().getValue("AutoAlign/Stop")), -1.0, 1.0);
                        Optional<Alliance> alliance = DriverStation.getAlliance();
                        if (alliance.isPresent() && alliance.get() == Alliance.Blue) {
                            input.setXSpeed((ratio * Math.cos(Units.degreesToRadians(UserPolicy.getInstance().getTargetAngle())) * LiveTuningHandler.getInstance().getValue("AutoAlign/AutoSpeed")) + input.getXSpeed());
                            input.setYSpeed((Math.sin(Units.degreesToRadians(UserPolicy.getInstance().getTargetAngle())) * LiveTuningHandler.getInstance().getValue("AutoAlign/AutoSpeed") * ratio) + input.getYSpeed());
                        } else {
                            input.setXSpeed((ratio * Math.cos(Units.degreesToRadians(UserPolicy.getInstance().getTargetAngle())) * -LiveTuningHandler.getInstance().getValue("AutoAlign/AutoSpeed")) + input.getXSpeed());
                            input.setYSpeed((Math.sin(Units.degreesToRadians(UserPolicy.getInstance().getTargetAngle())) * -LiveTuningHandler.getInstance().getValue("AutoAlign/AutoSpeed") * ratio) + input.getYSpeed());
                        }
                    }
                }
            } else {
                UserPolicy.getInstance().setLaterallyAligning(false);
            }
        } else {
            if (RobotIO.getInstance().getVisionOutput().hasTarget()) {
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
                    return(Math.abs(t.getDistance() - LiveTuningHandler.getInstance().getValue("AutoAlign/AutoStop")) <= DIST_TOLERANCE) && (Math.abs(t.getTagXW() - UserPolicy.getInstance().getVisionPositionSetPoint()) <= TOLERANCE);
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
