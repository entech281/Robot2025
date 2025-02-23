package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.robot.io.RobotIO;
import frc.robot.livetuning.LiveTuningHandler;
import frc.robot.subsystems.elevator.ElevatorSubsystem;
import frc.robot.subsystems.elevator.ElevatorInput;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class ElevatorUpCommand extends EntechCommand {
  /** Creates a new ElevatorCommand. */
  private final ElevatorInput elevatorInput = new ElevatorInput();
  private final ElevatorSubsystem elevatorSS;

  public ElevatorUpCommand(ElevatorSubsystem elevatorSubsystem) {
    super(elevatorSubsystem);
    elevatorSS = elevatorSubsystem;
  }

  @Override
  public void initialize() {
    elevatorInput.setRequestedPosition(RobotIO.getInstance().getElevatorOutput().getCurrentPosition() - LiveTuningHandler.getInstance().getValue("ElevatorSubsystem/NudgeAmount"));
    elevatorSS.updateInputs(elevatorInput);
  }

  @Override
  public void execute() {
    
  }
  
  @Override
  public void end(boolean interrupted) {
    //Code stops on it's own so nothing to put in the end method
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}