package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.robot.io.RobotIO;
import frc.robot.livetuning.LiveTuningHandler;
import frc.robot.subsystems.elevator.ElevatorInput;
import frc.robot.subsystems.elevator.ElevatorSubsystem;

public class ElevatorMoveCommand extends EntechCommand {
  /** Creates a new PivotCommand. */
  private final ElevatorInput pivotInput = new ElevatorInput();
  private final ElevatorSubsystem elevatorSS;
  private final ElevatorInput.Position position;

  public ElevatorMoveCommand(ElevatorSubsystem elevatorSubsystem, ElevatorInput.Position position) {
    super(elevatorSubsystem);
    elevatorSS = elevatorSubsystem;
    this.position = position;
  }

  @Override
  public void initialize() {
    pivotInput.setRequestedPosition(LiveTuningHandler.getInstance().getValue(position.label));
  }

  @Override
  public void execute() {
    elevatorSS.updateInputs(pivotInput);
  }

  @Override
  public boolean isFinished() {
    return RobotIO.getInstance().getPivotOutput().atRequestedPosition();
  }
}