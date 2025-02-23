package frc.robot;

import edu.wpi.first.wpilibj.util.Color;
import frc.robot.operation.UserPolicy;
import frc.robot.subsystems.led.SubdividedLedString;
import frc.robot.subsystems.led.SubdividedLedString.LedSection;

public class DefaultLEDStringCreator {
    public SubdividedLedString createLEDString(boolean hasCoral, boolean aligned, boolean hasError, double targetAngle) {
        // Create a new subdivided LED string that will span the entire LED strip.
        SubdividedLedString subdivided = new SubdividedLedString();
        int ledCount = RobotConstants.LED.NUM_LEDS;
        int operator_leds_start = RobotConstants.LED.OPERATOR_LEDS_START_INDEX;
        int driver_leds_start = RobotConstants.LED.DRIVER_LEDS_START_INDEX;
        int operator_leds_end = RobotConstants.LED.OPERATOR_LEDS_END_INDEX;
        int driver_leds_end = RobotConstants.LED.DRIVER_LEDS_END_INDEX;

        //TODO move to RobotConstants?
        LedSection operator_section = subdivided.addSection(Color.kBlack, Color.kBlack, operator_leds_start, operator_leds_end);
        LedSection driver_section = subdivided.addSection(Color.kBlack, Color.kBlack, driver_leds_start, driver_leds_end);
        final int MAX_DEG_CAMERA_CAN_SEE = 180;


        if (hasError) {
            // In error state: blink with a configuration of red (foreground) and blue (background)
            operator_section.setFgColor(Color.kRed);
            operator_section.setBgColor(Color.kBlack);
            operator_section.setBlinking(true);

            driver_section.setFgColor(Color.kRed);
            driver_section.setBgColor(Color.kBlack);
            driver_section.setBlinking(true);
        } else {
            if (hasCoral) {
                operator_section.setFgColor(Color.kPurple);
                operator_section.setBgColor(Color.kBlack);
                operator_section.setBlinking(false);
            } else {
                operator_section.setFgColor(Color.kPurple);
                operator_section.setBgColor(Color.kBlack);
                operator_section.setBlinking(true);
            }

            if (aligned) {
                driver_section.setFgColor(Color.kGreen);
                driver_section.setBgColor(Color.kBlack);
                driver_section.setBlinking(true);
            } else {
                //Since a new SubdividedLEDString is created
                //Each time this method is called, 
                //We shouldn't have to worry about tracking
                //and later removing these sections.
                //But we do have to remove the driver_section
                //Because it is created in this method
                subdivided.removeSection(driver_section);
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

        // driver_section = subdivided.addSection(Color.kBlack, Color.kBlack, driver_leds_start, driver_leds_end);
        // driver_section.setBlinking(false);
        // In normal state: solid display with green (foreground) and orange (as backup if blinking)
        // input.setBlinking(false);
        // subdivided.addSection(Color.kGreen, Color.kBlack, 0, ledCount);
        }
        return subdivided;
    }
}