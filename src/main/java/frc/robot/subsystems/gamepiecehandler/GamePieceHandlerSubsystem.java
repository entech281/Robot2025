package frc.robot.subsystems.gamepiecehandler;

import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.wpilibj2.command.Command;
import frc.entech.subsystems.EntechSubsystem;
import frc.entech.subsystems.SparkMaxOutput;
import frc.robot.RobotConstants;
import frc.robot.io.RobotIO;

public class GamePieceHandlerSubsystem extends EntechSubsystem<GamePieceHandlerInput, GamePieceHandlerOutput> {
    private static final boolean ENABLED = true;
    private static final boolean IS_INVERTED = true;

    private GamePieceHandlerInput currentInput = new GamePieceHandlerInput();
    private SparkMax coralIntakeMotor;
    private IdleMode mode;
    private SparkMaxConfig coralConfig;

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
    public void updateInputs(GamePieceHandlerInput input) {
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
    public GamePieceHandlerOutput toOutputs() {
        GamePieceHandlerOutput output = new GamePieceHandlerOutput();
        if (ENABLED) {
            output.setRunning(currentInput.getActivate());
            output.setCurrentSpeed(coralIntakeMotor.getEncoder().getVelocity());
            output.setBrakeModeEnabled(IdleMode.kBrake == mode);

            SparkMaxOutput smo = SparkMaxOutput.createOutput(coralIntakeMotor);
            output.setMotor(smo);
        }
        return output;
    }

    @Override
    public Command getTestCommand() {
        return new TestGamePieceHandlerCommand(this);
    }

    @Override
    public void periodic() {
        if (ENABLED) {
            updateInputs(currentInput);

            if (currentInput.getActivate()) {
                double targetSpeed = currentInput.getRequestedSpeed();
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
        }
    }
}