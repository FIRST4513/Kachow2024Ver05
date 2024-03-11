package frc.robot.mechanisms.pivot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Robot;
import frc.robot.mechanisms.pivot.PivotConfig;
import frc.robot.mechanisms.pivot.PivotSubSys.PivotState;

public class PivotCmds {
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
            new InstantCommand(() -> Robot.pivot.setNewPivotState(PivotState.STOPPED), Robot.pivot)
        );
    }

    public static Command setManualCmd() {
        return new SequentialCommandGroup(
            new InstantCommand(() -> Robot.pivot.setNewPivotState(PivotState.MANUAL))
        );
    }

    public static Command setHPIntakeCmd() {
        return new SequentialCommandGroup(
            new InstantCommand(() -> Robot.pivot.setNewShooterAngle(PivotConfig.HP_INTAKE_ANGLE)),
            new InstantCommand(() -> Robot.pivot.setNewPivotState(PivotState.MANUAL))
        );
    }
}
