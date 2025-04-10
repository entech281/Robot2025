package frc.robot.subsystems.elevator;

import frc.entech.commands.EntechCommand;
import frc.robot.Position;

public class TestElevatorCommand extends EntechCommand{
    private ElevatorSubsystem elevatorSubsystem;
    private ElevatorInput elevatorInput = new ElevatorInput();

    public TestElevatorCommand(ElevatorSubsystem elevatorSubsystem)  {
        super(elevatorSubsystem);
        this.elevatorSubsystem = elevatorSubsystem;
    }

    @Override
    public void initialize() {
        elevatorInput.setActivate(true);
        elevatorInput.setRequestedPosition(Position.valueOf(Position.HOME.getElevatorKey()));
        elevatorSubsystem.updateInputs(elevatorInput);
    }
}
