package frc.robot.subsystems.drive;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;

public class OdometryData {
    private double timestamp;
    private Rotation2d rawGyroRotation;
    private SwerveModulePosition[] modulePositions;

    public OdometryData(double timestamp, Rotation2d rawGyroRotation, SwerveModulePosition[] modulePositions) {
        this.timestamp = timestamp;
        this.rawGyroRotation = rawGyroRotation;
        this.modulePositions = modulePositions;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public Rotation2d getRawGyroRotation() {
        return rawGyroRotation;
    }

    public SwerveModulePosition[] getModulePositions() {
        return modulePositions;
    }


}
