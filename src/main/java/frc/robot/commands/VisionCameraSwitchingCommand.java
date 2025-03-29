package frc.robot.commands;

import java.util.function.DoubleSupplier;

import frc.entech.commands.EntechCommand;
import frc.robot.operation.UserPolicy;
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
        if (!UserPolicy.getInstance().getSelectedTargetLocations().stream().toList().isEmpty()) {
            vi.setCamera(UserPolicy.getInstance().getSelectedTargetLocations().stream().toList().get(0).camera);
            vision.updateInputs(vi);
        }
    }
}
