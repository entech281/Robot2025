// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.ArrayList;
import java.util.List;
import frc.entech.subsystems.EntechSubsystem;
import frc.robot.io.RobotIO;
import frc.robot.subsystems.drive.DriveSubsystem;
import frc.robot.subsystems.navx.NavXSubsystem;
// import frc.robot.subsystems.vision.VisionSubsystem;

/**
 * Manages the subsystems and the interactions between them.
 */
public class SubsystemManager {
  private final DriveSubsystem driveSubsystem = new DriveSubsystem();
  // private final VisionSubsystem visionSubsystem = new VisionSubsystem();
  private final NavXSubsystem navXSubsystem = new NavXSubsystem();

  public SubsystemManager() {
    navXSubsystem.initialize();
    driveSubsystem.initialize();
    // visionSubsystem.initialize();

    periodic();
  }

  public DriveSubsystem getDriveSubsystem() {
    return driveSubsystem;
  }

  // public VisionSubsystem getVisionSubsystem() {
  //   return visionSubsystem;
  // }

  public NavXSubsystem getNavXSubsystem() {
    return navXSubsystem;
  }

  public List<EntechSubsystem<?, ?>> getSubsystemList() {
    ArrayList<EntechSubsystem<?, ?>> r = new ArrayList<>();
    r.add(navXSubsystem);
    r.add(driveSubsystem);
    // r.add(visionSubsystem);

    return r;
  }

  public void periodic() {
    RobotIO outputs = RobotIO.getInstance();

    // outputs.updateVision(visionSubsystem.getOutputs());

    if (driveSubsystem.isEnabled()) {
      outputs.updateDrive(driveSubsystem.getOutputs());
    }

    outputs.updateNavx(navXSubsystem.getOutputs());
  }
}
