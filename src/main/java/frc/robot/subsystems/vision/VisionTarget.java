package frc.robot.subsystems.vision;

import org.littletonrobotics.junction.Logger;

public class VisionTarget {
    private final int tagID;
    private final String cameraName;
    private final int tagHeight;
    private final int tagWidth;
    private final double tagX;
    private final double tagY;
    private final double distance;
    private final double tagXW;
    private final long timestamp;

    public VisionTarget(int tagID, int tagHeight, int tagWidth, double tagX, double tagY, double distance, double tagXW, long timestamp, String cameraName) {
        this.tagID = tagID;
        this.tagHeight = tagHeight;
        this.tagWidth = tagWidth;
        this.tagX = tagX;
        this.tagY = tagY;
        this.distance = distance;
        this.tagXW = tagXW;
        this.timestamp = timestamp;
        this.cameraName = cameraName;
    }


    public int getTagID() {
        return this.tagID;
    }

    public int getTagHeight() {
        return this.tagHeight;
    }

    public int getTagWidth() {
        return this.tagWidth;
    }

    public double getTagX() {
        return this.tagX;
    }

    public double getTagY() {
        return this.tagY;
    }

    public double getDistance() {
        return this.distance;
    }

    public double getTagXW() {
        return this.tagXW;
    }

    public long getTimestamp() {
        return this.timestamp;
    }


    public String getCameraName() {
        return this.cameraName;
    }

    public void log(String table) {
        Logger.recordOutput(table + "/tagID", tagID);
        Logger.recordOutput(table + "/tagHeight", tagHeight);
        Logger.recordOutput(table + "/tagWidth", tagWidth);
        Logger.recordOutput(table + "/tagX", tagX);
        Logger.recordOutput(table + "/tagY", tagY);
        Logger.recordOutput(table + "/distance", distance);
        Logger.recordOutput(table + "/tagXW", tagXW);
        Logger.recordOutput(table + "/timestamp", timestamp);
        Logger.recordOutput(table + "/cameraName", cameraName);
    }

    @Override
    public String toString() {
        return "{" +
            " tagID=" + getTagID() +
            ", tagHeight=" + getTagHeight() +
            ", tagWidth=" + getTagWidth() +
            ", tagX=" + getTagX() +
            ", tagY=" + getTagY() +
            ", distance=" + getDistance() +
            ", tagXP=" + getTagXW() + "'" +
            ", timestamp=" + getTimestamp() +
            "}";
    }
}
