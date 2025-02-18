package frc.robot.subsystems.led;

import edu.wpi.first.wpilibj.util.Color;
import frc.entech.commands.EntechCommand;
import frc.entech.util.StoppingCounter;
import frc.robot.RobotConstants;

/**
 * TestLEDCommand cycles through different LED configurations using the new subdivided LED string.
 * Each stage creates a new LEDInput with a subdivided LED string that covers the entire LED strip.
 * At stage 3, blinking is enabled.
 */
public class TestLEDCommand extends EntechCommand {
  private final LEDSubsystem ledSubsystem;
  private final StoppingCounter counter =
      new StoppingCounter(RobotConstants.TEST_CONSTANTS.STANDARD_TEST_LENGTH);
  private int stage = 0;

  public TestLEDCommand(LEDSubsystem ledSubsystem) {
    this.ledSubsystem = ledSubsystem;
  }

  @Override
  public void initialize() {
    counter.reset();
    stage = 0;
  }

  @Override
  public void execute() {
    LEDInput input = new LEDInput();
    // Create new subdivided LED string for configuring segments.
    SubdividedLedString subdivided = new SubdividedLedString();
    int numLEDs = RobotConstants.LED.NUM_LEDS;
    Color fgColor;
    Color bgColor = Color.kBlack;  // default background

    switch (stage) {
      case 0:
        fgColor = Color.kWhite;
        bgColor = Color.kBlack;
        break;
      case 1:
        fgColor = Color.kRed;
        bgColor = Color.kBlue;
        break;
      case 2:
        fgColor = Color.kOrange;
        bgColor = Color.kYellow;
        break;
      case 3:
        fgColor = Color.kGreen;
        bgColor = Color.kCyan;
        input.setBlinking(true);
        break;
      default:
        fgColor = Color.kAntiqueWhite;
        bgColor = Color.kBeige;
        break;
    }
    // Add one section that covers the entire LED strip.
    subdivided.addSection(fgColor, bgColor, 0, numLEDs);
    input.setSubdividedString(subdivided);

    ledSubsystem.updateInputs(input);

    if (counter.isFinished(true)) {
      counter.reset();
      stage++;
    }
  }

  @Override
  public boolean isFinished() {
    return stage >= 4;
  }
}