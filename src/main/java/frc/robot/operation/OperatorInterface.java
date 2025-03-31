package frc.robot.operation;

import java.util.List;

import org.littletonrobotics.junction.Logger;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.entech.commands.InstantAnytimeCommand;
import frc.entech.subsystems.EntechSubsystem;
import frc.robot.CommandFactory;
import frc.robot.Position;
import frc.robot.RobotConstants;
import frc.robot.SubsystemManager;
import frc.robot.commands.AlgaeHoldCommand;
import frc.robot.commands.AutoDealgifyCommand;
import frc.robot.commands.DriveCommand;
import frc.robot.commands.FireCoralCommand;
import frc.robot.commands.GyroReset;
import frc.robot.commands.IntakeAlgaeCommand;
import frc.robot.commands.IntakeCoralCommand;
import frc.robot.commands.PivotMoveCommand;
import frc.robot.commands.ResetOdometryCommand;
import frc.robot.commands.RotateToAngleCommand;
import frc.robot.commands.RunTestCommand;
import frc.robot.commands.TeleFullAutoAlign;
import frc.robot.commands.TwistCommand;
import frc.robot.commands.VisionCameraSwitchingCommand;
import frc.robot.commands.XDriveCommand;
import frc.robot.io.DebugInput;
import frc.robot.io.DebugInputSupplier;
import frc.robot.io.DriveInputSupplier;
import frc.robot.io.OperatorInput;
import frc.robot.io.OperatorInputSupplier;
import frc.robot.io.RobotIO;
import frc.robot.livetuning.LiveTuningHandler;
import frc.robot.processors.OdometryProcessor;
import frc.robot.subsystems.drive.DriveInput;
import frc.robot.subsystems.led.TestLEDCommand;
import frc.robot.subsystems.vision.TargetLocation;
import frc.robot.subsystems.vision.VisionTarget;

