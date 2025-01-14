package frc.robot.processors.filters;

import frc.robot.subsystems.drive.DriveInput;

public class LateralAlignFilter implements DriveFilterI {
    @Override
    public DriveInput process(DriveInput input) {
        DriveInput processedInput = new DriveInput(input);
        
        

        return processedInput;
    }
}
