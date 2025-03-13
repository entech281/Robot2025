package frc.robot.processors.filters;

import java.util.Optional;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.io.RobotIO;
import frc.robot.operation.UserPolicy;
import frc.robot.subsystems.drive.DriveInput;

public class LateralAlignFilter implements DriveFilterI {
    private final PIDController controller = new PIDController(0.075, 0.0, 0.0);
    public static final double TOLERANCE = 0.1;

    public LateralAlignFilter() {
        controller.setTolerance(TOLERANCE);
    }

    @Override
    public DriveInput process(DriveInput input) {
        DriveInput processedInput = new DriveInput(input);
        
        if (UserPolicy.getInstance().isLaterallyAligning() && !UserPolicy.getInstance().isTwistable()) {
            processedInput = operatorDirectionalSnap(processedInput, UserPolicy.getInstance().getTargetAngle());

            if (RobotIO.getInstance().getVisionOutput().hasTarget() && (Math.abs(RobotIO.getInstance().getVisionOutput().getTargets().get(0).getTagXW() - UserPolicy.getInstance().getVisionPositionSetPoint()) >= TOLERANCE)) {
                processedInput = motionTowardsAlignment(
                    processedInput,
                    controller.calculate(RobotIO.getInstance().getVisionOutput().getTargets().get(0).getTagXW(), UserPolicy.getInstance().getVisionPositionSetPoint()),
                    UserPolicy.getInstance().getTargetAngle()
                );
            }
        }

        return processedInput;
    }

    public static DriveInput motionTowardsAlignment(DriveInput input, double magnitude, double goalAngle) {
        DriveInput processedInput = new DriveInput(input);

        double mag = MathUtil.clamp(magnitude, -1, 1);

        double angleRadians;
        Optional<DriverStation.Alliance> team = DriverStation.getAlliance();
        if (team.isPresent() && team.get() == DriverStation.Alliance.Red) {
            angleRadians = Units.degreesToRadians(goalAngle - 90);
        } else {
            angleRadians = Units.degreesToRadians(goalAngle + 90);
        }
        processedInput.setXSpeed(input.getXSpeed() + Math.cos(angleRadians) * mag);
        processedInput.setYSpeed(input.getYSpeed() + Math.sin(angleRadians) * mag);

        return processedInput;
    }

    /**
     * Removes motion side to side relative to target angle.
     * 
     * @param input
     * @param goalAngle
     * @return Input with direction side to side relative to target angle removed
     */
    public static DriveInput operatorDirectionalSnap(DriveInput input, double goalAngle) {
        DriveInput correctInput = new DriveInput(input);

        double angleRadians = Units.degreesToRadians(goalAngle);
        
        double inputAngle = Math.atan2(input.getYSpeed(), input.getXSpeed());

        double inputMag = Math.sqrt(Math.pow(input.getXSpeed(), 2) + Math.pow(input.getYSpeed(), 2));

        double outputMag = inputMag * Math.cos(angleRadians - inputAngle);

        correctInput.setXSpeed(Math.cos(angleRadians) * outputMag);
        correctInput.setYSpeed(Math.sin(angleRadians) * outputMag);

        return correctInput;
    }
}
