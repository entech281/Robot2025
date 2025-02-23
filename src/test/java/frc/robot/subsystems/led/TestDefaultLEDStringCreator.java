package frc.robot.subsystems.led;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import edu.wpi.first.wpilibj.util.Color;
import frc.robot.DefaultLEDStringCreator;
import frc.robot.RobotConstants;
import frc.robot.subsystems.led.SubdividedLedString.LedSection;

public class TestDefaultLEDStringCreator {

    @Test
    public void testHasError() {
        DefaultLEDStringCreator creator = new DefaultLEDStringCreator();
        int operator_leds_start = RobotConstants.LED.OPERATOR_LEDS_START_INDEX;
        int driver_leds_start = RobotConstants.LED.DRIVER_LEDS_START_INDEX;
        int operator_leds_end = RobotConstants.LED.OPERATOR_LEDS_END_INDEX;
        int driver_leds_end = RobotConstants.LED.DRIVER_LEDS_END_INDEX;
        
        SubdividedLedString subdivided = creator.createLEDString(true, false, true, 0);

        SubdividedLedString correctString = new SubdividedLedString();
        correctString.addSection(Color.kRed, Color.kBlack, operator_leds_start, operator_leds_end);
        correctString.addSection(Color.kRed, Color.kBlack, driver_leds_start, driver_leds_end);

        assertArrayEquals(correctString.toColorList().toArray(), subdivided.toColorList().toArray());
        assertTrue(subdivided.getSections().get(0).isBlinking());
        testSubdividedLedStringDoesNotExceedBoundaries(subdivided);
    }

    @Test
    public void testHasCoralAndAligned() {
        DefaultLEDStringCreator creator = new DefaultLEDStringCreator();
        int operator_leds_start = RobotConstants.LED.OPERATOR_LEDS_START_INDEX;
        int driver_leds_start = RobotConstants.LED.DRIVER_LEDS_START_INDEX;
        int operator_leds_end = RobotConstants.LED.OPERATOR_LEDS_END_INDEX;
        int driver_leds_end = RobotConstants.LED.DRIVER_LEDS_END_INDEX;
        
        SubdividedLedString subdivided = creator.createLEDString(true, true, false, 0);

        SubdividedLedString correctString = new SubdividedLedString();
        correctString.addSection(Color.kPurple, Color.kBlack, operator_leds_start, operator_leds_end);
        correctString.addSection(Color.kGreen, Color.kBlack, driver_leds_start, driver_leds_end);

        assertArrayEquals(correctString.toColorList().toArray(), subdivided.toColorList().toArray());
        assertTrue(!subdivided.getSections().get(0).isBlinking());
        assertTrue(subdivided.getSections().get(1).isBlinking());
        testSubdividedLedStringDoesNotExceedBoundaries(subdivided);
    }


    @Test
    public void testNoCoralAndAligned() {
        DefaultLEDStringCreator creator = new DefaultLEDStringCreator();
        int operator_leds_start = RobotConstants.LED.OPERATOR_LEDS_START_INDEX;
        int driver_leds_start = RobotConstants.LED.DRIVER_LEDS_START_INDEX;
        int operator_leds_end = RobotConstants.LED.OPERATOR_LEDS_END_INDEX;
        int driver_leds_end = RobotConstants.LED.DRIVER_LEDS_END_INDEX;
        
        SubdividedLedString subdivided = creator.createLEDString(false, true, false, 0);

        SubdividedLedString correctString = new SubdividedLedString();
        correctString.addSection(Color.kPurple, Color.kBlack, operator_leds_start, operator_leds_end);
        correctString.addSection(Color.kGreen, Color.kBlack, driver_leds_start, driver_leds_end);

        assertArrayEquals(correctString.toColorList().toArray(), subdivided.toColorList().toArray());
        assertTrue(subdivided.getSections().get(0).isBlinking());
        assertTrue(subdivided.getSections().get(1).isBlinking());
        testSubdividedLedStringDoesNotExceedBoundaries(subdivided);
    }

    @Test
    public void testNotAligned() {
        DefaultLEDStringCreator creator = new DefaultLEDStringCreator();

        SubdividedLedString subdivided = creator.createLEDString(true, false, false, 1.6);

        boolean redOnce = false;
        boolean passedTest = true;
        for (LedSection section : subdivided.getSections().subList(1, subdivided.getSections().size())) {
            if (section.getFgColor().equals(Color.kRed)) {
                if (redOnce) {
                    passedTest = false;
                    break;
                }
                redOnce = true;
            } else if (!section.getFgColor().equals(Color.kGreen)) {
                passedTest = false;
                break;
            }
        }

        if (!redOnce) {
            passedTest = false;
        }

        assertTrue(passedTest);
        testSubdividedLedStringDoesNotExceedBoundaries(subdivided);
    }

    //helper test
    public void testSubdividedLedStringDoesNotExceedBoundaries(SubdividedLedString subdivided) {
        for (LedSection section : subdivided.getSections()) {
            assertTrue(section.getEndIndex() <= RobotConstants.LED.NUM_LEDS);
        }
    }
    
}
