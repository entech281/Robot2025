package frc.robot.subsystems.vision_simulation;

import org.ejml.simple.UnsupportedOperation;
import org.littletonrobotics.junction.LogTable;

import frc.entech.subsystems.SubsystemInput;


public class VisionSimulationInput implements SubsystemInput {
  @Override
  public void toLog(LogTable table) {
    throw new UnsupportedOperation();
  }

  @Override
  public void fromLog(LogTable table) {
    throw new UnsupportedOperation();
  }
}