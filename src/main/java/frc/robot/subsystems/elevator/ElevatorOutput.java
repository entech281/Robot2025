package frc.robot.subsystems.elevator;

import org.littletonrobotics.junction.Logger;

import frc.entech.subsystems.SubsystemOutput;

public class ElevatorOutput extends SubsystemOutput{
    private double encoderRight;
    private double encoderLeft;
    private double position;
    private double speedRight;
    private double speedLeft;
    private boolean lowerLimit;
    private boolean upperLimit;
    private boolean isAtRequestedPosition;

    @Override
    public void toLog() {
        Logger.recordOutput("ElevatorOutput/encoderRight", encoderRight);
        Logger.recordOutput("ElevatorOutput/encoderLeft", encoderLeft);
        Logger.recordOutput("ElevatorOutput/speedRight", speedRight);
        Logger.recordOutput("ElevatorOutput/speedLeft", speedLeft);
        Logger.recordOutput("ElevatorOutput/position", position);
        Logger.recordOutput("ElevatorOutput/lowerLimit", lowerLimit);
        Logger.recordOutput("ElevatorOutput/upperLimit", upperLimit);
        Logger.recordOutput("ElevatorOutput/isAtRequestedPosition", isAtRequestedPosition);
    }

    public double getEncoderRight() {
        return this.encoderRight;
    }

    public void setEncoderRight(double encoderRight) {
        this.encoderRight = encoderRight;
    }

    public double getEncoderLeft() {
        return this.encoderLeft;
    }

    public void setEncoderLeft(double encoderLeft) {
        this.encoderLeft = encoderLeft;
    }

    public double getPosition() {
        return this.position;
    }

    public void setPosition(double position) {
        this.position = position;
    }

    public double getSpeedRight() {
        return this.speedRight;
    }

    public void setSpeedRight(double speedRight) {
        this.speedRight = speedRight;
    }

    public double getSpeedLeft() {
        return this.speedLeft;
    }

    public void setSpeedLeft(double speedLeft) {
        this.speedLeft = speedLeft;
    }

    public boolean isLowerLimit() {
        return this.lowerLimit;
    }

    public void setLowerLimit(boolean lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public boolean isUpperLimit() {
        return this.upperLimit;
    }

    public void setUpperLimit(boolean upperLimit) {
        this.upperLimit = upperLimit;
    }

    public boolean isIsAtRequestedPosition() {
        return this.isAtRequestedPosition;
    }

    public void setIsAtRequestedPosition(boolean isAtRequestedPosition) {
        this.isAtRequestedPosition = isAtRequestedPosition;
    }

}
