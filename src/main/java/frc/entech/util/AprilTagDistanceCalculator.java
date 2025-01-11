package frc.entech.util;

public class AprilTagDistanceCalculator {
 
   public static double calculateCurrentDistanceInches( AprilTagDistanceCalibration calibration, int tagHeight ){
         double distanceInches = ( calibration.distanceFeet * calibration.screen_hight_pixels * calibration.tagWidthPixels ) / ( tagHeight * calibration.screen_width_pixels );
         return distanceInches;

}
}
