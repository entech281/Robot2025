package frc.robot.processors.filters;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.util.Units;
import frc.robot.operation.UserPolicy;
import frc.robot.subsystems.drive.DriveInput;
import frc.robot.io.RobotIO;

public class LateralAlignFilter implements DriveFilterI {
    private final PIDController controller = new PIDController(0.27, 0, 0.0);

    public LateralAlignFilter() {
        controller.setTolerance(0.0075);
    }

    @Override
    public DriveInput process(DriveInput input) {
        DriveInput processedInput = new DriveInput(input);
        
        if (UserPolicy.getInstance().isLaterallyAligning() && !UserPolicy.getInstance().isTwistable()) {
            processedInput = operatorDirectionalSnap(processedInput, UserPolicy.getInstance().getTargetAngle());
            processedInput = motionTowardsAlignment(
                processedInput,
                controller.calculate(RobotIO.getInstance().getVisionOutput().getTagX(), UserPolicy.getInstance().getVisionPositionSetPoint()),
                UserPolicy.getInstance().getTargetAngle()
            );
        }

        return processedInput;
    }

    public static DriveInput motionTowardsAlignment(DriveInput input, double magnitude, double goalAngle) {
        DriveInput processedInput = new DriveInput(input);

        double angleRadians = Units.degreesToRadians(goalAngle + 90);
        processedInput.setXSpeed(input.getXSpeed() + Math.cos(angleRadians) * magnitude);
        processedInput.setYSpeed(input.getYSpeed() + Math.sin(angleRadians) * magnitude);

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
