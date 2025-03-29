package frc.robot.processors.filters;

import frc.robot.io.RobotIO;
import frc.robot.subsystems.drive.DriveInput;

public class SpeedConstraintFilter implements DriveFilterI {
        public static final double MAX_SPEED = 1.0;
        public static final double MIN_SPEED_HEIGHT = 4.4;
        private double speedLimit;
        private double position;

        public SpeedConstraintFilter(double speedLimit, double position) {
            this.speedLimit = speedLimit;
            this.position = position;
        }


        //public  double  map(double value) {
        //  return (value - this.position) * (MAX_SPEED - this.speedLimit) / (this.position - MIN_SPEED_HEIGHT) + this.speedLimit;
        //}

      public  double  map(double currentElevatorPosition, double inputSpeed) {
          return inputSpeed * ((this.position- currentElevatorPosition)/this.position )* ( MAX_SPEED - this.speedLimit) + this.speedLimit;
      }

        @Override
        public DriveInput process(DriveInput input) {

            double currentElevatorPosition = RobotIO.getInstance().getElevatorOutput().getCurrentPosition();
            boolean intakeHasCoral = RobotIO.getInstance().getInternalCoralDetectorOutput().hasCoral();
            boolean intakeRunning = RobotIO.getInstance().getCoralMechanismOutput().isRunning();
            
            if (currentElevatorPosition >= position || (!intakeHasCoral && intakeRunning)) {
                return getConstrainedInput(input,currentElevatorPosition);
            }
            else {
                return input;
            }
        }

        private DriveInput getConstrainedInput(DriveInput input, double currentElevatorPosition) {
            DriveInput processedInput = new DriveInput(input);

                processedInput.setXSpeed(map(currentElevatorPosition,input.getXSpeed()));
                processedInput.setYSpeed(map(currentElevatorPosition,input.getYSpeed()));

                return processedInput;
        }
}
