// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.
package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.robot.io.RobotIO;
import frc.robot.livetuning.LiveTuningHandler;
import frc.robot.subsystems.elevator.ElevatorInput;
import frc.robot.subsystems.elevator.ElevatorSubsystem;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class ElevatorUpCommand extends EntechCommand {
  /** Creates a new PivotCommand. */
  private final ElevatorInput elevatorInput = new ElevatorInput();
  private final ElevatorSubsystem elevatorSS;

  public ElevatorUpCommand(ElevatorSubsystem elevatorSubsystem) {
    super(elevatorSubsystem);
    elevatorSS = elevatorSubsystem;
  }

  @Override
  public void initialize() {
    elevatorInput.setRequestedPosition(RobotIO.getInstance().getElevatorOutput().getCurrentPosition() + LiveTuningHandler.getInstance().getValue("ElevatorSubsystem/NudgeAmount"));
  }

  @Override
  public void execute() {
    elevatorSS.updateInputs(elevatorInput);
  }
  
  @Override
  public void end(boolean interrupted) {
    //Code stops on it's own so nothing to put in the end method
  }

  @Override
  public boolean isFinished() {
    return RobotIO.getInstance().getElevatorOutput().isAtRequestedPosition();
  }
}
