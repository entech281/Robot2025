package frc.robot.subsystems.led;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.first.wpilibj.util.Color;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestLEDSubsystem {

  private LEDSubsystem ledSubsystem;
  private LEDInput input;
  // Test LED count value
  private final int testNumLEDs = 100;

  @BeforeEach
  void setup() {
    ledSubsystem = new LEDSubsystem(false);
    input = new LEDInput();
    ledSubsystem.initialize();
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
    // One section: entire range with foreground RED and background BLACK.
    SubdividedLedString.LedSection section = subdivided.addSection(Color.kRed, Color.kBlack, 0, testNumLEDs);
    section.setBlinking(false);
    section.on(); // Set to foreground

    input.setSubdividedString(subdivided);
    input.setBlinking(false); // Global flag false; should propagate to section

    ledSubsystem.updateInputs(input);

    // Run several cycles so no blinking effect occurs.
    for (int i = 0; i < 5; i++) {
      ledSubsystem.periodic();
    }

    LEDOutput output = ledSubsystem.toOutputs();
    assertNotNull(output.getSubdividedString(), "Subdivided LED string should not be null.");
    SubdividedLedString.LedSection outSection = output.getSubdividedString().getSections().get(0);
    assertEquals(Color.kRed, outSection.getCurrentColor(), "Non-blinking section should maintain foreground RED.");
  }

  /**
   * Tests that when a section is set to blink, its color toggles between foreground and background.
   */
  @Test
  void testBlinkingOutputToggles() {
    SubdividedLedString subdivided = new SubdividedLedString();
    // One section: entire range with foreground BLUE and background GREEN.
    SubdividedLedString.LedSection section = subdivided.addSection(Color.kBlue, Color.kGreen, 0, testNumLEDs);
    section.setBlinking(true);
    section.on(); // Start with foreground (BLUE)

    input.setSubdividedString(subdivided);
    input.setBlinking(true); // Global flag true; propagates toggling

    ledSubsystem.updateInputs(input);



    // Record the initial color (should be BLUE)
    LEDOutput output = ledSubsystem.toOutputs();
    SubdividedLedString.LedSection outSection = output.getSubdividedString().getSections().get(0);
    Color initialColor = outSection.getCurrentColor();

    // Wait for blink timer to elapse (blink cycle threshold is 0.25 sec)
    try {
      Thread.sleep(300);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    ledSubsystem.periodic();
    
    LEDOutput outputAfter = ledSubsystem.toOutputs();
    SubdividedLedString.LedSection sectionAfter = outputAfter.getSubdividedString().getSections().get(0);
    // Expect toggled color
    assertNotEquals(initialColor, sectionAfter.getCurrentColor(), "Blinking section should toggle its color.");
  }

  /**
   * Tests that multiple sections update independently:
   * One section is blinking and one is static. Only the blinking section toggles.
   */
  @Test
  void testMultipleSectionsIndependentBlinking() {
    SubdividedLedString subdivided = new SubdividedLedString();
    // Section 1: entire range (0 to 50): Foreground MAGENTA, non-blinking.
    SubdividedLedString.LedSection section1 = subdivided.addSection(Color.kMagenta, Color.kBlack, 0, 50);
    section1.setBlinking(false);
    section1.on();
    // Section 2: entire range (50 to 100): Foreground ORANGE, blinking.
    SubdividedLedString.LedSection section2 = subdivided.addSection(Color.kOrange, Color.kGray, 50, 100);
    section2.setBlinking(true);
    section2.on();
    
    input.setSubdividedString(subdivided);


    ledSubsystem.updateInputs(input);
    
    // Let time pass so that any blinking sections could toggle.
    try {
      Thread.sleep(300);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    ledSubsystem.periodic();
    
    LEDOutput output = ledSubsystem.toOutputs();
    SubdividedLedString.LedSection outSection1 = output.getSubdividedString().getSections().get(0);
    SubdividedLedString.LedSection outSection2 = output.getSubdividedString().getSections().get(1);
    
    // Section1 should remain unchanged (still MAGENTA)
    assertEquals(Color.kMagenta, outSection1.getCurrentColor(), "Non-blinking Section1 should remain MAGENTA.");
    // Section2 (blinking) should have toggled to background (GRAY) after one toggle event.
    assertEquals(Color.kGray, outSection2.getCurrentColor(), "Blinking Section2 should toggle to GRAY (background) after blinking.");
  }
  
  /**
   * Tests that a global call to input.setBlinking overwrites each section's blinking state.
   */
  @Test
  void testGlobalBlinkingOverride() {
    SubdividedLedString subdivided = new SubdividedLedString();
    // Create two sections with different individual blinking settings.
    SubdividedLedString.LedSection section1 = subdivided.addSection(Color.kWhite, Color.kBlack, 0, 50);
    section1.setBlinking(false);
    section1.on();
    SubdividedLedString.LedSection section2 = subdivided.addSection(Color.kBlue, Color.kGreen, 50, 100);
    section2.setBlinking(true);
    section2.on();
    
    // Before global override, check individual blinking states.
    assertFalse(section1.isBlinking(), "Section1 should initially be non-blinking.");
    assertTrue(section2.isBlinking(), "Section2 should initially be blinking.");
    
    // Global call to set blinking to true.
    input.setSubdividedString(subdivided);
    input.setBlinking(true);
    
    // Both sections should now be blinking due to propagation.
    assertTrue(section1.isBlinking(), "Section1 should be overridden to blinking.");
    assertTrue(section2.isBlinking(), "Section2 remains blinking.");
    
    ledSubsystem.updateInputs(input);

    
    // Wait and toggle blinking.
    try {
      Thread.sleep(300);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    ledSubsystem.periodic();
    
    LEDOutput output = ledSubsystem.toOutputs();
    SubdividedLedString.LedSection outSection1 = output.getSubdividedString().getSections().get(0);
    SubdividedLedString.LedSection outSection2 = output.getSubdividedString().getSections().get(1);

    // Check that at least one blink occurred (colors toggled from initial state).
    // For section1, initial color was White.
    // For section2, initial color was Blue.
    // They should have toggled after blinking.
    assertNotEquals(Color.kWhite, outSection1.getCurrentColor(), "Section1's color should have toggled due to global blinking override.");
    assertNotEquals(Color.kBlue, outSection2.getCurrentColor(), "Section2's color should have toggled due to blinking.");
  }
}
