package frc.robot.subsystems.pivot;

import org.littletonrobotics.junction.Logger;

import frc.entech.subsystems.SubsystemOutput;

public class PivotOutput extends SubsystemOutput {
    private double absoluteEncoder;
    private double motorEncoder;
    private double speed;
    private boolean atRequestedPosition;
    private boolean moving = false;
    private boolean brakeModeEnabled = false;
    private double requestedPosition = 0.0;
    private double currentPosition = 0.0;

    @Override
    public void toLog() {
        Logger.recordOutput("PivotOutput/absoluteEncoder", absoluteEncoder);
        Logger.recordOutput("PivotOutput/motorEncoder", motorEncoder);
        Logger.recordOutput("PivotOutput/speed", speed);
        Logger.recordOutput("PivotOutput/isAtRequestedPosition", atRequestedPosition);
        Logger.recordOutput("PivotOutput/moving", moving);
        Logger.recordOutput("PivotOutput/brakeModeEnabled", brakeModeEnabled);
        Logger.recordOutput("PivotOutput/requestedPosition", requestedPosition);
        Logger.recordOutput("PivotOutput/currentPosition", currentPosition);
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

    public boolean isAtRequestedPosition() {
        return this.atRequestedPosition;
    }

    public void setAtRequestedPosition(boolean atRequestedPosition) {
        this.atRequestedPosition = atRequestedPosition;
    }

    public boolean isMoving() {
        return this.moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public boolean isBrakeModeEnabled() {
        return this.brakeModeEnabled;
    }

    public void setBrakeModeEnabled(boolean brakeModeEnabled) {
        this.brakeModeEnabled = brakeModeEnabled;
    }

    public double getRequestedPosition() {
        return this.requestedPosition;
    }

    public void setRequestedPosition(double requestedPosition) {
        this.requestedPosition = requestedPosition;
    }

    public double getCurrentPosition() {
        return this.currentPosition;
    }

    public void setCurrentPosition(double currentPosition) {
        this.currentPosition = currentPosition;
    }
}