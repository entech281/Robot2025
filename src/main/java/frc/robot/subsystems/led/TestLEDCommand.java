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
      case 0 -> input.setColor(Color.kWhite);
      case 1 -> input.setColor(Color.kRed);
      case 2 -> input.setColor(Color.kBlue);
      case 3 -> {
        input.setColor(Color.kGreen);
        input.setBlinking(true);
      }
      default -> input.setColor(Color.kBlack);
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