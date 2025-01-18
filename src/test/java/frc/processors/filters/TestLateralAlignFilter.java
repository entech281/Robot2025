package frc.processors.filters;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import frc.robot.processors.filters.LateralAlignFilter;
import frc.robot.subsystems.drive.DriveInput;

class TestLateralAlignFilter {
    @Test
    void testOperatorDirectionalSnap() {
        DriveInput input = new DriveInput();
        input.setXSpeed(0);
        input.setYSpeed(0);

        DriveInput output = LateralAlignFilter.operatorDirectionalSnap(input, 0);

        assertEquals(0, output.getYSpeed(), 0.001);
        assertEquals(0, output.getXSpeed(), 0.001);
    }

    @Test
    void testMotionTowardsAlignment() {
        DriveInput input = new DriveInput();
        input.setXSpeed(0);
        input.setYSpeed(0);

        DriveInput output = LateralAlignFilter.motionTowardsAlignment(input, 0, 0);

        assertEquals(0, output.getYSpeed(), 0.001);
        assertEquals(0, output.getXSpeed(), 0.001);
    }
}
