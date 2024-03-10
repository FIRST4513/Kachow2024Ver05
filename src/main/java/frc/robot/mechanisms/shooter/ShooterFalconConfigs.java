package frc.robot.mechanisms.shooter;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.signals.NeutralModeValue;

public class ShooterFalconConfigs {
    /* Inverts */
    protected static final boolean bottomMotorInvert = true;
    protected static final boolean topMotorInvert = true;
    
    /* PID Values */
    private static final double kP = 0.65;   // (P)roportional value
    private static final double kI = 0.4;   // (I)ntegral Value
    private static final double kD = 0.0;   // (D)erivative Value
    private static final double kV = 0.15;  // Kind of like a Feed Forward
    private static final double kS = 0.01;  // (S)tiction Value:

    /* Current Limiting */
    private static final boolean enableCurrentLimitting = true;
    private static final double suppCurrent = 38.8;      // Max Amps allowed in Supply
    private static final double suppTimeThresh = 0.1;  // How long to allow unlimited Supply (s)

    /* Neutral Mode */
    private static final NeutralModeValue neutralMode = NeutralModeValue.Coast;

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
