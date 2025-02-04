package frc.robot.subsystems.elevator;

import org.littletonrobotics.junction.LogTable;

import frc.entech.subsystems.SubsystemInput;

public class ElevatorInput implements SubsystemInput {
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
