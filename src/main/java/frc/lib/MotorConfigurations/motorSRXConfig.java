package frc.lib.MotorConfigurations;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;

public class motorSRXConfig {
    /* Can ID */
    public int canID = 0;

    /* At Speed Tolerance */
    public double atSpeedTolerance = 0.05;

    /* Neutral Mode */
    public NeutralMode neutralMode = NeutralMode.Brake;

    /* Invert */
    public boolean motorInvert = false;

    /* PID Values */
    public double kP = 0;
    public double kI = 0;
    public double kD = 0;
    public double kF = 0;
    public int allowableError = 0;

    /* Motor Current Limiting */
    public int     continuousCurrentLimit = 30;
    public int     peakCurrentLimit       = 30;
    public int     peakCurrentDurationMs  = 100;

    /* Ramp Rate */
    public double openLoopRamp = 0;
    public double closedLoopRamp = 0;

    /* ----- With-methods for easier setup and chaining ----- */
    public motorSRXConfig withCanID(int newCanID) {
        this.canID = newCanID;
        return this;
    }

    public motorSRXConfig withSpeedTolerance(int newTolerance) {
        this.atSpeedTolerance = newTolerance;
        return this;
    }

    public motorSRXConfig withNeutralMode(NeutralMode newMode) {
        this.neutralMode = newMode;
        return this;
    }

    public motorSRXConfig withInvert(boolean newInvert) {
        this.motorInvert = newInvert;
        return this;
    }

    public motorSRXConfig withkP(double newkP) {
        this.kP = newkP;
        return this;
    }

    public motorSRXConfig withkI(double newkI) {
        this.kI = newkI;
        return this;
    }

    public motorSRXConfig withkD(double newkD) {
        this.kD = newkD;
        return this;
    }

    public motorSRXConfig withkF(double newkF) {
        this.kF = newkF;
        return this;
    }

    public motorSRXConfig withAllowableError(int newAllowableError) {
        this.allowableError = newAllowableError;
        return this;
    }

    public motorSRXConfig withContinuousCurrentLimit(int newLimit) {
        this.continuousCurrentLimit = newLimit;
        return this;
    }

    public motorSRXConfig withPeakCurrentLimit(int newLimit) {
        this.peakCurrentLimit = newLimit;
        return this;
    }

    public motorSRXConfig withPeakCurrentDurration(int newDurationMs) {
        this.peakCurrentDurationMs = newDurationMs;
        return this;
    }

    public motorSRXConfig withOpenLoopRamp(double newRamp) {
        this.openLoopRamp = newRamp;
        return this;
    }

    public motorSRXConfig withClosedLoopRamp(double newRamp) {
        this.closedLoopRamp = newRamp;
        return this;
    }

    // --------------- Constuctor Setting Up Motor Config values -------------
    public TalonSRXConfiguration getConfig() {
        /* Intake Motor Configurations */
        TalonSRXConfiguration srxConfig = new TalonSRXConfiguration();

        srxConfig.slot0.kP = kP;
        srxConfig.slot0.kI = kI;
        srxConfig.slot0.kD = kD;
        srxConfig.slot0.kF = kF;
        srxConfig.slot0.allowableClosedloopError = allowableError;
        srxConfig.openloopRamp                   = openLoopRamp;
        srxConfig.closedloopRamp                 = closedLoopRamp;
        srxConfig.continuousCurrentLimit         = continuousCurrentLimit;
        srxConfig.peakCurrentLimit               = peakCurrentLimit;         
        srxConfig.peakCurrentDuration            = peakCurrentDurationMs;

        return srxConfig;
    }
}
