package frc.robot;

import java.util.Map;

import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.math.util.Units;
import frc.entech.util.AprilTagDistanceCalibration;
import frc.robot.commandchecker.SafeZone;


public final class RobotConstants {
  public static final double TIME_PER_PERIODICAL_LOOP_SECONDS = 0.00;

  public static interface DrivetrainConstants {
    // Driving Parameters - Note that these are not the maximum capable speeds of
    // the robot, rather the allowed maximum speeds
    public static final double MAX_SPEED_METERS_PER_SECOND = 6.0; // 4.42; //4.8;
    public static final double MAX_ANGULAR_SPEED_RADIANS_PER_SECOND = 4 * Math.PI;
    // radians per second

    public static final double DIRECTION_SLEW_RATE = 1.2; // radians per second
    public static final double MAGNITUDE_SLEW_RATE = 4.75;
    // 2.0; //1.8; // percent per second (1 = 100%)
    public static final double ROTATIONAL_SLEW_RATE = 3.5;
    // 20.0; //2.0; // percent per second (1 = 100%)

    // Chassis configuration
    public static final double TRACK_WIDTH_METERS = Units.inchesToMeters(21.5);

    // Distance between centers of right and left wheels on robot
    public static final double WHEEL_BASE_METERS = Units.inchesToMeters(18);

    // Distance to farthest module
    public static final double DRIVE_BASE_RADIUS_METERS = 0.39;

    // Distance between front and back wheels on robot
    public static final SwerveDriveKinematics DRIVE_KINEMATICS =
        new SwerveDriveKinematics(new Translation2d(WHEEL_BASE_METERS / 2, TRACK_WIDTH_METERS / 2),
            new Translation2d(WHEEL_BASE_METERS / 2, -TRACK_WIDTH_METERS / 2),
            new Translation2d(-WHEEL_BASE_METERS / 2, TRACK_WIDTH_METERS / 2),
            new Translation2d(-WHEEL_BASE_METERS / 2, -TRACK_WIDTH_METERS / 2));

    public static final boolean GYRO_REVERSED = false;
    public static final boolean RATE_LIMITING = true;
  }

  public static interface SafeZones {
    public static final SafeZone[] SAFE_ZONES = new SafeZone[] {
      new SafeZone(-0.05, 22, 30, 38),
      new SafeZone(6.3, 22, 30, 168),
      new SafeZone(3.2, 22, 30, 43),
      new SafeZone(11, 22, 30, 168),
      new SafeZone(18.7, 22, 30, 83),
      new SafeZone(20, 22, 30, 173)
    };
  }


  public static interface SwerveModuleConstants {
    public static final double FREE_SPEED_RPM = 5676;

    // The MAXSwerve module can be configured with one of three pinion gears: 12T,
    // 13T, or 14T.
    // This changes the drive speed of the module (a pinion gear with more teeth
    // will result in a
    // robot that drives faster).
    public static final int DRIVING_MOTOR_PINION_TEETH = 14;

    // Invert the turning encoder, since the output shaft rotates in the opposite
    // direction of
    // the steering motor in the MAXSwerve Module.
    public static final boolean TURNING_ENCODER_INVERTED = true;

    // Calculations required for driving motor conversion factors and feed forward
    public static final double DRIVING_MOTOR_FREE_SPEED_RPS = FREE_SPEED_RPM / 60;
    public static final double WHEEL_DIAMETER_METERS = Units.inchesToMeters(3.8); // 4.125;
                                                                                  // distance 8.62
    public static final double WHEEL_CIRCUMFERENCE_METERS = WHEEL_DIAMETER_METERS * Math.PI;
    public static final double DRIVING_MOTOR_REDUCTION =
        (45.0 * 17 * 50) / (DRIVING_MOTOR_PINION_TEETH * 15 * 27);
    public static final double DRIVE_WHEEL_FREE_SPEED_RPS =
        (DRIVING_MOTOR_FREE_SPEED_RPS * WHEEL_CIRCUMFERENCE_METERS) / DRIVING_MOTOR_REDUCTION;

    public static final double DRIVING_ENCODER_POSITION_FACTOR_METERS_PER_ROTATION =
        (WHEEL_DIAMETER_METERS * Math.PI) / DRIVING_MOTOR_REDUCTION; // meters, per rotation
    public static final double DRIVING_ENCODER_VELOCITY_FACTOR_METERS_PER_SECOND_PER_RPM =
        ((WHEEL_DIAMETER_METERS * Math.PI) / DRIVING_MOTOR_REDUCTION) / 60.0;
    // meters per second, per RPM

