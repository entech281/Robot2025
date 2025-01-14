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
        input.setColor(Color.kWhite);
        break;
      case 1:
        input.setColor(Color.kRed);
        break;
      case 2:
        input.setColor(Color.kBlue);
        break;
      case 3:
        input.setColor(Color.kGreen);
        input.setBlinking(true);
        break;
      default:
        input.setColor(Color.kBlack);
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