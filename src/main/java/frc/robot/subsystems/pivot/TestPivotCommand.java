
package frc.robot.subsystems.pivot;

import frc.entech.commands.EntechCommand;
import frc.entech.util.StoppingCounter;
import frc.robot.RobotConstants;

public class TestPivotCommand extends EntechCommand{
  private final PivotSubsystem pivot;
  private final StoppingCounter counter =
      new StoppingCounter(RobotConstants.TEST_CONSTANTS.STANDARD_TEST_LENGTH); 
  private int stage = 0;

   public TestPivotCommand(PivotSubsystem pivotSubsystem) {
    super(pivotSubsystem);
    this.pivot = pivotSubsystem;
  }

  @Override
  public void execute() {
    PivotInput input = new PivotInput();

    input.setActivate(true);
    input.setRequestedPosition(0);

    switch (stage) {
      case 0 -> input.setRequestedPosition(5);
      case 1 -> input.setRequestedPosition(0);
      default -> { break; }
    }

    if (counter.isFinished(true)) {
      counter.reset();
      stage++;
    }

    if (stage != 1)
      pivot.updateInputs(input);
  }

  @Override
  public void initialize() {
    counter.reset();
    PivotInput stop = new PivotInput();
    stage = 0;

    stop.setRequestedPosition(0);
    stop.setActivate(true);

    pivot.updateInputs(stop);
  }

  @Override
  public boolean isFinished() {
    return stage > 1;
  }

  @Override
  public void end(boolean interrupted) {
    PivotInput stop = new PivotInput();

    stop.setActivate(false);
    stop.setRequestedPosition(0);

    pivot.updateInputs(stop);
  }
    
}
