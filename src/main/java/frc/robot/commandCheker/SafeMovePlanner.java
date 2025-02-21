package frc.robot.commandCheker;

import java.util.ArrayList;
import java.util.List;
import frc.robot.RobotConstants;

/**
 * SafeMovePlanner computes a series of safe moves required to transition
 * from the current mechanism position (elevator height and pivot angle) to a
 * target position. The planner interpolates intermediate moves and adjusts any
 * moves that do not conform to the safe-zone constraints (as defined in RobotConstants)
 * so that every planned move is safe.
 * <p>
 * In this version, if an interpolated candidate move is unsafe, the candidate is
 * adjusted (by clamping) to the safe boundaries defined in the corresponding SafeZone.
 * Both elevator height and pivot angle are adjusted based on the safe zone boundaries.
 * A small epsilon is subtracted from upper bounds to meet strict safety criteria.
 * </p>
 */
public class SafeMovePlanner {

  private static final int NUM_STEPS = 10;
  private static final double EPSILON = 0.001;
  private final SafeCommandChecker checker;

  /**
   * Constructs a new SafeMovePlanner.
   */
  public SafeMovePlanner() {
    this.checker = new SafeCommandChecker();
  }

  /**
   * Computes a list of safe moves from the current position to the target position.
   *
   * @param currentElevator the current elevator height
   * @param currentPivot    the current pivot angle
   * @param targetElevator  the desired target elevator height
   * @param targetPivot     the desired target pivot angle
   * @return a List of {@link Move} objects representing intermediate safe moves
   */
  public List<Move> planMoves(double currentElevator, double currentPivot,
                              double targetElevator, double targetPivot) {
    List<Move> moves = new ArrayList<>();

    // Compute incremental step sizes for elevator and pivot.
    double elevatorStep = (targetElevator - currentElevator) / NUM_STEPS;
    double pivotStep = (targetPivot - currentPivot) / NUM_STEPS;

    // Generate candidate moves via linear interpolation.
    for (int i = 1; i <= NUM_STEPS; i++) {
      double candidateElevator = currentElevator + i * elevatorStep;
      double candidatePivot = currentPivot + i * pivotStep;
      Move candidate = new Move(candidateElevator, candidatePivot);

      // If the candidate move is not safe, adjust it.
      if (!checker.isSafe(candidate)) {
        // Determine an appropriate safe zone using the candidate pivot.
        SafeZone zone = getSafeZoneForPivot(candidatePivot);
        if (zone != null) {
          // Clamp the candidate values to the safe zone boundaries.
          double safeElevator = clamp(candidateElevator, zone.getElevatorStart(), zone.getElevatorEnd());
          double safePivot = clamp(candidatePivot, zone.getPivotStart(), zone.getPivotEnd());
          candidate = new Move(safeElevator, safePivot);
        }
      }
      moves.add(candidate);
    }

    return moves;
  }

  /**
   * Clamps a value between a minimum and maximum such that the returned value is strictly less than max.
   *
   * @param value the value to clamp
   * @param min   the minimum value to allow
   * @param max   the maximum value to allow (exclusive)
   * @return value if between min and (max - epsilon), or min if value is less than min,
   *         or (max - epsilon) if value is greater than or equal to max.
   */
  private double clamp(double value, double min, double max) {
    double clamped = Math.max(min, Math.min(value, max));
    if (clamped >= max) {
      clamped = max - EPSILON;
    }
    return clamped;
  }

  /**
   * Searches for a safe zone based on the provided pivot angle.
   * The safe zones are defined in {@link RobotConstants.SafeZones}.
   * A candidate pivot angle that falls between the zone's pivot start and pivot end is used.
   *
   * @param pivot the pivot angle to test
   * @return the {@link SafeZone} that best contains the given pivot value, or null if none match.
   */
  private SafeZone getSafeZoneForPivot(double pivot) {
    for (SafeZone zone : RobotConstants.SafeZones.safeZones) {
      if (pivot >= zone.getPivotStart() && pivot <= zone.getPivotEnd()) {
        return zone;
      }
    }
    return null;
  }
}
