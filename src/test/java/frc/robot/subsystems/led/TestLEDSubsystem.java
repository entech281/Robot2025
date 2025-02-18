package frc.robot.subsystems.led;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.first.wpilibj.util.Color;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestLEDSubsystem {

  private LEDSubsystem ledSubsystem;
  private LEDInput input;
  // Adjust LED count if necessary; here we’re using a test value.
  private final int testNumLEDs = 100;

  @BeforeEach
  void setup() {
    ledSubsystem = new LEDSubsystem();
    input = new LEDInput();
  }

  @AfterEach
  void tearDown() {
    ledSubsystem.close();
  }

  /**
   * Tests that when blinking is disabled the LED section maintains its foreground color.
   */
  @Test
  void testNonBlinkingOutput() {
    SubdividedLedString subdivided = new SubdividedLedString();
    // Add one section covering the entire strip with foreground RED and background BLACK.
    subdivided.addSection(Color.kRed, Color.kBlack, 0, testNumLEDs);
    input.setSubdividedString(subdivided);
    input.setBlinking(false);

    ledSubsystem.updateInputs(input);
    ledSubsystem.initialize();

    // Call periodic repeatedly (no blinking expected).
    for (int i = 0; i < 5; i++) {
      ledSubsystem.periodic();
    }
    LEDOutput output = ledSubsystem.toOutputs();

    // Verify blinking flag is false.
    assertFalse(output.isBlinking(), "Blinking flag should be false.");
    // Verify the subdivided LED string is defined.
    assertNotNull(output.getSubdividedString(), "Subdivided LED string should not be null.");
    // Check that the one section's current color remains the foreground color.
    SubdividedLedString.LedSection section = output.getSubdividedString().getSections().get(0);
    assertEquals(Color.kRed, section.getCurrentColor(), "Current color should be the foreground color (RED).");
  }

  /**
   * Tests that when blinking is enabled the LED section toggles between its foreground and background colors.
   */
  @Test
  void testBlinkingOutputToggles() {
    SubdividedLedString subdivided = new SubdividedLedString();
    // Create a section covering the entire strip with foreground BLUE and background GREEN.
    subdivided.addSection(Color.kBlue, Color.kGreen, 0, testNumLEDs);
    input.setSubdividedString(subdivided);
    input.setBlinking(true);

    ledSubsystem.updateInputs(input);
    ledSubsystem.initialize();

    // Capture the initial color (should be foreground color BLUE).
    LEDOutput output = ledSubsystem.toOutputs();
    SubdividedLedString.LedSection section = output.getSubdividedString().getSections().get(0);
    Color initialColor = section.getCurrentColor();
    // Let the blink timer elapse (blink threshold is 0.25 sec).
    try {
      Thread.sleep(300);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    ledSubsystem.periodic();
    LEDOutput outputAfter = ledSubsystem.toOutputs();
    SubdividedLedString.LedSection sectionAfter = outputAfter.getSubdividedString().getSections().get(0);
    // The color should have toggled to the background (GREEN) if it was initially BLUE.
    assertNotEquals(initialColor, sectionAfter.getCurrentColor(), "Current color should have toggled after blinking.");
  }
}
