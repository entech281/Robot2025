package frc.robot.subsystems.pivot;

import org.littletonrobotics.junction.LogTable;

import frc.entech.subsystems.SubsystemInput;

public class PivotInput implements SubsystemInput {
    private boolean activate = true;
    private double requestedPosition = 0.0;

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

    public enum Position {
        HOME("PivotSubsystem/home"),
        L1("PivotSubsystem/L1"),
        L2("PivotSubsystem/L2"),
        L3("PivotSubsystem/L3"),
        L4("PivotSubsystem/L4"),
        ALGAE_L2("PivotSubsystem/algae_L2"),
        ALGAE_L3("PivotSubsystem/algae_L3"),
        ALGAE_GROUND("PivotSubsystem/algae_ground"),
        BARGE("PivotSubsystem/barge");

        public final String label;

        private Position(String label) {
            this.label = label;
        }
    }
}