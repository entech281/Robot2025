package frc.robot.commands;

import java.util.function.DoubleSupplier;

import frc.entech.commands.EntechCommand;
import frc.robot.io.RobotIO;
import frc.robot.subsystems.vision.VisionInput;
import frc.robot.subsystems.vision.VisionSubsystem;

public class VisionCameraSwitchingCommand extends EntechCommand {
    private final VisionSubsystem vision;
    private final VisionInput vi = new VisionInput();
    private final DoubleSupplier axis;
    
    public VisionCameraSwitchingCommand(VisionSubsystem vision, DoubleSupplier axis) {
        super(vision);
        this.vision = vision;
        this.axis = axis;
    }

    @Override
    public boolean runsWhenDisabled() {
        return true;
    }

    @Override
    public void execute() {
        if (RobotIO.getInstance().getInternalCoralDetectorOutput().hasCoral()) {
            vi.setCamera(VisionInput.Camera.SIDE);
        } else {
            if (axis.getAsDouble() <= -0.1) {
                vi.setCamera(VisionInput.Camera.BOTTOM);
            } else {
                vi.setCamera(VisionInput.Camera.TOP);
            }
        }
        vision.updateInputs(vi);
    }
}
