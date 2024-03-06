package frc.robot.mechanisms.shooter;

import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Robot;
import frc.robot.RobotConfig.Motors;

public class ShooterSubSys extends SubsystemBase {
    public enum FireState{
        STOPPED, 
        AMP,
        SPEAKER,
        RETRACT,
        MANUAL
    }
    
    private FireState state = FireState.STOPPED;

    // Devices
    // protected TalonFX bottomMotor = new TalonFX(Motors.shooterMotorID, "CANFD");
    // protected TalonFX topMotor = new TalonFX(Motors.shooterMotorID2, "CANFD");
    protected TalonFX bottomMotor = new TalonFX(Motors.shooterMotorID);
    protected TalonFX topMotor = new TalonFX(Motors.shooterMotorID2);


    // Control for motors
    private DutyCycleOut bottomCtrlr = new DutyCycleOut(0);
    private DutyCycleOut topCtrlr = new DutyCycleOut(0);

    // Constructor
    public ShooterSubSys() { 
        configureMotors();
        stopMotors();
    } 

    @Override
    public void periodic() {
        // Set motor speeds on periodic based on current state
        switch (state) {
            case AMP: bottomMotor.setControl(bottomCtrlr.withOutput(ShooterConfig.AMP_BOTTOM));
                      topMotor.setControl(topCtrlr.withOutput(ShooterConfig.AMP_TOP));
                      break;
            case SPEAKER: bottomMotor.setControl(bottomCtrlr.withOutput(ShooterConfig.SPEAKER_BOTTOM));
                          topMotor.setControl(topCtrlr.withOutput(ShooterConfig.SPEAKER_TOP));
                          break;
            case RETRACT: bottomMotor.setControl(bottomCtrlr.withOutput(ShooterConfig.RETRACT_BOTTOM));
                          topMotor.setControl(topCtrlr.withOutput(ShooterConfig.RETRACT_TOP));
                          break;
            case MANUAL:  bottomMotor.setControl(bottomCtrlr.withOutput(Robot.operatorGamepad.getTriggerTwist()));
                          topMotor.setControl(topCtrlr.withOutput(Robot.operatorGamepad.getTriggerTwist()));
                          break;
            case STOPPED:
            default:    bottomMotor.setControl(bottomCtrlr.withOutput(0));
                        topMotor.setControl(topCtrlr.withOutput(0));
        }                 
    }

    // ---------------------------------------------------------
    // ---------------- Shooter Motor Methods ------------------
    // ---------------------------------------------------------
    public void setNewState(FireState newState) {
        state = newState;
    }
    public void stopMotors() {
        bottomMotor.stopMotor();
        topMotor.stopMotor();
        setNewState(FireState.STOPPED);
    }
    
    public double getMotorSpeed1() { return bottomMotor.get(); }
    public double getMotorSpeed2() { return topMotor.get(); }

    public String getStateString() {
        switch (state) {
            case AMP: return "AMP";
            case SPEAKER: return "SPEAKER";
            case RETRACT: return "RETRACT";
            case STOPPED:
            default:      return "STOPPED";
        }            
    }

    // -----------------------------------------------------------
    // ---------------- Configure Shooter Motor ------------------
    // -----------------------------------------------------------
    public void configureMotors(){
        // This config is for the Falcon 500 Talon FX Controller(s)

        // Bottom Motor
        bottomMotor.getConfigurator().apply(ShooterConfig.getConfig());
        bottomMotor.setInverted(ShooterConfig.bottomMotorInvert);

        // Motor 2
        topMotor.getConfigurator().apply(ShooterConfig.getConfig());
        topMotor.setInverted(ShooterConfig.topMotorInvert);
    }
}
