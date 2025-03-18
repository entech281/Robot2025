package frc.robot.subsystems.vision;

import java.util.List;
import java.util.Optional;

public class VisionColorChecker {
    public static final int MISSING_TARGET_COUNT = 3;

    private VisionTarget lastTarget;
    private int missingCounter = 0;

    public Optional<VisionTarget> getSelectedTarget(int selectedTagId, List<VisionTarget> targetList) {

        Optional<VisionTarget> selectedTarget = Optional.empty();
        for (VisionTarget target : targetList) {
            if (target.getTagID() == selectedTagId){
                selectedTarget = Optional.of(target);
            }
        }
        if (selectedTarget.isPresent()){
            VisionTarget s = selectedTarget.get();
            if (s.getTimestamp() > lastTarget.getTimestamp()){
                lastTarget = s;
                return Optional.of(s);
            }
            else {
                missingCounter += 1;
                if(missingCounter > MISSING_TARGET_COUNT) {
                    missingCounter = 0;
                    return Optional.empty();
                }
                else {
                    return Optional.of(lastTarget);
                }
            }
        }
        else{
            return Optional.empty();
        }
    }
    

    public static final String getCloseness(double selectedTagWidth) {
        if (selectedTagWidth >= 200 && selectedTagWidth <= 250) {
          return "#00FF00";
        }
        else if (selectedTagWidth >= 144 && selectedTagWidth < 200) {
          return "#FFFF00";
        }
        else if (selectedTagWidth > 250) {
          return "#FF0000";
        }
        else {
          return "#000000";
        }
    }
}