    public static final double TURNING_MOTOR_REDUCTION = 150.0 / 7.0;
    // ratio between internal relative encoder and
    // Through Bore (or Thrifty in our case)
    // absolute encoder - 150.0 / 7.0

    public static final double TURNING_ENCODER_POSITION_FACTOR_RADIANS_PER_ROTATION =
        (2 * Math.PI) / TURNING_MOTOR_REDUCTION; // radians, per rotation
    public static final double TURNING_ENCODER_VELOCITY_FACTOR_RADIANS_PER_SECOND_PER_RPM =
        (2 * Math.PI) / TURNING_MOTOR_REDUCTION / 60.0; // radians per second, per RPM

    public static final double TURNING_ENCODER_POSITION_PID_MIN_INPUT_RADIANS = 0; // radians
    public static final double TURNING_ENCODER_POSITION_PID_MAX_INPUT_RADIANS = (2 * Math.PI);
    // radians

    public static final double DRIVING_P = 0.2; // Origional P = 0.07
    public static final double DRIVING_I = 0;
    public static final double DRIVING_D = 0;
    public static final double DRIVING_FF = 1 / DRIVE_WHEEL_FREE_SPEED_RPS;
    public static final double DRIVING_MIN_OUTPUT_NORMALIZED = -1;
    public static final double DRIVING_MAX_OUTPUT_NORMALIZED = 1;

    public static final double TURNING_P = 1.0;
    // 1.0; // 1.0 might be a bit too much - reduce a bit if needed
    public static final double TURNING_I = 0;
    public static final double TURNING_D = 0;
    public static final double TURNING_FF = 0;
    public static final double TURNING_MIN_OUTPUT_NORMALIZED = -1;
    public static final double TURNING_MAX_OUTPUT_NORMALIZED = 1;

    public static final IdleMode DRIVING_MOTOR_IDLE_MODE = IdleMode.kBrake;
    public static final IdleMode TURNING_MOTOR_IDLE_MODE = IdleMode.kBrake;

    public static final int DRIVING_MOTOR_CURRENT_LIMIT_AMPS = 40; // 50; // amps
    public static final int TURNING_MOTOR_CURRENT_LIMIT_AMPS = 20; // amps

    public static final double FRONT_LEFT_VIRTUAL_OFFSET_RADIANS = 0.25552591580217987;
    public static final double FRONT_RIGHT_VIRTUAL_OFFSET_RADIANS = -0.4542734933134782;
    public static final double REAR_LEFT_VIRTUAL_OFFSET_RADIANS = -2.1838283853944285;
    public static final double REAR_RIGHT_VIRTUAL_OFFSET_RADIANS = -0.8463679267332642;
  }

  public static interface LiveTuning {
    public static final Map<String, Double> VALUES = Map.ofEntries(
      Map.entry("ElevatorSubsystem/NudgeAmount", 5.0),
      Map.entry("PivotSubsystem/NudgeAmount", 5.0),
      Map.entry(Position.ALGAE_GROUND.getElevatorKey(), 0.001),
      Map.entry(Position.ALGAE_L2.getElevatorKey(), 6.3),
      Map.entry(Position.ALGAE_L3.getElevatorKey(), 11.0),
      Map.entry(Position.L1.getElevatorKey(), 1.0),
      Map.entry(Position.L2.getElevatorKey(), 3.2),
      Map.entry(Position.L3.getElevatorKey(), 8.5),
      Map.entry(Position.L4.getElevatorKey(), 18.7),
      Map.entry(Position.BARGE.getElevatorKey(), 21.5),
      Map.entry(Position.HOME.getElevatorKey(), 0.001),
      Map.entry(Position.SAFE_EXTEND.getElevatorKey(), -999.0),
      Map.entry(Position.ALGAE_GROUND.getPivotKey(), 180.0),
      Map.entry(Position.ALGAE_L2.getPivotKey(), 165.0),
      Map.entry(Position.ALGAE_L3.getPivotKey(), 165.0),
      Map.entry(Position.L1.getPivotKey(), 35.0),
      Map.entry(Position.L2.getPivotKey(), 37.0),
      Map.entry(Position.L3.getPivotKey(), 37.0),
      Map.entry(Position.L4.getPivotKey(), 80.0),
      Map.entry(Position.BARGE.getPivotKey(), 80.0),
      Map.entry(Position.HOME.getPivotKey(), 15.0),
      Map.entry(Position.ALGAE_HOME.getElevatorKey(), 0.0),
      Map.entry(Position.ALGAE_HOME.getPivotKey(), 100.0),
      Map.entry(Position.SAFE_EXTEND.getPivotKey(), 35.0),
      Map.entry("CoralMechanismSubsystem/StartSpeed", 0.2),
      Map.entry("CoralMechanismSubsystem/FireSpeed", 1.0),
      Map.entry("CoralMechanismSubsystem/SlowDownSpeed", 0.1),
      Map.entry("CoralMechanismSubsystem/L1FireSpeed", 0.3),
      Map.entry("CoralMechanismSubsystem/AlgaeIntakeSpeed", 0.2)
    );
  }

