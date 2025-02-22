package frc.robot.subsystems.elevator;

import org.littletonrobotics.junction.LogTable;

import frc.entech.subsystems.SubsystemInput;
import frc.robot.RobotConstants;

public class ElevatorInput implements SubsystemInput {
  private boolean activate = true;
  private double requestedPosition = RobotConstants.ELEVATOR.INITIAL_POSITION;

  @Override
  public void toLog(LogTable table) {
    table.put("Activate", activate);
    table.put("Requested position", requestedPosition);
  }

  @Override
  public void fromLog(LogTable table) {
    activate = table.get("Activate", activate);
    requestedPosition = table.get("Requested position", 0.0);
  }

  public boolean getActivate() {
    return this.activate;
  }

  public void setActivate(boolean activate) {
    this.activate = activate;
  }

  public double getRequestedPosition() {
    return this.requestedPosition;
  }

  public void setRequestedPosition(double requestedPosition) {
    this.requestedPosition = requestedPosition;
  }

  public enum Position {
    HOME("ElevatorSubsystem/home"),
    L1("ElevatorSubsystem/L1"),
    L2("ElevatorSubsystem/L2"),
    L3("ElevatorSubsystem/L3"),
    L4("ElevatorSubsystem/L4"),
    ALGAE_L2("ElevatorSubsystem/algae_L2"),
    ALGAE_L3("ElevatorSubsystem/algae_L3"),
    ALGAE_GROUND("ElevatorSubsystem/algae_ground"),
    BARGE("ElevatorSubsystem/barge");

    public final String label;

    private Position(String label) {
        this.label = label;
    }
  }
}