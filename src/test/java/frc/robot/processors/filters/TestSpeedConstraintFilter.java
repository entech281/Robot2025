package frc.robot.processors.filters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import frc.robot.io.RobotIO;
import frc.robot.subsystems.coraldetector.InternalCoralDetectorOutput;
import frc.robot.subsystems.coralmechanism.CoralMechanismOutput;
import frc.robot.subsystems.drive.DriveInput;
import frc.robot.subsystems.elevator.ElevatorOutput;

public class TestSpeedConstraintFilter {

    private SpeedConstraintFilter filter = new SpeedConstraintFilter();
    public static final double TOLERANCE = 0.01;



    @Test
    public void shouldBeFullSpeedWithNoCoral_ElevatorDown(){
        assertEquals(
            SpeedConstraintFilter.FULL_SPEED,
            SpeedConstraintFilter.computeMaxSpeed(0,false,false),
            TOLERANCE
        );
    }

    @Test
    public void shouldBeFullSpeedWithCoralAndIntakeRunning_ElevatorDown(){
        assertEquals(
            SpeedConstraintFilter.FULL_SPEED,
            SpeedConstraintFilter.computeMaxSpeed(0,true,true),
            TOLERANCE
        );
    }

    @Test
    void shouldBeSlowSpeedWithIntakeRunningButNoCoral(){
        assertEquals(
            SpeedConstraintFilter.SLOWEST_SPEED,
            SpeedConstraintFilter.computeMaxSpeed(0,false,true),
            TOLERANCE
        );
    }

    @Test
    void shouldBeModerateSpeedWithElevatorHalfUp(){
        assertEquals(
            0.6,
            SpeedConstraintFilter.computeMaxSpeed(10.0,false,false),
            TOLERANCE
        );
    }

    @Test
    void shouldBeSlowSpeedWithElevatorUp(){
        assertEquals(
            SpeedConstraintFilter.SLOWEST_SPEED,
            SpeedConstraintFilter.computeMaxSpeed(SpeedConstraintFilter.ELEVATOR_HEIGHT_AT_TOP,false,false),
            TOLERANCE
        );
    }

    @Test
    void crazyHighValueofElevatorShouldStillGiveSlowSpeed(){
        assertEquals(
            SpeedConstraintFilter.SLOWEST_SPEED,
            SpeedConstraintFilter.computeMaxSpeed(100.0,false,false),
            TOLERANCE
        );
    }

    @Test
    void crazyLowValueofElevatorShouldStillGiveFullSpeed(){
        assertEquals(
            SpeedConstraintFilter.FULL_SPEED,
            SpeedConstraintFilter.computeMaxSpeed(-100.0,false,false),
            TOLERANCE
        );
    }

    @Test
    gitvoid testSpeedWhenElevatorUpUsingActualFilter(){
        //only have to test this once to make sure values are correct
        //when coming from robot values

        setRobotIOValues(18.0,false,false);
        DriveInput input = new DriveInput();
        input.setXSpeed(1.0);

        assertEquals(0.28, filter.process(input).getXSpeed(),TOLERANCE);
        assertEquals(0.28, filter.process(input).getXSpeed(),TOLERANCE);
    }

    private static void setRobotIOValues (double elevatorPosition, boolean hasCoral, boolean intakeRunning){
        //this crazyness is why singletons are really bad for unit testing
        RobotIO rio = RobotIO.getInstance();
        ElevatorOutput eo = new ElevatorOutput();
        CoralMechanismOutput cmo = new CoralMechanismOutput();
        InternalCoralDetectorOutput cdo = new InternalCoralDetectorOutput();

        eo.setCurrentPosition(elevatorPosition);
        cmo.setRunning(intakeRunning);
        cdo.setCoralSensor(hasCoral);
        rio.setCoralMechanism(cmo);
        rio.setInternalCoralDetector(cdo);
        rio.setElevator(eo);
    }

}
