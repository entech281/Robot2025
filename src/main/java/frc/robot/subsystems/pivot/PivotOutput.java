package frc.robot.subsystems.pivot;

import org.littletonrobotics.junction.Logger;

import frc.entech.subsystems.SubsystemOutput;

public class PivotOutput extends SubsystemOutput{
    private double absoluteEncoder;
    private double motorEncoder;
    private double speed;
    private double isAtRequestedPosition;

    @Override
    public void toLog() {
        Logger.recordOutput("ElevatorOutput/absoluteEncoder", absoluteEncoder);
        Logger.recordOutput("ElevatorOutput/motorEncoder", motorEncoder);
        Logger.recordOutput("ElevatorOutput/speed", speed);
        Logger.recordOutput("ElevatorOutput/isAtRequestedPosition", isAtRequestedPosition);
    }

    public double getAbsoluteEncoder() {
        return this.absoluteEncoder;
    }

    public void setAbsoluteEncoder(double absoluteEncoder) {
        this.absoluteEncoder = absoluteEncoder;
    }

    public double getMotorEncoder() {
        return this.motorEncoder;
    }

    public void setMotorEncoder(double motorEncoder) {
        this.motorEncoder = motorEncoder;
    }

    public double getSpeed() {
        return this.speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getIsAtRequestedPosition() {
        return this.isAtRequestedPosition;
    }

    public void setIsAtRequestedPosition(double isAtRequestedPosition) {
        this.isAtRequestedPosition = isAtRequestedPosition;
    }
}
