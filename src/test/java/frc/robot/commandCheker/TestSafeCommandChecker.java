package frc.robot.commandCheker;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestSafeCommandChecker {

  private SafeCommandChecker checker;
  private SafeMovementChecker moveChecker;

  @BeforeEach
  public void setup() {
    checker = new SafeCommandChecker();
    moveChecker = new SafeMovementChecker();
  }

  @Test
  public void testBadPos() {
    Move badMove = new Move(0, 0);
    assertFalse(checker.isSafe(badMove));
  }

  @Test
  public void testGoodPos() {
    assertTrue(moveChecker.isSafeElevatorMove(21, 39));
  }
}