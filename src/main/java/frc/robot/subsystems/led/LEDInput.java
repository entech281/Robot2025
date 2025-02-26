package frc.robot.subsystems.led;

import org.littletonrobotics.junction.LogTable;
import frc.entech.subsystems.SubsystemInput;

/**
 * LEDInput encapsulates the desired configuration for the LED subsystem
 * using a SubdividedLedString. It specifies the per‐segment LED colors and
 * an overall blinking flag. Setting the global blinking flag propagates the value
 * to each LED segment.
 */
public class LEDInput implements SubsystemInput {

  private SubdividedLedString subdividedString = new SubdividedLedString();
  private boolean blinking = false;

  public LEDInput(SubdividedLedString subdividedString){
    this.subdividedString = subdividedString;
  }
  /**
   * Returns the subdivided LED string configuration.
   *
   * @return the subdivided LED string (or null if not set)
   */
  public SubdividedLedString getSubdividedString() {
    return subdividedString;
  }


  /**
   * Returns the blinking flag.
   *
   * @return true if blinking is enabled, false otherwise
   */
  public boolean getBlinking() {
    return blinking;
  }

  /**
   * Sets whether the LEDs should blink.
   * This will also update each LED section's blinking configuration.
   *
   * @param blinking true to enable blinking, false to disable
   */
  public void setBlinking(boolean blinking) {
    this.blinking = blinking;
    if (subdividedString != null) {
      subdividedString.getSections().forEach(section -> section.setBlinking(blinking));
    }
  }

  @Override
  public void toLog(LogTable table) {
    table.put("Blinking", blinking);
    table.put("SubdividedString", subdividedString != null 
        ? subdividedString.getSections().toString() : "null");
  }

  @Override
  public void fromLog(LogTable table) {
    blinking = table.get("Blinking", false);
    // Optionally, update subdividedString based on logged data.
  }
}
