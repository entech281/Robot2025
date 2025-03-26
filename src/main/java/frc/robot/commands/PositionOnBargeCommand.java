package frc.robot.commands;

import edu.wpi.first.math.geometry.Rotation2d;
import frc.entech.commands.EntechCommand;
import frc.robot.subsystems.bargedetector.BargeDetectorOutput;
import frc.robot.subsystems.bargedetector.BargeDetectorSubsystem;
import frc.robot.subsystems.drive.DriveInput;
import frc.robot.subsystems.drive.DriveSubsystem;

public class PositionOnBargeCommand extends EntechCommand {
    private BargeDetectorSubsystem bargeDetectorSubsystem;
    private DriveSubsystem driveSubsystem;

    public PositionOnBargeCommand(BargeDetectorSubsystem bargeDetectorSubsystem, DriveSubsystem driveSubsystem) {
        this.bargeDetectorSubsystem = bargeDetectorSubsystem;
        this.driveSubsystem = driveSubsystem;
    }

    @Override
    public void initialize() {}

    @Override
    public void execute() {
        BargeDetectorOutput output = bargeDetectorSubsystem.toOutputs();

        if (output.hasLine()) {
            new XDriveCommand(driveSubsystem).schedule();
        } else {
            DriveInput dInput = new DriveInput();
            dInput.setGyroAngle(Rotation2d.fromDegrees(0.0));
            dInput.setXSpeed(-.5);

            driveSubsystem.updateInputs(dInput);
        }
    }

    @Override
    public boolean isFinished() {
        return bargeDetectorSubsystem.toOutputs().hasLine();
    }
}
