package frc.robot.drivetrain.config;

import com.ctre.phoenix6.configs.ClosedLoopGeneralConfigs;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.FeedbackConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.signals.FeedbackSensorSourceValue;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

public class AngleFalconConfig {
    // kP need to increase a LOT??? (Other teams have well over 50)
    // private static final double kP = 1.25;   // (P)roportional value
    private static final double kP = 50.0;   // (P)roportional value
    private static final double kI = 0.0;   // (I)ntegral Value
    private static final double kD = 0.0;   // (D)erivative Value
    private static final double kV = 0.0;  // Volts/100 (?)
    private static final double kS = 0.0;  // (S)tiction Value: 

    // public static final int angleContinuousCurrentLimit = 20;
    private static final boolean enableSuppCurrLimit = true;
    private static final double suppCurrent = 40;      // Max Amps allowed in Supply
    private static final double suppTimeThresh = 0.1;  // How long to allow unlimited Supply (s)
    private static final boolean enableStatCurrLimit = true;
    private static final double statCurrent = 30;
    
    private static final NeutralModeValue neutralMode = NeutralModeValue.Brake;

    /**
     * The number to multiply by to convert from <i>wheel revolutions</i> to <i>falcon revolutions</i>
     * <p>
     * Once called <i>`MK4i_L2_angleGearRatio`</i>
     */
    public static final double angleGearRatio = (150.0 / 7.0);

    /**
     * Get a configuration object configured for a Talon FX turning a Swerve module
     * 
     * @param modID the integer ID num of the module requesting the config object
     * @return A TalonFXConfiguration object
     */
    public static TalonFXConfiguration getConfig(int modID) {
        // Initialize config object
        TalonFXConfiguration config = new TalonFXConfiguration();
        
        // Configure PID Slot0 Values (PIDVS)
        Slot0Configs slot0Configs = config.Slot0;
        slot0Configs.kP = kP;
        slot0Configs.kI = kI;
        slot0Configs.kD = kD;
        slot0Configs.kV = kV;
        slot0Configs.kS = kS;

        // Configure Motor Output Values (Inverted, Neutral Mode)
        MotorOutputConfigs motorOutput = config.MotorOutput;
        motorOutput.Inverted = DrivetrainConfig.getModAngleInvert(modID);
        motorOutput.NeutralMode = neutralMode;

        // Configure Feedback Values (gear ratios to output and CAN Coder)
        FeedbackConfigs feedback = config.Feedback;
        feedback.FeedbackRemoteSensorID = DrivetrainConfig.getModCanCoderID(modID);
        feedback.FeedbackSensorSource = FeedbackSensorSourceValue.RemoteCANcoder;
        feedback.RotorToSensorRatio = angleGearRatio;

        // Closed Loop General Configs
        ClosedLoopGeneralConfigs closedLoopConfigs = config.ClosedLoopGeneral;
        closedLoopConfigs.ContinuousWrap = true;

        // Configure Current Limits
        CurrentLimitsConfigs currentLimits = config.CurrentLimits;
        currentLimits.SupplyCurrentLimitEnable = enableSuppCurrLimit;
        currentLimits.SupplyCurrentLimit = suppCurrent;
        currentLimits.SupplyTimeThreshold = suppTimeThresh;
        currentLimits.StatorCurrentLimitEnable = enableStatCurrLimit;
        currentLimits.StatorCurrentLimit = statCurrent;

        // finally return an object that will represent the configs we would like to 
        return config;
    }
}
