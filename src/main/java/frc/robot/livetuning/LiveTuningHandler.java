package frc.robot.livetuning;

import java.util.Map;

import edu.wpi.first.networktables.DoublePublisher;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.robot.RobotConstants;

public class LiveTuningHandler {
    private static LiveTuningHandler INSTANCE;
    private NetworkTable table;
    
    public static LiveTuningHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LiveTuningHandler();
        }
        return INSTANCE;
    }

    private LiveTuningHandler() {
    }

    /**
     * Loads default values from constants into network tables then loads JSON values.
     */
    public void init() {
        table = NetworkTableInstance.getDefault().getTable("LiveTuning");
        resetToDefaults();
        resetToJSON();
    }

    public void resetToDefaults() {
        Map<String, Double> values = RobotConstants.LiveTuning.VALUES;

        for (Map.Entry<String, Double> value : values.entrySet()) {
            DoublePublisher publisher = table.getDoubleTopic(value.getKey()).publish();
            publisher.accept(value.getValue());
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