  public static interface ELEVATOR {
    public static final double INITIAL_POSITION = 0.0;
    public static final double UPPER_SOFT_LIMIT_DEG = 22.25;
    public static final double LOWER_SOFT_LIMIT_DEG = 1;
    public static final double ELEVATOR_CONVERSION_FACTOR = 1.9;
    public static final double POSITION_TOLERANCE_DEG = 2;
    public static final double SLOT0_MAX_VELOCITY = 5600;
    public static final double SLOT0_MAX_ACCELERATION = 3200;
    public static final double SLOT1_MAX_VELOCITY = 5600;
    public static final double SLOT1_MAX_ACCELERATION = 3200;
    public static final double SLOT0_ALLOWED_ERROR = 0.15;
    public static final double SLOT1_ALLOWED_ERROR = 0.15;
  }

  public static interface CORAL{
    public static final double CORAL_CONVERSION_FACTOR = 2.4;
    public static final double CORAL_TOLERANCE_DEG = 2;
  }

  public static interface PIVOT {
    public static final double POSITION_TOLERANCE_DEG = 2;
    public static final double POSITION_TOLERANCE_BIG = 4;
  }

  public static interface LED {
    public static final int PORT = 0;
    public static final int NUM_LEDS = 100;
    public static final double BLINK_INTERVAL = 0.25;
    public static final int OPERATOR_LEDS_START_INDEX = 0;
    public static final int OPERATOR_LEDS_END_INDEX = 67;
    public static final int DRIVER_LEDS_START_INDEX = 68;
    public static final int DRIVER_LEDS_END_INDEX = NUM_LEDS;
  }


  public static interface PORTS {

    public static interface ANALOG {
      public static final int FRONT_LEFT_TURNING_ABSOLUTE_ENCODER = 0;
      public static final int REAR_LEFT_TURNING_ABSOLUTE_ENCODER = 2;
      public static final int FRONT_RIGHT_TURNING_ABSOLUTE_ENCODER = 1;
      public static final int REAR_RIGHT_TURNING_ABSOLUTE_ENCODER = 3;
    }


    public static interface CAN {
      public static final int FRONT_LEFT_DRIVING = 12;
      public static final int FRONT_RIGHT_DRIVING = 22;
      public static final int REAR_LEFT_DRIVING = 32;
      public static final int REAR_RIGHT_DRIVING = 42;

      public static final int FRONT_LEFT_TURNING = 11;
      public static final int FRONT_RIGHT_TURNING = 21;
      public static final int REAR_LEFT_TURNING = 31;
      public static final int REAR_RIGHT_TURNING = 41;

      public static final int ELEVATOR_A = 13;
      public static final int ELEVATOR_B = 14;

      public static final int CORAL_MOTOR = 35; 

      public static final int PIVOT_MOTOR = 18;


    }


    public static interface CONTROLLER {
      public static final double JOYSTICK_AXIS_THRESHOLD = 0.2;
      public static final int DRIVER_CONTROLLER = 0;
      public static final int PANEL = 1;
      public static final int TEST_JOYSTICK = 2;
      public static final int TUNING_CONTROLLER = 3;

      public static interface BUTTONS_JOYSTICK {
        public static final int TWIST = 1;
        public static final int RUN_TESTS = 7;
        public static final int GYRO_RESET = 11;
        public static final int RESET_ODOMETRY = 3;
        public static final int CLIMB_JOG_LEFT = 9;
        public static final int CLIMB_JOG_RIGHT = 10;
      }

      public static interface BUTTONS_XBOX {
        public static final int GYRO_RESET = 7;
        public static final int NOTE_ALIGN = 2;
        public static final int FULL_PIVOT = 8;
        public static final int TARGET_AMP = 4;
        public static final int TARGET_SPEAKER = 1;
        public static final int FEED_SHOOTER = 5;
        public static final int DRIVE_X = 3;
        public static final int RESET_ODOMETRY = 8;
      }
    }

