package frc.robot.subsystems.vision;

import org.littletonrobotics.junction.Logger;

public class VisionTarget {
    private int tagID;
    private String cameraName;
    private int tagHeight;
    private int tagWidth;
    private double tagX;
    private double tagY;
    private double distance;
    private double tagXW;
    private long timestamp;

    public int getTagID() {
        return this.tagID;
    }

    public void setTagID(int tagID) {
        this.tagID = tagID;
    }

    public String getCameraName() {
        return this.cameraName;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }

    public int getTagHeight() {
        return this.tagHeight;
    }

    public void setTagHeight(int tagHeight) {
        this.tagHeight = tagHeight;
    }

    public int getTagWidth() {
        return this.tagWidth;
    }

    public void setTagWidth(int tagWidth) {
        this.tagWidth = tagWidth;
    }

    public double getTagX() {
        return this.tagX;
    }

    public void setTagX(double tagX) {
        this.tagX = tagX;
    }

    public double getTagY() {
        return this.tagY;
    }

    public void setTagY(double tagY) {
        this.tagY = tagY;
    }

    public double getDistance() {
        return this.distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getTagXW() {
        return this.tagXW;
    }

    public void setTagXW(double tagXW) {
        this.tagXW = tagXW;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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
