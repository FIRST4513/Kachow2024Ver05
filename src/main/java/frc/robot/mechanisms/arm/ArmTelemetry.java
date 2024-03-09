package frc.robot.mechanisms.arm;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.robot.Robot;

public class ArmTelemetry {
    protected ShuffleboardTab tab;

    public ArmTelemetry(ArmSubSys arm) {
        // Get shuffleboard tab for name "Arm", and create one if it does not exist
        tab = Shuffleboard.getTab("Arm");

        // Important things
        tab.addString("Arm At Position",    () -> Robot.arm.getArmPosStr())         .withSize(2, 1) .withPosition(0, 0);
        tab.addNumber ("ARM- Target Angle", () -> Robot.arm.getArmTargetAngle())    .withSize(2, 1) .withPosition(0, 2);
        tab.addBoolean("ARM- At Target?",   () -> Robot.arm.isAtTarget())           .withSize(2, 1) .withPosition(0, 4);
        tab.addNumber ("ARM- Current Angle",() -> Robot.arm.getArmCurrentAngle())   .withSize(2, 1) .withPosition(0, 6);
        //                                                                             .withSize(2, 1) .withPosition(0, 3);
        // Other info
        tab.addNumber("ARM- Enc Value",       () -> Robot.arm.getEncoderPosition())   .withSize(2, 1) .withPosition(3, 0);
        tab.addNumber("ARM- Speed 1",   () -> Robot.arm.getArmSpeed())          .withSize(2, 1) .withPosition(3, 1);
        tab.addNumber("ARM- Speed 2",   () -> Robot.arm.armMotorFollower.getMotorOutputPercent())          .withSize(2, 1) .withPosition(3, 1);
    }
}
