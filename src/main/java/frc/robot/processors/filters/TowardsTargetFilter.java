package frc.robot.processors.filters;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import frc.robot.io.RobotIO;
import frc.robot.livetuning.LiveTuningHandler;
import frc.robot.operation.UserPolicy;
import frc.robot.subsystems.drive.DriveInput;
import frc.robot.subsystems.vision.VisionTarget;

public class TowardsTargetFilter implements DriveFilterI {
    @Override
    public DriveInput process(DriveInput input) {
        DriveInput processedInput = new DriveInput(input);

        if (RobotIO.getInstance().getVisionOutput().hasTarget() && UserPolicy.getInstance().isTowardsAlignment()) {
            for (VisionTarget t : RobotIO.getInstance().getVisionOutput().getTargets()) {
                if (t.getTagID() == UserPolicy.getInstance().getTargetTagID()) {
                    if (t.getDistance() > LiveTuningHandler.getInstance().getValue("AutoAlign/Stop")) {
                        double ratio = MathUtil.clamp(t.getDistance() / LiveTuningHandler.getInstance().getValue("AutoAlign/Start"), 0.0, 1.0);
                        if (DriverStation.getAlliance().isPresent() && DriverStation.getAlliance().get() == Alliance.Blue) {
                            processedInput.setXSpeed((ratio * Math.cos(Units.degreesToRadians(UserPolicy.getInstance().getTargetAngle())) * LiveTuningHandler.getInstance().getValue("AutoAlign/Speed")) + input.getXSpeed());
                            processedInput.setYSpeed((Math.sin(Units.degreesToRadians(UserPolicy.getInstance().getTargetAngle())) * LiveTuningHandler.getInstance().getValue("AutoAlign/Speed") * ratio) + input.getYSpeed());
                        } else {
                            processedInput.setXSpeed((ratio * Math.cos(Units.degreesToRadians(UserPolicy.getInstance().getTargetAngle())) * -LiveTuningHandler.getInstance().getValue("AutoAlign/Speed")) + input.getXSpeed());
                            processedInput.setYSpeed((Math.sin(Units.degreesToRadians(UserPolicy.getInstance().getTargetAngle())) * -LiveTuningHandler.getInstance().getValue("AutoAlign/Speed") * ratio) + input.getYSpeed());
                        }
                    }
                }
            }
        }

        return processedInput;
    }
}
