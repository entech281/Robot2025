package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.robot.io.RobotIO;
import frc.robot.livetuning.LiveTuningHandler;
import frc.robot.subsystems.pivot.PivotInput;
import frc.robot.subsystems.pivot.PivotSubsystem;

public class PivotMoveCommand extends EntechCommand {
  /** Creates a new PivotCommand. */
  private final PivotInput pivotInput = new PivotInput();
  private final PivotSubsystem pivotSS;
  private final PivotInput.Position position;

  public PivotMoveCommand(PivotSubsystem pivotSubsystem, PivotInput.Position position) {
    super(pivotSubsystem);
    pivotSS = pivotSubsystem;
    this.position = position;
  }

  @Override
  public void initialize() {
    pivotInput.setRequestedPosition(LiveTuningHandler.getInstance().getValue(position.label));
  }

  @Override
  public void execute() {
    pivotSS.updateInputs(pivotInput);
  }

  @Override
  public boolean isFinished() {
    return RobotIO.getInstance().getPivotOutput().atRequestedPosition();
  }
}
