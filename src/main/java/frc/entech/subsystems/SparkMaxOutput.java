package frc.entech.subsystems;

import org.littletonrobotics.junction.Logger;

import com.revrobotics.spark.SparkMax;

public class SparkMaxOutput {

  private SparkMaxOutput(){}

  private double currentSpeed;
  private double appliedOutput;
  private double busVoltage;
  private double motorTemperature;
  private double outputCurrent;
  private boolean hasActiveFault;
  private boolean hasActiveWarning;
  private boolean isFollower;
  private boolean isUpperLimitPressed;
  private boolean isLowerLimitPressed;
  private double currentPosition;


  public static SparkMaxOutput createOutput(SparkMax sm ){
      SparkMaxOutput smo = new SparkMaxOutput();
      smo.currentSpeed = sm.getEncoder().getVelocity();
      smo.busVoltage = sm.getBusVoltage();
      smo.motorTemperature = sm.getMotorTemperature();
      smo.outputCurrent = sm.getOutputCurrent();
      smo.hasActiveFault = sm.hasActiveFault();
      smo.hasActiveWarning = sm.hasStickyFault();
      smo.isFollower = sm.isFollower();
      smo.appliedOutput = sm.getAppliedOutput();
      smo.isUpperLimitPressed = sm.getForwardLimitSwitch().isPressed();
      smo.isLowerLimitPressed = sm.getReverseLimitSwitch().isPressed();
      smo.currentPosition = sm.getEncoder().getPosition();
      return smo;
  }

  public void log(String baseName){
    Logger.recordOutput(baseName + "/currentSpeed", currentSpeed);
    Logger.recordOutput(baseName + "/busVoltage", busVoltage);
    Logger.recordOutput(baseName + "/motorTemperature", motorTemperature);
    Logger.recordOutput(baseName + "/outputCurrent", outputCurrent);
    Logger.recordOutput(baseName + "/hasActiveFault", hasActiveFault);
    Logger.recordOutput(baseName + "/hasActiveWarning", hasActiveWarning);
    Logger.recordOutput(baseName + "/isFollower", isFollower);
    Logger.recordOutput(baseName + "/appliedOutput", appliedOutput);
    Logger.recordOutput(baseName + "/isUpperLimitPressed", isUpperLimitPressed);
    Logger.recordOutput(baseName + "/isLowerLimitPressed", isLowerLimitPressed);
    Logger.recordOutput(baseName + "/currentPosition", currentPosition);

  }

}