public class OperatorInterface
    implements DriveInputSupplier, DebugInputSupplier, OperatorInputSupplier {
  private CommandJoystick joystickController;
  private CommandXboxController xboxController;

  private CommandXboxController tuningController;

  private CommandJoystick scoreOperatorPanel;
  private CommandJoystick alignOperatorPanel;

  private final CommandFactory commandFactory;
  private final SubsystemManager subsystemManager;
  private final OdometryProcessor odometry;

  private final SendableChooser<Command> testChooser;

  private IntakeCoralCommand intakeCommand;
  private FireCoralCommand fireCommand;
  private FireCoralCommand fireCommandL1;
  private RunCommand rumbleCommand;
  private FireCoralCommand algaeFireCommand;
  private FireCoralCommand algaeFireCommand2;

  public OperatorInterface(CommandFactory commandFactory, SubsystemManager subsystemManager,
      OdometryProcessor odometry) {
    this.commandFactory = commandFactory;
    this.subsystemManager = subsystemManager;
    this.odometry = odometry;
    this.testChooser = getTestCommandChooser();
  }

  public void create() {
    xboxController = new CommandXboxController(RobotConstants.PORTS.CONTROLLER.DRIVER_CONTROLLER);
    enableXboxBindings();
    if (DriverControllerUtils.controllerIsPresent(RobotConstants.PORTS.CONTROLLER.TEST_JOYSTICK)) {
      joystickController = new CommandJoystick(RobotConstants.PORTS.CONTROLLER.TEST_JOYSTICK);
      enableJoystickBindings();
    }


    if (DriverControllerUtils
        .controllerIsPresent(RobotConstants.PORTS.CONTROLLER.TUNING_CONTROLLER)) {
      tuningController =
          new CommandXboxController(RobotConstants.PORTS.CONTROLLER.TUNING_CONTROLLER);
      enableTuningControllerBindings();
    }

    scoreOperatorPanel = new CommandJoystick(RobotConstants.PORTS.CONTROLLER.SCORE_PANEL);
    scoreOperatorBindings();

    alignOperatorPanel = new CommandJoystick(RobotConstants.PORTS.CONTROLLER.ALIGN_PANEL);
    alignOperatorBindings();
  }

  public void enableTuningControllerBindings() {
    tuningController.a().whileTrue(Commands.none());
    tuningController.y().whileTrue(Commands.none());
  }

  public void configureBindings() {
    if (DriverControllerUtils.currentControllerIsXbox()) {
      xboxController = new CommandXboxController(RobotConstants.PORTS.CONTROLLER.DRIVER_CONTROLLER);
      enableXboxBindings();
    } else {
      joystickController = new CommandJoystick(RobotConstants.PORTS.CONTROLLER.DRIVER_CONTROLLER);
      enableJoystickBindings();
    }
  }

  public void enableJoystickBindings() {
    joystickController.button(RobotConstants.PORTS.CONTROLLER.BUTTONS_JOYSTICK.TWIST)
        .whileTrue(new TwistCommand());
    joystickController.button(RobotConstants.PORTS.CONTROLLER.BUTTONS_JOYSTICK.GYRO_RESET)
        .onTrue(new GyroReset(subsystemManager.getNavXSubsystem(), odometry));

    joystickController.button(RobotConstants.PORTS.CONTROLLER.BUTTONS_JOYSTICK.RUN_TESTS)
        .onTrue(new RunTestCommand(testChooser));

    subsystemManager.getDriveSubsystem()
        .setDefaultCommand(new DriveCommand(subsystemManager.getDriveSubsystem(), this));

    subsystemManager.getLEDSubsystem()
        .setDefaultCommand(new TestLEDCommand(subsystemManager.getLEDSubsystem()));

    joystickController.button(RobotConstants.PORTS.CONTROLLER.BUTTONS_JOYSTICK.RESET_ODOMETRY)
        .onTrue(new ResetOdometryCommand(odometry));
  }

  public void enableXboxBindings() {
    xboxController.button(RobotConstants.PORTS.CONTROLLER.BUTTONS_XBOX.GYRO_RESET)
        .onTrue(new GyroReset(subsystemManager.getNavXSubsystem(), odometry));

    subsystemManager.getDriveSubsystem()
        .setDefaultCommand(new DriveCommand(subsystemManager.getDriveSubsystem(), this));

    xboxController.button(9)
        .onTrue(new RunTestCommand(testChooser));


    xboxController.button(10)
        .whileTrue(commandFactory.getAlignmentCommand());

    xboxController.button(RobotConstants.PORTS.CONTROLLER.BUTTONS_XBOX.DRIVE_X)
        .whileTrue(new XDriveCommand(subsystemManager.getDriveSubsystem()));
    
    xboxController.button(RobotConstants.PORTS.CONTROLLER.BUTTONS_XBOX.RESET_ODOMETRY)
        .onTrue(new ResetOdometryCommand(odometry));

    xboxController.leftBumper().whileTrue(
      new ConditionalCommand(
        new TeleFullAutoAlign(),
        new RotateToAngleCommand(() -> DriverStation.getAlliance().isPresent() && DriverStation.getAlliance().get() == Alliance.Blue ? -53 : 53),
        () -> UserPolicy.getInstance().isAlgaeMode() || RobotIO.getInstance().getInternalCoralDetectorOutput().hasCoral()
      )
    );
    xboxController.rightBumper().whileTrue(
      new ConditionalCommand(
        new TeleFullAutoAlign(),
        new RotateToAngleCommand(() -> DriverStation.getAlliance().isPresent() && DriverStation.getAlliance().get() == Alliance.Blue ? 53 : -53),
        () -> UserPolicy.getInstance().isAlgaeMode() || RobotIO.getInstance().getInternalCoralDetectorOutput().hasCoral()
      )
    );

    xboxController.a().whileTrue(new AutoDealgifyCommand(subsystemManager.getDriveSubsystem(), subsystemManager.getCoralMechanismSubsystem(), commandFactory, Position.ALGAE_L2, "left"));

    rumbleCommand = new RunCommand(
      () -> {
        List<VisionTarget> foundTargets = RobotIO.getInstance().getVisionOutput().findSpecificTarget(UserPolicy.getInstance().getSelectedTargetLocations());
        if (foundTargets.isEmpty()) {
          xboxController.setRumble(RumbleType.kBothRumble, 0.0);
          Logger.recordOutput("ALIGNED", false);
        } else {
          VisionTarget t = foundTargets.get(0);
          xboxController.setRumble(RumbleType.kBothRumble, 1.0);
          Logger.recordOutput("ALIGNED", t.getDistance() <= 0.725 && Math.abs(t.getTagXW()) <= 0.125);
        }
      }, subsystemManager.getInternalAlgaeDetectorSubsystem()
    );

    subsystemManager.getInternalAlgaeDetectorSubsystem().setDefaultCommand(rumbleCommand);
    subsystemManager.getVisionSubsystem().setDefaultCommand(new VisionCameraSwitchingCommand(subsystemManager.getVisionSubsystem()));
  }

  public void scoreOperatorBindings() {
    testChooser.addOption("All tests", getTestCommand());
    Logger.recordOutput(RobotConstants.OperatorMessages.SUBSYSTEM_TEST, "No Current Test");
    SmartDashboard.putData("Test Chooser", testChooser);
    Shuffleboard.getTab("stuffs").add("Run Test", new RunTestCommand(testChooser));
    
    scoreOperatorPanel.button(RobotConstants.SCORE_OPERATOR_PANEL.BUTTONS.L1)
        .onTrue(
          new ConditionalCommand(
            commandFactory.getSafeElevatorPivotMoveCommand(Position.AUTO_L1),
            commandFactory.getSafeElevatorPivotMoveCommand(Position.L1),
            () -> xboxController.leftBumper().getAsBoolean() || xboxController.rightBumper().getAsBoolean()
          )
        )
        .onTrue(new InstantCommand(() ->  UserPolicy.getInstance().setAlgaeMode(false)))
        .onFalse(commandFactory.getSafeElevatorPivotMoveCommand(Position.HOME));
    
    scoreOperatorPanel.button(RobotConstants.SCORE_OPERATOR_PANEL.BUTTONS.L2)
        .onTrue(
          new ConditionalCommand(
            commandFactory.getSafeElevatorPivotMoveCommand(Position.AUTO_L2),
            commandFactory.getSafeElevatorPivotMoveCommand(Position.L2),
            () -> xboxController.leftBumper().getAsBoolean() || xboxController.rightBumper().getAsBoolean()
          )
        )
        .onTrue(new InstantCommand(() -> UserPolicy.getInstance().setAlgaeMode(false)))
        .onFalse(commandFactory.getSafeElevatorPivotMoveCommand(Position.HOME));

    scoreOperatorPanel.button(RobotConstants.SCORE_OPERATOR_PANEL.BUTTONS.L3)
        .onTrue(
          new ConditionalCommand(
            commandFactory.getSafeElevatorPivotMoveCommand(Position.AUTO_L3),
            commandFactory.getSafeElevatorPivotMoveCommand(Position.L3),
            () -> xboxController.leftBumper().getAsBoolean() || xboxController.rightBumper().getAsBoolean()
          )
        )
        .onTrue(new InstantCommand(() -> UserPolicy.getInstance().setAlgaeMode(false)))
        .onFalse(commandFactory.getSafeElevatorPivotMoveCommand(Position.HOME));

    scoreOperatorPanel.button(RobotConstants.SCORE_OPERATOR_PANEL.BUTTONS.L4)
        .onTrue(
          new ConditionalCommand(
            commandFactory.getSafeElevatorPivotMoveCommand(Position.AUTO_L4),
            commandFactory.getSafeElevatorPivotMoveCommand(Position.L4),
            () -> xboxController.leftBumper().getAsBoolean() || xboxController.rightBumper().getAsBoolean()
          )
        )
        .onTrue(new InstantCommand(() -> UserPolicy.getInstance().setAlgaeMode(false)))
        .onFalse(commandFactory.getSafeElevatorPivotMoveCommand(Position.HOME));

    scoreOperatorPanel.button(RobotConstants.SCORE_OPERATOR_PANEL.BUTTONS.ALGAE_GROUND)
        .onTrue(commandFactory.getSafeElevatorPivotMoveCommand(Position.ALGAE_GROUND))
        .onTrue(new InstantCommand(() -> UserPolicy.getInstance().setAlgaeMode(true)))
        .onFalse(
          new ConditionalCommand(
            commandFactory.getSafeElevatorPivotMoveCommand(Position.ALGAE_HOME),
            new SequentialCommandGroup(
              new InstantCommand(() -> UserPolicy.getInstance().setAlgaeMode(false)),
              commandFactory.getSafeElevatorPivotMoveCommand(Position.HOME)
            ),
            () -> RobotIO.getInstance().getInternalAlgaeDetectorOutput().hasAlgae()
          )
        );

    scoreOperatorPanel.button(RobotConstants.SCORE_OPERATOR_PANEL.BUTTONS.ALGAE_L2)
        .onTrue(commandFactory.getSafeElevatorPivotMoveCommand(Position.ALGAE_L2))
        .onTrue(new InstantCommand(() -> UserPolicy.getInstance().setAlgaeMode(true)))
        .onFalse(commandFactory.getSafeElevatorPivotMoveCommand(Position.ALGAE_HOME));

    scoreOperatorPanel.button(RobotConstants.SCORE_OPERATOR_PANEL.BUTTONS.ALGAE_L3)
        .onTrue(commandFactory.getSafeElevatorPivotMoveCommand(Position.ALGAE_L3))
        .onTrue(new InstantCommand(() -> UserPolicy.getInstance().setAlgaeMode(true)))
        .onFalse(commandFactory.getSafeElevatorPivotMoveCommand(Position.ALGAE_HOME));

        scoreOperatorPanel.button(RobotConstants.SCORE_OPERATOR_PANEL.BUTTONS.BARGE)
        .onTrue(commandFactory.getSafeElevatorPivotMoveCommand(Position.BARGE))
        .onTrue(new InstantCommand(() -> UserPolicy.getInstance().setAlgaeMode(true)))
        .onFalse(
          new ConditionalCommand(
            commandFactory.getSafeElevatorPivotMoveCommand(Position.ALGAE_HOME),
            new SequentialCommandGroup(
              new InstantCommand(() -> UserPolicy.getInstance().setAlgaeMode(false)),
              commandFactory.getSafeElevatorPivotMoveCommand(Position.HOME)
            ),
            () -> RobotIO.getInstance().getInternalAlgaeDetectorOutput().hasAlgae()
          )
        );

    intakeCommand = new IntakeCoralCommand(subsystemManager.getCoralMechanismSubsystem(), subsystemManager.getPivotSubsystem());
    fireCommand = new FireCoralCommand(subsystemManager.getCoralMechanismSubsystem(), LiveTuningHandler.getInstance().getValue("CoralMechanismSubsystem/FireSpeed"));
    fireCommandL1 = new FireCoralCommand(subsystemManager.getCoralMechanismSubsystem(), LiveTuningHandler.getInstance().getValue("CoralMechanismSubsystem/L1FireSpeed"));
    algaeFireCommand = new FireCoralCommand(subsystemManager.getCoralMechanismSubsystem(), LiveTuningHandler.getInstance().getValue("CoralMechanismSubsystem/AlgaeFireSpeed"));
    algaeFireCommand2 = new FireCoralCommand(subsystemManager.getCoralMechanismSubsystem(), LiveTuningHandler.getInstance().getValue("CoralMechanismSubsystem/AlgaeFireSpeed"));
    scoreOperatorPanel.button(RobotConstants.SCORE_OPERATOR_PANEL.BUTTONS.FIRE)
      .whileTrue(
        new ConditionalCommand(
          new ParallelCommandGroup(
            new ConditionalCommand(
              fireCommandL1,
              fireCommand, 
              () -> scoreOperatorPanel.button(RobotConstants.SCORE_OPERATOR_PANEL.BUTTONS.L1).getAsBoolean()
            ),
            new SequentialCommandGroup(
              new WaitUntilCommand(() -> !RobotIO.getInstance().getInternalCoralDetectorOutput().hasCoral()),
              new InstantCommand(() -> commandFactory.getSafeElevatorPivotMoveCommand(Position.HOME).schedule())
            )
          ),
          new ConditionalCommand(
            new IntakeAlgaeCommand(subsystemManager.getCoralMechanismSubsystem()),
            new ConditionalCommand(
              new ConditionalCommand(
                new ParallelCommandGroup(
                  algaeFireCommand2,
                  new SequentialCommandGroup(
                    new InstantCommand(() -> UserPolicy.getInstance().setAlgaeMode(false)),
                    new PivotMoveCommand(subsystemManager.getPivotSubsystem(), Position.FLICK_LEVEL)
                  )
                ),
                algaeFireCommand,
                () -> scoreOperatorPanel.button(RobotConstants.SCORE_OPERATOR_PANEL.BUTTONS.BARGE).getAsBoolean()
              ),
              intakeCommand,
              () -> scoreOperatorPanel.button(RobotConstants.SCORE_OPERATOR_PANEL.BUTTONS.BARGE).getAsBoolean() || scoreOperatorPanel.button(RobotConstants.SCORE_OPERATOR_PANEL.BUTTONS.ALGAE_GROUND).getAsBoolean()
            ),
            () -> scoreOperatorPanel.button(RobotConstants.SCORE_OPERATOR_PANEL.BUTTONS.ALGAE_L3).getAsBoolean() || scoreOperatorPanel.button(RobotConstants.SCORE_OPERATOR_PANEL.BUTTONS.ALGAE_L2).getAsBoolean()
          ),
          () -> RobotIO.getInstance().getInternalCoralDetectorOutput().hasCoral()
        )
      )
      .onFalse(new AlgaeHoldCommand(subsystemManager.getCoralMechanismSubsystem()));
  }

  public void alignOperatorBindings() {
    alignOperatorPanel.button(RobotConstants.ALIGN_OPERATOR_PANEL.BUTTONS.A)
        .onTrue(new InstantAnytimeCommand( () ->
            UserPolicy.getInstance().setTargetLocations(TargetLocation.BLUE_A, TargetLocation.RED_A)
        ));

    alignOperatorPanel.button(RobotConstants.ALIGN_OPERATOR_PANEL.BUTTONS.B)
        .onTrue(new InstantAnytimeCommand( () ->
            UserPolicy.getInstance().setTargetLocations(TargetLocation.BLUE_B, TargetLocation.RED_B)
        ));

    alignOperatorPanel.button(RobotConstants.ALIGN_OPERATOR_PANEL.BUTTONS.C)
        .onTrue(new InstantAnytimeCommand( () ->
            UserPolicy.getInstance().setTargetLocations(TargetLocation.BLUE_C, TargetLocation.RED_C)
        ));

    alignOperatorPanel.button(RobotConstants.ALIGN_OPERATOR_PANEL.BUTTONS.D)
        .onTrue(new InstantAnytimeCommand( () ->
            UserPolicy.getInstance().setTargetLocations(TargetLocation.BLUE_D, TargetLocation.RED_D)
        ));

    alignOperatorPanel.button(RobotConstants.ALIGN_OPERATOR_PANEL.BUTTONS.E)
        .onTrue(new InstantAnytimeCommand( () ->
            UserPolicy.getInstance().setTargetLocations(TargetLocation.BLUE_E, TargetLocation.RED_E)
        ));

    alignOperatorPanel.button(RobotConstants.ALIGN_OPERATOR_PANEL.BUTTONS.F)
        .onTrue(new InstantAnytimeCommand( () ->
            UserPolicy.getInstance().setTargetLocations(TargetLocation.BLUE_F, TargetLocation.RED_F)
        ));

    alignOperatorPanel.button(RobotConstants.ALIGN_OPERATOR_PANEL.BUTTONS.G)
        .onTrue(new InstantAnytimeCommand( () ->
            UserPolicy.getInstance().setTargetLocations(TargetLocation.BLUE_G, TargetLocation.RED_G)
        ));

    alignOperatorPanel.button(RobotConstants.ALIGN_OPERATOR_PANEL.BUTTONS.H)
        .onTrue(new InstantAnytimeCommand( () ->
            UserPolicy.getInstance().setTargetLocations(TargetLocation.BLUE_H, TargetLocation.RED_H)
        ));

    alignOperatorPanel.button(RobotConstants.ALIGN_OPERATOR_PANEL.BUTTONS.I)
        .onTrue(new InstantAnytimeCommand( () ->
            UserPolicy.getInstance().setTargetLocations(TargetLocation.BLUE_I, TargetLocation.RED_I)
        ));

    alignOperatorPanel.button(RobotConstants.ALIGN_OPERATOR_PANEL.BUTTONS.J)
        .onTrue(new InstantAnytimeCommand( () ->
            UserPolicy.getInstance().setTargetLocations(TargetLocation.BLUE_J, TargetLocation.RED_J)
        ));


    alignOperatorPanel.button(RobotConstants.ALIGN_OPERATOR_PANEL.BUTTONS.K)
        .onTrue(new InstantAnytimeCommand( () ->
            UserPolicy.getInstance().setTargetLocations(TargetLocation.BLUE_K, TargetLocation.RED_K)
        ));

    alignOperatorPanel.button(RobotConstants.ALIGN_OPERATOR_PANEL.BUTTONS.L)
        .onTrue(new InstantAnytimeCommand( () ->
            UserPolicy.getInstance().setTargetLocations(TargetLocation.BLUE_L, TargetLocation.RED_L)
        ));



    alignOperatorPanel.button(RobotConstants.ALIGN_OPERATOR_PANEL.BUTTONS.A)
    .or(alignOperatorPanel.button(RobotConstants.ALIGN_OPERATOR_PANEL.BUTTONS.B))
    .or(alignOperatorPanel.button(RobotConstants.ALIGN_OPERATOR_PANEL.BUTTONS.C))
    .or(alignOperatorPanel.button(RobotConstants.ALIGN_OPERATOR_PANEL.BUTTONS.D))
    .or(alignOperatorPanel.button(RobotConstants.ALIGN_OPERATOR_PANEL.BUTTONS.E))
    .or(alignOperatorPanel.button(RobotConstants.ALIGN_OPERATOR_PANEL.BUTTONS.F))
    .or(alignOperatorPanel.button(RobotConstants.ALIGN_OPERATOR_PANEL.BUTTONS.G))
    .or(alignOperatorPanel.button(RobotConstants.ALIGN_OPERATOR_PANEL.BUTTONS.H))
    .or(alignOperatorPanel.button(RobotConstants.ALIGN_OPERATOR_PANEL.BUTTONS.I))
    .or(alignOperatorPanel.button(RobotConstants.ALIGN_OPERATOR_PANEL.BUTTONS.J))
    .or(alignOperatorPanel.button(RobotConstants.ALIGN_OPERATOR_PANEL.BUTTONS.K))
    .or(alignOperatorPanel.button(RobotConstants.ALIGN_OPERATOR_PANEL.BUTTONS.L))
    .onFalse(new InstantAnytimeCommand( () -> 
          UserPolicy.getInstance().clearTargetLocations()
    ));
  }

  private SendableChooser<Command> getTestCommandChooser() {
    SendableChooser<Command> testCommandChooser = new SendableChooser<>();
    for (EntechSubsystem<?, ?> subsystem : subsystemManager.getSubsystemList()) {
      testCommandChooser.addOption(subsystem.getName(), subsystem.getTestCommand());
    }
    return testCommandChooser;
  }

  /*
   * These force commands to accept inputs, not raw joysticks and stuff also here we log any inputs
   * handed to consumers, so they dont have to
   */
  @Override
  public DebugInput getDebugInput() {
    DebugInput di = new DebugInput();
    RobotIO.processInput(di);
    return di;
  }

  @Override
  public DriveInput getDriveInput() {
    DriveInput di = new DriveInput();

    di.setGyroAngle(Rotation2d.fromDegrees(RobotIO.getInstance().getNavXOutput().getYaw()));
    di.setLatestOdometryPose(odometry.getEstimatedPose());
    di.setKey("initialRaw");

    if (DriverControllerUtils.currentControllerIsXbox()) {
      di.setXSpeed(-this.xboxController.getLeftY());
      di.setYSpeed(-this.xboxController.getLeftX());
      di.setRotation(DriverControllerUtils.getXboxRotation(this.xboxController));
    } else if (DriverControllerUtils.controllerIsPresent(RobotConstants.PORTS.CONTROLLER.TEST_JOYSTICK)) {
      di.setXSpeed(-this.joystickController.getY());
      di.setYSpeed(-this.joystickController.getX());
      di.setRotation(-this.joystickController.getZ());
    } else {
      di.setXSpeed(0);
      di.setYSpeed(0);
      di.setRotation(0);
    }

    RobotIO.processInput(di);
    return di;
  }

  @Override
  public OperatorInput getOperatorInput() {
    OperatorInput oi = new OperatorInput();
    RobotIO.processInput(oi);
    return oi;
  }

  public Command getTestCommand() {
    SequentialCommandGroup allTests = new SequentialCommandGroup();
    for (EntechSubsystem<?, ?> subsystem : subsystemManager.getSubsystemList()) {
      if (subsystem.isEnabled()) {
        addSubsystemTest(allTests, subsystem);
      }
    }
    allTests.addCommands(Commands.runOnce(() -> Logger
        .recordOutput(RobotConstants.OperatorMessages.SUBSYSTEM_TEST, "No Current Tests.")));
    return allTests;
  }

  private static void addSubsystemTest(SequentialCommandGroup group,
      EntechSubsystem<?, ?> subsystem) {

    group.addCommands(
        Commands.runOnce(() -> Logger.recordOutput(RobotConstants.OperatorMessages.SUBSYSTEM_TEST,
            String.format("%s: Start", subsystem.getName()))),
        subsystem.getTestCommand(),
        Commands.runOnce(() -> Logger.recordOutput(RobotConstants.OperatorMessages.SUBSYSTEM_TEST,
            String.format("%s: Finished", subsystem.getName()))));
  }
}
