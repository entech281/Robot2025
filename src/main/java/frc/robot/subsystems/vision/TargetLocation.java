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
    public String toString() {
        return "TargetLocation{" + "tagID=" + tagID + ", camera=" + camera + '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(tagID, camera);
    }

    public static final TargetLocation RED_H = new TargetLocation(10, VisionInput.Camera.TOP);
    public static final TargetLocation RED_G = new TargetLocation(10, VisionInput.Camera.SIDE);
    public static final TargetLocation RED_F = new TargetLocation(9, VisionInput.Camera.TOP);
    public static final TargetLocation RED_E = new TargetLocation(9, VisionInput.Camera.SIDE);
    public static final TargetLocation RED_C = new TargetLocation(8, VisionInput.Camera.TOP);
    public static final TargetLocation RED_D = new TargetLocation(8, VisionInput.Camera.SIDE);
    public static final TargetLocation RED_A = new TargetLocation(7, VisionInput.Camera.TOP);
    public static final TargetLocation RED_B = new TargetLocation(7, VisionInput.Camera.SIDE);
    public static final TargetLocation RED_K = new TargetLocation(6, VisionInput.Camera.TOP);
    public static final TargetLocation RED_L = new TargetLocation(6, VisionInput.Camera.SIDE);
    public static final TargetLocation RED_J = new TargetLocation(11, VisionInput.Camera.TOP);
    public static final TargetLocation RED_I = new TargetLocation(11, VisionInput.Camera.SIDE);

    public static final TargetLocation BLUE_H = new TargetLocation(21, VisionInput.Camera.TOP);
    public static final TargetLocation BLUE_G = new TargetLocation(21, VisionInput.Camera.SIDE);
    public static final TargetLocation BLUE_F = new TargetLocation(22, VisionInput.Camera.TOP);
    public static final TargetLocation BLUE_E = new TargetLocation(22, VisionInput.Camera.SIDE);
    public static final TargetLocation BLUE_C = new TargetLocation(17, VisionInput.Camera.TOP);
    public static final TargetLocation BLUE_D = new TargetLocation(17, VisionInput.Camera.SIDE);
    public static final TargetLocation BLUE_A = new TargetLocation(18, VisionInput.Camera.TOP);
    public static final TargetLocation BLUE_B = new TargetLocation(18, VisionInput.Camera.SIDE);
    public static final TargetLocation BLUE_K = new TargetLocation(19, VisionInput.Camera.TOP);
    public static final TargetLocation BLUE_L = new TargetLocation(19, VisionInput.Camera.SIDE);
    public static final TargetLocation BLUE_J = new TargetLocation(20, VisionInput.Camera.TOP);
    public static final TargetLocation BLUE_I = new TargetLocation(20, VisionInput.Camera.SIDE);
}
