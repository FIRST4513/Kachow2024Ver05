package frc.robot.mechanisms.climber;

import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Robot;
import frc.robot.RobotConfig;

public class ClimberSubSys extends SubsystemBase {
    public enum ClimbState {
        // Current height, sends 0 power to motors
        STOPPED,
        // Manual Control
        MANUAL,
        // No load
        BOTTOM,  // bottom, 0 height, includes holding power
        TOP,  // top, full height
        ON_CHAIN,  // lowered enough to hook and pull chain taught
        // Hold, one power, like a feed forward
        HOLD_WITH_LOAD
    }

    private ClimbState state = ClimbState.MANUAL;

    // Devices - Krakens
    protected TalonFX leftMotor  = new TalonFX(RobotConfig.Motors.climbLeftMotorID, "CANFD");
    protected TalonFX rightMotor = new TalonFX(RobotConfig.Motors.climbRightMotorID, "CANFD");

    // possible lower limit switch? Up to mechanical
    private DigitalInput leftLimitSw = new DigitalInput(RobotConfig.LimitSwitches.climberLeftLowerSw);
    private DigitalInput rightLimitSw = new DigitalInput(RobotConfig.LimitSwitches.climberRightLowerSw);

    // Controls- Phoenix 6 MotionMagic and target positions
    private MotionMagicVoltage mmCtrlr = new MotionMagicVoltage(0);
    private DutyCycleOut pwmCtrlr = new DutyCycleOut(0);

    // Constructor
    public ClimberSubSys() { 
        configureMotors();
        stopMotors();
    } 

    @Override
    public void periodic() {
        switch (state) {
            case STOPPED: stopMotors();
                          break;
            case MANUAL: setPWM(Robot.pilotGamepad.getClimberAdjustInput());
                         break;
            case BOTTOM: setBottom();
                         break;
            case TOP: setMM(ClimberConfig.posTop);
                      break;
            case ON_CHAIN: setMM(ClimberConfig.posOnChain);
                           break;
            case HOLD_WITH_LOAD: break;
            default: setPWM(0);
        }

        if (getLeftLowerSw()) { resetLeftEncoder(); }
        if (getRightLowerSw()) { resetRightEncoder(); }
    }

    // ---------------------------------------------------------
    // ---------------- Climber Motor Methods ------------------
    // ---------------------------------------------------------
    private void setBottom() {
        // Left
        if (getLeftLowerSw()) {
            leftMotor.setControl(pwmCtrlr.withOutput(-0.015));
            resetLeftEncoder();
        } else {
            leftMotor.setControl(pwmCtrlr.withOutput(-0.25));
        }

        // Right
        if (getRightLowerSw()) {
            rightMotor.setControl(pwmCtrlr.withOutput(-0.015));
            resetRightEncoder();
        } else {
            rightMotor.setControl(pwmCtrlr.withOutput(-0.25));
        }
    }

    private void setPWM(double output) {
        setLeftPWM(output);
        setRightPWM(output);
    }

    private void setLeftPWM(double output) {
        // going up check top
        if (output > 0) {
            if (getLeftRotations() > ClimberConfig.MAX_ROTATIONS) {
                leftMotor.setControl(pwmCtrlr.withOutput(0));
                return;
            }
        }
        
        // going down check bottom
        if (output < 0) {
            if ((getLeftLowerSw())) {
                leftMotor.setControl(pwmCtrlr.withOutput(0));
                return;
            }
        }

        // otherwise send power to motor
        leftMotor.setControl(pwmCtrlr.withOutput(output));
    }
    
    private void setRightPWM(double output) {
        // going up check top
        if (output > 0) {
            if (getRightRotations() > ClimberConfig.MAX_ROTATIONS) {
                rightMotor.setControl(pwmCtrlr.withOutput(0));
                return;
            }
        }
        
        // going down check bottom
        if (output < 0) {
            if ((getRightLowerSw())) {
                rightMotor.setControl(pwmCtrlr.withOutput(0));
                return;
            }
        }

        // otherwise send power to motor
        rightMotor.setControl(pwmCtrlr.withOutput(output));
    }

    private void setMM(double rotationTarget) {
        rotationTarget = clampInRange(rotationTarget);
        
        leftMotor.setControl(mmCtrlr.withPosition(rotationTarget));
        rightMotor.setControl(mmCtrlr.withPosition(rotationTarget));
    }

    private void resetLeftEncoder() {
        leftMotor.setPosition(0);
    }

    private void resetRightEncoder() {
        rightMotor.setPosition(0);
    }

    public void setNewState(ClimbState newState) {
        state = newState;
    }

    public void stopMotors() {
        leftMotor.stopMotor();
        rightMotor.stopMotor();
        state = ClimbState.STOPPED;
    }

    // ---------- Position Getters ----------
    public double getLeftRotations() { return leftMotor.getPosition().getValueAsDouble(); }
    public double getRightRotations() { return rightMotor.getPosition().getValueAsDouble(); }

    // ----- Conversion Methods -----

    protected double clampInRange(double rotations) {
        if (rotations < 0) return 0;
        if (rotations > ClimberConfig.MAX_ROTATIONS) return ClimberConfig.MAX_ROTATIONS;
        return rotations;
    }

    // ----- Speed Getters -----
    public double getLeftPower() { return leftMotor.get(); }
    public double getRightPower() { return rightMotor.get(); }

    // ---------- Limit Switch and Position Getters --------

    public boolean getLeftLowerSw() { return !leftLimitSw.get(); }
    public boolean getRightLowerSw() { return !rightLimitSw.get(); }
    public boolean isLeftAtTop() { return getLeftRotations() >= ClimberConfig.MAX_ROTATIONS; }
    public boolean isRightAtTop() { return getRightRotations() >= ClimberConfig.MAX_ROTATIONS; }
    public boolean isBothAtTop() { return isLeftAtTop() && isRightAtTop(); }
    public String getStateString() {
        switch (state) {
            case STOPPED: return "STOPPED";
            case MANUAL: return "MANUAL";
            case BOTTOM: return "BOTTOM";
            case TOP: return "CLEAR CHAIN";
            case ON_CHAIN: return "ON CHAIN";
            case HOLD_WITH_LOAD: return "HOLD WITH LOAD";
            default: return "DEFAULT";
        }
    }

    // ------------------------------------------------------------
    // ---------------- Configure Climber Motors ------------------
    // ------------------------------------------------------------
    public void configureMotors() {
        leftMotor.getConfigurator().apply(ClimberConfig.getConfig());
        leftMotor.setInverted(ClimberConfig.leftMotorInvert);

        rightMotor.getConfigurator().apply(ClimberConfig.getConfig());
        rightMotor.setInverted(ClimberConfig.rightMotorInvert);
    }
}