package frc.robot.mechanisms.intake;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Robot;
import frc.robot.RobotConfig.AnalogPorts;
import frc.robot.RobotConfig.Motors;

public class IntakeSubSys extends SubsystemBase {
    public enum IntakeState {
        SHOOTER_FEED,
        STOPPED,
        GROUND,
        MANUAL,
        TRAP,
        AMP,
    }

    private IntakeState state = IntakeState.STOPPED;
    
    // Devices
    public WPI_TalonSRX intakeMotor = new WPI_TalonSRX(Motors.intakeMotorID);
    public AnalogInput gamepieceDetectSensor = new AnalogInput(AnalogPorts.intakeGamepieceSensor);

    /* ----- Constructor ----- */
    public IntakeSubSys() { 
        configureTalonSRXControllers();
        stopMotors();
        setBrakeMode(true);
    } 

    /* ----- Periodic ----- */
    @Override
    public void periodic() {
        // drive motor based on the current state
        switch (state) {
            case SHOOTER_FEED: intakeMotor.set(IntakeConfig.FEED);
                               break;
            case GROUND: intakeMotor.set(IntakeConfig.GROUND);
                         break;
            case MANUAL: intakeMotor.set(Robot.operatorGamepad.getTriggerTwist());
                         break;
            case TRAP: intakeMotor.set(IntakeConfig.TRAP);
                       break;
            case AMP: intakeMotor.set(IntakeConfig.AMP);
                      break;
            // stopped included:
            default: intakeMotor.set(0);
        }
    }

    // --------------------------------------------------------
    // ---------------- Intake Motor Methods ------------------
    // --------------------------------------------------------

    /* ----- Setters ----- */

    public void setNewState(IntakeState newState) {
        state = newState;
    }

    public void stopMotors() {
        setBrakeMode(true);
        intakeMotor.stopMotor();
        state = IntakeState.STOPPED;
    }
    
    // ------ Set Brake Modes ---------
    public void setBrakeMode(Boolean enabled) {
        if (enabled) {
            intakeMotor.setNeutralMode(NeutralMode.Brake);
        } else {
            intakeMotor.setNeutralMode(NeutralMode.Coast);
        }
    }

    /* ----- Getters ---- */

    public double getMotorSpeed() { return intakeMotor.get(); }
    public IntakeState getState() { return state; }

    public String getStateString() {
        switch (state) {
            case SHOOTER_FEED: return "SHOOTER FEED";
            case GROUND:       return "GROUND";
            case MANUAL:       return "MANUAL";
            case TRAP:         return "TRAP";
            case AMP:          return "AMP";
            default:           return "STOPPED";
        }
    }

    // ----------------------------------------------------------------
    // ---------------- Intake Detect Methods -------------------------
    // ----------------------------------------------------------------

    // ---------- General Gamepiece Detects ----------
    public double getSensorVal() {
        return gamepieceDetectSensor.getAverageVoltage();
    }

    public boolean getGamepieceDetected() {
        if (getSensorVal() > IntakeConfig.gamepieceDetectDistance) { return true; }
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

    // ----------------------------------------------------------
    // ---------------- Configure Intake Motor ------------------
    // ----------------------------------------------------------
    public void configureTalonSRXControllers(){
        // Config the only Talon SRX motor
        intakeMotor.configFactoryDefault();
        intakeMotor.configAllSettings(IntakeConfig.getConfig());
        intakeMotor.setInverted(IntakeConfig.intakeMotorInvert);
        intakeMotor.setNeutralMode(IntakeConfig.intakeNeutralMode);
        intakeMotor.setSelectedSensorPosition(0);
    }
}
