package frc.robot.processors.filters;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.util.Units;
import frc.robot.io.RobotIO;
import frc.robot.operation.UserPolicy;
import frc.robot.subsystems.drive.DriveInput;

public class CollisionDampeningFilter implements DriveFilterI {
    private static final double START_DISTANCE = 8.0;
    private static final double MIN_SPEED = 0.0;

    @Override
    public DriveInput process(DriveInput input) {
        DriveInput processedInput = new DriveInput(input);

        if (UserPolicy.getInstance().isLaterallyAligning()) {
            double inputAngle = Math.atan2(input.getYSpeed(), input.getXSpeed());

            double inputMag = Math.sqrt(Math.pow(input.getXSpeed(), 2) + Math.pow(input.getYSpeed(), 2));
            if (Math.abs(Units.degreesToRadians(UserPolicy.getInstance().getTargetAngle()) - inputAngle) < 2) {
                double ratio = MathUtil.clamp(RobotIO.getInstance().getVisionOutput().getDistance() / START_DISTANCE, 0.0, 1.0);

                double outputMag = ratio * inputMag;

                if (outputMag < MIN_SPEED) {
                    outputMag = MIN_SPEED;
                }

                processedInput.setXSpeed(Math.cos(inputAngle) * outputMag);
                processedInput.setYSpeed(Math.sin(inputAngle) * outputMag);
            }    
        }
        return processedInput;
    }
    
}
