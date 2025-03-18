package frc.robot.subsystems.vision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class TestAprilTagDistanceColor {
    @Test
    void TestGreenArea() {
        VisionOutput output = new VisionOutput();

        output.setReefCloseness(VisionColorChecker.getCloseness(200));
        assertEquals("#00FF00", output.getReefCloseness());
        output.setReefCloseness(VisionColorChecker.getCloseness(250));
        assertEquals("#00FF00", output.getReefCloseness());
        output.setReefCloseness(VisionColorChecker.getCloseness(235));
        assertEquals("#00FF00", output.getReefCloseness());
        output.setReefCloseness(VisionColorChecker.getCloseness(222.2));
        assertEquals("#00FF00", output.getReefCloseness());
    }

    @Test
    void TestYellowArea() {
        VisionOutput output = new VisionOutput();

        output.setReefCloseness(VisionColorChecker.getCloseness(144));
        assertEquals("#FFFF00", output.getReefCloseness());
        output.setReefCloseness(VisionColorChecker.getCloseness(199));
        assertEquals("#FFFF00", output.getReefCloseness());
        output.setReefCloseness(VisionColorChecker.getCloseness(167));
        assertEquals("#FFFF00", output.getReefCloseness());
        output.setReefCloseness(VisionColorChecker.getCloseness(146.7));
        assertEquals("#FFFF00", output.getReefCloseness());
    }

    @Test
    void TestRedArea() {
        VisionOutput output = new VisionOutput();

        output.setReefCloseness(VisionColorChecker.getCloseness(251));
        assertEquals("#FF0000", output.getReefCloseness());
        output.setReefCloseness(VisionColorChecker.getCloseness(357));
        assertEquals("#FF0000", output.getReefCloseness());
        output.setReefCloseness(VisionColorChecker.getCloseness(274.3));
        assertEquals("#FF0000", output.getReefCloseness());
    }

    @Test
    void TestBlackArea() {
        VisionOutput output = new VisionOutput();

        output.setReefCloseness(VisionColorChecker.getCloseness(100));
        assertEquals("#000000", output.getReefCloseness());
        output.setReefCloseness(VisionColorChecker.getCloseness(-1));
        assertEquals("#000000", output.getReefCloseness());
        output.setReefCloseness(VisionColorChecker.getCloseness(57.4));
        assertEquals("#000000", output.getReefCloseness());
        output.setReefCloseness(VisionColorChecker.getCloseness(-2.6));
        assertEquals("#000000", output.getReefCloseness());
    }
}
