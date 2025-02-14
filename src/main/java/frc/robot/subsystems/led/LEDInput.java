package frc.robot.subsystems.led;

import org.littletonrobotics.junction.LogTable;
import edu.wpi.first.wpilibj.util.Color;
import frc.entech.subsystems.SubsystemInput;

public class LEDInput implements SubsystemInput {

  private Color[] colors = {Color.kGreen, Color.kOrange};
  private Color[] secondaryColors = {Color.kRed, Color.kBlue};
  private boolean blinking = false;
  private int[][] intervals = new int[2][2];

  public Color[] getSecondaryColors() {
    return secondaryColors;
  }

  public void setSecondaryColors(Color[] secondaryColors) {
    this.secondaryColors = secondaryColors;
  }

  @Override
  public void toLog(LogTable table) {
    table.put("Blinking", blinking);
    table.put("CurrentColor", colors + "");
  }

  @Override
  public void fromLog(LogTable table) {
    blinking = table.get("Blinking", false);
  }

  public Color[] getColors() {
    return colors;
  }

  public void setColors(Color[] colors) {
    this.colors = colors;
  }

  public boolean getBlinking() {
    return blinking;
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