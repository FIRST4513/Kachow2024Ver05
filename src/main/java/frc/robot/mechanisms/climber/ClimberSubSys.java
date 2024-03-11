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
        STOPPED,        // sends 0 power to motors
        MANUAL,         // manual control from Operator Gamepad
        BOTTOM,         // move to height of 0 at -0.25 pwr, includes holding power of -0.015
        TOP,            // mm to top height
        ON_CHAIN,       // mm to on-chain position, pulls it taught
        HOLD_WITH_LOAD  // hold power when climbed, like a feed forward
    }

    private ClimbState state = ClimbState.STOPPED;

    // Devices - Krakens
    protected TalonFX leftMotor  = new TalonFX(RobotConfig.Motors.climbLeftMotorID,  "CANFD");
    protected TalonFX rightMotor = new TalonFX(RobotConfig.Motors.climbRightMotorID, "CANFD");

    // Limit Switches
    private DigitalInput leftLimitSw  = new DigitalInput(RobotConfig.LimitSwitches.climberLeftLowerSw);
    private DigitalInput rightLimitSw = new DigitalInput(RobotConfig.LimitSwitches.climberRightLowerSw);

    // Phoenix 6 Control Methods, Motion Magic and PWM out
    private MotionMagicVoltage mmCtrlr = new MotionMagicVoltage(0);
    private DutyCycleOut pwmCtrlr = new DutyCycleOut(0);

    /* ----- Constructor ----- */
    public ClimberSubSys() {
        configureMotors();
        stopMotors();
    } 

    /* ----- Periodic ----- */
    @Override
    public void periodic() {
        // Move motors based on current state
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
            case HOLD_WITH_LOAD: break;  // TODO: ADD METHOD HERE FOR HOLD WITH LOAD
            default: setPWM(0);
        }

        // Set encoder to 0 if at bottom for each motor individually
        if (getLeftLowerSw()) { resetLeftEncoder(); }
        if (getRightLowerSw()) { resetRightEncoder(); }
    }

    // -------------------------------------------------
    // ---------------- Motor Methods ------------------
    // -------------------------------------------------

    /* ----- Basic Methods ----- */
    public void stopMotors() {
        leftMotor.stopMotor();
        rightMotor.stopMotor();
        state = ClimbState.STOPPED;
    }

    private void setPWM(double output) {
        setLeftPWM(output);
        setRightPWM(output);
    }

    private void setMM(double rotationTarget) {
        rotationTarget = clampInRange(rotationTarget);
        
        leftMotor.setControl(mmCtrlr.withPosition(rotationTarget));
        rightMotor.setControl(mmCtrlr.withPosition(rotationTarget));
    }

    /* ----- Smart(er) Methods ----- */

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

    // ---------------------------------------------------
    // ---------------- Encoder Methods ------------------
    // ---------------------------------------------------

    /* ----- Resetters ----- */

    private void resetLeftEncoder() {
        leftMotor.setPosition(0);
    }

    private void resetRightEncoder() {
        rightMotor.setPosition(0);
    }

    /* ----- Getters ----- */

    public double getLeftRotations() { return leftMotor.getPosition().getValueAsDouble(); }
    public double getRightRotations() { return rightMotor.getPosition().getValueAsDouble(); }

    // ---------------------------------------------------
    // ---------------- State Methods --------------------
    // ---------------------------------------------------

    public void setNewState(ClimbState newState) {
        state = newState;
    }

    /* ----- Getters ----- */

    public ClimbState getState() { return state; }

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

    // ---------------------------------------------------
    // ---------------- Misc. Getters --------------------
    // ---------------------------------------------------

    /* ----- Powers ----- */
    public double getLeftPower() { return leftMotor.get(); }
    public double getRightPower() { return rightMotor.get(); }

    /* ----- Limit Switches ----- */
    public boolean getLeftLowerSw() { return !leftLimitSw.get(); }
    public boolean getRightLowerSw() { return !rightLimitSw.get(); }
    public boolean isLeftAtTop() { return getLeftRotations() >= ClimberConfig.MAX_ROTATIONS; }
    public boolean isRightAtTop() { return getRightRotations() >= ClimberConfig.MAX_ROTATIONS; }
    public boolean isBothAtTop() { return isLeftAtTop() && isRightAtTop(); }


    /* ----- Conversion Methods ----- */

    protected double clampInRange(double rotations) {
        if (rotations < 0) return 0;
        if (rotations > ClimberConfig.MAX_ROTATIONS) return ClimberConfig.MAX_ROTATIONS;
        return rotations;
    }

    // ------------------------------------------------------------
    // ---------------- Configure Climber Motors ------------------
    // ------------------------------------------------------------
    public void configureMotors() {
        leftMotor.getConfigurator().apply(ClimberConfig.getConfig(RobotConfig.Motors.climbLeftMotorID));
        rightMotor.getConfigurator().apply(ClimberConfig.getConfig(RobotConfig.Motors.climbRightMotorID));
    }
}