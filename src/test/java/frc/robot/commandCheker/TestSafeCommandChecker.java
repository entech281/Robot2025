// package frc.robot.commandCheker;

// import static org.junit.jupiter.api.Assertions.*;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;

// public class TestSafeCommandChecker {

//   private SafeCommandChecker checker;

//   @BeforeEach
//   public void setup() {
//     checker = new SafeCommandChecker();
//   }

//   /**
//    * Tests that a move safely inside the first safe zone returns true.
//    * First safe zone: elevator (10, 50) and pivot (0, 30).
//    */
//   @Test
//   public void testMoveSafeInZone1() {
//     Move safeMove = new Move(20, 15); // inside (10,50) and (0,30)
//     assertTrue(checker.isSafe(safeMove), "Move with elevator=20, pivot=15 should be safe in zone 1.");
//   }

//   /**
//    * Tests that a move safely inside the second safe zone returns true.
//    * Second safe zone: elevator (20, 70) and pivot (31, 60).
//    */
//   @Test
//   public void testMoveSafeInZone2() {
//     Move safeMove = new Move(30, 40); // inside (20,70) and (31,60)
//     assertTrue(checker.isSafe(safeMove), "Move with elevator=30, pivot=40 should be safe in zone 2.");
//   }

//   /**
//    * Tests that a move safely inside the third safe zone returns true.
//    * Third safe zone: elevator (30, 90) and pivot (61, 90).
//    */
//   @Test
//   public void testMoveSafeInZone3() {
//     Move safeMove = new Move(50, 70); // inside (30,90) and (61,90)
//     assertTrue(checker.isSafe(safeMove), "Move with elevator=50, pivot=70 should be safe in zone 3.");
//   }

//   /**
//    * Tests that a move exactly on the lower boundary of a zone is unsafe given the exclusive comparisons.
//    */
//   @Test
//   public void testMoveOnLowerBoundaryUnsafe() {
//     // For first zone, elevator=10 is the lower boundary.
//     Move boundaryMove = new Move(10, 15); 
//     assertFalse(checker.isSafe(boundaryMove), "Move with elevator=10, pivot=15 should be considered unsafe.");
//   }

//   /**
//    * Tests that a move exactly on the upper boundary of a zone is unsafe.
//    */
//   @Test
//   public void testMoveOnUpperBoundaryUnsafe() {
//     // For first safe zone, elevator=50 is the upper boundary.
//     Move boundaryMove = new Move(20, 30); 
//     assertFalse(checker.isSafe(boundaryMove), "Move with elevator=20, pivot=30 should be considered unsafe.");
//   }

//   /**
//    * Tests that a move completely outside any safe zone returns false.
//    */
//   @Test
//   public void testMoveOutsideSafeZones() {
//     // Elevator and pivot values that do not fall into any safe zone.
//     Move unsafeMove = new Move(5, 100);
//     assertFalse(checker.isSafe(unsafeMove), "Move with elevator=5, pivot=100 should be unsafe.");
//   }
// }