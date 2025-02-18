package frc.robot.commands;

import edu.wpi.first.wpilibj.util.Color;
import frc.entech.commands.EntechCommand;
import frc.robot.subsystems.led.LEDInput;
import frc.robot.subsystems.led.LEDSubsystem;
import frc.robot.subsystems.led.SubdividedLedString;
import frc.robot.RobotConstants;

public class LEDDefaultCommand extends EntechCommand {
  private final LEDSubsystem ledSubsystem;
  private final LEDInput input = new LEDInput();

  public LEDDefaultCommand(LEDSubsystem ledSubsystem) {
    this.ledSubsystem = ledSubsystem;
    addRequirements(ledSubsystem);
  }

  @Override
  public void execute() {
    // Create a new subdivided LED string that will span the entire LED strip.
    SubdividedLedString subdivided = new SubdividedLedString();
    int ledCount = RobotConstants.LED.NUM_LEDS;

    if (hasError()) {
      // In error state: blink with a configuration of red (foreground) and blue (background)
      input.setBlinking(true);
      subdivided.addSection(Color.kRed, Color.kBlue, 0, ledCount);
    } else {
      // In normal state: solid display with green (foreground) and orange (as backup if blinking)
      input.setBlinking(false);
      subdivided.addSection(Color.kGreen, Color.kOrange, 0, ledCount);
    }
    input.setSubdividedString(subdivided);
    ledSubsystem.updateInputs(input);
  }
  
  // Replace this with your actual error checking
  private boolean hasError() {
    // Example: return true if there is an error detected.
    return false;
  }
}