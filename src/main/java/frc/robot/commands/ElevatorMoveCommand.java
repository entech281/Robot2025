package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.entech.util.StoppingCounter;
import frc.robot.Position;
import frc.robot.io.RobotIO;
import frc.robot.livetuning.LiveTuningHandler;
import frc.robot.subsystems.elevator.ElevatorInput;
import frc.robot.subsystems.elevator.ElevatorSubsystem;

public class ElevatorMoveCommand extends EntechCommand {
  private final ElevatorInput elevatorInput = new ElevatorInput();
  private final StoppingCounter counter = new StoppingCounter(0.06);
  private final ElevatorSubsystem elevatorSS;
  private final Position position;

  public ElevatorMoveCommand(ElevatorSubsystem elevatorSubsystem, Position position) {
    super(elevatorSubsystem);
    elevatorSS = elevatorSubsystem;
    this.position = position;
  }

  @Override
  public void initialize() {
    elevatorInput.setRequestedPosition(LiveTuningHandler.getInstance().getValue(position.getElevatorKey()));
    counter.reset();
  }

  @Override
  public void execute() {
    elevatorSS.updateInputs(elevatorInput);
  }

  @Override
  public boolean isFinished() {
    return counter.isFinished(RobotIO.getInstance().getElevatorOutput().isAtRequestedPosition());
  }

  @Override
  public void end(boolean interrupted) {
    if (interrupted) {
      elevatorInput.setRequestedPosition(LiveTuningHandler.getInstance().getValue(Position.HOME.getElevatorKey()));
      elevatorSS.updateInputs(elevatorInput);
    }
  }
}