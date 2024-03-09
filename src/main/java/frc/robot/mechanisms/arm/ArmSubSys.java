package frc.robot.mechanisms.arm;

import java.util.function.DoubleSupplier;

import org.littletonrobotics.junction.Logger;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Robot;
import frc.robot.RobotConfig;
import frc.robot.mechanisms.arm.ArmConfig.ARMPOS;
import frc.robot.mechanisms.elevator.ElevatorConfig;

public class ArmSubSys extends SubsystemBase {
    private TalonSRX armMotorLeader;
    public TalonSRX armMotorFollower;


    private ArmSRXMotorConfig motorConfig;

    public double currentEncCount = 0.0;
    public double currentArmAngle = 0.0;
    public double currentArmPower = 0.0;

    public double targetArmAngle = ArmConfig.armStowAngle;
    public ARMPOS armPos = ARMPOS.UNKOWN;

    // ---------- CONSTRUCTOR ----------
    public ArmSubSys() {
        motorConfig = new ArmSRXMotorConfig();
        armMotorLeader = new TalonSRX(RobotConfig.Motors.armMotorID);
        armMotorFollower = new TalonSRX(RobotConfig.Motors.armMotorID2);
        configArmMotors();
        stopMotors();
    }

    @Override
    public void periodic() {
        updateCurrentPosData();

        // setSpeed(Robot.operatorGamepad.);

        // // Check For possible collision conditions
        // if (targetArmAngle <= ArmConfig.SAFE_TRAVEL_MIN ) {
        //     // Were moving to a position tht could be dangerous
        //     if (Robot.elevator.getRotations() > ElevatorConfig.trapArmAbleRot) {
        //         // We must be going for a trap because we are so high
        //         if (targetArmAngle >= ArmConfig.SAFE_TRAP_MIN ) {
        //             // this distance is OK in this case
        //             driveMotorByMM();
        //             currentArmPower = armMotorLeader.getMotorOutputPercent();
        //             return;
        //        } else {
        //         // this is not a good angle to goto 
        //         stopMotors();
        //         return;                
        //        }    
        //     // Were moving arm inside a zone that can collide if Elev not at bottom!
        //     } else if (Robot.elevator.isAtBottomPos() == false) {
        //         // Elevator not at bottom so don't move yet
        //         stopMotors();
        //         return;
        //     } else {
        //         // were moving arm in danger zone but elev is at the bottom so OK
        //         driveMotorByMM();
        //         currentArmPower = armMotorLeader.getMotorOutputPercent();                
        //     }
        // }

        // // This leaves Arm movement outside where it is always safe
        // driveMotorByMM();

        // stopMotors();
        currentArmPower = armMotorLeader.getMotorOutputPercent();

        stopMotors();

        Logger.recordOutput("Arm Enc Value", getEncoderPosition());
    }



    // -------- Motor Control Methods (Manually Drive Motors) ------------
    public void stopMotors() {
        setSpeedPWM(0);
        //targetArmAngle = getArmCurrentAngle(); 
    }

    public void setSpeedSafe(double speed) {
        if ((ArmConfig.armRetractLimit < currentArmAngle) && (currentArmAngle < ArmConfig.armExtendLimit)) {
            // armMotorLeader.set(TalonSRXControlMode.PercentOutput, speed);
        } else {
            stopMotors();
        }
    }

    private void setSpeedPWM(double speed) {
        // armMotorLeader.set(TalonSRXControlMode.PercentOutput, speed);
    }

    private double capSpeedToMax(double inputSpeed) {
        double output;
        if (inputSpeed > ArmConfig.maxArmSpeed) {
            output = ArmConfig.maxArmSpeed;
        } else  if (inputSpeed < -ArmConfig.maxArmSpeed) {
            output = -ArmConfig.maxArmSpeed;
        } else {
            output = inputSpeed;
        }
        return output;
    }

    // -------- Motor Control Methods (Motion Magically) ------------

    public void driveToAngle(double angle) {
        double difference = currentArmAngle - angle;

        // if within threshold, stop arm
        if (Math.abs(difference) < ArmConfig.isAtTargetError) {
            stopMotors();
            return;
        }

        if (difference > 0) {
            setSpeedSafe(-0.1);
        } else {
            setSpeedSafe(0.1);
        }
    }

    // public void driveMotorByMM() {
    //     armMotorLeader.set( ControlMode.MotionMagic, encAngleToCounts(targetArmAngle),
    //                         DemandType.ArbitraryFeedForward, ArmSRXMotorConfig.feedForwardScaler);
    // }

    // public void setMMAngle(double newAngle) {
    //     newAngle = limitTargetAngle(newAngle);
    //     targetArmAngle = newAngle;
    //     System.out.println("Setting new angle");
    // }

    // public void adjustTargetAngle(double angle) {
    //     setMMAngle(currentArmAngle - angle);
    // }

    // public void adjustMMTarget(double adjustAmount) {
    //     double newTarget = targetArmAngle + adjustAmount;
    //     newTarget = limitTargetAngle(newTarget);
    //     targetArmAngle = newTarget;
    // }

    // public void adjustMMTarget(DoubleSupplier adjustAmount) {
    //     adjustMMTarget(adjustAmount.getAsDouble());
    // }

    public double limitTargetAngle(double angle) {
        if (angle < ArmConfig.armExtendLimit) { angle = ArmConfig.armExtendLimit; }
        if (angle > ArmConfig.armRetractLimit) { angle = ArmConfig.armRetractLimit; }
        return angle;
    }

