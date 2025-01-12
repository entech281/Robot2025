package entech.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import frc.entech.util.AprilTagDistanceCalibration;

class AprilTagDistanceCalibrationTest {

    @Test
    void testGettersAndSetters() {
        AprilTagDistanceCalibration calibration = new AprilTagDistanceCalibration(1920, 1080, 200, 10);

        assertEquals(1920, calibration.getScreenWidthPixels());
        assertEquals(1080, calibration.getScreenHeightPixels());
        assertEquals(200, calibration.getTagWidthPixels());
        assertEquals(10, calibration.getDistanceFeet());

        calibration.setScreenWidthPixels(1280);
        calibration.setScreenHeightPixels(720);
        calibration.setTagWidthPixels(150);
        calibration.setDistanceFeet(15);

        assertEquals(1280, calibration.getScreenWidthPixels());
        assertEquals(720, calibration.getScreenHeightPixels());
        assertEquals(150, calibration.getTagWidthPixels());
        assertEquals(15, calibration.getDistanceFeet());
    }
}
