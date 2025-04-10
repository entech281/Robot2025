
package frc.robot.subsystems.gamepiecehandler;

import frc.entech.commands.EntechCommand;
import frc.entech.util.StoppingCounter;
import frc.robot.RobotConstants;

public class TestGamePieceHandlerCommand extends EntechCommand{
  private final GamePieceHandlerSubsystem mechanism;
  private final StoppingCounter counter =
      new StoppingCounter(RobotConstants.TEST_CONSTANTS.STANDARD_TEST_LENGTH); 
  private int stage = 0;

   public TestGamePieceHandlerCommand(GamePieceHandlerSubsystem mechanismSubsystem) {
    super(mechanismSubsystem);
    this.mechanism = mechanismSubsystem;
  }

  @Override
  public void execute() {
    GamePieceHandlerInput input = new GamePieceHandlerInput();

    input.setActivate(true);
    input.setRequestedSpeed(0);

    switch (stage) {
      case 0 -> input.setRequestedSpeed(1);
      case 1 -> input.setRequestedSpeed(0);
      default -> { break; }
    }

    if (counter.isFinished(true)) {
      counter.reset();
      stage++;
    }

    if (stage != 1)
      mechanism.updateInputs(input);
  }

  @Override
  public void initialize() {
    counter.reset();
    GamePieceHandlerInput stop = new GamePieceHandlerInput();
    stage = 0;

    stop.setRequestedSpeed(0);
    stop.setActivate(true);

    mechanism.updateInputs(stop);
  }

  @Override
  public boolean isFinished() {
    return stage > 1;
  }

  @Override
  public void end(boolean interrupted) {
    GamePieceHandlerInput stop = new GamePieceHandlerInput();

    stop.setActivate(false);
    stop.setRequestedSpeed(0);

    mechanism.updateInputs(stop);
  }
    
}
