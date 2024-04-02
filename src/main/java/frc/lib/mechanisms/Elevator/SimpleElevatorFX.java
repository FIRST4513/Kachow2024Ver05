package frc.lib.mechanisms.Elevator;

import java.util.function.DoubleSupplier;

import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib.MotorConfigurations.motorFXConfig;
import frc.lib.util.Util;

public class SimpleElevatorFX extends SubsystemBase {
    /* ----- Variables ----- */
    private ElevatorControlConfig conf;
    private double currentMotorRot = 0;

    /* ----- Enum for Elevator State ----- */
    public enum ElevatorState {
        STOPPED,
        BOTTOM_PWM,
        LOW,
        MID,
        TOP,
        CUSTOM_MM,
        MANUAL_PWM
    }
    private ElevatorState state = ElevatorState.STOPPED;
    private DoubleSupplier customValSupplier;

    /* ----- Devices ----- */
    private TalonFX[] motors;
    private motorFXConfig[] motorConfigs;
    private DigitalInput lowerLimitSw;

    /* ----- Control Methods ----- */
    private MotionMagicVoltage mmCtrlr = new MotionMagicVoltage(0);
    private DutyCycleOut pwmCtrlr = new DutyCycleOut(0);

    /* ----- Constructor ----- */
    public SimpleElevatorFX(ElevatorControlConfig controlConfig, int bottomLimitSwPin, motorFXConfig... motorConfigs) {
        this.conf = controlConfig;

        this.motorConfigs = motorConfigs;

        lowerLimitSw = new DigitalInput(bottomLimitSwPin);
        
        // Instantiate motors list to length of motor configs given
        motors = new TalonFX[motorConfigs.length];

        // Instantiate each motor
        for (int i = 0; i < motorConfigs.length; i++) {
            motors[i] = new TalonFX(motorConfigs[i].canID, motorConfigs[i].canBus);
        }

        configureMotors();
    }

    /* ----- Periodic ----- */
    @Override
    public void periodic() {
        updateCurrentPosData();

        // Run motors based on state
        switch (state) {
            case BOTTOM_PWM: setByPWM(conf.toBottomSpeed);
                             break;
            case LOW: setByMM(conf.lowRot);
                      break;
            case MID: setByMM(conf.midRot);
                      break;
            case TOP: setByMM(conf.topRot);
                      break;
            case CUSTOM_MM: setByMM(customValSupplier.getAsDouble());
                            break;
            case MANUAL_PWM: setByPWM(customValSupplier.getAsDouble());
                             break;
            case STOPPED:
            default: setByPWM(0);
        }
    }

    /* ----- Setters ----- */
    private void updateCurrentPosData() {
        currentMotorRot = motors[0].getPosition().getValueAsDouble();
    }

    public void stop() {
        motors[0].stopMotor();
    }

    private void setByPWM(double speed) {
        // if going down and already at bottom, send 0 power
        if ((speed < 0) && getLimitSw()) {
            motors[0].setControl(pwmCtrlr.withOutput(0));
            return;
        }
        
        // if going up and already at top limit, send 0 power
        if ((speed > 0) && (currentMotorRot > conf.topRot)){
            motors[0].setControl(pwmCtrlr.withOutput(0));
            return;
        }

        // otherwise, send the desired power
        motors[0].setControl(pwmCtrlr.withOutput(speed));
    }

    private void setByMM(double targetRotations) {
        motors[0].setControl(mmCtrlr.withPosition(targetRotations).withFeedForward(conf.mmFeedForward));
    }

    // States
    public void setBottom() { state = ElevatorState.BOTTOM_PWM; }
    public void setLow() { state = ElevatorState.LOW; }
    public void setMid() { state = ElevatorState.MID; }
    public void setTop() { state = ElevatorState.TOP; }

    public void setCustomMM(DoubleSupplier rotationSupplier) {
        state = ElevatorState.CUSTOM_MM;
        customValSupplier = rotationSupplier;
    }
    public void setCustomMM(double rotations) { setCustomMM(() -> rotations); }

    public void setManual(DoubleSupplier speedSupplier) {
        state = ElevatorState.MANUAL_PWM;
        customValSupplier = speedSupplier;
    }
    public void setManual(double speed) { setManual(() -> speed); }

    // Break Modes
    public void brakeOn() {
        for ( TalonFX motor : motors ) {
            motor.setNeutralMode(NeutralModeValue.Brake);
        }
    }

    public void brakeOff() {
        for ( TalonFX motor : motors ) {
            motor.setNeutralMode(NeutralModeValue.Coast);
        }
    }

    /* ----- Getters ----- */
    public double getSpeed() { return motors[0].get(); }
    public ElevatorState getState() { return state; }
    public String getStateString() {
        switch (state) {
            case BOTTOM_PWM: return "BOTTOM";
            case LOW: return "LOW";
            case MID: return "MID";
            case TOP: return "TOP";
            case CUSTOM_MM: return "CUSTOM";
            case MANUAL_PWM: return "MANUAL";
            case STOPPED: return "STOPPED";
            default: return "DEFAULT";
        }
    }

    public boolean isAtTarget() {
        switch (state) {
            case BOTTOM_PWM: return Util.checkInRange(motors[0].get(), conf.toBottomSpeed, conf.pwmIsAtTargetTolerance);
            case LOW: return Util.checkInRange(currentMotorRot, conf.lowRot, conf.mmIsAtTargetTolerance);
            case MID: return Util.checkInRange(currentMotorRot, conf.midRot, conf.mmIsAtTargetTolerance);
            case TOP: return Util.checkInRange(currentMotorRot, conf.topRot, conf.mmIsAtTargetTolerance);
            case CUSTOM_MM: return Util.checkInRange(currentMotorRot, customValSupplier.getAsDouble(), conf.mmIsAtTargetTolerance);
            case MANUAL_PWM: return Util.checkInRange(motors[0].get(), conf.toBottomSpeed, conf.pwmIsAtTargetTolerance);
            case STOPPED: return Util.checkInRange(motors[0].getVelocity().getValueAsDouble(), 0, 0.1);
            default: return false;
        }
    }

    public boolean getLimitSw() {
        return !lowerLimitSw.get();
    }

    public boolean isAtBottom() {
        return (getLimitSw() && (currentMotorRot < 0.05));
    }

    /* ----- Motor Configuration ----- */
    public void configureMotors() {
        for (int i = 0; i < motors.length; i++) {
            motors[i].getConfigurator().apply(motorConfigs[i].getConfig());
            
            // if not the first motor, tell them to follow the first motor
            if (i > 0) {
                motors[i].setControl(new Follower(motors[0].getDeviceID(), false));
            }
        }
    }
}
