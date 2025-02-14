package frc.robot.subsystems.led;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.wpilibj.util.Color;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LEDSubsystemTest {

  private LEDSubsystem ledSubsystem;
  private LEDInput ledInput;

  @BeforeAll
  public static void initHAL() {
    assert HAL.initialize(500, 0) : "HAL did not initialize.";
  }

  @BeforeEach
  public void setup() {
    ledSubsystem = new LEDSubsystem();
    ledInput = new LEDInput();
  }

  @AfterEach
  void shutdown() throws Exception {
    ledSubsystem.close();
  }

  @Test
  public void testNonBlinkingOutput() {
    Color[] expectedColors = new Color[] {Color.kYellow, Color.kPurple};
    ledInput.setColors(expectedColors);
    ledInput.setBlinking(false);
    ledSubsystem.updateInputs(ledInput);
    ledSubsystem.initialize();
    ledSubsystem.periodic();
    
    LEDOutput output = ledSubsystem.toOutputs();
    assertArrayEquals(expectedColors, output.getColors(), "The LED colors should match the input colors");
    assertFalse(output.isBlinking(), "The blinking flag should be false");
  }

  @Test
  public void testBlinkingOutput() {
    ledInput.setBlinking(true);
    ledSubsystem.updateInputs(ledInput);
    ledSubsystem.initialize();
    ledSubsystem.periodic();
    
    LEDOutput output = ledSubsystem.toOutputs();
    assertTrue(output.isBlinking(), "The blinking flag should be true");
  }

  @Test
  public void testPeriodicDoesNotThrow() {
    ledInput.setColors(new Color[] {Color.kGreen, Color.kOrange});
    ledInput.setBlinking(false);
    ledSubsystem.updateInputs(ledInput);
    ledSubsystem.initialize();

    for (int i = 0; i < 10; i++) {
      ledSubsystem.periodic();
    }
    assertTrue(true);
  }
}