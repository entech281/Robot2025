package frc.robot.subsystems.vision;

import org.littletonrobotics.junction.LogTable;

import frc.entech.subsystems.SubsystemInput;


public class VisionInput implements SubsystemInput {
  private String camera;

  public VisionInput() {
  }

  public VisionInput(String camera) {
    this.camera = camera;
  }
  @Override
  public void toLog(LogTable table) {
    table.put("Camera", this.camera);
  }

  @Override
  public void fromLog(LogTable table) {
    this.camera = table.get("Camera", "top");
  }

  public String getCamera() {
    return camera;
  }

  public void setCamera(String camera) {
    this.camera = camera;
  }
}