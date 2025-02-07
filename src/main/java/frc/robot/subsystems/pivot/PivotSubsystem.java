package frc.robot.subsystems.pivot;

import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.wpilibj2.command.Command;
import frc.entech.subsystems.EntechSubsystem;
import frc.robot.RobotConstants;
import frc.robot.io.RobotIO;

public class PivotSubsystem extends EntechSubsystem<PivotInput, PivotOutput> {
    private static final boolean ENABLED = true;
    private static final boolean IS_INVERTED = false;

    private PivotInput currentInput = new PivotInput();
    private SparkMax pivotMotor;
    private SparkClosedLoopController pidController;
    private IdleMode mode;

    public static double calculateMotorPositionFromDegrees(double degrees) {
        return degrees / RobotConstants.PIVOT.PIVOT_CONVERSION_FACTOR;
    }

    @Override
    public void initialize() {
        if (ENABLED) {
            SparkMaxConfig pivotConfig = new SparkMaxConfig();
            pivotMotor = new SparkMax(RobotConstants.PORTS.CAN.PIVOT_MOTOR, MotorType.kBrushless);

            pivotMotor.getEncoder().setPosition(0.0);
            pivotConfig.inverted(IS_INVERTED);
            pivotConfig.idleMode(IdleMode.kBrake);
            mode = IdleMode.kBrake;
            pidController = pivotMotor.getClosedLoopController();
        }
    }

    @Override
    public boolean isEnabled() {
        return ENABLED;
    }

    @Override
    public void updateInputs(PivotInput input) {
        RobotIO.processInput(input);
        this.currentInput = input;
    }

    @Override
    public PivotOutput toOutputs() {
        PivotOutput output = new PivotOutput();
        output.setMoving(pivotMotor.getEncoder().getVelocity() != 0);
        output.setBrakeModeEnabled(IdleMode.kBrake == mode);
        output.setCurrentPosition(pivotMotor.getEncoder().getPosition() * RobotConstants.PIVOT.PIVOT_CONVERSION_FACTOR);
        output.setAtRequestedPosition(Math.abs(output.getCurrentPosition() - currentInput.getRequestedPosition()) < RobotConstants.PIVOT.POSITION_TOLERANCE_DEG);
        output.setRequestedPosition(currentInput.getRequestedPosition());
        return output;
    }

    @Override
    public Command getTestCommand() {
        return null;
    }

    @Override
    public void periodic() {
        if (ENABLED) {
            if (currentInput.getActivate()) {
                double targetPosition = calculateMotorPositionFromDegrees(currentInput.getRequestedPosition());
                pidController.setReference(targetPosition, ControlType.kPosition);
            } else {
                pivotMotor.set(0);
            }
        }
    }
}