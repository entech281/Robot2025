package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.robot.subsystems.elevator.ElevatorInput;
import frc.robot.subsystems.elevator.ElevatorOutput;
import frc.robot.subsystems.elevator.ElevatorSubsystem;

public class ElevatorDownCommand extends EntechCommand {
  private final ElevatorSubsystem elevatorSubsystem;
  private final ElevatorOutput elevatorOutput;
  private final ElevatorInput elevatorInput;

  public ElevatorDownCommand(ElevatorSubsystem elevatorSubsystem, ElevatorOutput elevatorOutput, ElevatorInput elevatorInput) {
    this.elevatorSubsystem = elevatorSubsystem;
    this.elevatorInput = elevatorInput;
    this.elevatorOutput = elevatorOutput;
    addRequirements(elevatorSubsystem);
  }
   @Override
  public void execute() {
    elevatorSubsystem.updateInputs(elevatorInput);
  }

  @Override
  public void initialize() {
    double newPosition = elevatorOutput.getCurrentPosition() - 10;
    elevatorOutput.setRequestedPosition(newPosition);
  }

  @Override
  public boolean isFinished() {
    return elevatorOutput.isAtRequestedPosition();
  }
}