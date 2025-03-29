package frc.robot.processors.filters;

import frc.robot.io.RobotIO;
import frc.robot.subsystems.drive.DriveInput;

public class SpeedConstraintFilter implements DriveFilterI {
        public static final double MAX_SPEED = 1;
        public static final double MIN_SPEED_HEIGHT = 20.3;
        private double speedLimit;
        private double position;

        public SpeedConstraintFilter(double speedLimit, double position) {
            this.speedLimit = speedLimit;
            this.position = position;
        }

        public double map(double value) {
            return (value - this.position) * (this.speedLimit - MAX_SPEED) / (MIN_SPEED_HEIGHT - this.position) + MAX_SPEED;
        }

        @Override
        public DriveInput process(DriveInput input) {

            double currentElevatorPosition = RobotIO.getInstance().getElevatorOutput().getCurrentPosition();
            boolean intakeHasCoral = RobotIO.getInstance().getInternalCoralDetectorOutput().hasCoral();
            boolean intakeRunning = RobotIO.getInstance().getCoralMechanismOutput().isRunning();
            
            if (currentElevatorPosition >= position || (!intakeHasCoral && intakeRunning)) {
                return getConstrainedInput(input);
            }
            else {
                return input;
            }
        }

        private DriveInput getConstrainedInput(DriveInput input) {
            DriveInput processedInput = new DriveInput(input);

                processedInput.setXSpeed(map(input.getXSpeed()));
                processedInput.setYSpeed(map(input.getYSpeed()));

                return processedInput;
        }
}
