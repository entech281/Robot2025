package frc.robot.subsystems.vision;

import java.util.Objects;

public class TargetLocation {
    public int tagID;
    public VisionInput.Camera camera;

    public TargetLocation (int tagID, VisionInput.Camera camera) {
        this.tagID = tagID;
        this.camera = camera;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass()!= o.getClass()) {
            return false;
        }
        TargetLocation that = (TargetLocation) o;
        return Objects.equals(tagID, that.tagID) && camera == that.camera;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tagID, camera);
    }

    public static final TargetLocation RED_LEFT_N = new TargetLocation(10, VisionInput.Camera.TOP);
    public static final TargetLocation RED_RIGHT_N = new TargetLocation(10, VisionInput.Camera.SIDE);
    public static final TargetLocation RED_LEFT_NE = new TargetLocation(9, VisionInput.Camera.TOP);
    public static final TargetLocation RED_RIGHT_NE = new TargetLocation(9, VisionInput.Camera.SIDE);
    public static final TargetLocation RED_LEFT_SE = new TargetLocation(8, VisionInput.Camera.TOP);
    public static final TargetLocation RED_RIGHT_SE = new TargetLocation(8, VisionInput.Camera.SIDE);
    public static final TargetLocation RED_LEFT_S = new TargetLocation(7, VisionInput.Camera.TOP);
    public static final TargetLocation RED_RIGHT_S = new TargetLocation(7, VisionInput.Camera.SIDE);
    public static final TargetLocation RED_LEFT_SW = new TargetLocation(6, VisionInput.Camera.TOP);
    public static final TargetLocation RED_RIGHT_SW = new TargetLocation(6, VisionInput.Camera.SIDE);
    public static final TargetLocation RED_LEFT_NW = new TargetLocation(11, VisionInput.Camera.TOP);
    public static final TargetLocation RED_RIGHT_NW = new TargetLocation(11, VisionInput.Camera.SIDE);

    public static final TargetLocation BLUE_LEFT_N = new TargetLocation(10, VisionInput.Camera.TOP);
    public static final TargetLocation BLUE_RIGHT_N = new TargetLocation(10, VisionInput.Camera.SIDE);
    public static final TargetLocation BLUE_LEFT_NE = new TargetLocation(9, VisionInput.Camera.TOP);
    public static final TargetLocation BLUE_RIGHT_NE = new TargetLocation(9, VisionInput.Camera.SIDE);
    public static final TargetLocation BLUE_LEFT_SE = new TargetLocation(8, VisionInput.Camera.TOP);
    public static final TargetLocation BLUE_RIGHT_SE = new TargetLocation(8, VisionInput.Camera.SIDE);
    public static final TargetLocation BLUE_LEFT_S = new TargetLocation(7, VisionInput.Camera.TOP);
    public static final TargetLocation BLUE_RIGHT_S = new TargetLocation(7, VisionInput.Camera.SIDE);
    public static final TargetLocation BLUE_LEFT_SW = new TargetLocation(6, VisionInput.Camera.TOP);
    public static final TargetLocation BLUE_RIGHT_SW = new TargetLocation(6, VisionInput.Camera.SIDE);
    public static final TargetLocation BLUE_LEFT_NW = new TargetLocation(11, VisionInput.Camera.TOP);
    public static final TargetLocation BLUE_RIGHT_NW = new TargetLocation(11, VisionInput.Camera.SIDE);
}
