package frc.robot.livetuning;

import java.util.Map;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.robot.RobotConstants;

public class LiveTuningHandler {
    private static final LiveTuningHandler INSTANCE = new LiveTuningHandler();
    private final NetworkTable table;
    
    public static LiveTuningHandler getInstance() {
        return INSTANCE;
    }

    private LiveTuningHandler() {
        table = NetworkTableInstance.getDefault().getTable("LiveTuning");
    }

    /**
     * Loads default values from constants into network tables then loads JSON values.
     */
    public void init() {
        resetToDefaults();
        resetToJSON();
    }

    public void resetToDefaults() {
        Map<String, Double> values = RobotConstants.LiveTuning.VALUES;
        for (Map.Entry<String, Double> value : values.entrySet()) {
            try (NetworkTableEntry entry = new NetworkTableEntry(NetworkTableInstance.getDefault(), 0)) {
                entry.setDouble(value.getValue());
                table.putValue(value.getKey(), entry.getValue());
            }
        }
    }

    public void resetToJSON() {

    }

    public void saveToJSON() {

    }

    public double getValue(String key) {
        return table.getEntry(key).getDouble(0.0);
    }
}
