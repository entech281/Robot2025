package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.entech.util.StoppingCounter;
import frc.robot.Position;
import frc.robot.io.RobotIO;
import frc.robot.livetuning.LiveTuningHandler;
import frc.robot.subsystems.pivot.PivotInput;
import frc.robot.subsystems.pivot.PivotSubsystem;

public class PivotMoveCommand extends EntechCommand {
  /** Creates a new PivotCommand. */
  private final PivotInput pivotInput = new PivotInput();
  private final StoppingCounter counter = new StoppingCounter(0.06);
  private final PivotSubsystem pivotSS;
  private final Position position;

  public PivotMoveCommand(PivotSubsystem pivotSubsystem, Position position) {
    super(pivotSubsystem);
    pivotSS = pivotSubsystem;
    this.position = position;
  }

  @Override
  public void initialize() {
    pivotInput.setRequestedPosition(LiveTuningHandler.getInstance().getValue(position.getPivotKey()));
    counter.reset();
  }

  @Override
  public void execute() {
    pivotSS.updateInputs(pivotInput);
  }

  @Override
  public boolean isFinished() {
    return counter.isFinished(RobotIO.getInstance().getPivotOutput().isAtRequestedPosition());
  }
}
