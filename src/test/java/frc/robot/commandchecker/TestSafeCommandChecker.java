package frc.robot.commandchecker;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestSafeCommandChecker {

  private SafeCommandChecker checker;
  private SafeMovementChecker moveChecker;

  @BeforeEach
  void setup() {
    checker = new SafeCommandChecker();
    moveChecker = new SafeMovementChecker();
  }

  @Test
  void testBadPos() {
    Move badMove = new Move(0, 0);
    assertFalse(checker.isSafe(badMove));
  }

  @Test
  void testGoodPos() {
    assertTrue(moveChecker.isSafeElevatorMove(21, 39));
  }
}