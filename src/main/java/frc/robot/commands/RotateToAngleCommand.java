package frc.robot.commands;

import java.util.function.DoubleSupplier;

import frc.entech.commands.EntechCommand;
import frc.robot.io.RobotIO;
import frc.robot.operation.UserPolicy;

public class RotateToAngleCommand extends EntechCommand {
    private static final double TOLERANCE = 0.75;
    private final DoubleSupplier angle;
    
    public RotateToAngleCommand(DoubleSupplier angle) {
        this.angle = angle;
    }

    @Override
    public void end(boolean interrupted) {
        UserPolicy.getInstance().setAligningToAngle(false);
    }

    @Override
    public void execute() {
        UserPolicy.getInstance().setAligningToAngle(true);
        UserPolicy.getInstance().setTargetAngle(angle.getAsDouble());
    }

    @Override
    public void initialize() {
        execute();
    }

    @Override
    public boolean isFinished() {
        return Math.abs(RobotIO.getInstance().getOdometryPose().getRotation().getDegrees() - angle.getAsDouble()) <= TOLERANCE;
    }

    
}
