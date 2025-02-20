package frc.robot.commands;


import frc.robot.subsystems.led.SubdividedLedString.LedSection;
import edu.wpi.first.wpilibj.util.Color;
import frc.entech.commands.EntechCommand;
import frc.robot.subsystems.led.LEDInput;
import frc.robot.subsystems.led.LEDSubsystem;
import frc.robot.subsystems.led.SubdividedLedString;
import frc.robot.RobotConstants;
import frc.robot.operation.UserPolicy;


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
    int operator_leds_start = RobotConstants.LED.OPERATOR_LEDS_START_INDEX;
    int driver_leds_start = RobotConstants.LED.DRIVER_LEDS_START_INDEX;
    int operator_leds_end = RobotConstants.LED.OPERATOR_LEDS_END_INDEX;
    int driver_leds_end = RobotConstants.LED.DRIVER_LEDS_END_INDEX;

    //TODO move to RobotConstants?
    LedSection operator_section = subdivided.addSection(Color.kBlack, Color.kBlack, operator_leds_start, operator_leds_end);
    LedSection driver_section = subdivided.addSection(Color.kBlack, Color.kBlack, driver_leds_start, driver_leds_end);

    boolean hasPiece = false;
    boolean notAligned = UserPolicy.getInstance().isAligningToAngle();


    if (hasError()) {
      // In error state: blink with a configuration of red (foreground) and blue (background)
      input.setBlinking(true);
      subdivided.addSection(Color.kRed, Color.kBlack, 0, ledCount);
    } else {
      if (hasPiece) {
        operator_section = subdivided.addSection(Color.kPurple, Color.kBlack, operator_leds_start, operator_leds_end);
        operator_section.setBlinking(true);
      } else {
        subdivided.addSection(Color.kPurple, Color.kBlack, operator_leds_start, operator_leds_end);
        operator_section.setBlinking(false);
      }

      if (notAligned) {
        driver_section = subdivided.addSection(Color.kGreen, Color.kBlack, driver_leds_start, driver_leds_end);
        driver_section.setBlinking(true);
      } else {
        //TODO: Find out what getTargetAngle() actually returns
        //I assumed it returned radians and we can only see 180 deg
        int redIndex = (int) UserPolicy.getInstance().getTargetAngle();
        redIndex = (int) (redIndex / Math.PI * ledCount);
        if (redIndex > 0) {
          subdivided.addSection(Color.kGreen, Color.kGreen, 0, redIndex);
        }
        // Single LED marked red at the target.
        subdivided.addSection(Color.kRed, Color.kRed, redIndex, redIndex + 1);
        // From redIndex+1 to ledCount: green segment.
        if (redIndex < ledCount - 1) {
          subdivided.addSection(Color.kGreen, Color.kGreen, redIndex + 1, ledCount);
        }
      }

      // driver_section = subdivided.addSection(Color.kBlack, Color.kBlack, driver_leds_start, driver_leds_end);
      // driver_section.setBlinking(false);
      // In normal state: solid display with green (foreground) and orange (as backup if blinking)
      // input.setBlinking(false);
      // subdivided.addSection(Color.kGreen, Color.kBlack, 0, ledCount);
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