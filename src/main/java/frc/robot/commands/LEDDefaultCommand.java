package frc.robot.commands;



import frc.entech.commands.EntechCommand;
import frc.robot.subsystems.gamepiecehandler.GamePieceHandlerOutput;
import frc.robot.subsystems.gamepiecehandler.GamePieceHandlerSubsystem;
import frc.robot.subsystems.led.LEDInput;
import frc.robot.subsystems.led.LEDSubsystem;
import frc.robot.subsystems.led.SubdividedLedString;
import frc.robot.DefaultLEDStringCreator;
import frc.robot.operation.UserPolicy;


public class LEDDefaultCommand extends EntechCommand {
  private final GamePieceHandlerOutput gamePieceHandlerOutput;
  private final LEDSubsystem ledSubsystem;

  public LEDDefaultCommand(LEDSubsystem ledSubsystem, GamePieceHandlerSubsystem gamePieceHandlerSubsystem) {
    this.ledSubsystem = ledSubsystem;
    gamePieceHandlerOutput = gamePieceHandlerSubsystem.toOutputs();
    addRequirements(ledSubsystem);
  }

  @Override
  public void execute() {

    SubdividedLedString subdivided = new DefaultLEDStringCreator().createLEDString(gamePieceHandlerOutput.getHasCoral(), !UserPolicy.getInstance().isAligningToAngle(), hasError(), UserPolicy.getInstance().getTargetAngle());
    
    LEDInput input = new LEDInput(subdivided);
    ledSubsystem.updateInputs(input);
  }

  //TODO: Actual error checking
  private boolean hasError() {
      return false;
  }

}
