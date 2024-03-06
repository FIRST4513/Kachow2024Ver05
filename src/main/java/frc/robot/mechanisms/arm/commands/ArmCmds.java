package frc.robot.mechanisms.arm.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import frc.robot.Robot;
import frc.robot.mechanisms.arm.ArmConfig;

public class ArmCmds {
    public static void setupDefaultCommand() {
        // possible command to have manual control by default
        // Robot.arm.setDefaultCommand(new RunCommand(() -> Robot.arm.setSpeed(Robot.operatorGamepad.getArmInput())));
        Robot.arm.setDefaultCommand(new RunCommand(() -> Robot.arm.stopMotors()));
    }

    // if needed for testing, un-comment
    // public static Command armByPilotCmd() {
    //     return new RunCommand(
    //         () -> Robot.arm.setSpeed(Robot.pilotGamepad.getRightStickY()), Robot.arm)
    //     .withName("Arm By Pilot Cmd");
    // }
    // ------------- Arm Motion Commands -------------
    public static Command ArmByJoystickCmd() {
        return new RunCommand(
        // () -> Robot.arm.adjustMMTarget(() -> Robot.operatorGamepad.getArmInput()), Robot.arm);
        () -> Robot.arm.setSpeedSafe(Robot.operatorGamepad.getArmInput()), Robot.arm);
    }

    public static Command stopArmCmd() {
        return new InstantCommand(() -> Robot.arm.stopMotors(), Robot.arm)
        .withName("Stop Arm Cmd");
    }

    public static Command armToAngleCmd(double angle) {
        // return new InstantCommand(() -> Robot.arm.setMMAngle(angle), Robot.arm);
        return new ArmDrivetoPositionCmd(angle, 10);
    }

    public static Command armToStowCmd() {
        // return new InstantCommand(() -> Robot.arm.setMMAngle(ArmConfig.armStowAngle), Robot.arm);
        return new ArmDrivetoPositionCmd(ArmConfig.armStowAngle, 10);
    }

    public static Command armToShooterCmd() {
        // return new InstantCommand(() -> Robot.arm.setMMAngle(ArmConfig.armShooterAngle), Robot.arm);
        return new ArmDrivetoPositionCmd(ArmConfig.armShooterAngle, 10);
    }

    public static Command armToGroundCmd() {
        // return new InstantCommand(() -> Robot.arm.setMMAngle(ArmConfig.armGroundAngle), Robot.arm);
        return new ArmDrivetoPositionCmd(ArmConfig.armGroundAngle, 10);
    }

    public static Command armToAmpCmd() {
        // return new InstantCommand(() -> Robot.arm.setMMAngle(ArmConfig.armAmpAngle), Robot.arm);
        return new ArmDrivetoPositionCmd(ArmConfig.armAmpAngle, 10);
    }

    public static Command armToTrapCmd() {
        // return new InstantCommand(() -> Robot.arm.setMMAngle(ArmConfig.armTrapAngle), Robot.arm);
        return new ArmDrivetoPositionCmd(ArmConfig.armTrapAngle, 10);
    }

    public static Command armToSafeTravelCmd() {
        // return new InstantCommand(() -> Robot.arm.setMMAngle(ArmConfig.armSafeToTravelAngle), Robot.arm);
        return new ArmDrivetoPositionCmd(ArmConfig.armSafeToTravelAngle, 10);
    }
}
