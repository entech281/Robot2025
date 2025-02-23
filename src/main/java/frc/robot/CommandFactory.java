package frc.robot;

import java.io.IOException;
import java.util.*;

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
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.entech.commands.AutonomousException;
import frc.robot.commandCheker.*;
import frc.robot.commands.ElevatorMoveCommand;
import frc.robot.commands.PivotMoveCommand;
import frc.robot.commands.RelativeVisionAlignmentCommand;
import frc.robot.io.RobotIO;
import frc.robot.livetuning.LiveTuningHandler;
import frc.robot.processors.OdometryProcessor;
import frc.robot.subsystems.drive.DriveSubsystem;
import frc.robot.subsystems.elevator.ElevatorInput;
import frc.robot.subsystems.elevator.ElevatorSubsystem;
import frc.robot.subsystems.led.LEDSubsystem;
import frc.robot.subsystems.navx.NavXSubsystem;
import frc.robot.subsystems.pivot.PivotInput;
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
  private final SendableChooser<Command> autoChooser;
  private final SafeMovePlanner planner;


  public CommandFactory(SubsystemManager subsystemManager, OdometryProcessor odometry) {
    this.driveSubsystem = subsystemManager.getDriveSubsystem();
    this.navXSubsystem = subsystemManager.getNavXSubsystem();
    this.ledSubsystem = subsystemManager.getLEDSubsystem();
    this.elevatorSubsystem = subsystemManager.getElevatorSubsystem();
    this.pivotSubsystem = subsystemManager.getPivotSubsystem();
    this.odometry = odometry;
    this.subsystemManager = subsystemManager;
    this.planner = new SafeMovePlanner();

    RobotConfig config;
    try{
      config = RobotConfig.fromGUISettings();
    } catch (Exception e) {
      throw new AutonomousException("Failed to load robot config", e);
    }

    ShuffleboardTab tab = Shuffleboard.getTab("stuffs");
    tab.add("Save", new InstantCommand(() -> LiveTuningHandler.getInstance().saveToJSON()));
    tab.add("Load", new InstantCommand(() -> LiveTuningHandler.getInstance().resetToJSON()));

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

    NamedCommands.registerCommand("L1", Commands.none());
    NamedCommands.registerCommand("L2", Commands.none());
    NamedCommands.registerCommand("L3", Commands.none());
    NamedCommands.registerCommand("L4", Commands.none());
    NamedCommands.registerCommand("DeAlgae", Commands.none());
    NamedCommands.registerCommand("Home", Commands.none());
    NamedCommands.registerCommand("AlignToFace", Commands.none());
    NamedCommands.registerCommand("ScoreCoral", Commands.none());

    autoChooser = AutoBuilder.buildAutoChooser();
    SmartDashboard.putData("Auto Chooser", autoChooser);
  }

  public Command safeElevatorAndPivotMove(double targetElevator, double targetPivot) {
    Map<ElevatorInput.Position, Double> elePosMap = new HashMap<>();
    Map<PivotInput.Position, Double> pivPosMap = new HashMap<>();
    for (ElevatorInput.Position pos : ElevatorInput.Position.values()) {
      elePosMap.put(pos, LiveTuningHandler.getInstance().getValue(pos.label));
    }
    for (PivotInput.Position pos : PivotInput.Position.values()) {
      pivPosMap.put(pos, LiveTuningHandler.getInstance().getValue(pos.label));
    }
    List<Object> moves = planner.planMovesNames(RobotIO.getInstance().getElevatorOutput().getCurrentPosition(), RobotIO.getInstance().getPivotOutput().getCurrentPosition(), targetElevator, targetPivot, elePosMap, pivPosMap);
    SequentialCommandGroup commands = new SequentialCommandGroup();
    DriverStation.reportWarning(moves.size() + "", false);
    DriverStation.reportWarning(moves + "", false);
    DriverStation.reportWarning(moves.get(0).getClass() + "", false);
    for (Object move : moves) {
      if (move instanceof ElevatorInput.Position) {
        commands.addCommands(new ElevatorMoveCommand(elevatorSubsystem, (ElevatorInput.Position) move));
      }

      if (move instanceof PivotInput.Position) {
        commands.addCommands(new PivotMoveCommand (pivotSubsystem, (PivotInput.Position) move));
      }
    }

    return commands;
  }

  public Command safeElevatorMove(double target) {
    return safeElevatorAndPivotMove(target, RobotIO.getInstance().getPivotOutput().getCurrentPosition());
  }

  public Command safePivotMove(double target) {
    return safeElevatorAndPivotMove(RobotIO.getInstance().getElevatorOutput().getCurrentPosition(), target);
  }

  public Command getAutoCommand() {
    SequentialCommandGroup auto = new SequentialCommandGroup();
    auto.addCommands(new WaitCommand(0.5));
    return auto;
  }

  public Command getAlignmentCommand() {
    return new RelativeVisionAlignmentCommand();
  }
}
