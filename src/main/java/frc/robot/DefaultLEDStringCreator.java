package frc.robot;

import edu.wpi.first.wpilibj.util.Color;
import frc.robot.subsystems.led.SubdividedLedString;
import frc.robot.subsystems.led.SubdividedLedString.LedSection;

public class DefaultLEDStringCreator {
    public SubdividedLedString createLEDString(boolean hasCoral, boolean aligned, boolean hasError, double targetAngle) {
        // Create a new subdivided LED string that will span the entire LED strip.
        SubdividedLedString subdivided = new SubdividedLedString();
        int ledCount = RobotConstants.LED.NUM_LEDS;
        int operatorLedsStart = RobotConstants.LED.OPERATOR_LEDS_START_INDEX;
        int driverLedsStart = RobotConstants.LED.DRIVER_LEDS_START_INDEX;
        int operatorLedsEnd = RobotConstants.LED.OPERATOR_LEDS_END_INDEX;
        int driverLedsEnd = RobotConstants.LED.DRIVER_LEDS_END_INDEX;

        //TODO move to RobotConstants?
        LedSection operatorSection = subdivided.addSection(Color.kBlack, Color.kBlack, operatorLedsStart, operatorLedsEnd);
        LedSection driverSection = subdivided.addSection(Color.kBlack, Color.kBlack, driverLedsStart, driverLedsEnd);
        final int MAX_DEG_CAMERA_CAN_SEE = 180;


        if (hasError) {
            // In error state: blink with a configuration of red (foreground) and blue (background)
            operatorSection.setFgColor(Color.kRed);
            operatorSection.setBgColor(Color.kBlack);
            operatorSection.setBlinking(true);

            driverSection.setFgColor(Color.kRed);
            driverSection.setBgColor(Color.kBlack);
            driverSection.setBlinking(true);
        } else {
            if (hasCoral) {
                operatorSection.setFgColor(Color.kPurple);
                operatorSection.setBgColor(Color.kBlack);
                operatorSection.setBlinking(false);
            } else {
                operatorSection.setFgColor(Color.kPurple);
                operatorSection.setBgColor(Color.kBlack);
                operatorSection.setBlinking(true);
            }

            if (aligned) {
                driverSection.setFgColor(Color.kGreen);
                driverSection.setBgColor(Color.kBlack);
                driverSection.setBlinking(true);
            } else {
                //Since a new SubdividedLEDString is created
                //Each time this method is called, 
                //We shouldn't have to worry about tracking
                //and later removing these sections.
                //But we do have to remove the driverSection
                //Because it is created in this method
                subdivided.removeSection(driverSection);
                //TODO: Find out what getTargetAngle() actually returns
                //I assumed it returned radians and we can only see 180 deg
                //TODO: clean out magic numbers
                int redIndex = (int) targetAngle;
                redIndex = (int) ((redIndex * (180 / Math.PI)) / MAX_DEG_CAMERA_CAN_SEE * ledCount);
                if (redIndex - 6 > 0) {
                    subdivided.addSection(Color.kGreen, Color.kGreen, 0, redIndex - 6);
                }
                // Single LED marked red at the target.
                subdivided.addSection(Color.kYellow, Color.kYellow, redIndex - 6, Math.max(redIndex - 6, 0));
                subdivided.addSection(Color.kRed, Color.kRed, redIndex, redIndex + 1);
                subdivided.addSection(Color.kYellow, Color.kYellow, redIndex, Math.min(redIndex + 6, ledCount - 1));
                // From redIndex+1 to ledCount: green segment.
                if (redIndex + 6 < ledCount - 1) {
                    subdivided.addSection(Color.kGreen, Color.kGreen, redIndex + 6, ledCount);
                }
            }
        }
        return subdivided;
    }
}