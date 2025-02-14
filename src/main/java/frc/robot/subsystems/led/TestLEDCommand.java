package frc.robot.subsystems.led;

import edu.wpi.first.wpilibj.util.Color;
import frc.entech.commands.EntechCommand;
import frc.entech.util.StoppingCounter;
import frc.robot.RobotConstants;

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
    switch (stage) {
      case 0:
        input.setColors(new Color[]{Color.kWhite, Color.kBlack});
        break;
      case 1:
        input.setColors(new Color[] {Color.kRed, Color.kBlue});
        break;
      case 2:
        input.setColors(new Color[] {Color.kOrange, Color.kYellow});
        break;
      case 3:
        input.setColors(new Color[] {Color.kGreen, Color.kCyan});
        input.setBlinking(true);
        break;
      default:
        input.setColors(new Color[] {Color.kAntiqueWhite, Color.kBeige});
        break;
    }
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