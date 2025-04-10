package frc.robot.io;

import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.inputs.LoggableInputs;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.RobotConstants;
import frc.robot.commandchecker.SafeMovementChecker;
import frc.robot.subsystems.drive.DriveInput;
import frc.robot.subsystems.drive.DriveOutput;
import frc.robot.subsystems.elevator.ElevatorOutput;
import frc.robot.subsystems.gamepiecehandler.GamePieceHandlerOutput;
import frc.robot.subsystems.led.LEDOutput;
import frc.robot.subsystems.navx.NavXOutput;
import frc.robot.subsystems.pivot.PivotOutput;
import frc.robot.subsystems.vision.VisionOutput;

public class RobotIO implements DriveInputSupplier {
  private static final RobotIO instance = new RobotIO();

  public static RobotIO getInstance() {
    return instance;
  }

  public static void processInput(LoggableInputs in) {
    Logger.processInputs(in.getClass().getSimpleName(), in);
  }

  private RobotIO() {}

  @Override
  public DriveInput getDriveInput() {
    DriveInput di = new DriveInput();
    di.setGyroAngle(Rotation2d.fromDegrees(RobotIO.getInstance().getNavXOutput().getYaw()));
    di.setLatestOdometryPose(latestOdometryPose);
    di.setKey("initialRaw");
    di.setRotation(0.0);
    di.setXSpeed(0.0);
    di.setYSpeed(0.0);
    processInput(di);
    return di;
  }

  public DriveOutput getDriveOutput() {
    return latestDriveOutput;
  }

  public NavXOutput getNavXOutput() {
    return latestNavXOutput;
  }

  public VisionOutput getVisionOutput() {
    return latestVisionOutput;
  }

  public ElevatorOutput getElevatorOutput() {
    return latestElevatorOutput;
  }

  public PivotOutput getPivotOutput() {
    return latestPivotOutput;
  }

  public LEDOutput getLEDOutput() {
    return latestLEDOutput;
  }

  public Pose2d getOdometryPose() {
    return latestOdometryPose;
  }

  public GamePieceHandlerOutput getGamePieceHandlerOutput() {
    return latestGamePieceHandlerOutput;
  }

  public void updateNavx(NavXOutput no) {
    latestNavXOutput = no;
    no.log();
  }

  public void updateDrive(DriveOutput dro) {
    latestDriveOutput = dro;
    dro.log();
  }
  
  public void updateOdometryPose(Pose2d pose) {
    latestOdometryPose = pose;
    Logger.recordOutput("OdometryPose", pose);
  }

  public void updateVision(VisionOutput vo) {
    latestVisionOutput = vo;
    vo.log();
  }

  public void updateElevator(ElevatorOutput elo) {
    latestElevatorOutput = elo;
    elo.log();
  }

  public void updatePivot(PivotOutput po) {
    latestPivotOutput = po;
    po.log();
  }

  public void updateLED(LEDOutput ledo) {
    latestLEDOutput = ledo;
    ledo.log();
  }

  public void updateGamePieceHandler(GamePieceHandlerOutput cmo) {
    latestGamePieceHandlerOutput = cmo;
    cmo.log();
  }

  public boolean isSafeElevatorMove(double elev) {
    return moveChecker.isSafeElevatorMove(elev, latestPivotOutput.getCurrentPosition());
  }

  public boolean isSafePivotMove(double piv) {
    return moveChecker.isSafePivotMove(piv, latestElevatorOutput.getCurrentPosition());
  }

  private final SafeMovementChecker moveChecker = new SafeMovementChecker();

  private VisionOutput latestVisionOutput;
  private NavXOutput latestNavXOutput;
  private DriveOutput latestDriveOutput;
  private ElevatorOutput latestElevatorOutput;
  private PivotOutput latestPivotOutput;
  private LEDOutput latestLEDOutput;
  private GamePieceHandlerOutput latestGamePieceHandlerOutput;
   private Pose2d latestOdometryPose = RobotConstants.ODOMETRY.INITIAL_POSE;
}
