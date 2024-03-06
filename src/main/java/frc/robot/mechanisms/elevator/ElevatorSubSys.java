package frc.robot.mechanisms.elevator;

import java.util.function.DoubleSupplier;

import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Robot;
import frc.robot.RobotConfig;
import frc.robot.mechanisms.arm.ArmConfig;
import frc.robot.mechanisms.arm.ArmConfig.ARMPOS;
import frc.robot.mechanisms.elevator.ElevatorConfig.ELEVPOS;
import frc.robot.Robot;

public class ElevatorSubSys extends SubsystemBase {
    // Devices - Krakens
    protected TalonFX elevatorMotor = new TalonFX(RobotConfig.Motors.elevatorMotorID, "CANFD");
    
    // possible lower limit switch? Up to mechanical
    private DigitalInput lowerLimitSw = new DigitalInput(RobotConfig.LimitSwitches.elevatorLowerLimitSw);

    // Controls- Phoenix 6 MotionMagic and target positions
    private double targetRotations = 0;                     // default position at bottom
    private MotionMagicVoltage mmCtrlr = new MotionMagicVoltage(targetRotations);
    private DutyCycleOut pwmCtrlr = new DutyCycleOut(0);
    private double currentElevPosRot = 0;
    private ElevatorConfig.ELEVPOS elevPos = ElevatorConfig.ELEVPOS.UNKOWN;

    // Constructor
    public ElevatorSubSys() { 
        configureMotor();
        stopMotor();
    } 

    @Override
    public void periodic() {
        updateCurrentPosData();

        stopMotor();

        // // Reset Motor Encoder Pos at bottom
        // if (isAtBottomPos())      {
        //      elevatorMotor.setPosition(0); 
        // }

        // // If were at the bottom and our target was bottom - stop motor no need to hold position
        // if (targetRotations <= 0.0) {
        //     if (isAtBottomPos() || currentElevPosRot < 0.15) {
        //     // elevatorMotor.stopMotor();
        //     return;
        //     }
        // }
        
        // if (getRotations() >= 20) {
        //     // Were above 20 inches must be doing trap you can drive up and down a little
        //     // Looks OK to move the elevator
        //     // setByMM(targetRotations);    
        //     return;       
        // }


        // // Check to prevent collision
        // if (targetRotations > 0.0)
        //     // Were trying to Raise elevator
        //     if ( Robot.arm.getArmCurrentAngle() < ArmConfig.SAFE_TRAVEL_MIN) {
        //         // The arm is a dangerous position - so don't raise the elev
        //         // stopMotor();
        //         return;
        // }

        // Looks OK to move the elevator
        // setByMM(targetRotations);


        // Robot.print("Tgt Rot: " + targetRotations);
        // check for limits and going wrong way, stop motors
        // if ((getLowerLimitSw() && input < -0.05)) {  //  || (isAtTop() && input > 0.05)
        //     elevatorMotor.setControl(pwmCtrlr.withOutput(0));
        // } else {
        //     // else just tell motors to go to the input speed
        //     elevatorMotor.setControl(pwmCtrlr.withOutput(input));
        // }
        // if ((-1 < currentElevPosRot) && (currentElevPosRot < 0.05) && (targetRotations < 0.05)) {
        //     stopMotor();
        // } else {
        //     setByMM(targetRotations);
        // }
    }


    // ---------------------------------------------------------
    // ---------------- Climber Motor Methods ------------------
    // ---------------------------------------------------------

    public void setTargetRotations(double newRotations) { targetRotations = newRotations; }
    public void setTargetHeight(double height) { targetRotations = heightToRotations(height); }

    public void adjustTargetHeight(double deltaHeight) { targetRotations += heightToRotations(deltaHeight); }

    public void stopMotor() {
        elevatorMotor.stopMotor();
        targetRotations = getRotations();
    }

    public void setByMM(double targetRotations) {
        this.targetRotations = limitTargetRot(targetRotations);
        // Should we check for arm pos before permitting a climb
        //if ((targetRotations > 2.0) && (Robot.arm.getArmCurrentAngle() < 20.0 )) return // Not OK to Climb

        // elevatorMotor.setControl(mmCtrlr.withPosition(targetRotations).withFeedForward(0.04));
    }
    
