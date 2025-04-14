package frc.robot.commands;

import java.util.List;
import java.util.Optional;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.entech.commands.EntechCommand;
import frc.robot.CommandFactory;
import frc.robot.Position;
import frc.robot.operation.UserPolicy;
import frc.robot.subsystems.coralmechanism.CoralMechanismSubsystem;
import frc.robot.subsystems.drive.DriveSubsystem;
import frc.robot.subsystems.elevator.ElevatorSubsystem;
import frc.robot.subsystems.pivot.PivotSubsystem;
import frc.robot.subsystems.vision.TargetLocation;
import frc.robot.subsystems.vision.VisionInput;

public class AutoDealgifyCommand extends EntechCommand {

    private final DriveSubsystem driveSubsystem;
    private final CommandFactory commandFactory;
    private final CoralMechanismSubsystem coralMechanismSubsystem;
    private Command runningCommand;
    private TargetLocation currentLoc;
    ElevatorSubsystem elevatorSubsystem;
    PivotSubsystem pivotSubsystem;

    public AutoDealgifyCommand(DriveSubsystem driveSubsystem, CoralMechanismSubsystem coralMechanismSubsystem, ElevatorSubsystem elevatorSubsystem, PivotSubsystem pivotSubsystem, CommandFactory commandFactory) {
        this.driveSubsystem = driveSubsystem;
        this.commandFactory = commandFactory;
        this.coralMechanismSubsystem = coralMechanismSubsystem;
        this.elevatorSubsystem = elevatorSubsystem;
        this.pivotSubsystem = pivotSubsystem;
    }

    @Override
    public void initialize() {
        Position targetPos;

        List<TargetLocation> pos = UserPolicy.getInstance().getSelectedTargetLocations().stream().toList();
                
        if (pos.isEmpty()) {
            return;
        }

        currentLoc = pos.get(0);

        Optional<Alliance> alliance = DriverStation.getAlliance();

        if (currentLoc.tagID > 15 && alliance.isPresent() && alliance.get().equals(Alliance.Red)) {
            currentLoc = pos.get(1);
        }

        if (currentLoc.tagID < 15 && alliance.isPresent() && alliance.get().equals(Alliance.Blue)) {
            currentLoc = pos.get(1);
        }

        if (alliance.isPresent() && alliance.get().equals(Alliance.Blue)) {
            targetPos = currentLoc.tagID % 2 == 0 ? Position.ALGAE_L3 : Position.ALGAE_L2;
        } else {
            targetPos = currentLoc.tagID % 2 == 0 ? Position.ALGAE_L2 : Position.ALGAE_L3;
        }
        if (targetPos == null) {
            return;
        }

        runningCommand = new SequentialCommandGroup(

            commandFactory.formSafeMovementCommand(Position.HOME),

            // Drive backward continuously for 0.5 seconds
            new RunCommand(() -> driveSubsystem.pathFollowDrive(new ChassisSpeeds(-1.0, 0.0, 0.0)), driveSubsystem).withTimeout(1.0),

            // Stop the drivetrain and perform algae motions
            new InstantCommand(() -> {
                driveSubsystem.pathFollowDrive(new ChassisSpeeds(0.0, 0.0, 0.0));
                UserPolicy.getInstance().setAlgaeMode(true);
            }),

            new SequentialCommandGroup(
                new PivotMoveCommand(pivotSubsystem, Position.SAFE_EXTEND),
                new ElevatorMoveCommand(elevatorSubsystem, targetPos),
                new PivotMoveCommand(pivotSubsystem, targetPos)
            ),

            // Drive laterally based on the current side
            new RunCommand(() -> {
                if (currentLoc.camera.equals(VisionInput.Camera.TOP)) {
                    driveSubsystem.pathFollowDrive(new ChassisSpeeds(0.0, -0.5, 0.0));
                } else {
                    driveSubsystem.pathFollowDrive(new ChassisSpeeds(0.0, 0.5, 0.0));
                }
            }).withTimeout(0.5),

            new ParallelCommandGroup(

                new IntakeAlgaeCommand(coralMechanismSubsystem),

            // Drive forward continuously for 0.5 seconds and start algae intake
                new RunCommand(() -> driveSubsystem.pathFollowDrive(new ChassisSpeeds(1.0, 0.0, 0.0))).withTimeout(1.0)
            ),
                       
            // Stop the drivetrain and continue algae intake
            new InstantCommand(() -> driveSubsystem.pathFollowDrive(new ChassisSpeeds(0.0, 0.0, 0.0)), driveSubsystem),

            // Drive backward to return to the starting position and reset elevator pivot
            new ParallelDeadlineGroup(
                new RunCommand(() -> driveSubsystem.pathFollowDrive(new ChassisSpeeds(-1.0, 0.0, 0.0)), driveSubsystem).withTimeout(1.0),
                new AlgaeHoldCommand(coralMechanismSubsystem)
            ),

            new ParallelDeadlineGroup(
                new InstantCommand(() -> {
                    driveSubsystem.pathFollowDrive(new ChassisSpeeds(0.0, 0.0, 0.0));
                    commandFactory.getSafeElevatorPivotMoveCommand(Position.ALGAE_HOME).schedule();
                }, driveSubsystem),
                new AlgaeHoldCommand(coralMechanismSubsystem)
            ),
            new WaitCommand(1.0).andThen(new InstantCommand(() -> {}, coralMechanismSubsystem))
        );

        runningCommand.schedule();
    }

    @Override
    public void execute() {}

    @Override
    public boolean isFinished() {
        return runningCommand == null || runningCommand.isFinished();
    }
}
