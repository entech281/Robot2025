package frc.robot.processors.filters;

import edu.wpi.first.math.util.Units;
import frc.robot.io.RobotIO;
import frc.robot.operation.UserPolicy;
import frc.robot.subsystems.drive.DriveInput;

public class SlowDownAproachFilter implements DriveFilterI {
    private static final double MAX_SPEED = 0.075;
    private static final double SAFETY_DISTANCE = 3.25;

    @Override
    public DriveInput process(DriveInput input) {
        DriveInput processedInput = new DriveInput(input);

        if (UserPolicy.getInstance().isLaterallyAligning()) {
            double angleRadians = Units.degreesToRadians(UserPolicy.getInstance().getTargetAngle());
            
            double inputAngle = Math.atan2(input.getYSpeed(), input.getXSpeed());

            if (Math.abs(angleRadians - inputAngle) <= Math.PI/3) {
                if (RobotIO.getInstance().getVisionOutput().getDistance() <= SAFETY_DISTANCE) {
                    double inputMag = Math.sqrt(Math.pow(input.getXSpeed(), 2) + Math.pow(input.getYSpeed(), 2));

                    if (inputMag > MAX_SPEED) {
                        processedInput.setXSpeed(Math.cos(angleRadians) * MAX_SPEED);
                        processedInput.setYSpeed(Math.sin(angleRadians) * MAX_SPEED);
                    }
                }
            }
        }
        return processedInput;
    }
    
}
