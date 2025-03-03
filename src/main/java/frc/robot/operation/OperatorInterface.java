package frc.robot.operation;

import org.littletonrobotics.junction.Logger;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.entech.subsystems.EntechSubsystem;
import frc.robot.CommandFactory;
import frc.robot.Position;
import frc.robot.RobotConstants;
import frc.robot.SubsystemManager;
import frc.robot.commands.DriveCommand;
import frc.robot.commands.ElevatorUpCommand;
import frc.robot.commands.ElevatorDownCommand;
import frc.robot.commands.ElevatorMoveCommand;
import frc.robot.commands.FireCoralCommand;
import frc.robot.commands.GyroReset;
import frc.robot.commands.IntakeCoralCommand;
import frc.robot.commands.PivotMoveCommand;
import frc.robot.commands.ResetOdometryCommand;
import frc.robot.commands.RunTestCommand;
import frc.robot.commands.TwistCommand;
import frc.robot.commands.VisionCameraSwitchingCommand;
import frc.robot.commands.XDriveCommand;
import frc.robot.io.DebugInput;
import frc.robot.io.DebugInputSupplier;
import frc.robot.io.DriveInputSupplier;
import frc.robot.io.OperatorInput;
import frc.robot.io.OperatorInputSupplier;
import frc.robot.io.RobotIO;
import frc.robot.processors.OdometryProcessor;
import frc.robot.subsystems.drive.DriveInput;
import frc.robot.subsystems.led.TestLEDCommand;

