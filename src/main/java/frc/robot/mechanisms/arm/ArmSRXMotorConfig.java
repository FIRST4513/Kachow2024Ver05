package frc.robot.mechanisms.arm;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;

public class ArmSRXMotorConfig {
    // TalonSRX config object
    public static TalonSRXConfiguration config = new TalonSRXConfiguration();

    // Motor control sets
    public static final NeutralMode armDefaultNeutralMode = NeutralMode.Brake;
    public static final boolean armMotorInvert = ArmConfig.armMotorsInvert;
    public static final boolean armEncoderInvert = false;

    // Motor Limits (Degree Angles)
    public static final int armExtendSoftLimitThreshold = ArmConfig.armExtendLimit;
    public static final int armRetractSoftLimitTrheshold = ArmConfig.armRetractLimit;

    // Motion Magic Control Loop Constants
    public static final double feedForwardScaler = 0.0;
    public final double motionCruiseVelocity = 2500;    //3500; // Used 2/27/24
    public final double motionAcceleration = 4000;
    public static final int allowableError = 10;
    public final int motionCurveStrength = 1;

    // PID Loop Constants
    public final double kP = 2.0;  // was 3.5 10-28-2023
    public final double kI = 0.0;
    public final double kD = 0.0;
    public final double kF = 0.0;
    public final double kIz = 0.0;

    // Current Limiting
    public static final int continuousCurrentLimit = 30;
    public static final int peakCurrentLimit = 40;
    public static final int peakCurrentDuration = 200;
    
    // Voltage Compensation
    public final double voltageCompSaturation = 12;

    // Ramp Rate
    public final double openLoopRamp = 0;
    public final double closedLoopRamp = 0;

    // Motor Characterization Value
    public final double kS = 0;
    public final double kV = 0;
    public final double kA = 0;

    // ---------- CONSTRUCTOR ----------
    public ArmSRXMotorConfig() {
        config.slot0.kP = kP;
        config.slot0.kI = kI;
        config.slot0.kD = kD;
        config.slot0.kF = kF;
        config.slot0.integralZone               = kIz;
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
        // config.forwardSoftLimitThreshold = Robot.arm.encAngleToCounts(armConfig.armExtendLimit);
        // config.forwardSoftLimitThreshold = 3200;

        config.reverseSoftLimitEnable = false;
        // config.reverseSoftLimitThreshold = 3600;
    }
}
