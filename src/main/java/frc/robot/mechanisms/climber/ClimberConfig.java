package frc.robot.mechanisms.climber;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MotionMagicConfigs;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import frc.robot.RobotConfig;

public class ClimberConfig {
    /* constants for height definitions */
    public static final double MAX_ROTATIONS = 55;

    public static final double posTop       = 50;
    public static final double posOnChain   = 28;
    public static final double posMidClimb  = 11.37;
    public static final double posFullClimb = 0;

    /* configuration constants */
    private static final double mmCruiseVelocity = 50;  // 5 rpm cruise
    private static final double mmAcceleration   = 30;  // ~0.5 seconds to max vel.
    private static final double mmJerk           = 75;  // ~0.2 seconds to max accel.

    private static final double nonload_kP = 1.0;   // (P)roportional value
    private static final double nonload_kI = 0.0;   // (I)ntegral Value
    private static final double nonload_kD = 0.0;   // (D)erivative Value
    private static final double nonload_kV = 0.12;  // Volts/100 (?)
    private static final double nonload_kS = 0.05;  // (S)tiction Value:

    private static final boolean enableCurrentLimitting = true;
    private static final double  suppCurrent = 40;      // Max Amps allowed in Supply
    private static final double  suppTimeThresh = 0.1;  // How long to allow unlimited Supply (s)

    protected static final InvertedValue leftMotorInvert  = InvertedValue.CounterClockwise_Positive;  // when running at negative power: should be clockwise
    protected static final InvertedValue rightMotorInvert = InvertedValue.Clockwise_Positive;  // when running at negative power: should be counterclockwise

    private static final NeutralModeValue neutralMode = NeutralModeValue.Brake;

    // --------------- Constuctor Setting Up Motor Config values -------------
    protected static TalonFXConfiguration getConfig(int motorCanID) {
        /* Declare Configuration Object */
        TalonFXConfiguration config = new TalonFXConfiguration();

        // Configure Motion Magic Values
        MotionMagicConfigs mm = config.MotionMagic;
        mm.MotionMagicCruiseVelocity = mmCruiseVelocity;
        mm.MotionMagicAcceleration = mmAcceleration;
        mm.MotionMagicJerk = mmJerk;

        // Configure PID Slot0 Values (PIDVS)
        Slot0Configs slot0Configs = config.Slot0;
        slot0Configs.kP = nonload_kP;
        slot0Configs.kI = nonload_kI;
        slot0Configs.kD = nonload_kD;
        slot0Configs.kV = nonload_kV;
        slot0Configs.kS = nonload_kS;

        // Configure Current Limits
        CurrentLimitsConfigs currentLimits = config.CurrentLimits;
        currentLimits.SupplyCurrentLimitEnable = enableCurrentLimitting;
        currentLimits.SupplyCurrentLimit = suppCurrent;
        currentLimits.SupplyTimeThreshold = suppTimeThresh;

        // Configure Soft Limits
        config.SoftwareLimitSwitch.ForwardSoftLimitEnable = false;
        config.SoftwareLimitSwitch.ForwardSoftLimitThreshold = MAX_ROTATIONS;

        // Configure neutral mode
        config.MotorOutput.NeutralMode = neutralMode;

        // Configure Inverts based on which motor is being configured
        if (motorCanID == RobotConfig.Motors.climbLeftMotorID) {
            config.MotorOutput.Inverted = leftMotorInvert;
        } else if (motorCanID == RobotConfig.Motors.climbRightMotorID) {
            config.MotorOutput.Inverted = rightMotorInvert;
        } else {
            // do nothing, leave at default
        }

        // finally return an object that will represent the configs we would like to 
        return config;
    }
}