public class OperatorInterface
    implements DriveInputSupplier, DebugInputSupplier, OperatorInputSupplier {
  private CommandJoystick joystickController;
  private CommandXboxController xboxController;

  private CommandXboxController tuningController;

  private CommandJoystick operatorPanel;

  private final CommandFactory commandFactory;
  private final SubsystemManager subsystemManager;
  private final OdometryProcessor odometry;

  private final SendableChooser<Command> testChooser;

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

    if (DriverStation.isJoystickConnected(RobotConstants.PORTS.CONTROLLER.PANEL)) {
      operatorPanel = new CommandJoystick(RobotConstants.PORTS.CONTROLLER.PANEL);
      operatorBindings();
    }
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
        
    xboxController.button(4)
      .onTrue(new ElevatorUpCommand(subsystemManager.getElevatorSubsystem()));

    xboxController.button(1)
      .onTrue(new ElevatorDownCommand(subsystemManager.getElevatorSubsystem()));

    xboxController.button(2)
      .whileTrue(new IntakeCoralCommand(subsystemManager.getCoralMechanismSubsystem()));

    xboxController.button(0)
      .whileTrue(new FireCoralCommand(subsystemManager.getCoralMechanismSubsystem(), 1.1));

    xboxController.button(6)
        .whileTrue(commandFactory.getAlignmentCommand());

    xboxController.button(RobotConstants.PORTS.CONTROLLER.BUTTONS_XBOX.DRIVE_X)
        .whileTrue(new XDriveCommand(subsystemManager.getDriveSubsystem()));
    
    xboxController.button(RobotConstants.PORTS.CONTROLLER.BUTTONS_XBOX.RESET_ODOMETRY)
        .onTrue(new ResetOdometryCommand(odometry));

    subsystemManager.getVisionSubsystem().setDefaultCommand(new VisionCameraSwitchingCommand(subsystemManager.getVisionSubsystem(), xboxController::getRightY));
  }

  public void operatorBindings() {
    testChooser.addOption("All tests", getTestCommand());
    Logger.recordOutput(RobotConstants.OperatorMessages.SUBSYSTEM_TEST, "No Current Test");
    SmartDashboard.putData("Test Chooser", testChooser);

    testChooser.addOption("All tests", getTestCommand());

    operatorPanel.button(RobotConstants.OPERATOR_PANEL.BUTTONS.RUN_TEST)
        .onTrue(new RunTestCommand(testChooser));
    
    operatorPanel.button(RobotConstants.OPERATOR_PANEL.BUTTONS.L1)
        .onTrue(new ElevatorMoveCommand(subsystemManager.getElevatorSubsystem(), Position.L1));
    
    operatorPanel.button(RobotConstants.OPERATOR_PANEL.BUTTONS.L2)
        .onTrue(new ElevatorMoveCommand(subsystemManager.getElevatorSubsystem(), Position.L2));

    operatorPanel.button(RobotConstants.OPERATOR_PANEL.BUTTONS.L3)
        .onTrue(new ElevatorMoveCommand(subsystemManager.getElevatorSubsystem(), Position.L3));

    operatorPanel.button(RobotConstants.OPERATOR_PANEL.BUTTONS.L4)
        .onTrue(new ElevatorMoveCommand(subsystemManager.getElevatorSubsystem(), Position.L4));

    operatorPanel.button(RobotConstants.OPERATOR_PANEL.BUTTONS.ALGAE_GROUND)
        .onTrue(new ElevatorMoveCommand(subsystemManager.getElevatorSubsystem(), Position.ALGAE_GROUND));

    operatorPanel.button(RobotConstants.OPERATOR_PANEL.BUTTONS.ALGAE_L2)
        .onTrue(new ElevatorMoveCommand(subsystemManager.getElevatorSubsystem(), Position.ALGAE_L2));

    operatorPanel.button(RobotConstants.OPERATOR_PANEL.BUTTONS.ALGAE_L3)
        .onTrue(new ElevatorMoveCommand(subsystemManager.getElevatorSubsystem(), Position.ALGAE_L3));

    operatorPanel.button(RobotConstants.OPERATOR_PANEL.BUTTONS.BARGE)
        .onTrue(new ElevatorMoveCommand(subsystemManager.getElevatorSubsystem(), Position.BARGE));

    operatorPanel.button(RobotConstants.OPERATOR_PANEL.BUTTONS.L1)
        .onTrue(new PivotMoveCommand(subsystemManager.getPivotSubsystem(), Position.L1));
    
    operatorPanel.button(RobotConstants.OPERATOR_PANEL.BUTTONS.L2)
        .onTrue(new PivotMoveCommand(subsystemManager.getPivotSubsystem(), Position.L2));

    operatorPanel.button(RobotConstants.OPERATOR_PANEL.BUTTONS.L3)
        .onTrue(new PivotMoveCommand(subsystemManager.getPivotSubsystem(), Position.L3));

    operatorPanel.button(RobotConstants.OPERATOR_PANEL.BUTTONS.L4)
        .onTrue(new PivotMoveCommand(subsystemManager.getPivotSubsystem(), Position.L4));

    operatorPanel.button(RobotConstants.OPERATOR_PANEL.BUTTONS.ALGAE_GROUND)
        .onTrue(new PivotMoveCommand(subsystemManager.getPivotSubsystem(), Position.ALGAE_GROUND));

    operatorPanel.button(RobotConstants.OPERATOR_PANEL.BUTTONS.ALGAE_L2)
        .onTrue(new PivotMoveCommand(subsystemManager.getPivotSubsystem(), Position.ALGAE_L2));

    operatorPanel.button(RobotConstants.OPERATOR_PANEL.BUTTONS.ALGAE_L3)
        .onTrue(new PivotMoveCommand(subsystemManager.getPivotSubsystem(), Position.ALGAE_L3));

    operatorPanel.button(RobotConstants.OPERATOR_PANEL.BUTTONS.BARGE)
        .onTrue(new PivotMoveCommand(subsystemManager.getPivotSubsystem(), Position.BARGE));

      operatorPanel.button(RobotConstants.OPERATOR_PANEL.BUTTONS.FIRE)
        .onTrue(new IntakeCoralCommand(subsystemManager.getCoralMechanismSubsystem()));
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
