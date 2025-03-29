package frc.robot.util;

import edu.wpi.first.math.geometry.Translation2d;
import frc.robot.subsystems.drive.RobotToFieldConverter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestRobotToFieldConverter {

    private static final double DELTA = 1e-6; // Tolerance for floating-point comparisons

    @Test
    void testZeroHeading() {
        // Robot-relative inputs
        double xSpeed = 1.0; // Forward
        double ySpeed = 0.0; // No lateral movement
        double heading = 0.0; // Robot is facing forward (0 degrees)

        // Convert to field-relative
        Translation2d result = RobotToFieldConverter.toFieldRelative(xSpeed, ySpeed, heading);

        // Assert field-relative outputs
        assertEquals(1.0, result.getX(), DELTA); // Should remain forward
        assertEquals(0.0, result.getY(), DELTA); // No lateral movement
    }

    @Test
    void testNinetyDegreeHeading() {
        // Robot-relative inputs
        double xSpeed = 1.0; // Forward
        double ySpeed = 0.0; // No lateral movement
        double heading = 90.0; // Robot is facing right (90 degrees)

        // Convert to field-relative
        Translation2d result = RobotToFieldConverter.toFieldRelative(xSpeed, ySpeed, heading);

        // Assert field-relative outputs
        assertEquals(0.0, result.getX(), DELTA); // Forward becomes lateral
        assertEquals(1.0, result.getY(), DELTA); // Forward becomes right
    }

    @Test
    void testOneEightyDegreeHeading() {
        // Robot-relative inputs
        double xSpeed = 1.0; // Forward
        double ySpeed = 0.0; // No lateral movement
        double heading = 180.0; // Robot is facing backward (180 degrees)

        // Convert to field-relative
        Translation2d result = RobotToFieldConverter.toFieldRelative(xSpeed, ySpeed, heading);

        // Assert field-relative outputs
        assertEquals(-1.0, result.getX(), DELTA); // Forward becomes backward
        assertEquals(0.0, result.getY(), DELTA); // No lateral movement
    }

    @Test
    void testTwoSeventyDegreeHeading() {
        // Robot-relative inputs
        double xSpeed = 1.0; // Forward
        double ySpeed = 0.0; // No lateral movement
        double heading = 270.0; // Robot is facing left (270 degrees)

        // Convert to field-relative
        Translation2d result = RobotToFieldConverter.toFieldRelative(xSpeed, ySpeed, heading);

        // Assert field-relative outputs
        assertEquals(0.0, result.getX(), DELTA); // Forward becomes lateral
        assertEquals(-1.0, result.getY(), DELTA); // Forward becomes left
    }

    @Test
    void testDiagonalMovement() {
        // Robot-relative inputs
        double xSpeed = 1.0; // Forward
        double ySpeed = 1.0; // Right
        double heading = 45.0; // Robot is facing 45 degrees

        // Convert to field-relative
        Translation2d result = RobotToFieldConverter.toFieldRelative(xSpeed, ySpeed, heading);

        // Assert field-relative outputs
        assertEquals(0.0, result.getX(), DELTA); // Forward + right aligns with field forward
        assertEquals(Math.sqrt(2), result.getY(), DELTA); // Diagonal movement
    }

    @Test
    void testNegativeHeading() {
        // Robot-relative inputs
        double xSpeed = 1.0; // Forward
        double ySpeed = 0.0; // No lateral movement
        double heading = -90.0; // Robot is facing left (-90 degrees)

        // Convert to field-relative
        Translation2d result = RobotToFieldConverter.toFieldRelative(xSpeed, ySpeed, heading);

        // Assert field-relative outputs
        assertEquals(0.0, result.getX(), DELTA); // Forward becomes lateral
        assertEquals(-1.0, result.getY(), DELTA); // Forward becomes left
    }

    @Test
    void testNoMovementNinetyDegrees() {
        double xSpeed = 0.0;
        double ySpeed = 0.0;
        double heading = 90.0;

        assertEquals(0.0, RobotToFieldConverter.toFieldRelative(xSpeed, ySpeed, heading).getX(), DELTA);
        assertEquals(0.0, RobotToFieldConverter.toFieldRelative(xSpeed, ySpeed, heading).getY(), DELTA);
    }

    @Test
    void testNoMovementZeroDegrees() {
        double xSpeed = 0.0;
        double ySpeed = 0.0;
        double heading = 0.0;

        assertEquals(0.0, RobotToFieldConverter.toFieldRelative(xSpeed, ySpeed, heading).getX(), DELTA);
        assertEquals(0.0, RobotToFieldConverter.toFieldRelative(xSpeed, ySpeed, heading).getY(), DELTA);
    }
}