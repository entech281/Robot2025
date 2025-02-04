package frc.robot.subsystems.pivot;

import org.littletonrobotics.junction.LogTable;

import frc.entech.subsystems.SubsystemInput;

public class PivotInput implements SubsystemInput {
    private double requestedPosition;

    @Override
    public void toLog(LogTable table) {
        table.put("requestedPosition", requestedPosition);
    }

    @Override
    public void fromLog(LogTable table) {
        requestedPosition = table.get("requestedPosition", 0.0);
    }
}
