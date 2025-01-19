package frc.robot;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.wpilibj.Joystick;
import frc.entech.subsystems.EntechSubsystem;
import frc.entech.subsystems.SubsystemInput;
import frc.entech.subsystems.SubsystemOutput;
import frc.robot.io.RobotIO;
import frc.robot.subsystems.drive.DriveSubsystem;
import frc.robot.subsystems.navx.NavXSubsystem;
import frc.robot.subsystems.vision.VisionSubsystem;
import frc.robot.subsystems.vision_simulation.VisionSimulationSubsystem;
import frc.robot.commands.VisionSimulationCommand;

/**
 * Manages the subsystems and the interactions between them.
 */
public class SubsystemManager {
  private final DriveSubsystem driveSubsystem = new DriveSubsystem();
  private final NavXSubsystem navXSubsystem = new NavXSubsystem();
  private final VisionSubsystem visionSubsystem = new VisionSubsystem();
  private VisionSimulationSubsystem visionSimulationSubsystem;

  public SubsystemManager() {
    navXSubsystem.initialize();
    driveSubsystem.initialize();
    visionSubsystem.initialize();
    periodic();
  }

  public void initializeVisionSimulationSubsystem(VisionSubsystem visionSubsystem, Joystick joystick) {
    visionSimulationSubsystem = new VisionSimulationSubsystem(visionSubsystem, joystick);
    visionSimulationSubsystem.initialize();
    visionSimulationSubsystem.setDefaultCommand(new VisionSimulationCommand(visionSimulationSubsystem));
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

  public VisionSimulationSubsystem getVisionSimulationSubsystem() {
    return visionSimulationSubsystem;
  }

  public List<EntechSubsystem<? extends SubsystemInput, ? extends SubsystemOutput>> getSubsystemList() {
    ArrayList<EntechSubsystem<? extends SubsystemInput, ? extends SubsystemOutput>> r = new ArrayList<>();
    r.add(navXSubsystem);
    r.add(driveSubsystem);
    r.add(visionSubsystem);
    if (visionSimulationSubsystem != null) {
      r.add(visionSimulationSubsystem);
    }
    return r;
  }

  public void periodic() {
    RobotIO outputs = RobotIO.getInstance();

    if (driveSubsystem.isEnabled()) {
      outputs.updateDrive(driveSubsystem.getOutputs());
    }

    outputs.updateNavx(navXSubsystem.getOutputs());
  }
}