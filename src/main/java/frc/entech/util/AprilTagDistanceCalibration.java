package frc.entech.util;

public class AprilTagDistanceCalibration {
    public double screen_width_pixels;
    public double screen_hight_pixels;
    public double tagWidthPixels;
    public double distanceFeet;
    
    public AprilTagDistanceCalibration ( int screen_width_pixels, int screen_hight_pixels, int tagWidthPixels, double distanceFeet ){
        this.screen_width_pixels = screen_width_pixels;
        this.screen_hight_pixels = screen_hight_pixels;
        this.tagWidthPixels = tagWidthPixels;
        this.distanceFeet = distanceFeet;
    }

}
