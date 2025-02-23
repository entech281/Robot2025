// package frc.robot.commandCheker;

// import static org.junit.jupiter.api.Assertions.*;

// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;

// import frc.robot.RobotConstants;
// import frc.robot.subsystems.elevator.ElevatorInput;
// import frc.robot.subsystems.pivot.PivotInput;

// public class TestSafeMovePlanner {

//   private SafeMovePlanner planner;
//   private SafeCommandChecker checker;
//   // NOTE: Use a tolerance for double comparisons
//   private static final double TOLERANCE = 0.001;

//   @BeforeEach
//   public void setup() {
//     planner = new SafeMovePlanner();
//     checker = new SafeCommandChecker();
    
    
//   }
  
//   /**
//    * Test that when the current to target path is entirely within safe zone 1,
//    * the planner returns a direct move (with no subdivision).
//    */
//   @Test
//   public void testPlanMovesAllSafeDirect() {
//     // Safe zone 1: Elevator [10,50] and pivot [0,30] (per RobotConstants)
//     double currentElevator = 15.0;
//     double currentPivot = 5.0;
//     double targetElevator = 45.0;
//     double targetPivot = 25.0;
//     Map<ElevatorInput.Position, Double> elePosMap = new HashMap<>();
//     Map<PivotInput.Position, Double> pivPosMap = new HashMap<>();
//     for (ElevatorInput.Position pos : ElevatorInput.Position.values()) {
//       elePosMap.put(pos, RobotConstants.LiveTuning.VALUES.get(pos.label));
//     }
//     for (PivotInput.Position pos : PivotInput.Position.values()) {
//       pivPosMap.put(pos, RobotConstants.LiveTuning.VALUES.get(pos.label));
//     }

//     List<Move> moves = planner.planMoves(currentElevator, currentPivot, targetElevator, targetPivot, elePosMap, pivPosMap);
//     // Expect one direct move because the entire path is safe.
//     assertEquals(1, moves.size(), "Direct move expected when path is safe.");
    
//     Move move = moves.get(0);
//     assertEquals(targetElevator, move.getTargetElevator(), TOLERANCE);
//     assertEquals(targetPivot, move.getTargetPivot(), TOLERANCE);
//     // And the direct move should be considered safe.
//     assertTrue(checker.isSafe(move), "Direct move should be safe per checker.");
//   }
  
//   /**
//    * Test that moves requiring clamping (target exceeds safe elevator max in zone1)
//    * are subdivided and the final move is clamped to a value strictly less than the safe max.
//    */
//   @Test
//   public void testPlanMovesClampingElevator() {
//     double currentElevator = 15.0;
//     double currentPivot = 10.0;
//     double targetElevator = 100.0; // exceeds safe zone 1 (max 50)
//     double targetPivot = 20.0;     // pivot remains within safe [0,30]
    
//     boolean hasErrored = false;
//     Map<ElevatorInput.Position, Double> elePosMap = new HashMap<>();
//     Map<PivotInput.Position, Double> pivPosMap = new HashMap<>();
//     for (ElevatorInput.Position pos : ElevatorInput.Position.values()) {
//       elePosMap.put(pos, RobotConstants.LiveTuning.VALUES.get(pos.label));
//     }
//     for (PivotInput.Position pos : PivotInput.Position.values()) {
//       pivPosMap.put(pos, RobotConstants.LiveTuning.VALUES.get(pos.label));
//     }
//     try {
//       List<Move> moves = planner.planMoves(currentElevator, currentPivot, targetElevator, targetPivot, elePosMap, pivPosMap);
//     } catch (IllegalArgumentException e) {
//       hasErrored = true;
//     }
    
//     assertTrue(hasErrored);
//   }
  
//   /**
//    * Test that moves requiring clamping for both elevator and pivot (for safe zone 2)
//    * are subdivided and adjusted so that no segment exceeds the zone boundaries.
//    */
//   @Test
//   public void testPlanMovesClampingElevatorAndPivot() {
//     // Reference safe zone 2: Elevator [20,70] and Pivot [31,60]
//     double currentElevator = 25.0;
//     double currentPivot = 35.0;
//     double targetElevator = 100.0; // above safe zone 2 max (70)
//     double targetPivot = 100.0;    // above safe zone 2 max (60)
//     Map<ElevatorInput.Position, Double> elePosMap = new HashMap<>();
//     Map<PivotInput.Position, Double> pivPosMap = new HashMap<>();
//     for (ElevatorInput.Position pos : ElevatorInput.Position.values()) {
//       elePosMap.put(pos, RobotConstants.LiveTuning.VALUES.get(pos.label));
//     }
//     for (PivotInput.Position pos : PivotInput.Position.values()) {
//       pivPosMap.put(pos, RobotConstants.LiveTuning.VALUES.get(pos.label));
//     }
    
//     assertThrows(IllegalArgumentException.class, () -> planner.planMoves(currentElevator, currentPivot, targetElevator, targetPivot, elePosMap, pivPosMap));
    
//   }

