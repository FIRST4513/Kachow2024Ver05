package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class RobotConfig {
    // Required variables for libraries and DriverStation managing
    private static final boolean FAKE_FMS = true;
    public  static final boolean ENABLE_DASHBOARD = true;
    public  static final double  loopPeriodSecs = 0.02;
    public  static final boolean TUNING_MODE = false;
    
    public final class Motors {
        // ----- Swerve drive -----
        public final static int FLdriveMotorID     =  1;  // Can ID Kraken
        public final static int FRdriveMotorID     =  2;  // Can ID Kraken
        public final static int BLdriveMotorID     =  3;  // Can ID Kraken
        public final static int BRdriveMotorID     =  4;  // Can ID Kraken

        public final static int FLangleMotorID     =  5;  // Can ID Kraken
        public final static int FRangleMotorID     =  6;  // Can ID Kraken
        public final static int BLangleMotorID     =  7;  // Can ID Kraken
        public final static int BRangleMotorID     =  8;  // Can ID Kraken

        // ----- Other subsystems -----
        public final static int elevatorMotorID    = 14;  // Can ID Kraken - 5 -> 6
        public final static int climberMotorID1    = 15;  // Can ID Kraken - 5 -> 6
        public final static int climberMotorID2    = 16;  // Can ID Kraken - 5 -> 6
        public final static int armMotorID2        = 17;  // Can ID Redline - 5
        public final static int armMotorID         = 18;  // Can ID Redline - 5  // has abs encoder
        public final static int intakeMotorID      = 19;  // Can ID Redline - 5
        public final static int shooterMotorID     = 20;  // Can ID Falcon 500 - 5 -> 6
        public final static int shooterMotorID2    = 21;  // Can ID Falcon 500 - 5 -> 6

        // Can ID Max: theoretical 40, possible 27 [https://www.chiefdelphi.com/t/multiple-can-busses-without-canivore/403994]
    }

    public final class Gyros {
        public final static int Pigeon2ID          = 13;  // Can ID Pigeon 2   - 6
    }

    public final class Encoders {
        // Swerve Angles
        public final static int FLcanCoderID       =  9;  // Cancorder CAN ID - 6
        public final static int FRcanCoderID       = 10;  // Cancorder CAN ID - 6
        public final static int BLcanCoderID       = 11;  // Cancorder CAN ID - 6
        public final static int BRcanCoderID       = 12;  // Cancorder CAN ID - 6
    }

    public final class LimitSwitches {
        public final static int elevatorLowerLimitSw  =  7;  // DIO Port
        public final static int climberMidLimitSw   =  8;  // DIO Port
        public final static int climberLowerLimitSw   =  9;  // DIO Port
        
        // DIO Max Ports: 10 (w/o breakout board)
    } 

    public final class AnalogPorts {
        public final static int intakeGamepieceSensor =  3;  // ANALOG Port
        public final static int rotarySwitch          =  3;  // ANALOG Port
        
        // Analog Max Ports: 4
    }

    public final class Gamepads {
        public final static int pilotGamepadPort      = 0;  // USB PORT
        public final static int operatorGamepadPort   = 1;  // USB PORT

        // USB Max Ports: 2 (1 driver, 1 co-driver/operator)
    }

    //Check if we are FMSattached or Faking it
    public static boolean isFMSEnabled (){
        return ((DriverStation.isFMSAttached() || FAKE_FMS));
    }
}
