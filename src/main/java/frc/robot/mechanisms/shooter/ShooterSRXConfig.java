package frc.robot.mechanisms.shooter;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;

public class ShooterSRXConfig {
    // Motor control sets
    protected static final NeutralMode neutralMode = NeutralMode.Brake;
    protected static final boolean motorInvert = false;

    // Motion Magic Control Loop Constants
    private static final double motionCruiseVelocity = 2500;    //3500; // Used 2/27/24
    private static final double motionAcceleration = 4000;
    private static final int allowableError = 10;

    // Current Limiting
    private static final int continuousCurrentLimit = 30;
    private static final int peakCurrentLimit = 40;
    private static final int peakCurrentDuration = 200;
    
    // Voltage Compensation
    private static final double voltageCompSaturation = 12;

    // Ramp Rate
    private static final double openLoopRamp = 0;
    private static final double closedLoopRamp = 0;

    // ---------- CONSTRUCTOR ----------
    public static TalonSRXConfiguration getConfig() {
        TalonSRXConfiguration config = new TalonSRXConfiguration();

        config.motionCruiseVelocity             = motionCruiseVelocity;
        config.motionAcceleration               = motionAcceleration;
        
        config.openloopRamp                     = openLoopRamp;
        config.closedloopRamp                   = closedLoopRamp;
        config.voltageCompSaturation            = voltageCompSaturation;

        config.continuousCurrentLimit           = continuousCurrentLimit; 
        config.peakCurrentLimit                 = peakCurrentLimit;         
        config.peakCurrentDuration              = peakCurrentDuration;
        config.slot0.allowableClosedloopError   = allowableError;

        config.forwardSoftLimitEnable = false;
        config.reverseSoftLimitEnable = false;

        return config;
    }
}