//   /**
//    * Test that when current and target positions are identical, the planner returns moves that 
//    * simply reproduce the same position.
//    */
//   @Test
//   public void testNoMovement() {
//     double currentElevator = 30.0;
//     double currentPivot = 40.0;
//     double targetElevator = 30.0;
//     double targetPivot = 40.0;
//     Map<ElevatorInput.Position, Double> elePosMap = new HashMap<>();
//     Map<PivotInput.Position, Double> pivPosMap = new HashMap<>();
//     for (ElevatorInput.Position pos : ElevatorInput.Position.values()) {
//       elePosMap.put(pos, RobotConstants.LiveTuning.VALUES.get(pos.label));
//     }
//     for (PivotInput.Position pos : PivotInput.Position.values()) {
//       pivPosMap.put(pos, RobotConstants.LiveTuning.VALUES.get(pos.label));
//     }
//     List<Move> moves = planner.planMoves(currentElevator, currentPivot, targetElevator, targetPivot, elePosMap, pivPosMap);
//     // Even if no movement is required, our planning should return at least one move.
//     assertTrue(!moves.isEmpty(), "Even no movement should yield at least one move.");
    
//     // All moves must equal the current/target state.
//     for (Move m : moves) {
//       assertEquals(30.0, m.getTargetElevator(), TOLERANCE, "Elevator should equal 30.0");
//       assertEquals(40.0, m.getTargetPivot(), TOLERANCE, "Pivot should equal 40.0");
//     }
//   }
  
//   /**
//    * Test that every adjacent pair of moves in the planned path can be traversed safely in a direct move.
//    * This ensures that the planning recursion produces segments that are individually safe.
//    */
//   @Test
//   public void testEachSegmentDirectlySafe() {
//     double currentElevator = 15.0;
//     double currentPivot = 5.0;
//     double targetElevator = 55.0; // deliberately choose a target that will require subdivision
//     double targetPivot = 35.0;
//     Map<ElevatorInput.Position, Double> elePosMap = new HashMap<>();
//     Map<PivotInput.Position, Double> pivPosMap = new HashMap<>();
//     for (ElevatorInput.Position pos : ElevatorInput.Position.values()) {
//       elePosMap.put(pos, RobotConstants.LiveTuning.VALUES.get(pos.label));
//     }
//     for (PivotInput.Position pos : PivotInput.Position.values()) {
//       pivPosMap.put(pos, RobotConstants.LiveTuning.VALUES.get(pos.label));
//     }
//     List<Move> moves = planner.planMoves(currentElevator, currentPivot, targetElevator, targetPivot, elePosMap, pivPosMap);
//     // For each adjacent pair, sample some intermediate points and verify safety.
//     int samples = 10;
//     for (int i = 0; i < moves.size() - 1; i++) {
//       Move m1 = moves.get(i);
//       Move m2 = moves.get(i+1);
//       for (int s = 0; s <= samples; s++) {
//         double fraction = s / (double) samples;
//         double interElevator = m1.getTargetElevator() + fraction * (m2.getTargetElevator() - m1.getTargetElevator());
//         double interPivot = m1.getTargetPivot() + fraction * (m2.getTargetPivot() - m1.getTargetPivot());
//         Move intermediate = new Move(interElevator, interPivot);
//         assertTrue(checker.isSafe(intermediate),
//             "Intermediate move between segment " + i + " and " + (i+1) + " is unsafe: elevator=" + interElevator + ", pivot=" + interPivot);
//       }
//     }
//   }
  
//   /**
//    * Test the conversion of moves to known position names.
//    * This verifies that for each planned Move, a closest known position (as defined in PivotInput.Position)
//    * is selected.
//    */
//   @Test
//   public void testConvertMovesToKnownPositions() {
//     // Use a target that will require subdivision.
//     double currentElevator = 20.0;
//     double currentPivot = 10.0;
//     double targetElevator = 35.0;
//     double targetPivot = 25.0;
//     Map<ElevatorInput.Position, Double> elePosMap = new HashMap<>();
//     Map<PivotInput.Position, Double> pivPosMap = new HashMap<>();
//     for (ElevatorInput.Position pos : ElevatorInput.Position.values()) {
//       elePosMap.put(pos, RobotConstants.LiveTuning.VALUES.get(pos.label));
//     }
//     for (PivotInput.Position pos : PivotInput.Position.values()) {
//       pivPosMap.put(pos, RobotConstants.LiveTuning.VALUES.get(pos.label));
//     }
//     List<String> knownPosNames = new ArrayList<>();
//     try {
//      knownPosNames = planner.planMovesNames(currentElevator, currentPivot, targetElevator, targetPivot, elePosMap, pivPosMap);
//     } catch (Exception e) {
//       e.printStackTrace();
//     }
//     // Verify that the conversion produces a non-empty list and that each name corresponds
//     // to a valid pivot position label.
//     assertNotNull(knownPosNames, "Conversion to known positions should not be null.");
//     assertFalse(knownPosNames.isEmpty(), "There should be at least one known position conversion.");
//     for (String pos : knownPosNames) {
//       // Assuming PivotInput.Position enum contains the label.
//       boolean found = false;
//       for (PivotInput.Position p : PivotInput.Position.values()) {
//         if (p.label.equals(pos)) {
//           found = true;
//           break;
//         }
//       }
//       assertTrue(found, "Converted position '" + pos + "' is not a valid known position label.");
//     }
//   }
// }