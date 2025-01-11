package frc.robot;

import java.util.Optional;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import com.pathplanner.lib.controllers.PPHolonomicDriveController;
import com.pathplanner.lib.path.PathPlannerPath;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.commands.GyroResetByAngleCommand;
import frc.robot.commands.ResetOdometryCommand;
import frc.robot.io.RobotIO;
import frc.robot.operation.UserPolicy;
import frc.robot.processors.OdometryProcessor;
import frc.robot.subsystems.drive.DriveSubsystem;
import frc.robot.subsystems.navx.NavXSubsystem;
import com.pathplanner.lib.config.PIDConstants;
import com.pathplanner.lib.config.RobotConfig;

@SuppressWarnings("unused")
public class CommandFactory {
  private final DriveSubsystem driveSubsystem;
  private final NavXSubsystem navXSubsystem;
  private final OdometryProcessor odometry;
  private final SubsystemManager subsystemManager;
  private final SendableChooser<Command> autoChooser;


  public CommandFactory(SubsystemManager subsystemManager, OdometryProcessor odometry) {
    this.driveSubsystem = subsystemManager.getDriveSubsystem();
    this.navXSubsystem = subsystemManager.getNavXSubsystem();
    this.odometry = odometry;
    this.subsystemManager = subsystemManager;

    RobotConfig config;
    try{
      config = RobotConfig.fromGUISettings();
    } catch (Exception e) {
      throw new RuntimeException("Failed to load robot config", e);
    }

    AutoBuilder.configure(odometry::getEstimatedPose,
        odometry::resetOdometry,
        driveSubsystem::getChassisSpeeds,
        (speeds, feedforwards) -> driveSubsystem.pathFollowDrive(speeds),
        new PPHolonomicDriveController(
            new PIDConstants(8.5, 3, 0.1),
            new PIDConstants(RobotConstants.AUTONOMOUS.ROTATION_CONTROLLER_P, 0.0, 0.0)
        ), config, () -> {

          var alliance = DriverStation.getAlliance();
          if (alliance.isPresent()) {
            return alliance.get() == DriverStation.Alliance.Red;
          }
          return false;
        }, driveSubsystem);

    autoChooser = AutoBuilder.buildAutoChooser();
    SmartDashboard.putData("Auto Chooser", autoChooser);
  }

  public Command getAutoCommand() {
    SequentialCommandGroup auto = new SequentialCommandGroup();
    auto.addCommands(
        new GyroResetByAngleCommand(navXSubsystem, odometry, autoChooser.getSelected().getName()));
    auto.addCommands(new WaitCommand(0.5));
    auto.addCommands(autoChooser.getSelected());
    return auto;
  }
}
