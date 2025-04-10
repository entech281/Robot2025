package frc.robot.subsystems.gamepiecehandler;

import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.entech.subsystems.EntechSubsystem;
import frc.entech.subsystems.SparkMaxOutput;
import frc.robot.RobotConstants;
import frc.robot.io.RobotIO;

public class GamePieceHandlerSubsystem extends EntechSubsystem<GamePieceHandlerInput, GamePieceHandlerOutput> {
    public Trigger hasCoral = new Trigger(() -> this.hasCoral());
    public Trigger hasAlgae = new Trigger(() -> this.hasAlgae());
    private static final boolean ENABLED = true;
    private static final boolean IS_INVERTED = true;

    private GamePieceHandlerInput currentInput = new GamePieceHandlerInput();
    private SparkMax coralIntakeMotor;
    private IdleMode mode;
    private SparkMaxConfig coralConfig;

    private DigitalInput coralSensor;
    private DigitalInput algaeSensor;

    private enum HandlerState {
        EMPTY, INTAKING_CORAL, HOLDING_CORAL, SHOOTING_CORAL,
        INTAKING_ALGAE, HOLDING_ALGAE, SHOOTING_ALGAE
    }

    private HandlerState currentState;

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

            coralSensor = new DigitalInput(RobotConstants.PORTS.HAS_CORAL.INTERNAL_SENSOR_FORWARD);
            algaeSensor = new DigitalInput(RobotConstants.PORTS.HAS_ALGAE.INTERNAL_ALGAE_SENSOR);

            
            if (hasCoral()) {
                currentState = HandlerState.HOLDING_CORAL;
            } else if (hasAlgae()) {
                currentState = HandlerState.HOLDING_ALGAE;
            } else {
                currentState = HandlerState.EMPTY;
            }
            
        }
    }

    @Override
    public boolean isEnabled() {
        return ENABLED;
    }
  
    public boolean hasCoral() {
        return coralSensor.get();
    }

    public boolean hasAlgae() {
        return algaeSensor.get();
    }

    @Override
    public void updateInputs(GamePieceHandlerInput input) {
        RobotIO.processInput(input);
        this.currentInput = input;
    }

    public void intakeCoral() {
        if (!hasCoral() && !hasAlgae()) {
            currentState = HandlerState.INTAKING_CORAL;
        }
    }

    public void shootCoral() {
        if (hasCoral()) {
            currentState = HandlerState.SHOOTING_CORAL;
        }
    }

    public void intakeAlgae() {
        if (!hasCoral() && !hasAlgae()) {
            currentState = HandlerState.INTAKING_ALGAE;
        }
    }

    public void holdAlgae() {
      if (hasAlgae()) {
      }
    }

    public void shootAlgae() {
      if (hasAlgae()) {
        currentState = HandlerState.SHOOTING_ALGAE;
        currentInput.setActivate(true);
        currentInput.setRequestedSpeed(-0.05);
      }
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

            output.setHasCoral(hasCoral());
            output.setHasAlgae(hasAlgae());
        }
        return output;
    }

    @Override
    public Command getTestCommand() {
        return new TestGamePieceHandlerCommand(this);
    }

    private void updateState() {
        switch (currentState) {
        case EMPTY:
            currentInput.setActivate(false);
            currentInput.setRequestedSpeed(0.0);
            break;
        case INTAKING_CORAL:
            break;
        case HOLDING_CORAL:
            currentInput.setActivate(false);
            currentInput.setRequestedSpeed(0.0);
            break;
        case SHOOTING_CORAL:
            break;
        case INTAKING_ALGAE:
            currentInput.setActivate(true);
            currentInput.setRequestedSpeed(-0.05);
            break;
        case HOLDING_ALGAE:
            currentInput.setActivate(true);
            currentInput.setRequestedSpeed(-0.05);
            break;
        case SHOOTING_ALGAE:
            break;
        }
    }

    @Override
    public void periodic() {
        if (ENABLED) {
            updateState();
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