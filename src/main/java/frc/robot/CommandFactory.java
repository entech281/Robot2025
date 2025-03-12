package frc.robot;

import java.io.IOException;

import org.json.simple.parser.ParseException;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import com.pathplanner.lib.config.PIDConstants;
import com.pathplanner.lib.config.RobotConfig;
import com.pathplanner.lib.controllers.PPHolonomicDriveController;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.entech.commands.AutonomousException;
import frc.entech.commands.InstantAnytimeCommand;
import frc.robot.commands.AutoAlignToScoringLocationCommand;
import frc.robot.commands.ElevatorMoveCommand;
import frc.robot.commands.FireCoralCommand;
import frc.robot.commands.FireCoralCommandAuto;
import frc.robot.commands.GyroResetByAngleCommand;
import frc.robot.commands.IntakeAlgaeCommand;
import frc.robot.commands.IntakeCoralCommand;
import frc.robot.commands.PivotMoveCommand;
import frc.robot.commands.RelativeVisionAlignmentCommand;
import frc.robot.io.RobotIO;
import frc.robot.livetuning.LiveTuningHandler;
import frc.robot.operation.UserPolicy;
import frc.robot.processors.OdometryProcessor;
import frc.robot.subsystems.coralmechanism.CoralMechanismSubsystem;
import frc.robot.subsystems.drive.DriveSubsystem;
import frc.robot.subsystems.elevator.ElevatorSubsystem;
import frc.robot.subsystems.led.LEDSubsystem;
import frc.robot.subsystems.navx.NavXSubsystem;
import frc.robot.subsystems.pivot.PivotSubsystem;

@SuppressWarnings("unused")
public class CommandFactory {
  private final DriveSubsystem driveSubsystem;
  private final NavXSubsystem navXSubsystem;
  private final PivotSubsystem pivotSubsystem;
  private final ElevatorSubsystem elevatorSubsystem;
  private final OdometryProcessor odometry;
  private final SubsystemManager subsystemManager;
  private final LEDSubsystem ledSubsystem;
  private final CoralMechanismSubsystem coralMechanismSubsystem;
  private final SendableChooser<Command> autoChooser;


