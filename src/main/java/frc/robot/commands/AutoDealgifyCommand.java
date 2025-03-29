package frc.robot.commands;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.entech.commands.EntechCommand;
import frc.robot.CommandFactory;
import frc.robot.Position;
import frc.robot.io.DriveInputSupplier;
import frc.robot.operation.UserPolicy;
import frc.robot.subsystems.coralmechanism.CoralMechanismSubsystem;
import frc.robot.subsystems.drive.DriveSubsystem;

public class AutoDealgifyCommand extends EntechCommand {

    private final DriveSubsystem driveSubsystem;
    private final Position targetPos;
    private final CommandFactory commandFactory;
    private final CoralMechanismSubsystem coralMechanismSubsystem;
    private Command runningCommand;
    private final String curSide;

    public AutoDealgifyCommand(DriveInputSupplier driveInputSupplier, DriveSubsystem driveSubsystem, CoralMechanismSubsystem coralMechanismSubsystem, CommandFactory commandFactory, Position targetPos, String curSide) {
        this.driveSubsystem = driveSubsystem;
        this.targetPos = targetPos;
        this.commandFactory = commandFactory;
        this.coralMechanismSubsystem = coralMechanismSubsystem;
        this.curSide = curSide;
    }

    @Override
    public void initialize() {
        runningCommand = new SequentialCommandGroup(
            // Drive backward continuously for 0.5 seconds
            new RunCommand(() -> driveSubsystem.pathFollowDrive(new ChassisSpeeds(-1.0, 0.0, 0.0)), driveSubsystem).withTimeout(1.0),

            // Stop the drivetrain and perform algae motions
            new InstantCommand(() -> {
                driveSubsystem.pathFollowDrive(new ChassisSpeeds(0.0, 0.0, 0.0));
                UserPolicy.getInstance().setAlgaeMode(true);
                commandFactory.getSafeElevatorPivotMoveCommand(targetPos).schedule();
            }),

            new WaitCommand(1.0),

            // Drive laterally based on the current side
            new RunCommand(() -> {
                if (this.curSide.equals("left")) {
                    driveSubsystem.pathFollowDrive(new ChassisSpeeds(0.0, -0.5, 0.0));
                } else {
                    driveSubsystem.pathFollowDrive(new ChassisSpeeds(0.0, 0.5, 0.0));
                }
            }, driveSubsystem).withTimeout(0.5),

            // Drive forward continuously for 0.5 seconds and start algae intake
            new RunCommand(() -> {
                driveSubsystem.pathFollowDrive(new ChassisSpeeds(1.0, 0.0, 0.0));
                new IntakeAlgaeCommand(coralMechanismSubsystem).schedule();
        }).withTimeout(1.0),
            // new RunCommand(() -> new AutoIntakeAlgaeCommand(coralMechanismSubsystem).schedule()).withTimeout(1.0),

            // Stop the drivetrain and continue algae intake
            new InstantCommand(() -> {
                driveSubsystem.pathFollowDrive(new ChassisSpeeds(0.0, 0.0, 0.0));
                new AutoIntakeAlgaeCommand(coralMechanismSubsystem).schedule();
            }),

            // Wait for 3 seconds to complete algae intake
            new WaitCommand(2),

            // Drive backward to return to the starting position and reset elevator pivot
            new RunCommand(() -> driveSubsystem.pathFollowDrive(new ChassisSpeeds(-1.0, 0.0, 0.0)), driveSubsystem).withTimeout(1.0),
            new InstantCommand(() -> {
                driveSubsystem.pathFollowDrive(new ChassisSpeeds(0.0, 0.0, 0.0));
                commandFactory.getSafeElevatorPivotMoveCommand(Position.ALGAE_HOME).schedule();
            }),

            // Stop the drivetrain
            new InstantCommand(() -> driveSubsystem.pathFollowDrive(new ChassisSpeeds(0.0, 0.0, 0.0)))
        );

        runningCommand.schedule();
    }

    @Override
    public void execute() {}

    @Override
    public boolean isFinished() {
        return runningCommand.isFinished();
    }
}
