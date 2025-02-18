package frc.robot.subsystems.led;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import frc.entech.subsystems.EntechSubsystem;
import frc.robot.RobotConstants;
import frc.robot.io.RobotIO;

/**
 * LEDSubsystem controls the LED hardware using a subdivided LED string.
 * The configuration is provided via LEDInput that encapsulates a SubdividedLedString,
 * which defines individual LED segments and their colors. An overall blinking flag is
 * supported by toggling each segment's display between its foreground and background colors.
 */
public class LEDSubsystem extends EntechSubsystem<LEDInput, LEDOutput> {

  private static final boolean ENABLED = true;

  private AddressableLED leds;
  private AddressableLEDBuffer buffer;
  private LEDInput currentInput = new LEDInput();
  private Timer blinkTimer = new Timer();

  /**
   * Constructs the LEDSubsystem.
   * If enabled, initializes the AddressableLED with the configured port and number of LEDs.
   */
  public LEDSubsystem() {
    if (ENABLED) {
      leds = new AddressableLED(RobotConstants.LED.PORT);
      buffer = new AddressableLEDBuffer(RobotConstants.LED.NUM_LEDS);
      leds.setLength(buffer.getLength());
      leds.start();
    }
  }

  /**
   * Initializes the LED subsystem.
   * Configures the LED output based on the current subdivided LED string
   * and starts the blink timer.
   */
  @Override
  public void initialize() {
    updateLEDs();
    blinkTimer.start();
  }

  /**
   * Periodically updates the LED output.
   * If blinking is enabled, toggles the LED segments when the blink timer elapses.
   * Otherwise, continuously updates the LED output with the current configuration.
   */
  @Override
  public void periodic() {
    if (ENABLED) {
      if (currentInput.getBlinking()) {
        if (blinkTimer.hasElapsed(0.25)) {
          toggleLED();
          blinkTimer.restart();
        }
      } else {
        updateLEDs();
      }
    }
  }

  /**
   * Toggles each LED segment's state between its foreground and background color.
   */
  private void toggleLED() {
    if (currentInput.getSubdividedString() != null) {
      for (SubdividedLedString.LedSection section : currentInput.getSubdividedString().getSections()) {
        // Toggle between foreground (on) and background (off).
        if (section.getCurrentColor().equals(section.getFgColor())) {
          section.off();
        } else {
          section.on();
        }
      }
      updateLEDs();
    }
  }

  /**
   * Updates the LED buffer based on the subdivided LED string defined in the current input.
   * Each section's current color is applied to its designated indices.
   */
  private void updateLEDs() {
    if (currentInput.getSubdividedString() != null) {
      for (SubdividedLedString.LedSection section : currentInput.getSubdividedString().getSections()) {
        // Ensure indices are within bounds.
        int startIdx = Math.max(0, section.getStartIndex());
        int endIdx = Math.min(buffer.getLength(), section.getEndIndex());
        for (int i = startIdx; i < endIdx; i++) {
          buffer.setLED(i, section.getCurrentColor());
        }
      }
      leds.setData(buffer);
    }
  }

  /**
   * Indicates whether the subsystem is enabled.
   *
   * @return true if enabled, false otherwise
   */
  @Override
  public boolean isEnabled() {
    return ENABLED;
  }

  /**
   * Updates the subsystem inputs.
   *
   * @param input the new LEDInput to process
   */
  @Override
  public void updateInputs(LEDInput input) {
    RobotIO.processInput(input);
    this.currentInput = input;
  }

  /**
   * Returns a test command for the LED subsystem.
   *
   * @return a command instance that tests LED behavior
   */
  @Override
  public Command getTestCommand() {
    return new TestLEDCommand(this);
  }

  /**
   * Converts the subsystem’s current state into an LEDOutput.
   *
   * @return the LEDOutput representing the current state
   */
  @Override
  public LEDOutput toOutputs() {
    LEDOutput output = new LEDOutput();
    if (ENABLED) {
      output.setBlinking(currentInput.getBlinking());
      output.setSubdividedString(currentInput.getSubdividedString());
    }
    return output;
  }

  /**
   * Releases hardware resources allocated by the AddressableLED.
   */
  public void close() {
    leds.close();
  }
}