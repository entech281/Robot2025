package frc.robot.subsystems.led;

import org.junit.jupiter.api.Test;
import edu.wpi.first.wpilibj.util.Color;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Arrays;

public class TestSubdividedLedString {


    @Test
    public void testTwoSegments(){
        SubdividedLedString sls = new SubdividedLedString();
        SubdividedLedString.LedSection section1 = sls.addSection(Color.kAqua,Color.kBlack,0,5);
        SubdividedLedString.LedSection section2 = sls.addSection(Color.kAliceBlue,Color.kBlack,5,10);

        section1.setBlinking(false);
        section1.on();
        section2.setBlinking(true);
        section2.off();

        List<Color> r = sls.toColorList();
        assertArrayEquals( new Color[]{
            Color.kAqua, //0
            Color.kAqua, //1
            Color.kAqua, //2
            Color.kAqua, //3
            Color.kAqua, //4
            Color.kAliceBlue, //5
            Color.kAliceBlue, //6
            Color.kAliceBlue, //7
            Color.kAliceBlue, //8
            Color.kAliceBlue, //9

        }, r.toArray());
    }


}