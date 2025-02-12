// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.robot.subsystems.pivot.PivotInput;
import frc.robot.subsystems.pivot.PivotOutput;
import frc.robot.subsystems.pivot.PivotSubsystem;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class PivotDownCommand extends EntechCommand {
  /** Creates a new PivotCommand. */
  private PivotInput pivotInput = new PivotInput();
  private PivotOutput pivotOutput;
  private PivotSubsystem pivotSS;

  public PivotDownCommand(PivotSubsystem pivotSubsystem) {
    super(pivotSubsystem);
    pivotSS = pivotSubsystem;
  }

  @Override
  public void initialize() {
    pivotOutput = pivotSS.toOutputs();
    pivotInput.setRequestedPosition(pivotOutput.getCurrentPosition() + 5);
  }

  @Override
  public void execute() {
    pivotSS.updateInputs(pivotInput);
  }

  //Code stops on it's own so nothing to put in the end method
  @Override
  public void end(boolean interrupted) {
  }

  @Override
  public boolean isFinished() {
    pivotOutput = pivotSS.toOutputs();
    return pivotOutput.atRequestedPosition();
  }
}
