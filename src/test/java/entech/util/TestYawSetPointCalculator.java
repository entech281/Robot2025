package entech.util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import frc.entech.util.YawSetPointCalculator;

class TestYawSetPointCalculator {

    @Test
    void testCalculator() {
        YawSetPointCalculator yc = new YawSetPointCalculator(20, 90.0, 60.0);
        assertEquals(90.0, yc.get(15));  // outside range ==> capped
        assertEquals(90.0, yc.get(20));  // starting limit
        assertEquals(75.0, yc.get(210)); // intermediate value
        assertFalse(yc.isReturningFinal());
        assertEquals(75.0, yc.get(150)); // don't allow smaller target
        assertEquals(60.0, yc.get(400)); // reach desired yaw at 400 pixel width
        assertTrue(yc.isReturningFinal());
        assertEquals(60.0, yc.get(640)); // past upper limit ==> capped
    }
}
