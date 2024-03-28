package frc.lib.MotorConfigurations;

import com.ctre.phoenix6.configs.ClosedLoopGeneralConfigs;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.FeedbackConfigs;
import com.ctre.phoenix6.configs.MotionMagicConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.signals.FeedbackSensorSourceValue;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

// 18 references from with-methods
public class motorFXConfig {
    /* Can Bus Options */
    public int canID = 0;
    public String canBus = "rio";  // otherwise use custom for CANivore

    /* At Speed Tolerance */
    public double atSpeedTolerance = 0.05;

    /* PID Values */
    public double kP = 0;
    public double kI = 0;
    public double kD = 0;
    public double kV = 0;
    public double kS = 0;
    public int allowableError = 0;

    /* Motion Magic Values */
    private double mmCruiseVelocity = 10;
    private double mmAcceleration   = 10;
    private double mmJerk           = 10;

    /* Feedback */
    public int remoteSensorID = 0;
    public FeedbackSensorSourceValue sensorSource = FeedbackSensorSourceValue.RotorSensor;
    public double rotorToSensorRatio = 1.0;

    /* Closed Loop General Configs */
    public boolean continuousWrap = true;

    /* Neutral Mode */
    public NeutralModeValue neutralMode = NeutralModeValue.Brake;

    /* Invert */
    public InvertedValue motorInvert = InvertedValue.CounterClockwise_Positive;

    /* Motor Current Limiting */
    private boolean enableSuppCurrLimit = true;
    private double  suppCurrent = 40;      // Max Amps allowed in Supply
    private double  suppTimeThresh = 0.1;  // How long to allow unlimited Supply (s)
    private boolean enableStatCurrLimit = true;
    private double  statCurrent = 30;

    /* ----- With-methods for easier setup and chaining ----- */
    public motorFXConfig withCanID(int newCanID) {
        this.canID = newCanID;
        return this;
    }

    public motorFXConfig withSpeedTolerance(int newTolerance) {
        this.atSpeedTolerance = newTolerance;
        return this;
    }

    public motorFXConfig withkP(double newkP) {
        this.kP = newkP;
        return this;
    }

    public motorFXConfig withkI(double newkI) {
        this.kI = newkI;
        return this;
    }

    public motorFXConfig withkD(double newkD) {
        this.kD = newkD;
        return this;
    }

    public motorFXConfig withkV(double newkV) {
        this.kV = newkV;
        return this;
    }

    public motorFXConfig withkS(double newkS) {
        this.kS = newkS;
        return this;
    }

    public motorFXConfig withCruiseVelocity(double newCruise) {
        this.mmCruiseVelocity = newCruise;
        return this;
    }

    public motorFXConfig withAcceleration(double newAcceleration) {
        this.mmCruiseVelocity = newAcceleration;
        return this;
    }

    public motorFXConfig withJerk(double newJerk) {
        this.mmCruiseVelocity = newJerk;
        return this;
    }

    public motorFXConfig withNeutralMode(NeutralModeValue newMode) {
        this.neutralMode = newMode;
        return this;
    }

    public motorFXConfig withInvert(InvertedValue newInvert) {
        this.motorInvert = newInvert;
        return this;
    }

    public motorFXConfig withAllowableError(int newAllowableError) {
        this.allowableError = newAllowableError;
        return this;
    }

    public motorFXConfig withSuppCurrEnabled(boolean newEnabled) {
        this.enableSuppCurrLimit = newEnabled;
        return this;
    }

    public motorFXConfig withSuppCurrent(double newCurrent) {
        this.suppCurrent = newCurrent;
        return this;
    }

    public motorFXConfig withSuppTimeThreshold(double newSeconds) {
        this.suppTimeThresh = newSeconds;
        return this;
    }

    public motorFXConfig withStatCurrLimitEnabled(boolean newEnabled) {
        this.enableStatCurrLimit = newEnabled;
        return this;
    }

    public motorFXConfig withStatCurrent(double newCurrent) {
        this.statCurrent = newCurrent;
        return this;
    }

    // --------------- Constuctor Setting Up Motor Config values -------------
    public TalonFXConfiguration getConfig() {
        // Initialize config object
        TalonFXConfiguration config = new TalonFXConfiguration();
        
        // Configure PID Slot0 Values (PIDVS)
        Slot0Configs slot0Configs = config.Slot0;
        slot0Configs.kP = kP;
        slot0Configs.kI = kI;
        slot0Configs.kD = kD;
        slot0Configs.kV = kV;
        slot0Configs.kS = kS;

        // Configure Motion Magic Values (Cruise, Accel, Jerk)
        MotionMagicConfigs mmConfigs = config.MotionMagic;
        mmConfigs.MotionMagicCruiseVelocity = mmCruiseVelocity;
        mmConfigs.MotionMagicAcceleration = mmAcceleration;
        mmConfigs.MotionMagicJerk = mmJerk;

        // Configure Motor Output Values (Inverted & Neutral Mode)
        MotorOutputConfigs motorOutput = config.MotorOutput;
        motorOutput.NeutralMode = neutralMode;
        motorOutput.Inverted = motorInvert;

        // Configure Feedback Values (gear ratios to output and CAN Coder)
        FeedbackConfigs feedback = config.Feedback;
        feedback.FeedbackRemoteSensorID = remoteSensorID;
        feedback.FeedbackSensorSource = sensorSource;
        feedback.RotorToSensorRatio = rotorToSensorRatio;

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
