package entech.util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import frc.entech.util.StoppingCounter;

class TestStoppingCounter {

  @Test
  void testStopperStopsAllSuccess() {
    final double NUM_SECONDS = 0.08;

    StoppingCounter sc = new StoppingCounter(NUM_SECONDS);
    assertFalse(sc.isFinished(true));
    assertFalse(sc.isFinished(true));
    assertFalse(sc.isFinished(true));
    assertFalse(sc.isFinished(true));
    assertTrue(sc.isFinished(true));
  }

  @Test
  void testStopperResets() {
    final double NUM_SECONDS = 0.08;

    StoppingCounter sc = new StoppingCounter(NUM_SECONDS);
    assertFalse(sc.isFinished(true));
    assertFalse(sc.isFinished(true));
    assertFalse(sc.isFinished(true));
    assertFalse(sc.isFinished(false));
    assertFalse(sc.isFinished(true));
    assertFalse(sc.isFinished(true));
    assertFalse(sc.isFinished(true));
    assertFalse(sc.isFinished(true));
    assertTrue(sc.isFinished(true));
  }

}
