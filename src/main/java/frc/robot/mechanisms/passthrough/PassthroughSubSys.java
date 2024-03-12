package frc.robot.mechanisms.passthrough;

import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Robot;
import frc.robot.RobotConfig;
import frc.robot.RobotConfig.Motors;

public class PassthroughSubSys extends SubsystemBase {
    public enum PassthroughState {
        STOPPED,
        GROUND_INTAKE,
        HP_INTAKE,
        EJECT,
        MANUAL
    }

    private PassthroughState state = PassthroughState.STOPPED;

    // Devices
    protected TalonFX motor = new TalonFX(Motors.passthroughMotorID, "CANFD");
    protected AnalogInput gamepieceSensor = new AnalogInput(RobotConfig.AnalogPorts.passthroughSensor);

    // Phoenix 6 Control Method - PWM output
    protected DutyCycleOut pwmCtrlr = new DutyCycleOut(0);

    /* ----- Constructor ----- */
    public PassthroughSubSys() {
        configureMotors();
        stopMotors();
    }

    /* ----- Periodic ----- */
    @Override
    public void periodic() {
        // Set motor speeds on periodic based on current state
        switch (state) {
            case GROUND_INTAKE: motor.setControl(pwmCtrlr.withOutput(PassthroughConfig.GROUND_INTAKE));
                                break;
            case HP_INTAKE: motor.setControl(pwmCtrlr.withOutput(PassthroughConfig.HP_INTAKE));
                            break;
            case EJECT: motor.setControl(pwmCtrlr.withOutput(PassthroughConfig.SPEAKER_EJECT));
                        break;
            case MANUAL: motor.setControl(pwmCtrlr.withOutput(Robot.operatorGamepad.getTriggerTwist()));
                         break;
            case STOPPED:
            default:    motor.setControl(pwmCtrlr.withOutput(0));
        }    
    }

    // -------------------------------------------------
    // ---------------- Setter Methods -----------------
    // -------------------------------------------------
    public void setNewState(PassthroughState newState) {
        state = newState;
    }

    public void stopMotors() {
        motor.stopMotor();
        setNewState(PassthroughState.STOPPED);
    }

    // -------------------------------------------------
    // ---------------- Getter Methods -----------------
    // -------------------------------------------------

    /* ----- Motor Getters ----- */
    public double getMotorPower() { return motor.get(); }

    /* ----- State Getters ----- */

    public PassthroughState getState() { return state; }

    public String getStateString() {
        switch (state) {
            case STOPPED:       return "STOPPED";
            case GROUND_INTAKE: return "GROUND INTAKE";
            case HP_INTAKE:     return "HUMAN PLAYER INTAKE";
            case EJECT:         return "EJECT";
            default:            return "DEFAULT";
        }
    }

    /* ----- Gamepiece Detection Getters ----- */
    public double getSensorVal() {
        return gamepieceSensor.getAverageVoltage();
    }

    public boolean getGamepieceDetected() {
        if (getSensorVal() > PassthroughConfig.gamepieceDetectDistance) { return true; }
        return false;
    }

    public boolean getGamepieceNotDetected() {
        return !getGamepieceDetected();
    }

    public String isGamepieceDetected() {
        if(getGamepieceDetected()) { 
            return "Detected"; } 
        return "Not Detected";
    }

    // ------------------------------------------------------
    // ---------------- Motor Configuration -----------------
    // ------------------------------------------------------
    public void configureMotors(){
        motor.getConfigurator().apply(PassthroughConfig.getConfig());
    }
}
