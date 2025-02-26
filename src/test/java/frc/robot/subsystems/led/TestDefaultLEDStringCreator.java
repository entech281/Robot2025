package frc.robot.subsystems.led;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import edu.wpi.first.wpilibj.util.Color;
import frc.robot.DefaultLEDStringCreator;
import frc.robot.RobotConstants;
import frc.robot.subsystems.led.SubdividedLedString.LedSection;

class TestDefaultLEDStringCreator {

    @Test
    void testHasError() {
        DefaultLEDStringCreator creator = new DefaultLEDStringCreator();
        int operatorLedsStart = RobotConstants.LED.OPERATOR_LEDS_START_INDEX;
        int driverLedsStart = RobotConstants.LED.DRIVER_LEDS_START_INDEX;
        int operatorLedsEnd = RobotConstants.LED.OPERATOR_LEDS_END_INDEX;
        int driverLedsEnd = RobotConstants.LED.DRIVER_LEDS_END_INDEX;
        
        SubdividedLedString subdivided = creator.createLEDString(true, false, true, 0);

        SubdividedLedString correctString = new SubdividedLedString();
        correctString.addSection(Color.kRed, Color.kBlack, operatorLedsStart, operatorLedsEnd);
        correctString.addSection(Color.kRed, Color.kBlack, driverLedsStart, driverLedsEnd);

        assertArrayEquals(correctString.toColorList().toArray(), subdivided.toColorList().toArray());
        assertTrue(subdivided.getSections().get(0).isBlinking());
        testSubdividedLedStringDoesNotExceedBoundaries(subdivided);
    }

    @Test
    void testHasCoralAndAligned() {
        DefaultLEDStringCreator creator = new DefaultLEDStringCreator();
        int operatorLedsStart = RobotConstants.LED.OPERATOR_LEDS_START_INDEX;
        int driverLedsStart = RobotConstants.LED.DRIVER_LEDS_START_INDEX;
        int operatorLedsEnd = RobotConstants.LED.OPERATOR_LEDS_END_INDEX;
        int driverLedsEnd = RobotConstants.LED.DRIVER_LEDS_END_INDEX;
        
        SubdividedLedString subdivided = creator.createLEDString(true, true, false, 0);

        SubdividedLedString correctString = new SubdividedLedString();
        correctString.addSection(Color.kPurple, Color.kBlack, operatorLedsStart, operatorLedsEnd);
        correctString.addSection(Color.kGreen, Color.kBlack, driverLedsStart, driverLedsEnd);

        assertArrayEquals(correctString.toColorList().toArray(), subdivided.toColorList().toArray());
        assertTrue(!subdivided.getSections().get(0).isBlinking());
        assertTrue(subdivided.getSections().get(1).isBlinking());
        testSubdividedLedStringDoesNotExceedBoundaries(subdivided);
    }


    @Test
    void testNoCoralAndAligned() {
        DefaultLEDStringCreator creator = new DefaultLEDStringCreator();
        int operatorLedsStart = RobotConstants.LED.OPERATOR_LEDS_START_INDEX;
        int driverLedsStart = RobotConstants.LED.DRIVER_LEDS_START_INDEX;
        int operatorLedsEnd = RobotConstants.LED.OPERATOR_LEDS_END_INDEX;
        int driverLedsEnd = RobotConstants.LED.DRIVER_LEDS_END_INDEX;
        
        SubdividedLedString subdivided = creator.createLEDString(false, true, false, 0);

        SubdividedLedString correctString = new SubdividedLedString();
        correctString.addSection(Color.kPurple, Color.kBlack, operatorLedsStart, operatorLedsEnd);
        correctString.addSection(Color.kGreen, Color.kBlack, driverLedsStart, driverLedsEnd);

        assertArrayEquals(correctString.toColorList().toArray(), subdivided.toColorList().toArray());
        assertTrue(subdivided.getSections().get(0).isBlinking());
        assertTrue(subdivided.getSections().get(1).isBlinking());
        testSubdividedLedStringDoesNotExceedBoundaries(subdivided);
    }

    @Test
    void testNotAligned() {
        DefaultLEDStringCreator creator = new DefaultLEDStringCreator();

        SubdividedLedString subdivided = creator.createLEDString(true, false, false, 1.6);

        boolean redOnce = false;
        int numYellows = 0;
        boolean passedTest = true;
        for (LedSection section : subdivided.getSections().subList(1, subdivided.getSections().size())) {
            if (section.getFgColor().equals(Color.kRed)) {
                if (redOnce) {
                    passedTest = false;
                    break;
                }
                redOnce = true;
            } else if (section.getFgColor().equals(Color.kYellow)) {
                numYellows++;
                if (numYellows > 2) {
                    passedTest = false;
                    break;
                }
            } else if (!section.getFgColor().equals(Color.kGreen)) {
                passedTest = false;
                break;
            }
        }

        if (!redOnce) {
            passedTest = false;
        }

        if (numYellows != 2) {
            passedTest = false;
        }

        assertTrue(passedTest);
        testSubdividedLedStringDoesNotExceedBoundaries(subdivided);
    }

    //helper test
    void testSubdividedLedStringDoesNotExceedBoundaries(SubdividedLedString subdivided) {
        for (LedSection section : subdivided.getSections()) {
            assertTrue(section.getEndIndex() <= RobotConstants.LED.NUM_LEDS);
        }
    }
    
}
