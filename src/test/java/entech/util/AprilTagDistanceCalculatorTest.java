package entech.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import frc.entech.util.AprilTagDistanceCalculator;
import frc.entech.util.AprilTagDistanceCalibration;

class AprilTagDistanceCalculatorTest {

    @Test
    void testCalculateCurrentDistanceInches_ValidInputs() {
        AprilTagDistanceCalibration calibration = new AprilTagDistanceCalibration(1920, 1080, 200, 10);
        double newTagWidthPixels = 100;
        double expectedDistance = 20; // (200 * 10) / 100

        double calculatedDistance = AprilTagDistanceCalculator.calculateCurrentDistanceInches(calibration, newTagWidthPixels);
        assertEquals(expectedDistance, calculatedDistance, 0.001);
    }

    @Test
    void testCalculateCurrentDistanceInches_NewTagWidthPixelsZero() {
        AprilTagDistanceCalibration calibration = new AprilTagDistanceCalibration(1920, 1080, 200, 10);

        assertThrows(IllegalArgumentException.class, () -> {
            AprilTagDistanceCalculator.calculateCurrentDistanceInches(calibration, 0);
        });
    }

    @Test
    void testCalculateCurrentDistanceInches_InvalidCalibration() {
        AprilTagDistanceCalibration calibration = new AprilTagDistanceCalibration(1920, 1080, 0, 10);

        assertThrows(IllegalArgumentException.class, () -> {
            AprilTagDistanceCalculator.calculateCurrentDistanceInches(calibration, 100);
        });
    }
}
