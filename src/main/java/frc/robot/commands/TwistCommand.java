package frc.robot.commands;

import frc.entech.commands.EntechCommand;
import frc.robot.operation.UserPolicy;

public class TwistCommand extends EntechCommand {
  @Override
  public void initialize() {
    UserPolicy.getInstance().setIsTwistable(true);
  }

  @Override
  public void end(boolean interrupted) {
    UserPolicy.getInstance().setIsTwistable(false);
  }

  @Override
  public boolean runsWhenDisabled() {
    return true;
  }
}
