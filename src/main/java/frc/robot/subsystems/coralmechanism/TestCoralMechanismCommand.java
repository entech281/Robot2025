package frc.robot.subsystems.coralmechanism;

import frc.entech.commands.EntechCommand;
import frc.entech.util.StoppingCounter;
import frc.robot.RobotConstants;

public class TestCoralMechanismCommand extends EntechCommand{
  private final CoralMechanismSubsystem mechanism;
  private final StoppingCounter counter =
      new StoppingCounter(RobotConstants.TEST_CONSTANTS.STANDARD_TEST_LENGTH); 
  private int stage = 0;

   public TestCoralMechanismCommand(CoralMechanismSubsystem mechanismSubsystem) {
    super(mechanismSubsystem);
    this.mechanism = mechanismSubsystem;
  }

  @Override
  public void execute() {
    CoralMechanismInput input = new CoralMechanismInput();

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

    if (stage != 9)
      mechanism.updateInputs(input);
  }

  @Override
  public void initialize() {
    counter.reset();
    CoralMechanismInput stop = new CoralMechanismInput();
    stage = 0;

    stop.setRequestedSpeed(0);
    stop.setActivate(true);

    mechanism.updateInputs(stop);
  }

  @Override
  public boolean isFinished() {
    return stage > 9;
  }

  @Override
  public void end(boolean interrupted) {
    CoralMechanismInput stop = new CoralMechanismInput();

    stop.setActivate(false);
    stop.setRequestedSpeed(0);

    mechanism.updateInputs(stop);
  }
    
}
