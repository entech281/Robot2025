package entech.util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import frc.entech.util.YawSetPointCalculator;

class TestYawSetPointCalculator {

    @Test
    void testCalculator() {
        YawSetPointCalculator yc = new YawSetPointCalculator(25, 90.0, 60.0);
        assertEquals(90.0, yc.get(15));  // outside range ==> capped
        assertEquals(90.0, yc.get(25));  // starting limit
        assertEquals(67.5, yc.get(75)); // intermediate value
        assertFalse(yc.isReturningFinal());
        assertEquals(67.5, yc.get(50)); // don't allow smaller target
        assertEquals(60.0, yc.get(125)); // reach desired yaw at 400 pixel width
        assertTrue(yc.isReturningFinal());
        assertEquals(60.0, yc.get(200)); // past upper limit ==> capped
    }
}
