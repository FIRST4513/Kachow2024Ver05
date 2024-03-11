package frc.robot.mechanisms.pivot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Robot;
import frc.robot.RobotConfig.Motors;
import frc.robot.mechanisms.pivot.PivotConfig;

public class PivotSubSys  extends SubsystemBase  {
    
    public enum PivotState {
        TO_TARGET,  // like automatic
        MANUAL,
        STOPPED
    }
    private PivotState pivotState = PivotState.MANUAL;

    protected TalonSRX pivotMotor = new TalonSRX(Motors.pivotMotorID);
    
    // Current Position Data
    public double currentEncAbsolutePos = 0.0;
    public double currentEncRawCount = 0.0;
    public double currentPivotAngle = 0.0;
    public double currentShooterAngle = 0.0;
    public double currentPivotPower = 0.0;
    private double pivotTargetAngle = 0;
    private double shooterTargetAngle = 0;

    /* ----- Periodic ----- */
    @Override
    public void periodic() {
        updateCurrentPositions(); 
        
        switch (pivotState) {
            case TO_TARGET: setPivotToAngle(pivotTargetAngle);

            case MANUAL: setPivotManualLimits(Robot.operatorGamepad.getPivotAdjust());
            case STOPPED: stopMotors();
            default: setPivotByPWM(0);
        }
    }

    /* ----- State Setters ----- */
    public void setNewPivotState(PivotState newState)       { pivotState = newState; }
    public void setStopState()                              { pivotState = PivotState.STOPPED; }


    // ------------------------------------------------------
    // ---------------- Pivot Motor Methods -----------------
    // ------------------------------------------------------

    public void stopMotors() {
        pivotMotor.set(ControlMode.PercentOutput, 0);
        setNewPivotState(PivotState.STOPPED);
        //pivotTargetAngle = 0.0;     // clear any previous target
        //shooterTargetAngle = 0.0;        
    }

    private void setPivotByPWM(double power) {
        pivotMotor.set(ControlMode.PercentOutput, power);
        //pivotTargetAngle = 0.0;     // clear any previous target
        //shooterTargetAngle = 0.0;
    }

    private void setPivotManualLimits(double power) {
        // positive rotation
        if ((getEncoderAbsolutePosition() < PivotConfig.PIVOT_MAX_ADJUSTED_ENC) && 
            (getEncoderAbsolutePosition() > PivotConfig.PIVOT_MIN_ADJUSTED_ENC)) {
                    setPivotByPWM(power);
            } else {
                    setPivotByPWM(0);
            }
        
    }

    private void setPivotToAngle(double tgtAngle) {
        if ( tgtAngle > PivotConfig.PIVOT_MAX_ANGLE) tgtAngle = PivotConfig.PIVOT_MAX_ANGLE;
        if ( tgtAngle < PivotConfig.PIVOT_MIN_ANGLE) tgtAngle = PivotConfig.PIVOT_MIN_ANGLE;
        pivotTargetAngle = tgtAngle;

        double difference = currentPivotAngle - tgtAngle;

        // if within threshold, stop pivot
        if (Math.abs(difference) < PivotConfig.PIVOT_ANGLE_TOLDERANCE) {
            stopMotors();
            return;
        }

        if (difference > 180)  {
            // GO Counter Clockwise This is a shorter route we can go. Invert direction and recalculate
            setPivotByPWM(PivotConfig.PIVOT_CCW_SPEED);
            return;
        }
        if (difference < -180)  {
            // GO Counter Clockwise This is a shorter route we can go. Invert direction and recalculate
            setPivotByPWM(PivotConfig.PIVOT_CW_SPEED);
            return;
        }
        if (difference > 0)  {
            // GO Counter Clockwise This is a shorter route we can go. Invert direction and recalculate
            setPivotByPWM(PivotConfig.PIVOT_CW_SPEED);
            return;
        }
        if (difference < 0)  {
            // GO Counter Clockwise This is a shorter route we can go. Invert direction and recalculate
            setPivotByPWM(PivotConfig.PIVOT_CCW_SPEED);
            return;
        }
    }



    public void setNewEncoderAngle(double angle) {
        if ( angle > PivotConfig.PIVOT_MAX_ANGLE) angle = PivotConfig.PIVOT_MAX_ANGLE;
        if ( angle < PivotConfig.PIVOT_MIN_ANGLE) angle = PivotConfig.PIVOT_MIN_ANGLE;
        pivotTargetAngle = angle;
        shooterTargetAngle = shooterAngleFromEncoder(pivotTargetAngle);
    }

