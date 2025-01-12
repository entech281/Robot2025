package frc.entech.util;

import edu.wpi.first.wpilibj.DriverStation;

public class AprilTagDistanceCalculator {

      private AprilTagDistanceCalculator () {
 
      }
 
      public static double calculateCurrentDistanceInches( AprilTagDistanceCalibration calibration, double newTagWidthPixels ) throws ArithmeticException{

            double newDistanceFeet;

            try { 
                  newDistanceFeet = (calibration.getTagWidthPixels() * calibration.getDistanceFeet()) / newTagWidthPixels;
            }

            catch (ArithmeticException e) {

                  if (newTagWidthPixels == 0) {
                        DriverStation.reportWarning("newTagWidthPixels read as 0", e.getStackTrace());
                        throw e;
                  }
                  else {
                        DriverStation.reportWarning("Invalid input for calibration", e.getStackTrace());
                        throw e;
                  }
            }

            return newDistanceFeet;
      }
}
