package frc.robot.auto;

import com.pathplanner.lib.util.HolonomicPathFollowerConfig;
import com.pathplanner.lib.util.PIDConstants;
import com.pathplanner.lib.util.ReplanningConfig;

import frc.robot.drivetrain.config.DrivetrainConfig;

public class AutoConfig {

    // Auto menu Position selectors
    public static final String kSpkrLeftSelect          = "Spkr Left";
    public static final String kSpkrRightSelect         = "Spkr Right";
    public static final String kSpkrCtrSelect           = "Spkr Ctr";

    // Auto menu Action selectors
    public static final String kActionDoNothing         = "Do Nothing";
    public static final String kCrossOnlySelect         = "Cross Only";
    public static final String kActionOneNoteOnly       = "One Note Only";       // Shoot Only
    public static final String kSick360                 = "Sick 360"; //PLEASE DO NOT USE
    public static final String kLoganConePath           = "Logan Cone Path"; //READ ABOVE
    public static final String kOneNoteCrossOnlySelect  = "One Note and Cross line Only";
    public static final String kActionTwoNote           = "Two Note";       // Shoot and get second and Shoot

    // variables for tuning
    public static final double translationkP = 5.0;  // PID value (P)roportional
    public static final double translationkI = 0.0;  // PID value (I)ntegral
    public static final double translationkD = 0.0;  // PID value (D)erivative

    public static final double rotationkP = 3.0;  // PID value (P)roportional
    public static final double rotationkI = 0.0;  // PID value (I)ntegral
    public static final double rotationkD = 0.0;  // PID value (D)erivative

    /* Swerve Conroller Constants */
    public static final double kMaxSpeed = 2.7;     // note: doesn't seem to do much
    public static final double kMaxAccel = 2.4;     // 2 worked but took too long
    public static final double kGenPathMaxSpeed = 3.0;
    public static final double kGenPathMaxAceel = 3.0;
    public static double driveBaseRadius = Math.hypot(
                                                (DrivetrainConfig.trackWidth / 2.0),
                                                (DrivetrainConfig.wheelBase/2.0));

    public static HolonomicPathFollowerConfig AutoPathFollowerConfig =
        new HolonomicPathFollowerConfig(   // HolonomicPathFollowerConfig, for configuring path commands
            new PIDConstants(translationkP, translationkI, translationkD),  // Translation PID constants
            new PIDConstants(rotationkP, rotationkI, rotationkD),           // Rotation PID constants
            kMaxSpeed,                // Max module speed, in m/s
            driveBaseRadius,               // Drive base radius in meters. Distance from robot center to furthest module.
            new ReplanningConfig()         // Default path replanning config. See the API for the options here <https://pathplanner.dev/api/java/com/pathplanner/lib/util/ReplanningConfig.html>
        );
}
