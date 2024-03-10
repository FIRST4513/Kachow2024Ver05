package frc.robot.mechanisms.shooter;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Robot;
import frc.robot.RobotConfig.Motors;
import frc.robot.XBoxCtrlrs.operator.OperatorGamepad;

public class ShooterSubSys extends SubsystemBase {
    public enum FireState {
        STOPPED, 
        SPEAKER,
        HP_INTAKE,
        MANUAL
    }
    private FireState fireState = FireState.STOPPED;

    public enum PivotState {
        TO_TARGET,  // like automatic
        MANUAL,
        STOPPED
    }
    private PivotState pivotState = PivotState.MANUAL;
    private double pivotTargetAngle = 0;

    // Devices
    // protected TalonFX bottomMotor = new TalonFX(Motors.shooterMotorID, "CANFD");
    // protected TalonFX topMotor = new TalonFX(Motors.shooterMotorID2, "CANFD");
    protected TalonFX bottomMotor = new TalonFX(Motors.shooterMotorBottom);
    protected TalonFX topMotor = new TalonFX(Motors.shooterMotorTop);
    protected TalonSRX pivotMotor = new TalonSRX(Motors.pivotMotorID);

    // Control for motors
    private DutyCycleOut pwmCtrlr = new DutyCycleOut(0);
    private VelocityVoltage velocityPIDCtrlr = new VelocityVoltage(0);

    // Current Position Data
    public double currentEncCount = 0.0;
    public double currentArmAngle = 0.0;
    public double currentArmPower = 0.0;

    // Constructor
    public ShooterSubSys() { 
        configureMotors();
        stopMotors();
    } 

    @Override
    public void periodic() {
        updateCurrentPositions();

        // Set motor speeds on periodic based on current state
        switch (fireState) {
            case SPEAKER: bottomMotor.setControl(velocityPIDCtrlr.withVelocity(ShooterConfig.SPEAKER_BOTTOM));
                          topMotor.setControl(velocityPIDCtrlr.withVelocity(ShooterConfig.SPEAKER_TOP));
                          break;
            case HP_INTAKE: bottomMotor.setControl(velocityPIDCtrlr.withVelocity(ShooterConfig.RETRACT_BOTTOM));
                            topMotor.setControl(velocityPIDCtrlr.withVelocity(ShooterConfig.RETRACT_TOP));
                            break;
            case MANUAL:  bottomMotor.setControl(pwmCtrlr.withOutput(Robot.operatorGamepad.getTriggerTwist()));
                          topMotor.setControl(pwmCtrlr.withOutput(Robot.operatorGamepad.getTriggerTwist()));
                          break;
            case STOPPED:
            default:    bottomMotor.setControl(pwmCtrlr.withOutput(0));
                        topMotor.setControl(pwmCtrlr.withOutput(0));
        }     
        
        switch (pivotState) {
            case TO_TARGET: setPivotToAngle(pivotTargetAngle);
            case MANUAL: setPivotManualLimits(Robot.operatorGamepad.getPivotAdjust());
            case STOPPED:
            default: setPivotByPWM(0);
        }
    }

    /* Pivot Motor Methods */
    public void setPivotByPWM(double power) {
        pivotMotor.set(ControlMode.PercentOutput, power);
    }

    public void setPivotManualLimits(double power) {
        // positive rotation
        if ((getEncoderPosition() < ShooterConfig.PIVOT_MAX_ENC) && (getEncoderPosition() > ShooterConfig.PIVOT_MIN_ENC)) {
            setPivotByPWM(power);
        } else {
            setPivotByPWM(0);
        }
    }

    public void setPivotToAngle(double angle) {
        double difference = currentArmAngle - angle;

        // if within threshold, stop arm
        if (Math.abs(difference) < ShooterConfig.PIVOT_ANGLE_TOLDERANCE) {
            stopMotors();
            return;
        }

        if (difference > 0) {
            setPivotByPWM(-ShooterConfig.PIVOT_MOVE_SPEED);
        } else {
            setPivotByPWM(ShooterConfig.PIVOT_MOVE_SPEED);
        }
    }

    public double getEncoderPosition()      { return pivotMotor.getSelectedSensorPosition(); }
    public double getPivotPower() { return pivotMotor.getMotorOutputPercent(); }
    
    public void updateCurrentPositions() {
        currentEncCount = pivotMotor.getSelectedSensorPosition();
        currentEncCount = currentEncCount % 4096;
        currentArmAngle = encCountsToAngle(currentEncCount);
    }

    public boolean isAtTarget() {
        if ( checkInRange(currentArmAngle, pivotTargetAngle, ShooterConfig.PIVOT_ANGLE_TOLDERANCE)) {
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
        return (counts - ShooterConfig.PIVOT_ENC_OFFSET) * ShooterConfig.PIVOT_ENC_TO_DEG;
    }

    public double encAngleToCounts(double angle) {
        return (angle * ShooterConfig.PIVOT_DEG_TO_ENC) + ShooterConfig.PIVOT_ENC_OFFSET;
    }

    public double getTargetAngle() {
        return pivotTargetAngle;
    }

    // ---------------------------------------------------------
    // ---------------- Shooter Motor Methods ------------------
    // ---------------------------------------------------------
    public void setNewFireState(FireState newState) {
        fireState = newState;
    }

    public void setNewPivotState(PivotState newState) {
        pivotState = newState;
    }

    public void stopMotors() {
        bottomMotor.stopMotor();
        topMotor.stopMotor();
        pivotMotor.set(ControlMode.PercentOutput, 0);
        setNewFireState(FireState.STOPPED);
        setNewPivotState(PivotState.STOPPED);
    }

    public void stopPivot() {
        pivotMotor.set(ControlMode.PercentOutput, 0);
        setNewPivotState(PivotState.STOPPED);
    }
    
    public double getBottomPWM() { return bottomMotor.get(); }
    public double getTopPWM() { return topMotor.get(); }
    public double getTopRPS() { return topMotor.getVelocity().getValueAsDouble(); }
    public double getBottomRPS() { return bottomMotor.getVelocity().getValueAsDouble(); }

    public String getFireStateString() {
        switch (fireState) {
            case SPEAKER: return "SPEAKER";
            case HP_INTAKE: return "RETRACT";
            case MANUAL: return "MANUAL";
            case STOPPED:
            default:      return "STOPPED";
        }            
    }

    public String getPivotStateString() {
        switch (fireState) {
            case MANUAL: return "MANUAL";
            default:     return "DEFAULT";
        }            
    }

    // -----------------------------------------------------------
    // ---------------- Configure Shooter Motor ------------------
    // -----------------------------------------------------------
    public void configureMotors(){
        // Bottom Motor (Phoenix 6)
        bottomMotor.getConfigurator().apply(ShooterFalconConfigs.getConfig());
        bottomMotor.setInverted(ShooterFalconConfigs.bottomMotorInvert);

        // Top Motor (Phoenix 6)
        topMotor.getConfigurator().apply(ShooterFalconConfigs.getConfig());
        topMotor.setInverted(ShooterFalconConfigs.topMotorInvert);

        // Pivot Motor, Window driven by Talon SRX (Phoenix 5)
        pivotMotor.configFactoryDefault();
        pivotMotor.configAllSettings(PivotSRXConfig.getConfig());
        pivotMotor.setInverted(PivotSRXConfig.motorInvert);
        pivotMotor.setNeutralMode(PivotSRXConfig.neutralMode);
    }
}
