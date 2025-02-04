package frc.robot.subsystems.coralMechanism;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.wpilibj2.command.Command;

import frc.entech.subsystems.EntechSubsystem;
import frc.robot.io.RobotIO;

public class CoralMechanismSubsystem extends EntechSubsystem<CoralMechanismInput, CoralMechanismOutput> {
    private static final boolean ENABLED = true;
    private static final boolean IS_INVERTED = false;
    
    private CoralMechanismInput currentInput = new CoralMechanismInput();

    private SparkMax CoralMotor;

    public void initialize() {
        if (ENABLED) {
            SparkMaxConfig Coral = new SparkMaxConfig();
            
            CoralMotor = new SparkMax(0, SparkMax.MotorType.kBrushless);
            CoralMotor.getEncoder().setPosition(0.0);
            Coral.inverted(IS_INVERTED);
            
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
    public Command getTestCommand() {
        return null;
    }

    @Override
    public CoralMechanismOutput toOutputs() {
        throw new UnsupportedOperationException("Unimplemented method 'toOutputs'");
    }
}
