package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.robot.subsystems.elevator.ElevatorInput;
import frc.robot.subsystems.elevator.ElevatorOutput;
import frc.robot.subsystems.elevator.ElevatorSubsystem;

public class ElevatorStageCommand extends EntechCommand {
  private final ElevatorSubsystem elevatorSubsystem;
  private final double stagePosition;
  private ElevatorOutput elevatorOutput;
  private ElevatorInput elevatorInput;


  public ElevatorStageCommand(ElevatorSubsystem elevatorSubsystem, int stage, ElevatorInput elevatorInput, ElevatorOutput elevatorOutput) {
    this.elevatorSubsystem = elevatorSubsystem;
    this.stagePosition = getStagePosition(stage);
    this.elevatorInput = elevatorInput;
    this.elevatorOutput = elevatorOutput;
    addRequirements(elevatorSubsystem);
  }

  private double getStagePosition(int stage) {
    switch (stage) {
      case 1:
        return ElevatorSubsystem.STAGE1;
      case 2:
        return ElevatorSubsystem.STAGE2;
      case 3:
        return ElevatorSubsystem.STAGE3;
      case 4:
        return ElevatorSubsystem.STAGE4;
      default:
        throw new IllegalArgumentException("Invalid stage: " + stage);
    }
  }

  @Override
  public void execute() {
    elevatorSubsystem.updateInputs(elevatorInput);
  }

  @Override
  public void initialize() {
    elevatorOutput.setRequestedPosition(stagePosition);
  }

  @Override
  public boolean isFinished() {
    return elevatorOutput.isAtRequestedPosition();
  }
}