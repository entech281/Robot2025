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
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.entech.commands.AutonomousException;
import frc.entech.commands.InstantAnytimeCommand;
import frc.robot.commands.AutoAlignToScoringLocationCommand;
import frc.robot.commands.AutoDealgifyCommand;
import frc.robot.commands.AutoFireAlgaeCommand;
import frc.robot.commands.AutoIntakeAlgaeCommand;
import frc.robot.commands.AutoIntakeCoralCommand;
import frc.robot.commands.ElevatorMoveCommand;
import frc.robot.commands.FireCoralCommand;
import frc.robot.commands.FireCoralCommandAuto;
import frc.robot.commands.GyroResetByAngleCommand;
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
import frc.robot.subsystems.vision.TargetLocation;
import frc.robot.subsystems.vision.VisionInput;
import frc.robot.subsystems.vision.VisionSubsystem;

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
  private final VisionSubsystem visionSubsystem;
  private final SendableChooser<Command> autoChooser;


  public CommandFactory(SubsystemManager subsystemManager, OdometryProcessor odometry) {
    this.driveSubsystem = subsystemManager.getDriveSubsystem();
    this.navXSubsystem = subsystemManager.getNavXSubsystem();
    this.ledSubsystem = subsystemManager.getLEDSubsystem();
    this.elevatorSubsystem = subsystemManager.getElevatorSubsystem();
    this.pivotSubsystem = subsystemManager.getPivotSubsystem();
    this.coralMechanismSubsystem = subsystemManager.getCoralMechanismSubsystem();
    this.visionSubsystem = subsystemManager.getVisionSubsystem();
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
    tab.add("AUTO_ALIGN_TO_17", new AutoAlignToScoringLocationCommand(driveSubsystem, 18));

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
    
    NamedCommands.registerCommand("L1", Commands.deferredProxy(() -> formSafeMovementCommand(Position.L1)));
    NamedCommands.registerCommand("L2", Commands.deferredProxy(() -> formSafeMovementCommand(Position.L2)));
    NamedCommands.registerCommand("L3", Commands.deferredProxy(() -> formSafeMovementCommand(Position.L3)));
    NamedCommands.registerCommand("L4", Commands.deferredProxy(() -> formSafeMovementCommand(Position.L4)));
    NamedCommands.registerCommand("Home", Commands.deferredProxy(() -> formSafeMovementCommand(Position.HOME)));
    NamedCommands.registerCommand("Barge", Commands.deferredProxy(() -> formSafeMovementCommand(Position.BARGE)));
    NamedCommands.registerCommand("AlgaeL2", Commands.deferredProxy(() -> formSafeMovementCommand(Position.ALGAE_L2)));
    NamedCommands.registerCommand("AlgaeL3", Commands.deferredProxy(() -> formSafeMovementCommand(Position.ALGAE_L3)));
    NamedCommands.registerCommand("AlgaeGround", Commands.deferredProxy(() -> formSafeMovementCommand(Position.ALGAE_GROUND)));
    NamedCommands.registerCommand("AlgaeHome", Commands.deferredProxy(() -> formSafeMovementCommand(Position.ALGAE_HOME)));
    NamedCommands.registerCommand("AutoL1", Commands.deferredProxy(() -> formSafeMovementCommand(Position.AUTO_L1)));
    NamedCommands.registerCommand("AutoL2", Commands.deferredProxy(() -> formSafeMovementCommand(Position.AUTO_L2)));
    NamedCommands.registerCommand("AutoL3", Commands.deferredProxy(() -> formSafeMovementCommand(Position.AUTO_L3)));
    NamedCommands.registerCommand("AutoL4", Commands.deferredProxy(() -> formSafeMovementCommand(Position.AUTO_L4)));
    NamedCommands.registerCommand("SetAlgaeMode", new InstantCommand( () -> UserPolicy.getInstance().setAlgaeMode(true)));


    NamedCommands.registerCommand("MoveFromScoreAndDealgiyL2Left", new SequentialCommandGroup(new InstantCommand( () -> UserPolicy.getInstance().setTargetLocations(TargetLocation.BLUE_H, TargetLocation.RED_H)), new AutoDealgifyCommand(subsystemManager.getDriveSubsystem(), subsystemManager.getCoralMechanismSubsystem(), this), new InstantCommand( () -> {
      UserPolicy.getInstance().clearTargetLocations();
    }, driveSubsystem, coralMechanismSubsystem)));
    NamedCommands.registerCommand("MoveFromScoreAndDealgiyL2Right", new SequentialCommandGroup(new InstantCommand( () -> UserPolicy.getInstance().setTargetLocations(TargetLocation.BLUE_K, TargetLocation.RED_K)), new AutoDealgifyCommand(subsystemManager.getDriveSubsystem(), subsystemManager.getCoralMechanismSubsystem(), this), new InstantCommand( () -> UserPolicy.getInstance().clearTargetLocations())));
    var alliance = DriverStation.getAlliance();
      NamedCommands.registerCommand("AlignToReefFar", 
        new ConditionalCommand(
            new AutoAlignToScoringLocationCommand(driveSubsystem, 21),
            new AutoAlignToScoringLocationCommand(driveSubsystem, 10),
            () -> alliance.isPresent() && (alliance.get().equals(DriverStation.Alliance.Blue))
        )
      );

      NamedCommands.registerCommand("AlignToReefCloseRight", 
        new ConditionalCommand(
            new AutoAlignToScoringLocationCommand(driveSubsystem, 17),
            new AutoAlignToScoringLocationCommand(driveSubsystem, 8),
            () -> alliance.isPresent() && alliance.get().equals(DriverStation.Alliance.Blue)
        )
      );

      NamedCommands.registerCommand("AlignToReefFarRight", 
        new ConditionalCommand(
            new AutoAlignToScoringLocationCommand(driveSubsystem, 22),
            new AutoAlignToScoringLocationCommand(driveSubsystem, 9),
            () -> alliance.isPresent() && alliance.get().equals(DriverStation.Alliance.Blue)
        )
      );

      NamedCommands.registerCommand("AlignToReefCloseLeft", 
        new ConditionalCommand(
            new AutoAlignToScoringLocationCommand(driveSubsystem, 19),
            new AutoAlignToScoringLocationCommand(driveSubsystem, 6),
            () -> alliance.isPresent() && alliance.get().equals(DriverStation.Alliance.Blue)
        )
      );

      NamedCommands.registerCommand("AlignToReefFarLeft", 
        new ConditionalCommand(
            new AutoAlignToScoringLocationCommand(driveSubsystem, 20),
            new AutoAlignToScoringLocationCommand(driveSubsystem, 11),
            () -> alliance.isPresent() && alliance.get().equals(DriverStation.Alliance.Blue)
        )
      );
    NamedCommands.registerCommand("IntakeCoral", new IntakeCoralCommand(coralMechanismSubsystem, pivotSubsystem));
    NamedCommands.registerCommand("IntakeAlgae", new AutoIntakeAlgaeCommand(coralMechanismSubsystem));
    NamedCommands.registerCommand("FireAlgae", new AutoFireAlgaeCommand(coralMechanismSubsystem, 1.0));
    NamedCommands.registerCommand("SwitchToRightCamera", Commands.run(() -> {
      VisionInput in = new VisionInput();
      in.setCamera(VisionInput.Camera.SIDE);
      visionSubsystem.updateInputs(in);
    }, visionSubsystem).withTimeout(5));
    NamedCommands.registerCommand("SwitchToLeftCamera", Commands.run(() -> {
      VisionInput in = new VisionInput();
      in.setCamera(VisionInput.Camera.TOP);
      visionSubsystem.updateInputs(in);
    }, visionSubsystem).withTimeout(5));

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
