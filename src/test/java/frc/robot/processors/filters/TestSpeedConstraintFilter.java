package frc.robot.processors.filters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import frc.robot.RobotConstants;
import frc.robot.io.RobotIO;
import frc.robot.subsystems.coraldetector.InternalCoralDetectorOutput;
import frc.robot.subsystems.coralmechanism.CoralMechanismOutput;
import frc.robot.subsystems.drive.DriveInput;
import frc.robot.subsystems.elevator.ElevatorOutput;

public class TestSpeedConstraintFilter {

    RobotIO rio = RobotIO.getInstance();
    ElevatorOutput eo = new ElevatorOutput();
    CoralMechanismOutput cmo = new CoralMechanismOutput();
    InternalCoralDetectorOutput cdo = new InternalCoralDetectorOutput();

    @Test
    public void testSpeedWhenElevatorUp(){
        eo.setCurrentPosition(18);
        cmo.setRunning(false);
        cdo.setCoralSensor(false);
        rio.setCoralMechanism(cmo);
        rio.setInternalCoralDetector(cdo);
        rio.setElevator(eo);

        DriveInput input = new DriveInput();
        input.setXSpeed(1);
        SpeedConstraintFilter filter = new SpeedConstraintFilter(RobotConstants.DrivetrainConstants.SPEED_LIMIT, 20.3);
        assertEquals(0.2, filter.process(input).getXSpeed());
    }

    @Test
    public void testSpeedWhenElevatorDown(){
        eo.setCurrentPosition(8);
        cmo.setRunning(false);
        cdo.setCoralSensor(false);
        rio.setCoralMechanism(cmo);
        rio.setInternalCoralDetector(cdo);
        rio.setElevator(eo);

        DriveInput input = new DriveInput();
        input.setXSpeed(1);
        SpeedConstraintFilter filter = new SpeedConstraintFilter(RobotConstants.DrivetrainConstants.SPEED_LIMIT, 20.3);
        assertEquals(1, filter.process(input).getXSpeed());
    }

    @Test
    public void testSpeedWhenIntaking(){
        eo.setCurrentPosition(0);
        cmo.setRunning(true);
        cdo.setCoralSensor(false);
        rio.setCoralMechanism(cmo);
        rio.setInternalCoralDetector(cdo);
        rio.setElevator(eo);

        DriveInput input = new DriveInput();
        input.setXSpeed(1);
        SpeedConstraintFilter filter = new SpeedConstraintFilter(RobotConstants.DrivetrainConstants.SPEED_LIMIT, 20.3);
        assertEquals(0.2, filter.process(input).getXSpeed());
    }

    @Test
    public void testSpeedWhenNotIntaking(){
        eo.setCurrentPosition(0);
        cmo.setRunning(false);
        cdo.setCoralSensor(false);
        rio.setCoralMechanism(cmo);
        rio.setInternalCoralDetector(cdo);
        rio.setElevator(eo);

        DriveInput input = new DriveInput();
        input.setXSpeed(1);
        SpeedConstraintFilter filter = new SpeedConstraintFilter(RobotConstants.DrivetrainConstants.SPEED_LIMIT, 20.3);
        assertEquals(1, filter.process(input).getXSpeed());
    }

    @Test
    public void testMap(){
        eo.setCurrentPosition(4.4);
        cmo.setRunning(false);
        cdo.setCoralSensor(false);
        rio.setCoralMechanism(cmo);
        rio.setInternalCoralDetector(cdo);
        rio.setElevator(eo);

        DriveInput input = new DriveInput();
        input.setXSpeed(1);
        SpeedConstraintFilter filter = new SpeedConstraintFilter(RobotConstants.DrivetrainConstants.SPEED_LIMIT, 20.3);
        assertEquals(1, filter.process(input).getXSpeed());

        eo.setCurrentPosition(20.3);
        rio.setElevator(eo);
        assertEquals(0.2, filter.process(input).getXSpeed());
    }
}
