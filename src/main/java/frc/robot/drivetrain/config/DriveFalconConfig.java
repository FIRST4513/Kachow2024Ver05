package frc.robot.drivetrain.config;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

public class DriveFalconConfig {
    /* Calculate the ratio of drive motor rotation to meter on ground */
    public static final double rotationsPerWheelRotation = 6.746;  // falcon rotations per 1 wheel rotation
    public static final double metersPerWheelRotation = DrivetrainConfig.getWheelCircumference();  // meters traveled with 1 wheel rotation
    public static final double driveRotationsPerMeter = rotationsPerWheelRotation / metersPerWheelRotation;

    /* Drive Motor Characterization Values */
    private static final double kP = 0.04;   // (P)roportional value
    private static final double kI = 0.0;   // (I)ntegral Value
    private static final double kD = 0.01;  // (D)erivative Value
    // KS - Volts Stiction -     How many volts are needed to simply start moving/overcoming friction
    // KV - Volts Velocity -     How many volts it takes to achieve a constant, specified velocity
    // KA - Volts Acceleration - How many volts for a given acceleration (mps^2)

    // Scaled to (0 to 1) from (0 to 12) Volts as required for Arbitrary Feedforward
    private static final double kV = (3.0 / 12.0);    // Volts/100 (?)
    private static final double kS = (0.305 / 12.0);  // (S)tiction Value: 
    private static final double kA = (0.25 / 12.0);   // Volts (A)cceleration 
    
    private static final boolean enableSuppCurrLimit = true;
    private static final double suppCurrent = 40;      // Max Amps allowed in Supply
    private static final double suppTimeThresh = 0.1;  // How long to allow unlimited Supply (s)
    private static final boolean enableStatCurrLimit = true;
    private static final double statCurrent = 30;

    private static final InvertedValue inverted = InvertedValue.CounterClockwise_Positive;
    private static final NeutralModeValue neutralMode = NeutralModeValue.Brake;

    /**
     * Get a configuration object configured for a Talon FX driving a Swerve module
     * 
     * @return A TalonFXConfiguration object
     */
    public static TalonFXConfiguration getConfig() {
        // Initialize config object
        TalonFXConfiguration config = new TalonFXConfiguration();

        // Configure PID Slot0 Values (PIDVS)
        Slot0Configs slot0Configs = config.Slot0;
        slot0Configs.kP = kP;
        slot0Configs.kI = kI;
        slot0Configs.kD = kD;
        slot0Configs.kV = kV;
        slot0Configs.kS = kS;
        slot0Configs.kA = kA;

        // Configure Motor Output Values (Inverted, Neutral Mode)
        MotorOutputConfigs motorOutput = config.MotorOutput;
        motorOutput.Inverted = inverted;
        motorOutput.NeutralMode = neutralMode;

        // Configure Current Limits
        CurrentLimitsConfigs currentLimits = config.CurrentLimits;
        currentLimits.SupplyCurrentLimitEnable = enableSuppCurrLimit;
        currentLimits.SupplyCurrentLimit = suppCurrent;
        currentLimits.SupplyTimeThreshold = suppTimeThresh;
        currentLimits.StatorCurrentLimitEnable = enableStatCurrLimit;
        currentLimits.StatorCurrentLimit = statCurrent;

        // Finally return an object that will represent the configs we would like to 
        return config;
    }
}
