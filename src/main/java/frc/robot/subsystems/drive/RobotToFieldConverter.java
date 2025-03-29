package frc.robot.subsystems.drive;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import frc.robot.io.RobotIO;

public class RobotToFieldConverter {


    public static Translation2d toFieldRelative(double xSpeed, double ySpeed) {
        return toFieldRelative(xSpeed, ySpeed, RobotIO.getInstance().getNavXOutput().getYaw());
    }

    /**
     * Converts robot-relative inputs to field-relative inputs dynamically using the robot's current gyro heading.
     *
     * @param xSpeed Robot-relative x speed (forward/backward).
     * @param ySpeed Robot-relative y speed (left/right).
     * @return A Translation2d representing the field-relative x and y speeds.
     */
    public static Translation2d toFieldRelative(double xSpeed, double ySpeed, double heading) {

        // Convert the yaw to a Rotation2d object
        Rotation2d robotHeading = Rotation2d.fromDegrees(heading);

        // Create a Translation2d for the robot-relative inputs
        Translation2d robotRelative = new Translation2d(xSpeed, ySpeed);

        // Rotate the robot-relative inputs by the robot's heading to get field-relative inputs
        Translation2d fieldRelative = robotRelative.rotateBy(robotHeading);

        return fieldRelative;
    }
}
