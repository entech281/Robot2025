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
    // Create a new subdivided LED string that spans the entire LED strip.
    SubdividedLedString subdivided = new SubdividedLedString();
    int ledCount = RobotConstants.LED.NUM_LEDS;
    
    switch (stage) {
      case 0 -> subdivided.addSection(Color.kWhite, Color.kBlack, 0, ledCount);
      case 1 -> subdivided.addSection(Color.kRed, Color.kBlack, 0, ledCount);
      case 2 -> subdivided.addSection(Color.kBlue, Color.kBlack, 0, ledCount);
      case 3 -> {
        subdivided.addSection(Color.kGreen, Color.kBlack, 0, ledCount);
        input.setBlinking(true);
      }
      default -> subdivided.addSection(Color.kBlack, Color.kBlack, 0, ledCount);
    }
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