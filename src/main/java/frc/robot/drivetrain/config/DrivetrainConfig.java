package frc.robot.drivetrain.config;

import com.ctre.phoenix6.signals.InvertedValue;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.util.Units;
import frc.robot.RobotConfig;

public class DrivetrainConfig {
    // Odometry Type
    public static final boolean CANFDBus = false;           // Do we have a CANIvore FDBus controller

    // ----- Definitions for Kinematics -----
    public static final double trackWidth         = Units.inchesToMeters(23.75);  // Between Right/Left Wheel Ctrs
    public static final double wheelBase          = Units.inchesToMeters(23.75);  // Between Front/Back Wheel Ctrs
    private static final double wheelDiameter      = Units.inchesToMeters(3.82);   // 3.82 * 1.0243  // colson
    private static final double wheelCircumference = wheelDiameter * Math.PI;      // 2πr = πd = π * 0.094m ≈ 0.295m circumference

    // locations for teach wheel location on the robot relative to center
    private static final Translation2d frontLeftLocation =
            new Translation2d(wheelBase / 2.0, trackWidth / 2.0);
    private static final Translation2d frontRightLocation =
            new Translation2d(wheelBase / 2.0, -trackWidth / 2.0);
    private static final Translation2d backLeftLocation =
            new Translation2d(-wheelBase / 2.0, trackWidth / 2.0);
    private static final Translation2d backRightLocation =
            new Translation2d(-wheelBase / 2.0, -trackWidth / 2.0);

    public static final Translation2d[] moduleLocations = {
        frontLeftLocation,
        frontRightLocation,
        backLeftLocation,
        backRightLocation
    };

    // Increase these numbers to trust your model's state estimates less.
    public static final double kPositionStdDevX = 0.1;
    public static final double kPositionStdDevY = 0.1;
    public static final double kPositionStdDevTheta = 10;

    // Increase these numbers to trust global measurements from vision less.
    public static final double kVisionStdDevX = 5;
    public static final double kVisionStdDevY = 5;
    public static final double kVisionStdDevTheta = 500;

    /*Rotation Controller*/
    public static final double kPRotationController = 0.0;
    public static final double kIRotationController = 0.0;
    public static final double kDRotationController = 0.0;

    /*Profiling Configs*/
    // Max speed our robot can sprint at
    public static final double maxVelocity = 4.79;  // Speed at 12 volts MPS
    public static final double maxAccel = maxVelocity * 1.5; // take 1/2 sec to get to max speed.
    public static final double maxAngularVelocity =
            maxVelocity / (Math.hypot(wheelBase / 2.0, trackWidth / 2.0));
    public static final double maxAngularAcceleration = Math.pow(maxAngularVelocity, 2);

    // ----- Identification Arrays

    /**
     * Array[4] of Strings representing the names of each Swerve Module
     */
    private static final String moduleNames[] = {"FL Mod 0",
                                                 "FR Mod 1",
                                                 "BL Mod 2",
                                                 "BR Mod 3"};

    /**
     * Array[4] of doubles that represent the offsets needed to be
     * applied to each corresponding Module's CAN Coder
     * <p>
     * Process for re-calibrating CAN Coders:
     * <ul>
     * <li>Set all offsets to 0 below</li>
     * <li>Deploy Code, open ShuffleBoard</li>
     * <li>Align wheels with gears facing right of robot</li>
     * <li>Change values in array below to numbers seen in Telemetry for Can ABS Raw Rotations under Swerve</li>
     * <li>Deploy, and check CAN reports near 0 rotations in telemetry</li>
     * </ul>
     */
    // Calculations from Degrees to Rotations: degrees / 360
    // Numbers are from [-0.5 to 0.5] and CCW+
        
    // Competition Bot
//     private static final double angleOffsets[] = {       0.1955,  // FL 
//                                                         -0.4553,  // FR 
//                                                         -0.1252,  // BL 
//                                                          0.1010}; // BR

