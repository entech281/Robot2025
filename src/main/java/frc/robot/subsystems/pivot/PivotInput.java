package frc.robot.subsystems.pivot;

import org.littletonrobotics.junction.LogTable;

import frc.entech.subsystems.SubsystemInput;

public class PivotInput implements SubsystemInput {
    private boolean activate = true;
    private double requestedPosition = 15.0;

    @Override
    public void toLog(LogTable table) {
        table.put("Activate", activate);
        table.put("RequestedPosition", requestedPosition);
    }

    @Override
    public void fromLog(LogTable table) {
        activate = table.get("Activate", activate);
        requestedPosition = table.get("RequestedPosition", 0.0);
    }

    public boolean getActivate() {
        return this.activate;
    }

    public void setActivate(boolean activate) {
        this.activate = activate;
    }

    public double getRequestedPosition() {
        return this.requestedPosition;
    }

    public void setRequestedPosition(double requestedPosition) {
        this.requestedPosition = requestedPosition;
    }
}