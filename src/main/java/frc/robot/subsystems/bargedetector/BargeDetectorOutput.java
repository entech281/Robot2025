package frc.robot.subsystems.bargedetector;

import org.littletonrobotics.junction.Logger;

import edu.wpi.first.wpilibj.util.Color;
import frc.entech.subsystems.SubsystemOutput;

public class BargeDetectorOutput extends SubsystemOutput {

  private Color curColor;

  public void setColor(Color curColor) {
    this.curColor = curColor;
  }

  public Color getColor() {
    return this.curColor;
  }

  public boolean hasBlueLine() {
    return getColor().equals(Color.kBlue);
  }

  public boolean hasRedLine() {
    return getColor().equals(Color.kRed);
  }

  public boolean hasLine() {
    return hasBlueLine() || hasRedLine();
  }

  @Override
  public void toLog() {
    Logger.recordOutput("InternalAlgaeDetectorOutput/CoralSensor", curColor.toString());
  }

}
