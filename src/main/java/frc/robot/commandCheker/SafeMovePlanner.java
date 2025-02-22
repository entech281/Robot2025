package frc.robot.commandCheker;

import java.util.*;
import java.util.Map.Entry;

import frc.robot.RobotConstants;
import frc.robot.RobotConstants.LiveTuning;
import frc.robot.livetuning.LiveTuningHandler;
import frc.robot.subsystems.pivot.PivotInput;
import frc.robot.subsystems.elevator.ElevatorInput;

/**
 * SafeMovePlanner computes a series of safe moves required to transition
 * from the current mechanism position (elevator height and pivot angle) to a
 * target position. It uses recursive subdivision: if the direct path between
 * the current position and the target is unsafe, the path is split in half until
 * every segment (direct move between successive positions) is safe.
 */
public class SafeMovePlanner {

  private static final int MAX_RECURSION_DEPTH = 20;
  private static final double POSITION_TOLERANCE = 0.01;
  private static final double EPSILON = 0.001;
  private final SafeCommandChecker checker;

  public SafeMovePlanner() {
    this.checker = new SafeCommandChecker();
  }

  public List<Move> planMoves(double currentElevator, double currentPivot, double targetElevator, double targetPivot, Map<ElevatorInput.Position, Double> elevatorPos, Map<PivotInput.Position, Double> pivotPos) {
    return planMoves(currentElevator, currentPivot, new Move(targetElevator, targetPivot), elevatorPos, pivotPos);
  }

  /**
   * Plans a series of moves from the current mechanism position to the target move.
   * Each successive move is connected by a direct, safe path.
   *
   * @param currentElevator the current elevator height
   * @param currentPivot    the current pivot angle
   * @param command         the target move
   * @return a List of {@link Move} objects representing a safe path.
   */
  public List<Move> planMoves(double currentElevator, double currentPivot, Move command, Map<ElevatorInput.Position, Double> elevatorPos, Map<PivotInput.Position, Double> pivotPos) {
    if (!checker.isSafe(command)) {
      //since we have preset locations
      //this should never be thrown
      throw new IllegalStateException("Target move is unsafe.");
    }
    return planSegment(currentElevator, currentPivot, command, 100);
  }

  public List<String> planMovesNames(double currentElevator, double currentPivot, Move command, Map<ElevatorInput.Position, Double> elevatorPos, Map<PivotInput.Position, Double> pivotPos) {
    return convertMovesToKnownPositions(planMoves(currentElevator, currentPivot, command, elevatorPos, pivotPos), elevatorPos, pivotPos);
  }

  public List<String> planMovesNames(double currentElevator, double currentPivot, double targetElevator, double targetPivot, Map<ElevatorInput.Position, Double> elevatorPos, Map<PivotInput.Position, Double> pivotPos) {
    return planMovesNames(currentElevator, currentPivot, new Move(targetElevator, targetPivot), elevatorPos, pivotPos);
  }

  /**
   * Recursively plans a safe path from the current state to the target move.
   *
   * @param curElevator current elevator height
   * @param curPivot    current pivot angle
   * @param target      target move
   * @param depth       current recursion depth (to prevent infinite recursion)
   * @return a list of moves starting from just after the current state up to the target.
   */
  private List<Move> planSegment(double curElevator, double curPivot, Move target, int depth) {
    List<Move> moves = new ArrayList<>();

    // Base case: if the current position is essentially equal to the target.
    if (Math.abs(curElevator - target.getTargetElevator()) < POSITION_TOLERANCE && Math.abs(curPivot - target.getTargetPivot()) < POSITION_TOLERANCE) {
      moves.add(target);
      return moves;
    }

    // Check if the direct path is safe.
    if (canMoveDirect(curElevator, curPivot, target)) {
      moves.add(target);
      return moves;
    } 
    
    // Prevent runaway recursion.
    if (depth > MAX_RECURSION_DEPTH) {
      moves.add(target); // fallback, even if not safe.
      return moves;
    }
    
    // Otherwise, find the midpoint and plan segments on each side.
    double midElevator = (curElevator + target.getTargetElevator()) / 2.0;
    double midPivot = (curPivot + target.getTargetPivot()) / 2.0;
    Move midMove = new Move(midElevator, midPivot);
    
    // Recursively plan from current to midpoint.
    moves.addAll(planSegment(curElevator, curPivot, midMove, depth + 1));
    // Recursively plan from midpoint to target.
    moves.addAll(planSegment(midElevator, midPivot, target, depth + 1));
    
    return moves;
  }

  private List<String> getKnownPositions() {

    List<String> knownMoves = new ArrayList<String>();
    for (int i = 0; i < PivotInput.Position.values().length; i++) {
      knownMoves.add(PivotInput.Position.values()[i].label);
    }
    
    return knownMoves;
  }


  public List<String> convertMovesToKnownPositions (List<Move> moves, Map<ElevatorInput.Position, Double> elevatorPos, Map<PivotInput.Position, Double> pivotPos) {
    
    List<String> knownMoves = getKnownPositions();
    List<String> convertedMoves = new ArrayList<>();

    for (Move move : moves) {
      String closestPos = getClosestMove(knownMoves, move, elevatorPos, pivotPos);
      convertedMoves.add(closestPos);
    }
    
    return convertedMoves;
  }

  private String getClosestMove(List<String> str_moves, Move target, Map<ElevatorInput.Position, Double> elevatorPos, Map<PivotInput.Position, Double> pivotPos) {
    
    List<Move> moves = new ArrayList<Move>();
    for (String moveName : str_moves) {
      for (Entry<ElevatorInput.Position, Double> pos : elevatorPos.entrySet()) {
        if (pos.getKey().label.equals(moveName)) {
          for (Entry <PivotInput.Position, Double> pos2 : pivotPos.entrySet()) {
            if (pos2.getKey().label.equals(moveName)) {
              moves.add(new Move(pos.getValue(), pos2.getValue()));
            }
          }
        }
      }
    }

    Move closestMove = moves.get(0);
    
    double closestDistance = Double.MAX_VALUE;
    for (Move move : moves) {
      double distance = Math.sqrt(Math.pow(move.getTargetElevator() - target.getTargetElevator(), 2) + Math.pow(move.getTargetPivot() - target.getTargetPivot(), 2));
      if (distance < closestDistance) {
        closestMove = move;
        closestDistance = distance;
      }
    }
    
    return str_moves.get(moves.indexOf(closestMove));
  }
  

  /**
   * Determines whether moving directly from the current position to the requested move's target position
   * along a straight-line path would keep the mechanism within the safe zone at all times.
   *
   * @param currentElevator the current elevator height
   * @param currentPivot    the current pivot angle
   * @param command         the target move request (with desired elevator and pivot)
   * @return true if every intermediate position (sampled along the direct path) is safe; false otherwise.
   */
  private boolean canMoveDirect(double currentElevator, double currentPivot, Move command) {
    int samples = 20; // number of intermediate points to test along the path
    for (int i = 0; i <= samples; i++) {
      double fraction = i / (double) samples;
      double candidateElevator = currentElevator + fraction * (command.getTargetElevator() - currentElevator);
      double candidatePivot = currentPivot + fraction * (command.getTargetPivot() - currentPivot);
      Move intermediate = new Move(candidateElevator, candidatePivot);

      if (!checker.isSafe(intermediate)) {
        return false;
      }
    }
    return true;
  }
}
