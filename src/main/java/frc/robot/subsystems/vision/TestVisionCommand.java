package frc.robot.subsystems.vision;

import java.util.List;

import frc.entech.commands.EntechCommand;
import frc.entech.util.StoppingCounter;
import frc.robot.io.RobotIO;

public class TestVisionCommand extends EntechCommand {
    private final VisionSubsystem vision;
    private final VisionInput input = new VisionInput();
    private final StoppingCounter counter = new StoppingCounter(0.2);
    private int stage;

    public TestVisionCommand(VisionSubsystem vision) {
        super(vision);
        this.vision = vision;
    }

    @Override
    public void execute() {
        switch (stage) {
            case 0 -> input.setCamera(VisionInput.Camera.TOP);
            case 1 -> input.setCamera(VisionInput.Camera.BOTTOM);
            case 2 -> input.setCamera(VisionInput.Camera.SIDE);
            default -> input.setCamera(VisionInput.Camera.TOP);
        }

        List<VisionTarget> targets = RobotIO.getInstance().getVisionOutput().getTargets();
        if (!targets.isEmpty() && counter.isFinished(targets.get(0).getTagID() == 1)) {
            stage++;
            counter.reset();
        }

        vision.updateInputs(input);
    }

    @Override
    public void initialize() {
        stage = 0;
        counter.reset();
    }

    @Override
    public boolean isFinished() {
        return stage > 2;
    }
}
