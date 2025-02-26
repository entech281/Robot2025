package frc.robot.subsystems.elevator;

import frc.entech.commands.EntechCommand;
import frc.entech.util.StoppingCounter;
import frc.robot.RobotConstants;

public class TestElevatorCommand extends EntechCommand{
  private final ElevatorSubsystem elevator;
  private final StoppingCounter counter =
      new StoppingCounter(RobotConstants.TEST_CONSTANTS.STANDARD_TEST_LENGTH); 
  private int stage = 0;

   public TestElevatorCommand(ElevatorSubsystem elevatorSubsystem) {
    super(elevatorSubsystem);
    this.elevator = elevatorSubsystem;
  }

  @Override
  public void execute() {
    ElevatorInput input = new ElevatorInput();

    input.setActivate(true);
    input.setRequestedPosition(0);

    switch (stage) {
      case 0 -> input.setRequestedPosition(5);
      case 1 -> input.setRequestedPosition(0);
      default -> { break; }
    }

    if (counter.isFinished(true)) {
      counter.reset();
      stage++;
    }

    if (stage != 9)
      elevator.updateInputs(input);
  }

  @Override
  public void initialize() {
    counter.reset();
    ElevatorInput stop = new ElevatorInput();
    stage = 0;

    stop.setRequestedPosition(0);
    stop.setActivate(true);

    elevator.updateInputs(stop);
  }

  @Override
  public boolean isFinished() {
    return stage > 9;
  }

  @Override
  public void end(boolean interrupted) {
    ElevatorInput stop = new ElevatorInput();

    stop.setActivate(false);
    stop.setRequestedPosition(0);

    elevator.updateInputs(stop);
  }
    
}
