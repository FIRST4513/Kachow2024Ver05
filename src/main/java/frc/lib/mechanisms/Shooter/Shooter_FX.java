package frc.lib.mechanisms.Shooter;

import java.util.function.DoubleSupplier;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib.MotorConfigurations.motorFXConfig;

public class Shooter_FX extends SubsystemBase {
    /* ----- Variables for Speeds ----- */
    private double ejectSpeed;
    private double retractSpeed;
    private motorFXConfig[] motorConfigs;

    /* ----- Enum for Shooter State ----- */
    public enum ShooterState {
        STOPPED,
        RETRACT,
        EJECT,
        CUSTOM
    }
    private ShooterState state = ShooterState.STOPPED;
    private DoubleSupplier customSpeedSupplier;

    /* ----- Devices ----- */
    private TalonFX[] motors;

    /* ----- Constructor ----- */
    public Shooter_FX(double ejectSpeed, double retractSpeed, motorFXConfig... motorConfigs) {
        this.ejectSpeed = ejectSpeed;
        this.retractSpeed = retractSpeed;

        this.motorConfigs = motorConfigs;

        // Instantiate motors list to length of motor configs given
        motors = new TalonFX[motorConfigs.length];

        // Instantiate each motor
        for (int i = 0; i < motorConfigs.length; i++) {
            motors[i] = new TalonFX(motorConfigs[i].canID, motorConfigs[i].canBus);
        }

        configureMotors();
    }

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
