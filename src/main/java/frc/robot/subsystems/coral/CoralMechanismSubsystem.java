package frc.robot.subsystems.coralMechanism;

import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.wpilibj2.command.Command;
import frc.entech.subsystems.EntechSubsystem;
import frc.robot.RobotConstants;
import frc.robot.io.RobotIO;

public class CoralMechanismSubsystem extends EntechSubsystem<CoralMechanismInput, CoralMechanismOutput> {
    private static final boolean ENABLED = true;
    private static final boolean IS_INVERTED = false;

    private CoralMechanismInput currentInput = new CoralMechanismInput();
    private SparkMax coralMotor;
    private IdleMode mode;

    public static double calculateMotorSpeedFromInput(double inputSpeed) {
        return inputSpeed * RobotConstants.CORAL.CORAL_CONVERSION_FACTOR;
    }

    @Override
    public void initialize() {
        if (ENABLED) {
            SparkMaxConfig coralConfig = new SparkMaxConfig();
            coralMotor = new SparkMax(RobotConstants.PORTS.CAN.CORAL_MOTOR, MotorType.kBrushless);

            coralMotor.getEncoder().setPosition(0.0);
            coralConfig.inverted(IS_INVERTED);
            coralConfig.idleMode(IdleMode.kBrake);
            mode = IdleMode.kBrake;
        }
    }

    @Override
    public boolean isEnabled() {
        return ENABLED;
    }

    @Override
    public void updateInputs(CoralMechanismInput input) {
        RobotIO.processInput(input);
        this.currentInput = input;
    }

    @Override
    public CoralMechanismOutput toOutputs() {
        CoralMechanismOutput output = new CoralMechanismOutput();
        output.setRunning(currentInput.getActivate());
        output.setCurrentSpeed(coralMotor.getEncoder().getVelocity());
        output.setBrakeModeEnabled(IdleMode.kBrake == mode);
        return output;
    }

    @Override
    public Command getTestCommand() {
        return null;
    }

    @Override
    public void periodic() {
        if (ENABLED) {
            updateInputs(currentInput);

            if (currentInput.getActivate()) {
                double targetSpeed = calculateMotorSpeedFromInput(currentInput.getRequestedSpeed());
                coralMotor.set(targetSpeed);
            } else {
                coralMotor.set(0);
            }

            CoralMechanismOutput output = toOutputs();
            output.toLog();
        }
    }
}