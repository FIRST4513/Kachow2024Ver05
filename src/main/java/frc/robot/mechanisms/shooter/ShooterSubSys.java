package frc.robot.mechanisms.shooter;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Robot;
import frc.robot.RobotConfig.Motors;

public class ShooterSubSys extends SubsystemBase {
    public enum FireState {
        STOPPED, 
        SPEAKER,
        HP_INTAKE,
        MANUAL
    }
    private FireState fireState = FireState.STOPPED;


    // Devices
    // protected TalonFX bottomMotor = new TalonFX(Motors.shooterMotorID, "CANFD");
    // protected TalonFX topMotor = new TalonFX(Motors.shooterMotorID2, "CANFD");
    protected TalonFX bottomMotor = new TalonFX(Motors.shooterMotorBottom);
    protected TalonFX topMotor = new TalonFX(Motors.shooterMotorTop);


    // Control for motors
    private DutyCycleOut pwmCtrlr = new DutyCycleOut(0);
    private VelocityVoltage velocityPIDCtrlr = new VelocityVoltage(0);


    /* ----- Constructor ----- */
    public ShooterSubSys() { 
        configureMotors();
        stopMotors();
    } 

    /* ----- Periodic ----- */
    @Override
    public void periodic() {

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
        
    }

    // ---------------------------------------------------------
    // ---------------- Shooter Motor Methods ------------------
    // ---------------------------------------------------------

    /* ----- Setters ----- */

    public void stopMotors() {
        bottomMotor.stopMotor();
        topMotor.stopMotor();
        setNewFireState(FireState.STOPPED);
    }

    /* ----- Getters ----- */
    
    public double getBottomPWM() { return bottomMotor.get(); }
    public double getTopPWM() { return topMotor.get(); }
    public double getTopRPS() { return topMotor.getVelocity().getValueAsDouble(); }
    public double getBottomRPS() { return bottomMotor.getVelocity().getValueAsDouble(); }

    private boolean getMotorsAtVelocityTarget(double topVel, double bottomVel) {
        return (
            checkInRange(bottomMotor.getVelocity().getValueAsDouble(), bottomVel, ShooterConfig.SHOT_VELOCITY_TOLERANCE)
            &&
            checkInRange(topMotor.getVelocity().getValueAsDouble(), topVel, ShooterConfig.SHOT_VELOCITY_TOLERANCE)
        );
    }

    public boolean areMotorsAtVelocityTarget() {
        switch (fireState) {
            case SPEAKER:   return getMotorsAtVelocityTarget(ShooterConfig.SPEAKER_TOP, ShooterConfig.SPEAKER_BOTTOM);
            case HP_INTAKE: return getMotorsAtVelocityTarget(ShooterConfig.RETRACT_TOP, ShooterConfig.RETRACT_BOTTOM);
            case STOPPED:   return getMotorsAtVelocityTarget(0, 0);
            default:        return false;
            // Manual included in default
        }
    }

    // -------------------------------------------------
    // ---------------- State Methods ------------------
    // -------------------------------------------------

    /* ----- Setters ----- */

    public void setNewFireState(FireState newState) {
        fireState = newState;
    }

    /* ----- Getters ----- */

    public String getFireStateString() {
        switch (fireState) {
            case SPEAKER: return "SPEAKER";
            case HP_INTAKE: return "RETRACT";
            case MANUAL: return "MANUAL";
            case STOPPED:
            default:      return "STOPPED";
        }            
    }

    
    public boolean checkInRange(double value, double target, double tolerence) {
        if ((value < (target + tolerence)) &&
            (value > target - tolerence)) {
                return true;
            }
        return false;
    }


    // ----------------------------------------------------------------------
    // ---------------- Configure Shooter and Pivot Motors ------------------
    // ----------------------------------------------------------------------
    public void configureMotors(){
        // Bottom Motor (Phoenix 6)
        bottomMotor.getConfigurator().apply(ShooterFalconConfigs.getConfig());
        bottomMotor.setInverted(ShooterFalconConfigs.bottomMotorInvert);

        // Top Motor (Phoenix 6)
        topMotor.getConfigurator().apply(ShooterFalconConfigs.getConfig());
        topMotor.setInverted(ShooterFalconConfigs.topMotorInvert);

    }
}
