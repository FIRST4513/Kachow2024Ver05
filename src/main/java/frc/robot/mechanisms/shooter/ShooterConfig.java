package frc.robot.mechanisms.shooter;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.signals.NeutralModeValue;

public class ShooterConfig {
    // retract/eject speeds
    protected static final double AMP_BOTTOM = 0.5;
    protected static final double AMP_TOP = 0.5;

    protected static final double SPEAKER_BOTTOM = 1.0; //.425
    protected static final double SPEAKER_TOP = 1.0; //.45
    
    protected static final double RETRACT_BOTTOM = -1.0;
    protected static final double RETRACT_TOP = 1.0;

    /* Inverts */
    protected static final boolean bottomMotorInvert = false;
    protected static final boolean topMotorInvert = true;

    // TEST PID VALUES !!!!!!
    /* PID Values */
    private static final double kP = 1.0;   // (P)roportional value
    private static final double kI = 0.0;   // (I)ntegral Value
    private static final double kD = 0.0;   // (D)erivative Value
    private static final double kV = 0.12;  // Volts/100 (?)
    private static final double kS = 0.05;  // (S)tiction Value: 

    /* Current Limiting */
    private static final boolean enableCurrentLimitting = true;
    private static final double suppCurrent = 38.8;      // Max Amps allowed in Supply
    private static final double suppTimeThresh = 0.1;  // How long to allow unlimited Supply (s)

    /* Neutral Mode */
    private static final NeutralModeValue neutralMode = NeutralModeValue.Brake;

    // --------------- Constuctor Setting Up Motor Config values -------------
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

        // Configure Current Limits
        CurrentLimitsConfigs currentLimits = config.CurrentLimits;
        currentLimits.SupplyCurrentLimitEnable = enableCurrentLimitting;
        currentLimits.SupplyCurrentLimit = suppCurrent;
        currentLimits.SupplyTimeThreshold = suppTimeThresh;

        // Configure Neutral Mode
        config.MotorOutput.NeutralMode = neutralMode;

        // Finally return an object that will represent the configs we would like to 
        return config;
    }
}
