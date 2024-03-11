package frc.robot.mechanisms.shooter.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Robot;
import frc.robot.mechanisms.shooter.ShooterConfig;
import frc.robot.mechanisms.shooter.ShooterSubSys.FireState;
import frc.robot.mechanisms.shooter.ShooterSubSys.PivotState;

public class ShooterCmds {
    public static void setupDefaultCommand() {
        // Robot.shooter.setDefaultCommand(shooterByJoyCmd());
    }

    /* ----- Shooter And Pivot Combo Methods ----- */
    // stop      | shooter: STOPPED   | pivot: STOPPED
    // manual    | shooter: MANUAL    | pivot: MANUAL
    // hp intake | shooter: HP_INTAKE | pivot: TO TARGET [0]
    // speaker   | shooter: SPEAKER   | pivot: TO TARGET [º from vision]

    // Robot.shooter subsystem requirement should stop any running ShooterAimAndFireCmd
    public static Command stopShooterAndPivotCmd() {
        return new SequentialCommandGroup(
            new InstantCommand(() -> Robot.shooter.stopMotors(), Robot.shooter),
            new InstantCommand(() -> Robot.shooter.setNewPivotState(PivotState.STOPPED), Robot.shooter)
        );
    }

    public static Command setShooterAndPivotManualCmd() {
        return new SequentialCommandGroup(
            new InstantCommand(() -> Robot.shooter.setNewFireState(FireState.MANUAL)),
            new InstantCommand(() -> Robot.shooter.setNewPivotState(PivotState.MANUAL))
        );
    }

    public static Command setShooterAndPivotHPIntakeCmd() {
        return new SequentialCommandGroup(
            new InstantCommand(() -> Robot.shooter.setNewFireState(FireState.HP_INTAKE)),
            new InstantCommand(() -> Robot.shooter.setNewTargetAngle(ShooterConfig.HP_INTAKE_ANGLE)),
            new InstantCommand(() -> Robot.shooter.setNewPivotState(PivotState.MANUAL))
        );
    }

    public static Command setShooterAndPivotAutoSpeakerCmd() {
        return new SequentialCommandGroup(
            new InstantCommand(() -> Robot.shooter.setNewFireState(FireState.HP_INTAKE)),
            new InstantCommand(() -> Robot.shooter.setNewTargetAngle(ShooterConfig.HP_INTAKE_ANGLE)),
            new InstantCommand(() -> Robot.shooter.setNewPivotState(PivotState.MANUAL))
        );
    }
}