  public CommandFactory(SubsystemManager subsystemManager, OdometryProcessor odometry) {
    this.driveSubsystem = subsystemManager.getDriveSubsystem();
    this.navXSubsystem = subsystemManager.getNavXSubsystem();
    this.ledSubsystem = subsystemManager.getLEDSubsystem();
    this.elevatorSubsystem = subsystemManager.getElevatorSubsystem();
    this.pivotSubsystem = subsystemManager.getPivotSubsystem();
    this.coralMechanismSubsystem = subsystemManager.getCoralMechanismSubsystem();
    this.odometry = odometry;
    this.subsystemManager = subsystemManager;

    RobotConfig config;
    try{
      config = RobotConfig.fromGUISettings();
    } catch (IOException e) {
      throw new AutonomousException("Failed to load robot config", e);
    } catch (ParseException e) {
      throw new AutonomousException("Failed to parse robot config", e);
    }

    ShuffleboardTab tab = Shuffleboard.getTab("stuffs");
    tab.add("Save", new InstantAnytimeCommand(() -> LiveTuningHandler.getInstance().saveToJSON()));
    tab.add("Load", new InstantAnytimeCommand(() -> LiveTuningHandler.getInstance().resetToJSON()));
    tab.add("Code Defaults", new InstantAnytimeCommand(() -> LiveTuningHandler.getInstance().resetToDefaults()));
    tab.add("L1", getSafeElevatorPivotMoveCommand(Position.L1));
    tab.add("L2", getSafeElevatorPivotMoveCommand(Position.L2));
    tab.add("L3", getSafeElevatorPivotMoveCommand(Position.L3));
    tab.add("L4", getSafeElevatorPivotMoveCommand(Position.L4));
    tab.add("HOME", getSafeElevatorPivotMoveCommand(Position.HOME));
    tab.add("BARGE", getSafeElevatorPivotMoveCommand(Position.BARGE));
    tab.add("ALGAE_L2", getSafeElevatorPivotMoveCommand(Position.ALGAE_L2));
    tab.add("ALGAE_L3", getSafeElevatorPivotMoveCommand(Position.ALGAE_L3));
    tab.add("ALGAE_GROUND", getSafeElevatorPivotMoveCommand(Position.ALGAE_GROUND));

    AutoBuilder.configure(odometry::getEstimatedPose,
        odometry::resetOdometry,
        driveSubsystem::getChassisSpeeds,
        (speeds, feedForwards) -> driveSubsystem.pathFollowDrive(speeds),
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
    
    NamedCommands.registerCommand("L1", formSafeMovementCommand(Position.L1));
    NamedCommands.registerCommand("L2", formSafeMovementCommand(Position.L2));
    NamedCommands.registerCommand("L3", formSafeMovementCommand(Position.L3));
    NamedCommands.registerCommand("L4", formSafeMovementCommand(Position.L4));
    NamedCommands.registerCommand("Home", formSafeMovementCommand(Position.HOME));
    NamedCommands.registerCommand("Barge", formSafeMovementCommand(Position.BARGE));
    NamedCommands.registerCommand("AlgaeL2", formSafeMovementCommand(Position.ALGAE_L2));
    NamedCommands.registerCommand("AlgaeL3", formSafeMovementCommand(Position.ALGAE_L3));
    NamedCommands.registerCommand("AlgaeGround", formSafeMovementCommand(Position.ALGAE_GROUND));
    NamedCommands.registerCommand("AlignToReefFar", new AutoAlignToScoringLocationCommand(driveSubsystem, 21));
    NamedCommands.registerCommand("AlignToReefCloseRight", new AutoAlignToScoringLocationCommand(driveSubsystem, 17));
    NamedCommands.registerCommand("AlignToReefFarRight", new AutoAlignToScoringLocationCommand(driveSubsystem, 22));
    NamedCommands.registerCommand("AlignToReefCloseLeft", new AutoAlignToScoringLocationCommand(driveSubsystem, 22));
    NamedCommands.registerCommand("AlignToReefFarLeft", new AutoAlignToScoringLocationCommand(driveSubsystem, 20));
    NamedCommands.registerCommand("AlignToFeedStation", new AutoAlignToScoringLocationCommand(driveSubsystem, 12));
    NamedCommands.registerCommand("IntakeCoral", new IntakeCoralCommand(coralMechanismSubsystem));
    NamedCommands.registerCommand("IntakeAlgae", new IntakeAlgaeCommand(coralMechanismSubsystem));
    NamedCommands.registerCommand("FireAlgae", new FireCoralCommand(coralMechanismSubsystem, 1.0));

    //TODO: Remove magic number. RobotConstants?
    NamedCommands.registerCommand("ScoreCoral", new FireCoralCommandAuto(coralMechanismSubsystem, 1.0));
    NamedCommands.registerCommand("ScoreCoralL1", new FireCoralCommand(coralMechanismSubsystem, 0.05));

    autoChooser = AutoBuilder.buildAutoChooser();
    SmartDashboard.putData("Auto Chooser", autoChooser);
  }

  public Command getAutoCommand() {
    SequentialCommandGroup auto = new SequentialCommandGroup();
    auto.addCommands(new GyroResetByAngleCommand(navXSubsystem, odometry, autoChooser.getSelected().getName()));
    auto.addCommands(new WaitCommand(0.5));
    auto.addCommands(autoChooser.getSelected());
    return auto;
  }

  public Command getAlignmentCommand() {
    return new RelativeVisionAlignmentCommand();
  }

  public Command getSafeElevatorPivotMoveCommand(Position pos) {
    return new InstantCommand(() -> 
      formSafeMovementCommand(pos).schedule()
    );
  }

  public static final double ELEVATOR_PIVOT_LIMBO = 1.6;
  private Command formSafeMovementCommand(Position pos) {
    double goalHeight = LiveTuningHandler.getInstance().getValue(pos.getElevatorKey());
    double goalAngle = LiveTuningHandler.getInstance().getValue(pos.getPivotKey());
    double currentHeight = RobotIO.getInstance().getElevatorOutput().getCurrentPosition();
    double currentAngle = RobotIO.getInstance().getPivotOutput().getCurrentPosition();

    SequentialCommandGroup commands = new SequentialCommandGroup();

    if (UserPolicy.getInstance().isAlgaeMode()) {
        commands.addCommands(new PivotMoveCommand(subsystemManager.getPivotSubsystem(), Position.ALGAE_HOME));
        commands.addCommands(new ElevatorMoveCommand(subsystemManager.getElevatorSubsystem(), pos));
        commands.addCommands(new PivotMoveCommand(subsystemManager.getPivotSubsystem(), pos));
    } else {
      if (currentHeight < ELEVATOR_PIVOT_LIMBO || goalHeight < currentHeight || goalAngle < currentAngle) {
        commands.addCommands(new PivotMoveCommand(subsystemManager.getPivotSubsystem(), Position.SAFE_EXTEND));
        commands.addCommands(new ElevatorMoveCommand(subsystemManager.getElevatorSubsystem(), pos));
        commands.addCommands(new PivotMoveCommand(subsystemManager.getPivotSubsystem(), pos));
      } else {
        commands.addCommands(new ElevatorMoveCommand(subsystemManager.getElevatorSubsystem(), pos));
        commands.addCommands(new PivotMoveCommand(subsystemManager.getPivotSubsystem(), pos));
      }
    }
    return commands;
  }
}
