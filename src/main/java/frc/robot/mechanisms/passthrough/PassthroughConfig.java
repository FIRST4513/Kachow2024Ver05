package frc.robot.mechanisms.passthrough;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

public class PassthroughConfig {
    // Gamepiece Detect Distance Min
    protected static final double gamepieceDetectDistance = 2.5;

    /* intake/eject speeds (pwm) */
    protected static final double GROUND_INTAKE = -0.1;
    protected static final double HP_INTAKE = 0.2;
    protected static final double SPEAKER_EJECT = -1;

    /* Inverts */
    protected static final InvertedValue motorInvert = InvertedValue.CounterClockwise_Positive;

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

        // Configure Current Limits
        CurrentLimitsConfigs currentLimits = config.CurrentLimits;
        currentLimits.SupplyCurrentLimitEnable = enableCurrentLimitting;
        currentLimits.SupplyCurrentLimit = suppCurrent;
        currentLimits.SupplyTimeThreshold = suppTimeThresh;

        // Configure Neutral Mode and Invert
        config.MotorOutput.Inverted = motorInvert;
        config.MotorOutput.NeutralMode = neutralMode;

        // Finally return an object that will represent the configs we would like to 
        return config;
    }
}
