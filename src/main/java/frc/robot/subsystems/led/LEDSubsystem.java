package frc.robot.subsystems.led;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.entech.subsystems.EntechSubsystem;
import frc.robot.RobotConstants;
import frc.robot.io.RobotIO;
import edu.wpi.first.wpilibj.AddressableLED;

/**
 * LEDSubsystem controls the LED hardware using a subdivided LED string.
 * The configuration is provided via LEDInput that encapsulates a SubdividedLedString,
 * which defines individual LED segments and their colors. Each segment may be configured
 * to blink independently by setting its own blinking flag.
 */
public class LEDSubsystem extends EntechSubsystem<LEDInput, LEDOutput> {

  private static final boolean ENABLED = true;

  private AddressableLED leds = new AddressableLED(RobotConstants.LED.PORT);
  private  AddressableLEDBuffer buffer= new AddressableLEDBuffer(RobotConstants.LED.NUM_LEDS);
  private LEDInput currentInput = new LEDInput(new SubdividedLedString());
  private Timer blinkTimer = new Timer();


  public LEDSubsystem() {
    if (ENABLED) {
      buffer = new AddressableLEDBuffer(RobotConstants.LED.NUM_LEDS);
      leds.setLength(buffer.getLength());
      leds.start();
    }
  }

  /**
   * Initializes the LED subsystem.
   * Configures the LED output based on the current subdivided LED string and starts the blink timer.
   */
  @Override
  public void initialize() {
    updateLEDs();
      blinkTimer.start();
  }

  /**
   * Periodically updates the LED output.
   * Every 0.25 seconds, toggles only those LED sections whose blinking flag is enabled.
   * Otherwise, the LED buffer is updated continuously.
   */
  @Override
  public void periodic() {
    if (ENABLED) {
        if (blinkTimer.hasElapsed(0.25)) {
          toggleBlinkingSections();
          blinkTimer.restart();
        }
        updateLEDs();
    }
  }

  /**
   * Toggles the state (foreground/background) only for LED segments flagged to blink.
   */
  private void toggleBlinkingSections() {
    if (currentInput.getSubdividedString() != null) {
      for (SubdividedLedString.LedSection section : currentInput.getSubdividedString().getSections()) {
        if (section.isBlinking()) {
          // Toggle current color
          if (section.getCurrentColor().equals(section.getFgColor())) {
            section.off();
          } else {
            section.on();
          }
        }
      }
    }
  }

  /**
   * Updates the LED buffer based on the subdivided LED string defined in the current input.
   * Each section's current color is applied to its designated indices.
   */
  private void updateLEDs() {
    if (ENABLED && currentInput.getSubdividedString() != null) {
      for (SubdividedLedString.LedSection section : currentInput.getSubdividedString().getSections()) {
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
    if (ENABLED){
      RobotIO.processInput(input);
      this.currentInput = input;
    }

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
      output.setSubdividedString(currentInput.getSubdividedString());
      // Note: global blinking flag is no longer used; each section controls its own blink.
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
