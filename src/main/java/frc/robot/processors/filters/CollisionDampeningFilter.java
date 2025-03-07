package frc.robot.processors.filters;

import edu.wpi.first.math.MathUtil;
import frc.robot.RobotConstants;
import frc.robot.io.RobotIO;
import frc.robot.subsystems.drive.DriveInput;

public class CollisionDampeningFilter implements DriveFilterI {
    private static final double MIN_SPEED = 0.5;

    @Override
    public DriveInput process(DriveInput input) {
        DriveInput processedInput = new DriveInput(input);

        double inputAngle = Math.atan2(input.getYSpeed(), input.getXSpeed());

        double inputMag = Math.sqrt(Math.pow(input.getXSpeed(), 2) + Math.pow(input.getYSpeed(), 2));
        double ratio = MathUtil.clamp( 1 / (RobotIO.getInstance().getElevatorOutput().getCurrentPosition() / RobotConstants.ELEVATOR.UPPER_SOFT_LIMIT_DEG), 0.0, 1.0);

        double outputMag = ratio * inputMag;

        if (inputAngle > MIN_SPEED) {
            if (outputMag < MIN_SPEED) {
                outputMag = MIN_SPEED;
            }
        }

        processedInput.setXSpeed(Math.cos(inputAngle) * outputMag);
        processedInput.setYSpeed(Math.sin(inputAngle) * outputMag);
        return processedInput;
    }
    
}
