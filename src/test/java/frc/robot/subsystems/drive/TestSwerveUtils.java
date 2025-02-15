package frc.robot.subsystems.drive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class TestSwerveUtils {
    private static final double TOLLERANCE = 0.0001;
    @Test
    void TestWrapAngle() {
        assertEquals(Math.PI, SwerveUtils.wrapAngle(Math.PI * 3), TOLLERANCE);
        assertEquals(Math.PI, SwerveUtils.wrapAngle(-Math.PI * 3), TOLLERANCE);
        assertEquals(Math.PI, SwerveUtils.wrapAngle(-Math.PI), TOLLERANCE);
        assertEquals(Math.PI, SwerveUtils.wrapAngle(Math.PI), TOLLERANCE);
    }

    @Test
    void TestAngleDifference() {
        assertEquals(Math.PI, SwerveUtils.angleDifference(Math.PI, Math.PI * 2));
        assertEquals(0, SwerveUtils.angleDifference(Math.PI, Math.PI * 3));
        assertEquals(0, SwerveUtils.angleDifference(Math.PI, Math.PI));
    }
}
