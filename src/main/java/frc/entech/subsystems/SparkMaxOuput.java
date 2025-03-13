// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.entech.subsystems;

import org.littletonrobotics.junction.Logger;

import com.revrobotics.spark.SparkMax;
/** Add your docs here. */
public class SparkMaxOuput {


    private double busVoltage = 0.0;
    private boolean isAtUpperLimit = false;

    public boolean isAtUpperLimit() {
        return isAtUpperLimit;
    }

    public double getBusVoltage() {
        return busVoltage;
    }

    private SparkMaxOuput(){}

    public static SparkMaxOuput createOuput(SparkMax m) {
        SparkMaxOuput o = new SparkMaxOuput();
        o.busVoltage = m.getBusVoltage();
        o.isAtUpperLimit = m.getForwardLimitSwitch().isPressed();
        
        return o;
    };


    public void log(String rootName){
        Logger.recordOutput(rootName + "/busVoltage", busVoltage);
    }
}


