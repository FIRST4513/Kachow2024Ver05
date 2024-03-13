package frc.robot.mechanisms.intake;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;

public class IntakeConfig {
    // IR Prox distance value for detection of a gamepiece
    protected static final double gamepieceDetectDistance = 1;

    // retract/eject speeds
    protected static final double GROUND = 1.0;
    protected static final double AMP = 0.4;  // TODO: Remove for week 3 code
    protected static final double TRAP = 0.4;  // TODO: Remove for week 3 code
    protected static final double FEED = 1.2;

    /* Neutral Modes */
    protected static final NeutralMode intakeNeutralMode = NeutralMode.Brake;

    /* Inverts */
    protected static final boolean intakeMotorInvert = false;

    // increase to reduce jitter
    protected static final int intakeAllowableError = 0;

    /* Intake Motor Current Limiting */
    protected static final int     intakeContinuousCurrentLimit = 30;
    protected static final int     intakePeakCurrentLimit       = 30;
    protected static final int     intakePeakCurrentDuration    = 100;
    protected static final boolean intakeEnableCurrentLimit     = true;

    /* Ramp Rate */
    protected static final double openLoopRamp = 0;
    protected static final double closedLoopRamp = 0;

    // --------------- Constuctor Setting Up Motor Config values -------------
    protected static TalonSRXConfiguration getConfig() {
        /* Intake Motor Configurations */
        TalonSRXConfiguration intakeSRXConfig = new TalonSRXConfiguration();

        intakeSRXConfig.slot0.kP = 0;
        intakeSRXConfig.slot0.kI = 0;
        intakeSRXConfig.slot0.kD = 0;
        intakeSRXConfig.slot0.kF = 0;
        intakeSRXConfig.slot0.allowableClosedloopError = intakeAllowableError;
        intakeSRXConfig.openloopRamp                   = openLoopRamp;
        intakeSRXConfig.closedloopRamp                 = closedLoopRamp;
        intakeSRXConfig.continuousCurrentLimit         = intakeContinuousCurrentLimit;
        intakeSRXConfig.peakCurrentLimit               = intakePeakCurrentLimit;         
        intakeSRXConfig.peakCurrentDuration            = intakePeakCurrentDuration;

        return intakeSRXConfig;
    }
}
