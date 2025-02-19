// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj.DriverStation;
import frc.entech.commands.EntechCommand;
import frc.robot.io.RobotIO;
import frc.robot.subsystems.coral.CoralMechanismInput;
import frc.robot.subsystems.coral.CoralMechanismSubsystem;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class CoralOuttakeCommand extends EntechCommand {

  private final CoralMechanismInput coralInput = new CoralMechanismInput();
  private final CoralMechanismSubsystem coralSS;

  public CoralOuttakeCommand(CoralMechanismSubsystem coralSubsystem) {
    
    super(coralSubsystem);
    coralSS = coralSubsystem;
  }

  @Override
  public void initialize() {
    DriverStation.reportWarning("I started ",false);
    coralInput.setActivate(true);
    coralInput.setRequestedSpeed(1);
  }

  @Override
  public void execute() {

    coralSS.updateInputs(coralInput);
  }

  @Override
  public void end(boolean interrupted) {
    coralInput.setRequestedSpeed(0);
    coralInput.setActivate(false);
  }

  @Override
  public boolean isFinished() {
    DriverStation.reportWarning("I ended", false);
    return RobotIO.getInstance().getCoralMechanismOutput().isFinished();
  }
}
