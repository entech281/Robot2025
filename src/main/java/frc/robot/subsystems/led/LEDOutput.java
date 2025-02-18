package frc.robot.subsystems.led;

import org.littletonrobotics.junction.Logger;
import frc.entech.subsystems.SubsystemOutput;

/**
 * LEDOutput captures the current state of the LED subsystem.
 * It reflects the state of the subdivided LED string and the blinking flag.
 */
public class LEDOutput extends SubsystemOutput {

  private SubdividedLedString subdividedString;
  private boolean blinking;

  /**
   * Returns the current subdivided LED string state.
   *
   * @return the current subdivided LED string state
   */
  public SubdividedLedString getSubdividedString() {
    return subdividedString;
  }

  /**
   * Sets the current subdivided LED string state.
   *
   * @param subdividedString the new subdivided LED string state
   */
  public void setSubdividedString(SubdividedLedString subdividedString) {
    this.subdividedString = subdividedString;
  }

  @Override
  public void toLog() {
    Logger.recordOutput("LEDOutput/SubdividedString", subdividedString.toString());
    Logger.recordOutput("LEDOutput/Blinking", blinking);
  }

  public boolean isBlinking() {
    return this.blinking;
  }

  public void setBlinking(boolean blinking) {
    this.blinking = blinking;
  }
}