package frc.entech.util;
import edu.wpi.first.math.MathUtil;

// Calculate the yaw setpoint for auto alignment with AprilTag
// Algorithm assumes that as we get closer to the target, the yaw should get closer to the alignment value
// Closeness to the target is determined from the tag width (in pixels) ==> larger == closer
// We want the yaw to be at the desired value by the time the april tag width hits a magic number (FINAL_TAG_WIDTH_PX)

public class YawSetPointCalculator {

    private int start_tag_width_px;
    private int current_tag_width_px = 0;
    private final int FINAL_TAG_WIDTH_PX = 400;  // CAUTION: depends on camera resolution
    private double initial_yaw = 0.0;
    private double final_yaw = 0.0;
    private double current_yaw_setpoint;

    public YawSetPointCalculator(int tag_width_px, double current_yaw_angle, double desired_yaw_angle ) {
        this.start_tag_width_px = tag_width_px;
        this.current_tag_width_px = tag_width_px;
        this.initial_yaw = current_yaw_angle;
        this.final_yaw = desired_yaw_angle;
    }

    public double get(int tag_width_px) {
        // Make sure tag width never decreases
        current_tag_width_px = Math.max(tag_width_px,current_tag_width_px);

        // cap ratio into the 0.0-1.0 range
        double ratio = (double)(current_tag_width_px - start_tag_width_px)/(double)(FINAL_TAG_WIDTH_PX - start_tag_width_px);
        ratio = MathUtil.clamp(ratio,0.0,1.0);

        current_yaw_setpoint = ratio*final_yaw + (1.0-ratio)*initial_yaw;
        return current_yaw_setpoint;
    }

}