    public void setNewShooterAngle(double angle) {
        if ( angle > PivotConfig.SHOOTER_MAX_ANGLE) angle = PivotConfig.SHOOTER_MAX_ANGLE;
        if ( angle < PivotConfig.SHOOTER_MIN_ANGLE) angle = PivotConfig.SHOOTER_MIN_ANGLE;
        shooterTargetAngle = angle;
        pivotTargetAngle = encoderAngleFromShooterAngle(shooterTargetAngle);
    }

    private void setShooterToAngle( double angle) {
        // Angle 0 degrees to 17 Degrees
        if ( angle > PivotConfig.SHOOTER_MAX_ANGLE) angle = PivotConfig.SHOOTER_MAX_ANGLE;
        if ( angle < PivotConfig.SHOOTER_MIN_ANGLE) angle = PivotConfig.SHOOTER_MIN_ANGLE;
        shooterTargetAngle = angle;
        setPivotToAngle( encoderAngleFromShooterAngle(angle) );
    }

    // -------------- Pivot Angle Methods ----------------
    
    public boolean isAtTarget() {
        return ( checkInRange(currentPivotAngle, pivotTargetAngle, PivotConfig.PIVOT_ANGLE_TOLDERANCE));
    }

    public boolean checkInRange(double value, double target, double tolerence) {
        double diff = value - target;
        if (Math.abs(diff) < tolerence) {
            return true;
        }
        return false;
    }


    // --------------- Conversion Methods ----------------
    public double encAbsoluteCountsToAngle(double counts) {
        return (counts  * PivotConfig.PIVOT_ENC_TO_DEG);
    }

    public double encAngleToCounts(double angle) {
        return (angle * PivotConfig.PIVOT_DEG_TO_ENC) + PivotConfig.PIVOT_ENC_OFFSET;
    }

    public double encCountsToAbsolutePos( double counts) {
        return (counts - PivotConfig.PIVOT_ENC_OFFSET) % 4096.0;
    }

    public double shooterAngleFromEncoder ( double angle ){
        // Encoder Angle 0-110 degrees Shooter Angle 0-17 degrees
        return angle * PivotConfig.PIVOT_ANGLE_TO_SHOOTER_ANGLE;
    }

    public double encoderAngleFromShooterAngle ( double angle ){
        // Encoder Angle 0-110 degrees Shooter Angle 0-17 degrees
        return angle * PivotConfig.SHOOTER_ANGLE_TO_PIVOT_ANGLE;
    }


    // -------------- Update Current Position Data ------------------
    public void updateCurrentPositions() {
        currentPivotPower = pivotMotor.getMotorOutputPercent();

        currentEncRawCount =    pivotMotor.getSelectedSensorPosition() * PivotConfig.PIVOT_ENC_INVERT; // Inverts if necc.
        currentEncAbsolutePos = encCountsToAbsolutePos( currentEncRawCount );
        currentPivotAngle =     encCountsToAbsolutePos( currentEncAbsolutePos );
        currentShooterAngle =   encoderAngleFromShooterAngle( currentEncAbsolutePos);
    }

    /* ----- Getters For Telemetry ----- */
    public double getEncoderRawPosition()           { return currentEncRawCount; }
    public double getEncoderAbsolutePosition()      { return currentEncAbsolutePos; }
    public double getEncoderAngle()                 { return currentPivotAngle; }
    public double getShooterAngle()                 { return currentShooterAngle; }
    public double getTargetAngle()                  { return pivotTargetAngle; }

    public double getPivotPower()                   { return currentPivotPower; }

    public String getPivotStateString() {
        switch (pivotState) {
            case MANUAL: return "MANUAL";
            default:     return "DEFAULT";
        }            
    }

    // ----------------------------------------------------------------------
    // --------------------- Configure Pivot Motor --------------------------
    // ----------------------------------------------------------------------
    public void configureMotors(){
        // Pivot Motor, Window driven by Talon SRX (Phoenix 5)
        pivotMotor.configFactoryDefault();
        pivotMotor.configAllSettings(PivotSRXConfig.getConfig());
        pivotMotor.setInverted(PivotSRXConfig.motorInvert);
        pivotMotor.setNeutralMode(PivotSRXConfig.neutralMode);
    }
}

