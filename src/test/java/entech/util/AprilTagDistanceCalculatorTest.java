package entech.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import frc.entech.util.AprilTagDistanceCalculator;
import frc.entech.util.AprilTagDistanceCalibration;

public class AprilTagDistanceCalculatorTest {

    @Test
    public void testCalculateCurrentDistanceInches_ValidInputs() {
        AprilTagDistanceCalibration calibration = new AprilTagDistanceCalibration(1920, 1080, 200, 10);
        double newTagWidthPixels = 100;
        double expectedDistance = (200 * 10) / 100;

        double calculatedDistance = AprilTagDistanceCalculator.calculateCurrentDistanceInches(calibration, newTagWidthPixels);
        assertEquals(expectedDistance, calculatedDistance, 0.001);
    }

    @Test
    public void testCalculateCurrentDistanceInches_NewTagWidthPixelsZero() {
        AprilTagDistanceCalibration calibration = new AprilTagDistanceCalibration(1920, 1080, 200, 10);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            AprilTagDistanceCalculator.calculateCurrentDistanceInches(calibration, 0);
        });
        assertEquals("New tag width in pixels must be greater than zero.", exception.getMessage());
    }

    @Test
    public void testCalculateCurrentDistanceInches_InvalidCalibration() {
        AprilTagDistanceCalibration calibration = new AprilTagDistanceCalibration(1920, 1080, 0, 10);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            AprilTagDistanceCalculator.calculateCurrentDistanceInches(calibration, 100);
        });
        assertEquals("Calibration tag width must be greater than zero.", exception.getMessage());
    }
}