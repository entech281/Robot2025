package frc.robot.processors.filters;

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.util.WPIUtilJNI;
import frc.robot.Position;
import frc.robot.RobotConstants;
import frc.robot.io.RobotIO;
import frc.robot.livetuning.LiveTuningHandler;
import frc.robot.subsystems.drive.DriveInput;
import frc.robot.subsystems.drive.SwerveUtils;

public class AccelerationFilter implements DriveFilterI {
    private double currentTranslationDir = 0.0;
    private double currentTranslationMag = 0.0;
    private double prevTime = WPIUtilJNI.now() * 1e-6;
    private final SlewRateLimiter magLimiter = new SlewRateLimiter(RobotConstants.AccelerationFilter.MAGNITUDE_SLEW_RATE);
    private final SlewRateLimiter rotLimiter = new SlewRateLimiter(RobotConstants.AccelerationFilter.ROTATIONAL_SLEW_RATE);
    @Override
    public DriveInput process(DriveInput input) {
        DriveInput processedInput = new DriveInput(input);

        boolean intakeRunning = (!RobotIO.getInstance().getInternalCoralDetectorOutput().hasCoral()) && RobotIO.getInstance().getCoralMechanismOutput().isRunning();
        boolean elevatorHigh = RobotIO.getInstance().getElevatorOutput().getCurrentPosition() > LiveTuningHandler.getInstance().getValue(Position.L2.getElevatorKey());
        if (intakeRunning || elevatorHigh) {
            double[] limitedInputs =
                calculateSlewRateLimiting(input.getXSpeed(), input.getYSpeed(), input.getRotation());

            processedInput.setXSpeed(limitedInputs[0]);
            processedInput.setYSpeed(limitedInputs[1]);
            processedInput.setRotation(limitedInputs[2]);
        }

        return processedInput;
    }
    

    /**
     * @param xSpeed
     * @param ySpeed
     * @param rotSpeed
     * @return X Y Rotation
     */
    private double[] calculateSlewRateLimiting(double xSpeed, double ySpeed, double rotSpeed) {
        // Convert XY to polar for rate limiting
        double inputTranslationDir = Math.atan2(ySpeed, xSpeed);
        double inputTranslationMag = Math.sqrt(Math.pow(xSpeed, 2) + Math.pow(ySpeed, 2));

        // Calculate the direction slew rate based on an estimate of the lateral
        // acceleration
        double directionSlewRate;

        if (currentTranslationMag != 0.0) {
            directionSlewRate = Math.abs(RobotConstants.AccelerationFilter.DIRECTION_SLEW_RATE / currentTranslationMag);
        } else {
            directionSlewRate = 500.0; // some high number that means the slew rate is effectively
                                    // instantaneous
        }

        double currentTime = WPIUtilJNI.now() * 1e-6;
        double elapsedTime = currentTime - prevTime;
        double angleDif = SwerveUtils.angleDifference(inputTranslationDir, currentTranslationDir);

        if (angleDif < 0.45 * Math.PI) {
            currentTranslationDir = SwerveUtils.stepTowardsCircular(currentTranslationDir,
                inputTranslationDir, directionSlewRate * elapsedTime);
            currentTranslationMag = magLimiter.calculate(inputTranslationMag);
        } else if (angleDif > 0.85 * Math.PI) {
            if (currentTranslationMag > 1e-4) {
                currentTranslationMag = magLimiter.calculate(0.0);
            } else {
                currentTranslationDir = SwerveUtils.wrapAngle(currentTranslationDir + Math.PI);
                currentTranslationMag = magLimiter.calculate(inputTranslationMag);
            }
        } else {
            currentTranslationDir = SwerveUtils.stepTowardsCircular(currentTranslationDir,
                inputTranslationDir, directionSlewRate * elapsedTime);
            currentTranslationMag = magLimiter.calculate(0.0);
        }

        prevTime = currentTime;

        return new double[] {currentTranslationMag * Math.cos(currentTranslationDir),
            currentTranslationMag * Math.sin(currentTranslationDir), rotLimiter.calculate(rotSpeed)};
    }
}
