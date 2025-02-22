package frc.robot.subsystems.vision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class TestVisionTarget {
    @Test
    void TestIO() {
        VisionTarget target = new VisionTarget();

        target.setCameraName("a");
        assertEquals("a", target.getCameraName());
        target.setTagID(1);
        assertEquals(1, target.getTagID());
        target.setTagWidth(2);
        assertEquals(2, target.getTagWidth());
        target.setTagHeight(3);
        assertEquals(3, target.getTagHeight());
        target.setTagX(4);
        assertEquals(4, target.getTagX());
        target.setTagXW(5);
        assertEquals(5, target.getTagXW());
        target.setTagY(6);
        assertEquals(6, target.getTagY());
        target.setTimestamp(7);
        assertEquals(7, target.getTimestamp());
        target.setDistance(8);
        assertEquals(8, target.getDistance());
    }
}
