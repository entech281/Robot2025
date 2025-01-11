package frc.entech.util;

import edu.wpi.first.wpilibj.DriverStation;

public class AprilTagDistanceCalculator {
 
      public static double calculateCurrentDistanceInches( AprilTagDistanceCalibration calibration, int newTagWidthPixels ) {

            double distanceInches = 0;

            try { 
                  distanceInches = (calibration.tagWidthPixels * calibration.distanceFeet) / newTagWidthPixels;
            }

            catch (ArithmeticException e) {
                  
                  if (newTagWidthPixels == 0) {
                        DriverStation.reportWarning("newTagWidthPixels read as 0", e.getStackTrace());
                  }
                  else {
                        DriverStation.reportWarning("Invalid input for calibration", e.getStackTrace());
                  }
            }

            return distanceInches;
      }
}
