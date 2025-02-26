package frc.robot.commands;



import frc.entech.commands.EntechCommand;
import frc.robot.subsystems.coraldetector.InternalCoralDetectorOutput;
import frc.robot.subsystems.coraldetector.InternalCoralDetectorSubsystem;
import frc.robot.subsystems.led.LEDInput;
import frc.robot.subsystems.led.LEDSubsystem;
import frc.robot.subsystems.led.SubdividedLedString;
import frc.robot.DefaultLEDStringCreator;
import frc.robot.operation.UserPolicy;


public class LEDDefaultCommand extends EntechCommand {
  private final InternalCoralDetectorOutput coralDetectorOutput;
  private final LEDSubsystem ledSubsystem;

  public LEDDefaultCommand(LEDSubsystem ledSubsystem, InternalCoralDetectorSubsystem coralDetectorSubsystem) {
    this.ledSubsystem = ledSubsystem;
    coralDetectorOutput = coralDetectorSubsystem.toOutputs();
    addRequirements(ledSubsystem);
  }

  @Override
  public void execute() {

    SubdividedLedString subdivided = new DefaultLEDStringCreator().createLEDString(coralDetectorOutput.hasCoral(), !UserPolicy.getInstance().isAligningToAngle(), hasError(), UserPolicy.getInstance().getTargetAngle());
    
    LEDInput input = new LEDInput(subdivided);
    ledSubsystem.updateInputs(input);
  }

  //TODO: Actual error checking
  private boolean hasError() {
      return false;
  }

}
