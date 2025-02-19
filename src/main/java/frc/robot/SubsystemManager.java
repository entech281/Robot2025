// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.ArrayList;
import java.util.List;
import frc.entech.subsystems.EntechSubsystem;
import frc.entech.subsystems.SubsystemInput;
import frc.entech.subsystems.SubsystemOutput;
import frc.robot.io.RobotIO;
import frc.robot.subsystems.coral.CoralMechanismSubsystem;
import frc.robot.subsystems.coraldetector.InternalCoralDetectorSubsystem;
import frc.robot.subsystems.drive.DriveSubsystem;
import frc.robot.subsystems.elevator.ElevatorSubsystem;
import frc.robot.subsystems.led.LEDSubsystem;
import frc.robot.subsystems.navx.NavXSubsystem;
import frc.robot.subsystems.pivot.PivotSubsystem;
import frc.robot.subsystems.vision.VisionSubsystem;

/**
 * Manages the subsystems and the interactions between them.
 */
public class SubsystemManager {
  private final DriveSubsystem driveSubsystem = new DriveSubsystem();
  private final NavXSubsystem navXSubsystem = new NavXSubsystem();
  private final VisionSubsystem visionSubsystem = new VisionSubsystem();
  private final ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem();
  private final PivotSubsystem pivotSubsystem = new PivotSubsystem();
  private final LEDSubsystem ledSubsystem = new LEDSubsystem();
  private final CoralMechanismSubsystem coralMechanismSubsystem = new CoralMechanismSubsystem();
  private final InternalCoralDetectorSubsystem coralDetectorSubsystem = new InternalCoralDetectorSubsystem();

  public SubsystemManager() {
    navXSubsystem.initialize();
    driveSubsystem.initialize();
    visionSubsystem.initialize();
    elevatorSubsystem.initialize();
    pivotSubsystem.initialize();
    ledSubsystem.initialize();
    coralMechanismSubsystem.initialize();
    coralDetectorSubsystem.initialize();
    

    periodic();
  }

  public DriveSubsystem getDriveSubsystem() {
    return driveSubsystem;
  }

  public NavXSubsystem getNavXSubsystem() {
    return navXSubsystem;
  }

  public VisionSubsystem getVisionSubsystem() {
    return visionSubsystem;
  }

  public ElevatorSubsystem getElevatorSubsystem() {
    return elevatorSubsystem;
  }

  public PivotSubsystem getPivotSubsystem() {
    return pivotSubsystem;
  }

  public LEDSubsystem getLEDSubsystem() {
    return ledSubsystem;
  }

  public CoralMechanismSubsystem getCoralMechanismSubsystem() {
    return coralMechanismSubsystem;
  }

  public InternalCoralDetectorSubsystem getInternalCoralDetectorSubsystem(){
    return coralDetectorSubsystem;
  }

  public List<EntechSubsystem<? extends SubsystemInput, ? extends SubsystemOutput>> getSubsystemList() {
    ArrayList<EntechSubsystem<? extends SubsystemInput, ? extends SubsystemOutput>> r = new ArrayList<>();
    r.add(navXSubsystem);
    r.add(driveSubsystem);
    r.add(visionSubsystem);
    r.add(elevatorSubsystem);
    r.add(pivotSubsystem);
    r.add(ledSubsystem);
    r.add(coralMechanismSubsystem);
    r.add(coralDetectorSubsystem);

    return r;
  }

  public void periodic() {
    RobotIO outputs = RobotIO.getInstance();

    outputs.updateDrive(driveSubsystem.getOutputs());

    outputs.updateNavx(navXSubsystem.getOutputs());

    outputs.updateVision(visionSubsystem.getOutputs());

    outputs.updateElevator(elevatorSubsystem.getOutputs());

    outputs.updatePivot(pivotSubsystem.getOutputs());

    outputs.updateLED(ledSubsystem.getOutputs());

    outputs.updateCoralMechanism(coralMechanismSubsystem.getOutputs());

    outputs.updateInternalCoralDetector(coralDetectorSubsystem.getOutputs());
  }
}
