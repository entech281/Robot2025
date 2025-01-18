package frc.robot.processors.filters;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.util.Units;
import frc.robot.operation.UserPolicy;
import frc.robot.subsystems.drive.DriveInput;

public class LateralAlignFilter implements DriveFilterI {
    private final PIDController controller = new PIDController(0.0075, 0, 0.0);

    @Override
    public DriveInput process(DriveInput input) {
        DriveInput processedInput = new DriveInput(input);
        
        if (UserPolicy.getInstance().isLaterallyAligning() && !UserPolicy.getInstance().isTwistable()) {
            processedInput = operatorDirectionalSnap(processedInput, UserPolicy.getInstance().getTargetAngle());
            processedInput = motionTowardsAlignment(
                processedInput,
                controller.calculate(0, UserPolicy.getInstance().getVisionPositionSetPoint()),
                UserPolicy.getInstance().getTargetAngle()
            );
        }

        return processedInput;
    }

    public static DriveInput motionTowardsAlignment(DriveInput input, double magnitude, double goalAngle) {
        DriveInput processedInput = new DriveInput(input);

        double angleRadians = Units.degreesToRadians(goalAngle + 90);
        processedInput.setXSpeed(Math.sin(angleRadians) * magnitude);
        processedInput.setYSpeed(Math.cos(angleRadians) * magnitude);

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
        double xMax = Math.abs(Math.sin(angleRadians));
        double yMax = Math.abs(Math.cos(angleRadians));

        double xPlain = Math.min(xMax, Math.abs(input.getXSpeed()));
        double yPlain = Math.min(yMax, Math.abs(input.getYSpeed()));

        double xMargin = xPlain / xMax;
        double yMargin = yPlain / yMax;

        if (xMargin < yMargin) {
            yPlain = yMax * xMargin;
        } else if (yMargin < xMargin) {
            xPlain = xMax * yMargin;
        }

        if (xMax > yMax) {
            correctInput.setXSpeed(Math.copySign(xPlain, input.getXSpeed()));
            correctInput.setYSpeed(Math.copySign(yPlain, input.getXSpeed()));
        } else {
            correctInput.setXSpeed(Math.copySign(xPlain, input.getYSpeed()));
            correctInput.setYSpeed(Math.copySign(yPlain, input.getYSpeed()));
        }

        return correctInput;
    }
}
