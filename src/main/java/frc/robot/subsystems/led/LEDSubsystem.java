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
 *
 * @author dcowden
 */
public class LEDSubsystem extends EntechSubsystem<LEDInput, LEDOutput> {

  private static final boolean ENABLED = true;

  private AddressableLED leds;
  private AddressableLEDBuffer buffer;
  private Color[] currentColors;

  private LEDInput currentInput = new LEDInput();
  private Timer blinkTimer = new Timer();

  public LEDSubsystem() {
    if (ENABLED) {
      leds = new AddressableLED(RobotConstants.LED.PORT);
      buffer = new AddressableLEDBuffer(RobotConstants.LED.NUM_LEDS);
      leds.setLength(buffer.getLength());
      leds.start();
    }
  }

  @Override
  public void initialize() {
    setColor(currentInput.getColors(), currentInput.getIntervals());
    blinkTimer.start();
  }

  @Override
  public void periodic() {
    if (ENABLED) {
      if (currentInput.getBlinking()) {
        if (blinkTimer.hasElapsed(0.25)) {
          toggleColor();
          blinkTimer.restart();
        }
      } else {
        setColor(currentInput.getColors(), currentInput.getIntervals());
      }
    }
  }

  private void toggleColor() {
    if (currentColors.equals(currentInput.getSecondaryColors())) {
      setColor(currentInput.getColors(), currentInput.getIntervals());
      currentColors = currentInput.getColors();
    } else {
      setColor(currentInput.getSecondaryColors(), currentInput.getIntervals());
      currentColors = currentInput.getSecondaryColors();
    }
  }

  private void setColor(Color[] c, int[][] intervals) {
    if (ENABLED) {
      for (int i = 0; i < c.length; i++) {
        for (int j = intervals[i][0]; j < intervals[i][1]; j++) {
          buffer.setLED(j, c[i]);

        }
      }
      leds.setData(buffer);
    }
  }

  @Override
  public boolean isEnabled() {
    return ENABLED;
  }

  @Override
  public void updateInputs(LEDInput input) {
    RobotIO.processInput(input);
    this.currentInput = input;
  }

  @Override
  public Command getTestCommand() {
    return new TestLEDCommand(this);
  }

  @Override
  public LEDOutput toOutputs() {
    LEDOutput output = new LEDOutput();
    if (ENABLED) {
      output.setColors(currentInput.getColors());
      output.setBlinking(currentInput.getBlinking());
    }
    return output;
  }


  public void close() {
    leds.close();
  }
}