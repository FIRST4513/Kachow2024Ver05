package frc.robot.mechanisms.shooter.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Robot;
import frc.robot.mechanisms.shooter.ShooterSubSys.FireState;
import frc.robot.mechanisms.shooter.ShooterSubSys.PivotState;

public class ShooterCmds {
    public static void setupDefaultCommand() {
        // Robot.shooter.setDefaultCommand(shooterByJoyCmd());
        stopShooterCmd();
    }

    // ----- Shooter Stop -----
    public static Command stopShooterCmd() {
        return new InstantCommand(() -> Robot.shooter.stopMotors());
    }

    // ----- Shooter Set State -----
    public static Command shooterSetStateCmd(FireState newState) {
        return new InstantCommand(() -> Robot.shooter.setNewFireState(newState));
    }
    
    // ----- Shooter Set State Shortcuts -----
    public static Command shooterSetSpeakerCmd() {
        return shooterSetStateCmd(FireState.SPEAKER);
    }

    public static Command shooterSetHPIntakeCmd() {
        return shooterSetStateCmd(FireState.HP_INTAKE);
    }

    public static Command shooterSetManualCmd() {
        return shooterSetStateCmd(FireState.MANUAL);
    }

    // ----- Pivot Stop -----
    public static Command stopPivotCmd() {
        return new InstantCommand(() -> Robot.shooter.setNewPivotState(PivotState.STOPPED));
    }

    // ----- Other Pivot Cmds -----
    public static Command pivoitSetManualCmd() {
        return new InstantCommand(() -> Robot.shooter.setNewPivotState(PivotState.MANUAL));
    }

    public static Command pivotSetToTargetCmd() {
        return new InstantCommand(() -> Robot.shooter.setNewPivotState(PivotState.TO_TARGET));
    }
}