    public void adjustMMTarget(double adjustAmount) {
        double newTarget = targetRotations + adjustAmount;
        newTarget = limitTargetRot(newTarget);
        targetRotations = newTarget;
    }

    public void adjustMMTarget(DoubleSupplier adjustAmount) {
        Robot.print("Amount from supplier: " + adjustAmount.getAsDouble());
        adjustMMTarget(adjustAmount.getAsDouble());
    }
    public double limitTargetRot(double rot) {
        if (rot < 0.0) { rot = 0; }
        if (rot > ElevatorConfig.upperLimitRot) { rot = ElevatorConfig.upperLimitRot; }
        return rot;
    }
    // ---------- Position Getters ----------
    public double getRotations() { return elevatorMotor.getPosition().getValueAsDouble(); }
    public double getHeightInches() { return rotationsToHeight(getRotations()); }

    public void updateCurrentPosData(){
        currentElevPosRot = getRotations();
        // UpdatePosition Enum
        if (getLowerLimitSw()) {
                        elevPos = ELEVPOS.BOTTOM; 
        } else if (checkInRange(currentElevPosRot,    ElevatorConfig.posGroundRot,  ElevatorConfig.tolerence )) {
                        elevPos = ELEVPOS.MID;
        } else if (checkInRange(currentElevPosRot,    ElevatorConfig.posMidRot,     ElevatorConfig.tolerence )) {
                        elevPos = ELEVPOS.MID;
        } else if (checkInRange(currentElevPosRot,    ElevatorConfig.posTopRot,     ElevatorConfig.tolerence )) {
                        elevPos = ELEVPOS.TOP;
        } else if (checkInRange(currentElevPosRot,    ElevatorConfig.posTrapRot,    ElevatorConfig.tolerence )) {
                        elevPos = ELEVPOS.TRAP;
        } else {
                        elevPos = ELEVPOS.UNKOWN;
        }
    }

    public boolean isAtTarget() {
        if ( checkInRange(currentElevPosRot, targetRotations, ElevatorConfig.tolerence))  return true;
        return false;
    }
    
    public String getElevPosStr(){
        if (elevPos == ELEVPOS.BOTTOM) {return "BOTTOM";}
        if (elevPos == ELEVPOS.GROUNDPU) {return "GROUND PickUp Pos";}
        if (elevPos == ELEVPOS.MID) {return "MID";}
        if (elevPos == ELEVPOS.TRAP) {return "TRAP";}
        if (elevPos == ELEVPOS.TOP) {return "TOP";}
        return "Unknown";
    }
 
    // ----- Conversion Methods -----
    protected double rotationsToHeight(double rotations) { return rotations * ElevatorConfig.rotationsToHeight; }
    protected double heightToRotations(double height) { return height * ElevatorConfig.heighToRotations; }

    // ----- Speed Getters -----
    public double getSpeed() { return elevatorMotor.get(); }

    // ---------- Limit Switch and Position Getters --------
    public double getTargetRotations() { return targetRotations; }
    public boolean getLowerLimitSw() { return !lowerLimitSw.get(); }

    // ---------- Position Getters --------
    public boolean isAtBottomPos()      { return (elevPos == ELEVPOS.BOTTOM) ? true : false; }
    public boolean isAtGroundPUPos()    { return (elevPos == ELEVPOS.GROUNDPU) ? true : false; }
    public boolean isAtMidPos()         { return (elevPos == ELEVPOS.MID) ? true : false; }
    public boolean isAtTrapPos()        { return (elevPos == ELEVPOS.TRAP) ? true : false; }
    public boolean isAtTopPos()         { return (elevPos == ELEVPOS.TOP) ? true : false; }

    
    // Check for in range
    public boolean checkInRange(double value, double target, double tolerence) {
        if ((value < (target + tolerence)) &&
            (value > target - tolerence)) {
                return true;
            }
        return false;
    }

    // ------------------------------------------------------------
    // ---------------- Configure Climber Motors ------------------
    // ------------------------------------------------------------
    public void configureMotor() {
        elevatorMotor.getConfigurator().apply(ElevatorConfig.getConfig());
    }
}