    // --------- Get Sensor Values -----------------
    public void updateCurrentPosData(){
        currentEncCount = armMotorLeader.getSelectedSensorPosition();
        currentEncCount = currentEncCount % 4096;
        currentArmAngle = encCountsToAngle(currentEncCount);

        // input, mod, offset, to degrees
        // 1729, 1729, -376, -33
        // 5825, 1729, ", "
        // -2367, 1279, ", "

        // UpdatePosition Enum
        if      (checkInRange(currentArmAngle,      ArmConfig.armGroundAngle,   ArmConfig.isAtTargetError )) {
                        armPos = ARMPOS.GROUND;
        } else if (checkInRange(currentArmAngle,    ArmConfig.armStowAngle,     ArmConfig.isAtTargetError )) {
                        armPos = ARMPOS.STOW;
        } else if (checkInRange(currentArmAngle,    ArmConfig.armShooterAngle,  ArmConfig.isAtTargetError )) {
                        armPos = ARMPOS.SHOOTER;
        } else if (checkInRange(currentArmAngle,    ArmConfig.armAmpAngle,      ArmConfig.isAtTargetError )) {
                        armPos = ARMPOS.AMP;
        } else if (checkInRange(currentArmAngle,    ArmConfig.armTrapAngle,     ArmConfig.isAtTargetError )) {
                        armPos = ARMPOS.TRAP;
        } else {
                        armPos = ARMPOS.UNKOWN;
        }
    }

    // Checks for arm position
    public boolean isArmInside(){
        if (getArmCurrentAngle() < 0) {
            return true;
        }
        return false;
    }
    public boolean isArmOutside(){
        return !isArmInside();
    }


    public boolean isAtGroundPos()      { return (armPos == ARMPOS.GROUND) ? true : false; }
    public boolean isAtStowPos()        { return (armPos == ARMPOS.STOW) ? true : false; }
    public boolean isAtShooterPos()     { return (armPos == ARMPOS.SHOOTER) ? true : false; }
    public boolean isAtAmpPos()         { return (armPos == ARMPOS.AMP) ? true : false; }
    public boolean isAtTrapPos()        { return (armPos == ARMPOS.TRAP) ? true : false; }

    public boolean isAtSafeTravelPos()  { return ((ArmConfig.SAFE_TRAVEL_MIN < currentArmAngle) && (currentArmAngle < ArmConfig.SAFE_TRAVEL_MAX)); }

    // -----------  Telemtry Getters  -----------
    public double getArmTargetAngle()       { return targetArmAngle; }
    public double getEncoderPosition()      { return armMotorLeader.getSelectedSensorPosition(); }
    public double getArmCurrentAngle()      { return currentArmAngle; }
    public double getArmSpeed()             { return armMotorLeader.getMotorOutputPercent(); }

    public boolean isAtTarget() {
        if ( checkInRange(currentArmAngle, targetArmAngle, ArmConfig.isAtTargetError)) {
        //if (((targetArmAngle-ArmConfig.isAtTargetError) < currentArmAngle) &&
           // (currentArmAngle < (targetArmAngle+ArmConfig.isAtTargetError))) {
            return true; }
        return false;   // Otherwise
    }

    public String getArmPosStr(){
        if (armPos == ARMPOS.GROUND) {return "Ground";}
        if (armPos == ARMPOS.GROUND) {return "Stow";}
        if (armPos == ARMPOS.GROUND) {return "Shooter";}
        if (armPos == ARMPOS.GROUND) {return "HumanPlayer";}
        if (armPos == ARMPOS.GROUND) {return "Amp";}
        if (armPos == ARMPOS.GROUND) {return "Trap";}
        return "Unknown";
    }

    // ----- Misc Convertion Methods -------------
    public double encCountsToAngle(double counts) {
        return (counts - ArmConfig.encCountOffset) * ArmConfig.encDegsPerCount;
    }

    public double encAngleToCounts(double angle) {
        return (angle * ArmConfig.encCountsPerDeg) + ArmConfig.encCountOffset;
    }

    // Check for in range
    public boolean checkInRange(double value, double target, double tolerence) {
        if ((value < (target + tolerence)) &&
            (value > target - tolerence)) {
                return true;
            }
        return false;
    }

    // ----------- Config Method -----------
    public void configArmMotors() {
        armMotorLeader.configFactoryDefault();
        armMotorLeader.configAllSettings(ArmSRXMotorConfig.config);
        // armMotorLeader.configForwardSoftLimitThreshold(encAngleToCounts(ArmConfig.armExtendLimit));
        // armMotorLeader.configReverseSoftLimitThreshold(encAngleToCounts(ArmConfig.armRetractLimit));
        // armMotorLeader.configForwardSoftLimitThreshold(4000);
        // armMotorLeader.configReverseSoftLimitThreshold(1770);
        armMotorLeader.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute);
        
        armMotorFollower.configFactoryDefault();
        armMotorFollower.configAllSettings(ArmSRXMotorConfig.config);
        // armMotorFollower.configForwardSoftLimitThreshold(encAngleToCounts(ArmConfig.armExtendLimit));
        // armMotorFollower.configReverseSoftLimitThreshold(encAngleToCounts(ArmConfig.armRetractLimit));
        armMotorFollower.set(ControlMode.Follower, armMotorLeader.getDeviceID());
    }
}
