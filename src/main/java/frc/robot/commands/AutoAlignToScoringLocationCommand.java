package frc.robot.commands;

import edu.wpi.first.math.util.Units;
import frc.entech.commands.EntechCommand;
import frc.robot.RobotConstants;
import frc.robot.io.RobotIO;
import frc.robot.operation.UserPolicy;
import frc.robot.processors.DriveInputProcessor;
import frc.robot.subsystems.drive.DriveInput;
import frc.robot.subsystems.drive.DriveSubsystem;

public class AutoAlignToScoringLocationCommand extends EntechCommand {
    private static final double SPEED = 0.25;
    private static final double LATERAL_START_ANGLE = 22.5;
    private static final double STOPPING_DISTANCE = 0.8;
    private final DriveSubsystem drive;
    private final int tagID;
    private final DriveInputProcessor inputProcessor;

    public AutoAlignToScoringLocationCommand(DriveSubsystem drive, int tagID) {
        super(drive);

        this.drive = drive;
        this.tagID = tagID;
        this.inputProcessor = new DriveInputProcessor();
    }

    @Override
    public void initialize() {
        UserPolicy.getInstance().setTargetAngle(findTargetAngle(tagID));
        UserPolicy.getInstance().setVisionPositionSetPoint(0);
    }
    
    @Override
    public void execute() {
        UserPolicy.getInstance().setAligningToAngle(true);
        UserPolicy.getInstance().setTargetAngle(findTargetAngle(RobotIO.getInstance().getVisionOutput().getTagID()));

        if (Math.abs(RobotIO.getInstance().getOdometryPose().getRotation().getDegrees()) - (findTargetAngle(RobotIO.getInstance().getVisionOutput().getTagID() - 180)) < LATERAL_START_ANGLE) {
            UserPolicy.getInstance().setLaterallyAligning(true);
        } else {
            UserPolicy.getInstance().setLaterallyAligning(false);
        }

        double angle = Units.degreesToRadians(findTargetAngle(tagID));

        DriveInput input = new DriveInput(RobotIO.getInstance().getDriveInput());
        
        input.setXSpeed(Math.cos(angle) * SPEED);
        input.setYSpeed(Math.sin(angle) * SPEED);

        drive.updateInputs(inputProcessor.processInput(input));
    }

    @Override
    public boolean isFinished() {
        return RobotIO.getInstance().getVisionOutput().getDistance() <= STOPPING_DISTANCE;
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
