package frc.robot.mechanisms.shooter.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Robot;
import frc.robot.mechanisms.shooter.ShooterSubSys.FireState;

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
    public static Command stopCmd() {
        return new SequentialCommandGroup(
            new InstantCommand(() -> Robot.shooter.stopMotors(), Robot.shooter)
        );
    }

    public static Command setManualCmd() {
        return new SequentialCommandGroup(
            new InstantCommand(() -> Robot.shooter.setNewFireState(FireState.MANUAL))
        );
    }

    public static Command setHPIntakeCmd() {
        return new SequentialCommandGroup(
            new InstantCommand(() -> Robot.shooter.setNewFireState(FireState.HP_INTAKE))
        );
    }

    public static Command setSpeakerSpeedCmd() {
        return new InstantCommand(() -> Robot.shooter.setNewFireState(FireState.SPEAKER));
    }
}