    public static interface HAS_CORAL {
      public static final int INTERNAL_SENSOR_FORWARD = 9;
    }
  }

  public interface OPERATOR_PANEL {
    public static interface BUTTONS {
      public static final int RUN_TEST = 10;
      public static final int L1 = 8;
      public static final int L2 = 7;
      public static final int L3 = 6;
      public static final int L4 = 5;
      public static final int ALGAE_L2 = 2;
      public static final int ALGAE_L3 = 3;
      public static final int ALGAE_GROUND = 9;
      public static final int BARGE = 4;
      public static final int FIRE = 1;      
    }

    public static interface SWITCHES {
    }
  }

  public static interface Vision {
    public static final Matrix<N3, N1> VISION_STD_DEVS = VecBuilder.fill(5, 5, 1000000);


    public static interface Cameras {
      public static final String COLOR = "Arducam_OV9782_USB_Camera";
      public static final String LEFT = "Arducam_OV9281_USB_Camera";
      public static final String RIGHT = "Arducam_Alpha";
    }


    public static interface Filters {
      public static final double MAX_AMBIGUITY = 0.5;
      public static final double MAX_DISTANCE = 3.0;
      public static final int[] ALLOWED_TAGS =
          new int[] {1, 2, 5, 6, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22};
    }

    public static interface Resolution {
      public static final double[] COLOR_RESOLUTION = {320, 240};
    }


    public static interface Transforms {
      public static final Transform3d LEFT = new Transform3d(
          new Translation3d(Units.inchesToMeters(-6), Units.inchesToMeters(6.5),
              Units.inchesToMeters(18.5)),
          new Rotation3d(Units.degreesToRadians(0), Units.degreesToRadians(10),
              Units.degreesToRadians(90)));
      public static final Transform3d RIGHT = new Transform3d(
          new Translation3d(Units.inchesToMeters(-6), Units.inchesToMeters(-6.5),
              Units.inchesToMeters(18.5)),
          new Rotation3d(Units.degreesToRadians(0), Units.degreesToRadians(10),
              Units.degreesToRadians(-90)));
    }
  }

  public static interface AUTONOMOUS {
    public static final double MAX_MODULE_SPEED_METERS_PER_SECOND = 4.5; // 4.42

    public static final double TRANSLATION_CONTROLLER_P = 5;
    public static final double ROTATION_CONTROLLER_P = 5;
  }


  public static interface INDICATOR_VALUES {
    public static final double POSITION_UNKNOWN = -1.0;
    public static final double POSITION_NOT_SET = -1.1;
  }


  public static interface ODOMETRY {
    public static final double FIELD_LENGTH_INCHES = 54 * 12 + 3.25;
    public static final double FIELD_WIDTH_INCHES = 26 * 12 + 11.25;

    public static final Translation2d INITIAL_TRANSLATION =
        new Translation2d(Units.inchesToMeters(FIELD_LENGTH_INCHES / 2),
            Units.inchesToMeters(FIELD_WIDTH_INCHES / 2));
    public static final Rotation2d INITIAL_ROTATION = Rotation2d.fromDegrees(0);

    public static final Pose2d INITIAL_POSE = new Pose2d(INITIAL_TRANSLATION, INITIAL_ROTATION);
  }


  public static interface OperatorMessages {
    public static final String SUBSYSTEM_TEST = "SubsystemTest";
  }

  public static interface TEST_CONSTANTS {
    public static final double STANDARD_TEST_LENGTH = 1;
  }

  public static interface APRIL_TAG_DATA {
    public static final AprilTagDistanceCalibration CALIBRATION = new AprilTagDistanceCalibration(640, 480, 70.2, 37.25/12);
    public static final Map<Integer, Double> TAG_ANGLES = Map.ofEntries(
      Map.entry(6, 300.0),
      Map.entry(7, 0.0),
      Map.entry(8, 60.0),
      Map.entry(9, 120.0),
      Map.entry(10, 180.0),
      Map.entry(11, 240.0),
      Map.entry(17, 240.0),
      Map.entry(18, 180.0),
      Map.entry(19, 120.0),
      Map.entry(20, 60.0),
      Map.entry(21, 0.0),
      Map.entry(22, 300.0)
    );
  }

  private RobotConstants() {}
}
