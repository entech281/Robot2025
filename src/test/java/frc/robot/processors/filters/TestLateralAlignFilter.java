package frc.robot.processors.filters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import frc.robot.subsystems.drive.DriveInput;

class TestLateralAlignFilter {
    @Test
    void testOperatorDirectionalSnap() {
        DriveInput input = new DriveInput();
        input.setXSpeed(1.0);
        input.setYSpeed(0.0);

        DriveInput output = LateralAlignFilter.operatorDirectionalSnap(input, 0);


        assertEquals(0.0, output.getYSpeed(), 0.001);
        assertEquals(1.0, output.getXSpeed(), 0.001);

        input = new DriveInput();
        input.setXSpeed(0.0);
        input.setYSpeed(1.0);

        output = LateralAlignFilter.operatorDirectionalSnap(input, 0);

        assertEquals(0.0, output.getYSpeed(), 0.001);
        assertEquals(0.0, output.getXSpeed(), 0.001);

        input = new DriveInput();
        input.setXSpeed(0.0);
        input.setYSpeed(1.0);

        output = LateralAlignFilter.operatorDirectionalSnap(input, 90);

        assertEquals(1.0, output.getYSpeed(), 0.001);
        assertEquals(0.0, output.getXSpeed(), 0.001);

        input = new DriveInput();
        input.setXSpeed(1.0);
        input.setYSpeed(0.0);

        output = LateralAlignFilter.operatorDirectionalSnap(input, 90);

        assertEquals(0.0, output.getYSpeed(), 0.001);
        assertEquals(0.0, output.getXSpeed(), 0.001);
    }

    @Test
    void testMotionTowardsAlignment() {
        DriveInput input = new DriveInput();
        input.setXSpeed(1.0);
        input.setYSpeed(0.0);

        DriveInput output = LateralAlignFilter.motionTowardsAlignment(input, 0, 0);

        assertEquals(0.0, output.getYSpeed(), 0.001);
        assertEquals(1.0, output.getXSpeed(), 0.001);

        input = new DriveInput();
        input.setXSpeed(0.0);
        input.setYSpeed(1.0);

        output = LateralAlignFilter.motionTowardsAlignment(input, 0, 0);
        assertEquals(1.0, output.getYSpeed(), 0.001);
        assertEquals(0.0, output.getXSpeed(), 0.001);

        input = new DriveInput();
        input.setXSpeed(0.0);
        input.setYSpeed(0.0);

        output = LateralAlignFilter.motionTowardsAlignment(input, 1, 0);
        assertEquals(1.0, output.getYSpeed(), 0.001);
        assertEquals(0.0, output.getXSpeed(), 0.001);

        input = new DriveInput();
        input.setXSpeed(0.0);
        input.setYSpeed(1.0);

        output = LateralAlignFilter.motionTowardsAlignment(input, 1.0, -90);
        assertEquals(1.0, output.getYSpeed(), 0.001);
        assertEquals(1.0, output.getXSpeed(), 0.001);
    }
}
