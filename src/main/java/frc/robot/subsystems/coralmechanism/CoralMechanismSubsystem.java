package frc.robot.subsystems.coralmechanism;

import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.wpilibj2.command.Command;
import frc.entech.subsystems.EntechSubsystem;
import frc.robot.RobotConstants;
import frc.robot.io.RobotIO;

public class CoralMechanismSubsystem extends EntechSubsystem<CoralMechanismInput, CoralMechanismOutput> {
    private static final boolean ENABLED = false;
    private static final boolean IS_INVERTED = true;

    private CoralMechanismInput currentInput = new CoralMechanismInput();
    private SparkMax coralIntakeMotor;
    private IdleMode mode;
    private SparkMaxConfig coralConfig;

    public static double calculateMotorSpeedFromInput(double inputSpeed) {
        return inputSpeed * RobotConstants.CORAL.CORAL_CONVERSION_FACTOR;
    }

    @Override
    public void initialize() {
        if (ENABLED) {
            coralConfig = new SparkMaxConfig();
            coralIntakeMotor = new SparkMax(RobotConstants.PORTS.CAN.CORAL_MOTOR, MotorType.kBrushless);

            coralIntakeMotor.getEncoder().setPosition(0.0);
            coralConfig.inverted(IS_INVERTED);
            coralConfig.idleMode(IdleMode.kBrake);
            mode = IdleMode.kBrake;

            coralIntakeMotor.configure(coralConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
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

    private boolean hasAlgae() {
        currentInput.setActivate(true);
        currentInput.setRequestedSpeed(-0.05);
        updateInputs(currentInput);
        return coralIntakeMotor.get() < 0.0;
      }

    @Override
    public CoralMechanismOutput toOutputs() {
        CoralMechanismOutput output = new CoralMechanismOutput();
        if (ENABLED) {
            output.setRunning(currentInput.getActivate());
            output.setCurrentSpeed(coralIntakeMotor.getEncoder().getVelocity());
            output.setBrakeModeEnabled(IdleMode.kBrake == mode);
            output.setHasAlgae(hasAlgae());
        }
        return output;
    }

    @Override
    public Command getTestCommand() {
        return new TestCoralMechanismCommand(this);
    }

    @Override
    public void periodic() {
        if (ENABLED) {
            updateInputs(currentInput);

            if (currentInput.getActivate()) {
                double targetSpeed = calculateMotorSpeedFromInput(currentInput.getRequestedSpeed());
                coralIntakeMotor.set(targetSpeed);
            } else {
                coralIntakeMotor.set(0);
            }

            if (!currentInput.getBrakeMode() && mode != IdleMode.kCoast) {
                coralConfig.idleMode(IdleMode.kCoast);
                mode = IdleMode.kCoast;
                coralIntakeMotor.configure(coralConfig, null, null);
            } else if (currentInput.getBrakeMode() && mode != IdleMode.kBrake) {
                coralConfig.idleMode(IdleMode.kBrake);
                mode = IdleMode.kBrake;
                coralIntakeMotor.configure(coralConfig, null, null);
            }

            if (!currentInput.getBrakeMode() && mode != IdleMode.kCoast) {
                coralConfig.idleMode(IdleMode.kCoast);
                mode = IdleMode.kCoast;
                coralIntakeMotor.configure(coralConfig, null, null);
            } else if (currentInput.getBrakeMode() && mode != IdleMode.kBrake) {
                coralConfig.idleMode(IdleMode.kBrake);
                mode = IdleMode.kBrake;
                coralIntakeMotor.configure(coralConfig, null, null);
            }

            CoralMechanismOutput output = toOutputs();
            output.toLog();
        }
    }
}