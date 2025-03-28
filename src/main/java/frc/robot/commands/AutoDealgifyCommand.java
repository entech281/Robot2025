package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.entech.commands.EntechCommand;
import frc.robot.CommandFactory;
import frc.robot.Position;
import frc.robot.io.DriveInputSupplier;
import frc.robot.subsystems.coralmechanism.CoralMechanismSubsystem;
import frc.robot.subsystems.drive.DriveInput;
import frc.robot.subsystems.drive.DriveSubsystem;

public class AutoDealgifyCommand extends EntechCommand{

    private final DriveSubsystem driveSubsystem;
    private final Position targetPos;
    private final CommandFactory commandFactory;
    private final CoralMechanismSubsystem coralMechanismSubsystem;
    private Command runningCommand;
    private final String curSide;
    private final DriveInputSupplier driveInputSupplier;
    
    public AutoDealgifyCommand(DriveInputSupplier driveInputSupplier,DriveSubsystem driveSubsystem, CoralMechanismSubsystem coralMechanismSubsystem, CommandFactory commandFactory, Position targetPos, String curSide) {
        this.driveSubsystem = driveSubsystem;
        this.targetPos = targetPos;
        this.commandFactory = commandFactory;
        this.coralMechanismSubsystem = coralMechanismSubsystem;
        this.curSide = curSide;
        this.driveInputSupplier = driveInputSupplier;
    }

    @Override
    public void initialize() {
        DriveInput driveInput = driveInputSupplier.getDriveInput();
        runningCommand = new SequentialCommandGroup(new InstantCommand(() -> {
            driveInput.setXSpeed(-1.0);
            driveSubsystem.updateInputs(driveInput);
        }), new WaitCommand(0.5),
        new InstantCommand(() -> {
            driveInput.setXSpeed(0.0);
            driveSubsystem.updateInputs(driveInput);
            commandFactory.getSafeElevatorPivotMoveCommand(targetPos).schedule();
            

            
            if(this.curSide.equals("left")) {
                driveInput.setYSpeed(-0.5);
            } else {
                driveInput.setYSpeed(0.5);
            }

            driveSubsystem.updateInputs(driveInput);
        }), new WaitCommand(0.15), new InstantCommand(() -> {
            driveInput.setXSpeed(1.0);
            driveInput.setYSpeed(0.0);
            driveSubsystem.updateInputs(driveInput);
            new IntakeAlgaeCommand(coralMechanismSubsystem).schedule();
        }), new WaitCommand(0.5),
        new InstantCommand(() -> {
            driveInput.setXSpeed(0.0);
            driveSubsystem.updateInputs(driveInput);
        }));
        runningCommand.schedule();
    }

    @Override
    public void execute() {}

    @Override
    public boolean isFinished() {
        return runningCommand.isFinished();
    }
}
