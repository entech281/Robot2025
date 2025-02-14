package frc.robot.subsystems.led;


import org.littletonrobotics.junction.Logger;
import edu.wpi.first.wpilibj.util.Color;
import frc.entech.subsystems.SubsystemOutput;

import java.util.Arrays;

public class LEDOutput extends SubsystemOutput {

  private Color[] colors = {Color.kGreen, Color.kOrange};
  private boolean blinking;
  private Color[] secondaryColors = {Color.kRed, Color.kBlue};
  private int[][] intervals;

  public Color[] getSecondaryColors() {
    return secondaryColors;
  }

  public void setSecondaryColors(Color[] secondaryColors) {
    this.secondaryColors = secondaryColors;
  }

  @Override
  public void toLog() {
    Logger.recordOutput("LEDOutput/CurrentColor", Arrays.toString(colors) + "");
    Logger.recordOutput("LEDOutput/Blinking", blinking);
  }

  public Color[] getColors() {
    return this.colors;
  }

  public void setColors(Color[] colors) {
    this.colors = colors;
  }

  public boolean isBlinking() {
    return this.blinking;
  }

  public void setBlinking(boolean blinking) {
    this.blinking = blinking;
  }

  public void setIntervals(int[][] intervals) {
    this.intervals = intervals;
  }

  public int[][] getIntervals() {
    return intervals;
  }

}