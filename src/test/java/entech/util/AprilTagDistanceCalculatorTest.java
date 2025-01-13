package entech.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import frc.entech.util.AprilTagDistanceCalculator;
import frc.entech.util.AprilTagDistanceCalibration;

class AprilTagDistanceCalculatorTest {

    @Test
    void testCalculateCurrentDistanceInches_ValidInputs() {
        AprilTagDistanceCalibration calibration = new AprilTagDistanceCalibration(1920, 1080, 200, 10);
        double newTagWidthPixels = 100;
        double expectedDistance = (200 * 10) / 100;

        double calculatedDistance = AprilTagDistanceCalculator.calculateCurrentDistanceInches(calibration, newTagWidthPixels);
        assertEquals(expectedDistance, calculatedDistance, 0.001);
    }

    @Test
    void testCalculateCurrentDistanceInches_NewTagWidthPixelsZero() {
        AprilTagDistanceCalibration calibration = new AprilTagDistanceCalibration(1920, 1080, 200, 10);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            AprilTagDistanceCalculator.calculateCurrentDistanceInches(calibration, 0);
        });
        assertEquals("New tag width in pixels must be greater than zero.", exception.getMessage());
    }



    // @Test
    // void testCalculateCurrentTagWidthPixels_InvalidCalibration() {

    //     boolean negativePixels = false;

    //     try {
    //         AprilTagDistanceCalibration calibration = new AprilTagDistanceCalibration(1920, 1080, -5, 10);
    //         AprilTagDistanceCalculator.calculateCurrentDistanceInches(calibration, 100);
    //     } catch (IllegalArgumentException e) {
    //         negativePixels = true;
    //     }

    //     assertTrue(negativePixels);
    // }

    // @Test
    // void testCalculateCurrentDistanceInches_InvalidCalibration() {

    //     boolean negativeDistance = false;

    //     try {
    //         AprilTagDistanceCalibration calibration = new AprilTagDistanceCalibration(1920, 1080, 100, -7);
    //         AprilTagDistanceCalculator.calculateCurrentDistanceInches(calibration, 100);
    //     } catch (IllegalArgumentException e) {
    //         negativeDistance = true;
    //     }

    //     assertTrue(negativeDistance);
    // }
    @Test
    void testCalculateCurrentDistanceFeet_InvalidCalibration(){

        AprilTagDistanceCalibration calibration = new AprilTagDistanceCalibration(1920, 1080, 100, -9);

        assertThrows(IllegalArgumentException.class, () -> {
            AprilTagDistanceCalculator.calculateCurrentDistanceInches(calibration, 100);
        });

    }

    @Test
    void testCalculateCurrentWidthPixels_InvalidCalibration(){

        AprilTagDistanceCalibration calibration = new AprilTagDistanceCalibration(1920, 1080, -100, 9);

        assertThrows(IllegalArgumentException.class, () -> {
            AprilTagDistanceCalculator.calculateCurrentDistanceInches(calibration, 100);
        });

    }
    
}
