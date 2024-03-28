package frc.lib.mechanisms.Intake;

import java.util.function.DoubleSupplier;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib.MotorConfigurations.motorSRXConfig;
import frc.lib.util.Util;

public class Intake_SRX extends SubsystemBase {
    /* ----- Variables ----- */
    private double retractSpeed;
    private double ejectSpeed;
    private motorSRXConfig motorConfig;
    private double gamepieceDetectDistance;

    /* ----- Enum for Intake State ----- */
    public enum IntakeState {
        STOPPED,
        RETRACT,
        EJECT,
        CUSTOM
    }
    private IntakeState state = IntakeState.STOPPED;
    private DoubleSupplier customSpeedSupplier;

    /* ----- Devices ----- */
    private WPI_TalonSRX motor;
    public AnalogInput gamepieceDetectSensor;

    /* ----- Constructor ----- */
    public Intake_SRX(motorSRXConfig motorConfig, int analogSensorPort, double retractSpeed, double ejectSpeed, double gamepieceDetectDistance) {
        this.motorConfig = motorConfig;
        this.gamepieceDetectDistance = gamepieceDetectDistance;
        this.retractSpeed = retractSpeed;
        this.ejectSpeed = ejectSpeed;

        motor = new WPI_TalonSRX(motorConfig.canID);
        gamepieceDetectSensor = new AnalogInput(analogSensorPort);
        configureMotor();
    }

    /* ----- Periodic ----- */
    @Override
    public void periodic() {
        switch (state) {
            case RETRACT: motor.set(retractSpeed);
                          break;
            case EJECT: motor.set(ejectSpeed);
                        break;
            case CUSTOM: motor.set(customSpeedSupplier.getAsDouble());
                         break;
            default: motor.set(0);
        }
    }

    /* ----- Setters ----- */
    public void stop() {
        motor.stopMotor();
        state = IntakeState.STOPPED;
    }

    public void setRetract() { state = IntakeState.RETRACT; }

    public void setEject()   { state = IntakeState.EJECT; }

    public void setCustom(double customSpeed) { setCustom(() -> customSpeed); }

    public void setCustom(DoubleSupplier speedSupplier) {
        state = IntakeState.CUSTOM;
        customSpeedSupplier = speedSupplier;
    }

    public void brakeOn()  { motor.setNeutralMode(NeutralMode.Brake); }
    public void brakeOff() { motor.setNeutralMode(NeutralMode.Coast); }

    public void setNewRetractSpeed(double newSpeed) { retractSpeed = newSpeed; }
    public void setNewEjectSpeed(double newSpeed)   { ejectSpeed = newSpeed; }

    /* ----- Getters ----- */
    public double getSpeed() { return motor.get(); }
    public IntakeState getState() { return state; }
    public String getStateString() {
        switch (state) {
            case RETRACT: return "RETRACT";
            case EJECT: return "EJECT";
            case CUSTOM: return "CUSTOM";
            case STOPPED: return "STOPPED";
            default: return "UNKNOWN";
        }
    }
    public boolean isAtSpeed() {
        switch (state) {
            case RETRACT: return Util.checkInRange(getSpeed(), retractSpeed, motorConfig.atSpeedTolerance);
            case EJECT: return Util.checkInRange(getSpeed(), ejectSpeed, motorConfig.atSpeedTolerance);
            case CUSTOM: return Util.checkInRange(getSpeed(), customSpeedSupplier.getAsDouble(), motorConfig.atSpeedTolerance);
            case STOPPED: return Util.checkInRange(getSpeed(), 0, motorConfig.atSpeedTolerance);
            default: return false;
        }
    }
    public double getSensorVal() {
        return gamepieceDetectSensor.getAverageVoltage();
    }
    public boolean getGamepieceDetected() {
        if (getSensorVal() > gamepieceDetectDistance) { return true; }
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

    /* ----- Motor Configuration ----- */
    private void configureMotor() {
        motor.configFactoryDefault();
        motor.configAllSettings(motorConfig.getConfig());
        motor.setInverted(motorConfig.motorInvert);
        motor.setNeutralMode(motorConfig.neutralMode);
    }
}
