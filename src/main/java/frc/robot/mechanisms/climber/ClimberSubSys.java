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
        CURRENT,
        // Manual Control
        MANUAL,
        // No load
        BOTTOM,  // bottom, 0 height
        CLEAR_CHAIN,  // top, full height
        ON_CHAIN,  // lowered enough to hook and pull chain taught
        // Load, Motion Magig
        MID_CLIMB,  // lowered enough to raise robot off ground, down to intake clear height
        FULL_CLIMB,  // lowered fully with feed forward
        // Hold, one power, like a feed forward
        HOLD_CLIMB,
    }

    private ClimbState state = ClimbState.MANUAL;

    // Devices - Krakens
    public TalonFX climbMotor1 = new TalonFX(RobotConfig.Motors.climberMotorID1, "CANFD");
    protected TalonFX climbMotor2 = new TalonFX(RobotConfig.Motors.climberMotorID2, "CANFD");

    // possible lower limit switch? Up to mechanical
    private DigitalInput lowerLimitSw = new DigitalInput(RobotConfig.LimitSwitches.climberLowerLimitSw);
    private DigitalInput midLimitSw = new DigitalInput(RobotConfig.LimitSwitches.climberMidLimitSw);

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
        // switch (state) {
        //     case CURRENT: setPWM(0);
        //                   break;
        //     case MANUAL: setPWM(Robot.pilotGamepad.getClimberAdjustInput());
        //                  break;
        //     case BOTTOM: {
        //         if (getLowerLimitSw()) {
        //             setPWM(0);
        //             resetEncoders();
        //         } else {
        //             setPWM(-0.25);
        //         }
        //         break;
        //     }
            
        //     case CLEAR_CHAIN: setMM(ClimberConfig.posTop);
        //                       break;
        //     case ON_CHAIN: setMM(ClimberConfig.posOnChain);
        //                    break;
        //     default: setPWM(0);
        // }

        // if (getLowerLimitSw()) {
        //     resetEncoders();
        // }

        stopMotors();
        
        // manual control
        // double input = Robot.pilotGamepad.getClimberAdjustInput()/4;
        // check for lower limit
        // if (getLowerLimitSw()) {
        //     climbMotor1.setPosition(0);
        //     climbMotor2.setPosition(0);

        //     if (input > 0.05) {
        //         climbMotor1.setControl(pwmCtrlr.withOutput(input));
        //         climbMotor2.setControl(pwmCtrlr.withOutput(input));
        //     } else {
        //         climbMotor1.setControl(pwmCtrlr.withOutput(0));
        //         climbMotor2.setControl(pwmCtrlr.withOutput(0));
        //     }
        // } else if (getRotations() > ClimberConfig.MAX_ROTATIONS) {
        //     if (input < 0.05) {
        //         climbMotor1.setControl(pwmCtrlr.withOutput(input));
        //         climbMotor2.setControl(pwmCtrlr.withOutput(input));
        //     } else {
        //         climbMotor1.setControl(pwmCtrlr.withOutput(0));
        //         climbMotor2.setControl(pwmCtrlr.withOutput(0));
        //     }
        // } else {
        //     climbMotor1.setControl(pwmCtrlr.withOutput(input));
        //     climbMotor2.setControl(pwmCtrlr.withOutput(input));
        // }
    }

    // ---------------------------------------------------------
    // ---------------- Climber Motor Methods ------------------
    // ---------------------------------------------------------
    private void setPWM(double output) {
        // going up check top
        if (output > 0) {
            if (getRotations() > ClimberConfig.MAX_ROTATIONS) {
                climbMotor1.setControl(pwmCtrlr.withOutput(0));
                climbMotor2.setControl(pwmCtrlr.withOutput(0));
                return;
            }
        }
        
        // going down check bottom
        if (output < 0) {
            if ((getLowerLimitSw())) {
                climbMotor1.setControl(pwmCtrlr.withOutput(0));
                climbMotor2.setControl(pwmCtrlr.withOutput(0));
                return;
            }
        }

        // otherwise send power to motors
        climbMotor1.setControl(pwmCtrlr.withOutput(output));
        climbMotor2.setControl(pwmCtrlr.withOutput(output));
    }

    private void setMM(double rotationTarget) {
        rotationTarget = clampInRange(rotationTarget);
        
        climbMotor1.setControl(mmCtrlr.withPosition(rotationTarget));
        climbMotor2.setControl(mmCtrlr.withPosition(rotationTarget));
    }

    private void resetEncoders() {
        climbMotor1.setPosition(0);
        climbMotor2.setPosition(0);
    }

    public void setNewState(ClimbState newState) {
        state = newState;
    }


    // public void setTargetRotations(double newRotations) { targetRotations = newRotations; }
    // public void setTargetHeight(double height) { targetRotations = heightToRotations(height); }

    // public void adjustTargetHeight(double deltaHeight) { targetRotations += heightToRotations(deltaHeight); }

    public void stopMotors() {
        climbMotor1.stopMotor();
        climbMotor2.stopMotor();
        state = ClimbState.CURRENT;
        // targetRotations = getRotations();
    }

    // ---------- Position Getters ----------
    public double getRotations() { return climbMotor1.getPosition().getValueAsDouble(); }
    public double getHeightInches() { return rotationsToHeight(getRotations()); }

    // ----- Conversion Methods -----
    protected double rotationsToHeight(double rotations) { return rotations * ClimberConfig.rotationsToHeight; }
    protected double heightToRotations(double height) { return height * ClimberConfig.heighToRotations; }

    protected double clampInRange(double rotations) {
        if (rotations < 0) return 0;
        if (rotations > ClimberConfig.MAX_ROTATIONS) return ClimberConfig.MAX_ROTATIONS;
        return rotations;
    }

    // ----- Speed Getters -----
    public double getSpeed() { return climbMotor1.get(); }

    // ---------- Limit Switch and Position Getters --------

    public boolean getLowerLimitSw() { return !lowerLimitSw.get(); }
    public boolean getMidLimitSw() { return !midLimitSw.get(); }
    public boolean isAtTop() { return getRotations() >= ClimberConfig.MAX_ROTATIONS; }
    public String getStateString() {
        switch (state) {
            case CURRENT: return "CURRENT";
            case MANUAL: return "MANUAL";
            case BOTTOM: return "BOTTOM";
            case CLEAR_CHAIN: return "CLEAR CHAIN";
            case ON_CHAIN: return "ON CHAIN";
            default: return "DEFAULT";
        }
    }

    // ------------------------------------------------------------
    // ---------------- Configure Climber Motors ------------------
    // ------------------------------------------------------------
    public void configureMotors() {
        climbMotor1.getConfigurator().apply(ClimberConfig.getConfig());
        climbMotor2.getConfigurator().apply(ClimberConfig.getConfig());
    }
}