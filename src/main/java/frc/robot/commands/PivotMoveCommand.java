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
  
    public PivotMoveCommand(PivotSubsystem pivotSubsystem) {
      super(pivotSubsystem);
      pivotSS = pivotSubsystem;
    }
  
    @Override
    public void initialize() {
      pivotInput.setRequestedPosition(RobotIO.getInstance().getPivotOutput().getCurrentPosition() + LiveTuningHandler.getInstance().getValue("PivotSubsystem/NudgeAmount"));
    }
  
    @Override
    public void execute() {
      pivotSS.updateInputs(pivotInput);
    }
  
    @Override
    public void end(boolean interrupted) {
      //Code stops on it's own so nothing to put in the end method
    }
  
    @Override
    public boolean isFinished() {
      return RobotIO.getInstance().getPivotOutput().atRequestedPosition();
    }
  }
  