    // Practice Bot
    private static final double angleOffsets[] = { -0.15646484375,   // FL // comp bot 0.1955
                                                    0.16278515625,   // FR // comp bot -0.4553
                                                    0.444,           // BL // comp bot -0.1252
                                                    0.363};          // BR // comp bot 0.1010

//     // Practice Bot
    private static final InvertedValue angleInverts[] = {
        InvertedValue.Clockwise_Positive,  // FL
        InvertedValue.Clockwise_Positive,  // FR
        InvertedValue.Clockwise_Positive,  // BL
        InvertedValue.Clockwise_Positive   // BR
    };

    // Practice Bot
    private static final InvertedValue driveInverts[] = {
        InvertedValue.CounterClockwise_Positive,  // FL
        InvertedValue.CounterClockwise_Positive,  // FR
        InvertedValue.CounterClockwise_Positive,  // BL
        InvertedValue.CounterClockwise_Positive   // BR
    };

    // Competition Bot
//     private static final InvertedValue angleInverts[] = {
//         InvertedValue.Clockwise_Positive,         // FL
//         InvertedValue.Clockwise_Positive,         // FR
//         InvertedValue.CounterClockwise_Positive,  // BL
//         InvertedValue.Clockwise_Positive          // BR
//     };

//     // Competition Bot
//     private static final InvertedValue driveInverts[] = {
//         InvertedValue.CounterClockwise_Positive,  // FL
//         InvertedValue.CounterClockwise_Positive,  // FR
//         InvertedValue.CounterClockwise_Positive,  // BL
//         InvertedValue.Clockwise_Positive          // BR
//     };

    /**
     * Array[4] of ints that represent the drive motor CAN ID for each Module
     */
    private static final int driveCanIDs[] = {RobotConfig.Motors.FLdriveMotorID,
                                              RobotConfig.Motors.FRdriveMotorID,
                                              RobotConfig.Motors.BLdriveMotorID,
                                              RobotConfig.Motors.BRdriveMotorID};

    /**
     * Array[4] of ints that represent the angle motor CAN ID for each Module
     */
    private static final int angleCanIDs[] = {RobotConfig.Motors.FLangleMotorID,
                                              RobotConfig.Motors.FRangleMotorID,
                                              RobotConfig.Motors.BLangleMotorID,
                                              RobotConfig.Motors.BRangleMotorID};

    /**
     * Array[4] of ints that represents the CAN Coder CAN ID for each Module
     */
    private static final int canCoderIDs[] = {RobotConfig.Encoders.FLcanCoderID,
                                              RobotConfig.Encoders.FRcanCoderID,
                                              RobotConfig.Encoders.BLcanCoderID,
                                              RobotConfig.Encoders.BRcanCoderID};

    // ----- Getters for info about a specific module -----
    public static String getModName(int modID)        { return moduleNames[modID]; }
    public static double getModAngleOffset(int modID) { return angleOffsets[modID]; }
    public static InvertedValue getModAngleInvert(int modID) { return angleInverts[modID]; }
    public static InvertedValue getModDriveInvert(int modID) { return driveInverts[modID]; }
    public static int getModDriveCanID(int modID)     { return driveCanIDs[modID]; }
    public static int getModAngleCanID(int modID)     { return angleCanIDs[modID]; }
    public static int getModCanCoderID(int modID)     { return canCoderIDs[modID]; }

    // ----- Getter for Kinematics -----

    /**
     * Get a Kinematics objects based on module positions away from center of robot
     * 
     * @return A SwerveDriveKinematics object based on module positions
     */
    public static SwerveDriveKinematics getKinematics() {
        return new SwerveDriveKinematics(
                frontLeftLocation,
                frontRightLocation,
                backLeftLocation,
                backRightLocation);
    }

    // ----- Misc Getters -----
    public static double getWheelCircumference() { return wheelCircumference; }
    public static double getMaxVelocity() { return maxVelocity; }
}
