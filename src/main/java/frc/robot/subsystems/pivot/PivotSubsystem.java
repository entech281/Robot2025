package frc.robot.subsystems.pivot;

import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.ClosedLoopConfig.FeedbackSensor;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.wpilibj2.command.Command;
import frc.entech.subsystems.EntechSubsystem;
import frc.entech.subsystems.SparkMaxOutput;
import frc.entech.util.EntechUtils;
import frc.robot.RobotConstants;
import frc.robot.io.RobotIO;

public class PivotSubsystem extends EntechSubsystem<PivotInput, PivotOutput> {
    private static final boolean ENABLED = true;
    private static final boolean IS_INVERTED = true;

    private PivotInput currentInput = new PivotInput();
    private SparkMax pivotMotor;
    private SparkClosedLoopController pidController;
    private IdleMode mode;

    public static double calculateMotorPositionFromDegrees(double degrees) {
        return (degrees / 360) + ENCODER_ZERO_OFFSET;
    }

    private static final double STARTING_POSITION = 0.0416;
    private static final double ENCODER_ZERO_OFFSET = 0.670 - STARTING_POSITION;
    

    @Override
    public void initialize() {
        if (ENABLED) {
            SparkMaxConfig pivotConfig = new SparkMaxConfig();
            pivotMotor = new SparkMax(RobotConstants.PORTS.CAN.PIVOT_MOTOR, MotorType.kBrushless);

            pivotConfig.inverted(IS_INVERTED);
            pivotConfig.idleMode(IdleMode.kBrake);
            mode = IdleMode.kBrake;
            pivotConfig.closedLoop.feedbackSensor(FeedbackSensor.kAbsoluteEncoder);
            pivotConfig.closedLoop.pidf(4.5, 0, 0, 0);
            pivotConfig.closedLoop.outputRange(-0.8, 0.8);
            pivotConfig.closedLoop.positionWrappingEnabled(true);
            pivotConfig.closedLoop.positionWrappingInputRange(0, 1);
            pivotMotor.configure(pivotConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
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
        if (ENABLED) {
            output.setMoving(pivotMotor.getEncoder().getVelocity() != 0);
            output.setBrakeModeEnabled(IdleMode.kBrake == mode);
            output.setCurrentPosition(EntechUtils.normalizeAngle(((pivotMotor.getAbsoluteEncoder().getPosition() * 360) - (ENCODER_ZERO_OFFSET * 360)) - 180) + 180);
            output.setAtRequestedPosition(Math.abs(output.getCurrentPosition()
                    - currentInput.getRequestedPosition()) < ((currentInput.getRequestedPosition() > 90) ? RobotConstants.PIVOT.POSITION_TOLERANCE_BIG : RobotConstants.PIVOT.POSITION_TOLERANCE_DEG ));
            output.setRequestedPosition(currentInput.getRequestedPosition());
            output.setSpeed(pivotMotor.get());
            output.setAbsoluteEncoder(pivotMotor.getAbsoluteEncoder().getPosition());

            SparkMaxOutput smo = SparkMaxOutput.createOutput(pivotMotor);
            output.setMotor(smo);
        }
        return output;
    }

    @Override
    public Command getTestCommand() {
        return new TestPivotCommand(this);
    }

    @Override
    public void periodic() {
        if (ENABLED) {
            if (currentInput.getActivate()) {
                double targetPosition = calculateMotorPositionFromDegrees((currentInput.getRequestedPosition()));
                pidController.setReference(targetPosition, ControlType.kPosition);
            } else {
                pivotMotor.set(0);
            }
        }
    }
}