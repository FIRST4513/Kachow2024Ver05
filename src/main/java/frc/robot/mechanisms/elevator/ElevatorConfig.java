package frc.robot.mechanisms.elevator;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MotionMagicConfigs;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

public class ElevatorConfig {
    /* conversions from rotations <-> climber height in INCHES */
    protected static final double rotationsToHeight = 1.0;
    protected static final double heighToRotations = 1 / rotationsToHeight;

    /* constants for height definitions */
    protected static final double upperLimitRot = 24;
    protected static final double lowerLimitRot = 0.0;
    
    public static final double trapArmAbleRot = 20;
    public static final double posTopRot = 24;
    public static final double posTrapRot = 23.8;
    public static final double posMidRot = 20; //7.27
    public static final double posGroundRot = 1.0;

    public static enum ELEVPOS { BOTTOM, TOP, TRAP, MID, GROUNDPU, UNKOWN; }


    public static final double tolerence = 0.5; // rotations

    /* constant for invert */
    protected static final InvertedValue inverted = InvertedValue.Clockwise_Positive;

    /* configuration constants */
    // could be pushed a little harder, but not until mechanical is fixed
    private static final double mmCruiseVelocity = 35.0; // 50;   // 5 rps cruise
    private static final double mmAcceleration   = 75;  // ~0.5 seconds to max vel.
    private static final double mmJerk           = 225;  // ~0.2 seconds to max accel.

    private static final double kP = 8.0;   // (P)roportional value
    private static final double kI = 0.0;   // (I)ntegral Value
    private static final double kD = 0.0;   // (D)erivative Value
    private static final double kV = 0.12;  // Volts/100 (?)
    private static final double kS = 0.05;  // (S)tiction Value:

    private static final boolean enableCurrentLimitting = true;
    private static final double  suppCurrent = 40;      // Max Amps allowed in Supply
    private static final double  suppTimeThresh = 0.1;  // How long to allow unlimited Supply (s)

    private static final NeutralModeValue neutralMode = NeutralModeValue.Brake;

    // --------------- Constuctor Setting Up Motor Config values -------------
    protected static TalonFXConfiguration getConfig() {
        /* Declare Configuration Object */
        TalonFXConfiguration config = new TalonFXConfiguration();


        // Configure Motion Magic Values
        MotionMagicConfigs mm = config.MotionMagic;
        mm.MotionMagicCruiseVelocity = mmCruiseVelocity;
        mm.MotionMagicAcceleration = mmAcceleration;
        mm.MotionMagicJerk = mmJerk;

        // Configure PID Slot0 Values (PIDVS)
        Slot0Configs slot0Configs = config.Slot0;
        slot0Configs.kP = kP;
        slot0Configs.kI = kI;
        slot0Configs.kD = kD;
        slot0Configs.kV = kV;
        slot0Configs.kS = kS;

        // Configure Current Limits
        CurrentLimitsConfigs currentLimits = config.CurrentLimits;
        currentLimits.SupplyCurrentLimitEnable = enableCurrentLimitting;
        currentLimits.SupplyCurrentLimit = suppCurrent;
        currentLimits.SupplyTimeThreshold = suppTimeThresh;

        // Configure neutral mode
        config.MotorOutput.NeutralMode = neutralMode;
        config.MotorOutput.Inverted = inverted;

        // Config Soft Limits
        config.SoftwareLimitSwitch.ForwardSoftLimitEnable = true;
        config.SoftwareLimitSwitch.ForwardSoftLimitThreshold = upperLimitRot;

        // finally return an object that will represent the configs we would like to 
        return config;
    }
}
