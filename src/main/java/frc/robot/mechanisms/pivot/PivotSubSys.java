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

    private double pivotTargetAngle = 0;

    protected TalonSRX pivotMotor = new TalonSRX(Motors.pivotMotorID);
    
    // Current Position Data
    public double currentEncCount = 0.0;
    public double currentArmAngle = 0.0;
    public double currentArmPower = 0.0;

    /* ----- Periodic ----- */
    @Override
    public void periodic() {
        updateCurrentPositions(); 
        
        switch (pivotState) {
            case TO_TARGET: setPivotToAngle(pivotTargetAngle);
            case MANUAL: setPivotManualLimits(Robot.operatorGamepad.getPivotAdjust());
            case STOPPED:
            default: setPivotByPWM(0);
        }
    }

    /* ----- Setters ----- */

    public void stop() {
        pivotMotor.set(ControlMode.PercentOutput, 0);
        setNewPivotState(PivotState.STOPPED);
    }

    
    public void setNewPivotState(PivotState newState) {
        pivotState = newState;
    }


    // ------------------------------------------------------
    // ---------------- Pivot Motor Methods -----------------
    // ------------------------------------------------------

    /* ----- Setters ----- */
    private void setPivotByPWM(double power) {
        pivotMotor.set(ControlMode.PercentOutput, power);
    }

    private void setPivotManualLimits(double power) {
        // positive rotation
        if ((getEncoderPosition() < PivotConfig.PIVOT_MAX_ENC) && (getEncoderPosition() > PivotConfig.PIVOT_MIN_ENC)) {
            setPivotByPWM(power);
        } else {
            setPivotByPWM(0);
        }
    }

    private void setPivotToAngle(double angle) {
        double difference = currentArmAngle - angle;

        // if within threshold, stop arm
        if (Math.abs(difference) < PivotConfig.PIVOT_ANGLE_TOLDERANCE) {
            stop();
            return;
        }

        if (difference > 0) {
            setPivotByPWM(-PivotConfig.PIVOT_MOVE_SPEED);
        } else {
            setPivotByPWM(PivotConfig.PIVOT_MOVE_SPEED);
        }
    }

    public void setNewTargetAngle(double newTargetAngle) {
        pivotTargetAngle = newTargetAngle;
    }

    /* ----- Getters ----- */

    public double getEncoderPosition()      { return pivotMotor.getSelectedSensorPosition(); }
    public double getAngle() { return currentArmAngle; }
    public boolean getAtTarget() {
        double difference = currentArmAngle - pivotTargetAngle;

        // if within threshold, stop arm
        if (Math.abs(difference) < PivotConfig.PIVOT_ANGLE_TOLDERANCE) {
            return true;
        } else {
            return false;
        }
    }
    public double getPivotPower() { return pivotMotor.getMotorOutputPercent(); }
    
    public String getPivotStateString() {
        switch (pivotState) {
            case MANUAL: return "MANUAL";
            default:     return "DEFAULT";
        }            
    }

    public void updateCurrentPositions() {
        currentEncCount = pivotMotor.getSelectedSensorPosition();
        currentEncCount = currentEncCount % 4096;
        currentArmAngle = encCountsToAngle(currentEncCount);
    }

    public boolean isAtTarget() {
        if ( checkInRange(currentArmAngle, pivotTargetAngle, PivotConfig.PIVOT_ANGLE_TOLDERANCE)) {
            return true; }
        return false;   // Otherwise
    }

    public boolean checkInRange(double value, double target, double tolerence) {
        if ((value < (target + tolerence)) &&
            (value > target - tolerence)) {
                return true;
            }
        return false;
    }

    public double encCountsToAngle(double counts) {
        return (counts - PivotConfig.PIVOT_ENC_OFFSET) * PivotConfig.PIVOT_ENC_TO_DEG;
    }

    public double encAngleToCounts(double angle) {
        return (angle * PivotConfig.PIVOT_DEG_TO_ENC) + PivotConfig.PIVOT_ENC_OFFSET;
    }

    public double getTargetAngle() {
        return pivotTargetAngle;
    }


    // ----------------------------------------------------------------------
    // ---------------- Configure Shooter and Pivot Motors ------------------
    // ----------------------------------------------------------------------
    public void configureMotors(){

        // Pivot Motor, Window driven by Talon SRX (Phoenix 5)
        pivotMotor.configFactoryDefault();
        pivotMotor.configAllSettings(PivotSRXConfig.getConfig());
        pivotMotor.setInverted(PivotSRXConfig.motorInvert);
        pivotMotor.setNeutralMode(PivotSRXConfig.neutralMode);
    }
}

