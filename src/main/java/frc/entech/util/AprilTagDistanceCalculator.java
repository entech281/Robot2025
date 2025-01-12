package frc.entech.util;

import edu.wpi.first.wpilibj.DriverStation;

public class AprilTagDistanceCalculator {

      // private double newDistanceFeet;
      // private double newTagWidthPixels;
      // private AprilTagDistanceCalibration calibration;

      private AprilTagDistanceCalculator ( AprilTagDistanceCalibration calibration, double newTagWidthPixels) {
 
      }
 
      public static double calculateCurrentDistanceInches( AprilTagDistanceCalibration calibration, double newTagWidthPixels ) {

            double newDistanceFeet = 0;

            try { 
                  newDistanceFeet = (calibration.getTagWidthPixels() * calibration.getDistanceFeet()) / newTagWidthPixels;
            }

            catch (ArithmeticException e) {

                  if (newTagWidthPixels == 0) {
                        DriverStation.reportWarning("newTagWidthPixels read as 0", e.getStackTrace());
                  }
                  else {
                        DriverStation.reportWarning("Invalid input for calibration", e.getStackTrace());
                  }
            }

            return newDistanceFeet;
      }
}
