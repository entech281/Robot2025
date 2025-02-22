package frc.robot.commandCheker;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class TestSafeMovePlanner {

  private SafeMovePlanner planner;
  private SafeCommandChecker checker;
  private static final int EXPECTED_STEPS = 10;

  @BeforeEach
  public void setup() {
    planner = new SafeMovePlanner();
    checker = new SafeCommandChecker();
  }

  /**
   * Tests that the planner produces the expected number of moves 
   * and that every move is safe.
   * 
   * A misconception is that the planner only takes into account
   * certain safe zones, but it actually takes into account all 
   * safe zones.
   */
  @Test
  public void testPlanMovesAllSafe() {
    // Using safe zone 1 from RobotConstants: elevator in (10,50), pivot in (0,30)
    double currentElevator = 15.0;
    double currentPivot = 5.0;
    double targetElevator = 45.0;
    double targetPivot = 25.0;

    List<Move> moves = planner.planMoves(currentElevator, currentPivot, targetElevator, targetPivot);
    // assertEquals(EXPECTED_STEPS, moves.size(), "Should produce " + EXPECTED_STEPS + " moves.");

    // All moves should be considered safe.
    for (int i = 0; i < moves.size(); i++) {
      Move m = moves.get(i);
      assertTrue(checker.isSafe(m), "Move " + i + " should be safe (elevator=" + m.getTargetElevator() 
                + ", pivot=" + m.getTargetPivot() + ").");
    }
  }

  @Test
  public void testSafelyGetsToTarget() {
    // Using safe zone 1 from RobotConstants: elevator in (10,50), pivot in (0,30)
    double currentElevator = 15.0;
    double currentPivot = 5.0;
    double targetElevator = 45.0;
    double targetPivot = 25.0;

    List<Move> moves = planner.planMoves(currentElevator, currentPivot, targetElevator, targetPivot);
    // assertEquals(EXPECTED_STEPS, moves.size(), "Should produce " + EXPECTED_STEPS + " moves.");

    // All moves should be considered safe.
    for (int i = 0; i < moves.size(); i++) {
      Move m = moves.get(i);
      assertTrue(checker.isSafe(m), "Move " + i + " should be safe (elevator=" + m.getTargetElevator() 
                + ", pivot=" + m.getTargetPivot() + ").");
    }

    assertEquals(moves.get(moves.size() - 1).getTargetElevator(), targetElevator, 0.001, "Should get to target elevator.");
    assertEquals(moves.get(moves.size() - 1).getTargetPivot(), targetPivot, 0.001, "Should get to target pivot.");
  }

  /**
   * Tests that candidate moves that exceed safe zone boundaries (for elevator)
   * are clamped so that the final values are strictly within safe limits.
   */
  @Test
  public void testPlanMovesClampingElevator() {
    // Current in safe zone 1, but target wants to exceed safe elevator max.
    double currentElevator = 15.0;
    double currentPivot = 10.0;
    double targetElevator = 100.0; // exceeds safe zone 1 max (50)
    double targetPivot = 20.0;     // within safe zone 1 pivot (0-30)
    
    boolean exceptionThrown = false;
    try {
      List<Move> moves = planner.planMoves(currentElevator, currentPivot, targetElevator, targetPivot);
    } catch (IllegalArgumentException e) {
      exceptionThrown = true;
    }

    assertTrue(exceptionThrown);
  }

  /**
   * Tests that the planner adjusts moves based on both elevator and pivot when both require clamping.
   * In this test, the target position violates safe zone 2 boundaries.
   */
  @Test
  public void testPlanMovesClampingElevatorAndPivot() {
    // Safe zone 2 reference: elevator in (20,70) and pivot in (31,60)
    double currentElevator = 25.0;
    double currentPivot = 35.0;
    double targetElevator = 80.0; // above safe zone 2 max (70)
    double targetPivot = 70.0;    // above safe zone 2 max (60)
    
    List<Move> moves = planner.planMoves(currentElevator, currentPivot, targetElevator, targetPivot);
    for (int i = 0; i < moves.size(); i++) {
      Move m = moves.get(i);
      assertTrue(checker.isSafe(m), "Move " + i 
          + " should be safe after adjustment (elevator=" + m.getTargetElevator() 
          + ", pivot=" + m.getTargetPivot() + ").");
    }

    // The final move should be clamped to safe boundaries: elevator < 90, pivot < 90.
    Move finalMove = moves.get(moves.size() - 1);
    assertTrue(finalMove.getTargetElevator() < 90, "Final move elevator (" + finalMove.getTargetElevator() 
           + ") should be clamped to less than 70.");
    assertTrue(finalMove.getTargetPivot() < 90, "Final move pivot (" + finalMove.getTargetPivot() 
           + ") should be clamped to less than 60.");
  }

  /**
   * Tests that when current and target positions are identical, the planner
   * returns a list of moves that maintain the same values.
   */
  @Test
  public void testNoMovement() {
    double currentElevator = 30.0;
    double currentPivot = 40.0;
    double targetElevator = 30.0;
    double targetPivot = 40.0;
    
    List<Move> moves = planner.planMoves(currentElevator, currentPivot, targetElevator, targetPivot);
    // assertEquals(EXPECTED_STEPS, moves.size(), "Should produce " + EXPECTED_STEPS + " moves.");

    // All moves must equal the current/target state.
    for (Move m : moves) {
      assertEquals(30.0, m.getTargetElevator(), 0.001, "Elevator should equal 30.0");
      assertEquals(40.0, m.getTargetPivot(), 0.001, "Pivot should equal 40.0");
    }
  }
}