package frc.robot.commands;

import edu.wpi.first.wpilibj.util.Color;
import frc.entech.commands.EntechCommand;
import frc.robot.io.RobotIO;
import frc.robot.subsystems.led.LEDInput;
import frc.robot.subsystems.led.LEDSubsystem;

public class LEDDefaultCommand extends EntechCommand {
  private final LEDSubsystem ledSubsystem;
  private LEDInput input = new LEDInput();

  public LEDDefaultCommand(LEDSubsystem ledSubsystem) {
    super(ledSubsystem);
    this.ledSubsystem = ledSubsystem;
  }

  private boolean hasError() {
    return RobotIO.getInstance().getNavXOutput().isFaultDetected();
  }

  @Override
  public void execute() {
    if (hasError()) {
      input.setBlinking(true);
      input.setColors(new Color[] {Color.kRed, Color.kBlue});
      input.setSecondaryColors(new Color[] {Color.kGreen, Color.kRed});
     } else {
      input.setBlinking(false);
      input.setColors(new Color[] {Color.kGreen, Color.kOrange});
    }

    ledSubsystem.updateInputs(input);
  }

  @Override
  public boolean isFinished() {
    return false;
  }

  @Override
  public boolean runsWhenDisabled() {
    return true;
  }
}