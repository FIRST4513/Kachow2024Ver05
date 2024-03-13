package frc.robot.mechanisms.pivot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
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
            new InstantCommand(() -> Robot.pivot.setNewPivotState(PivotState.MANUAL), Robot.pivot)
        );
    }

    public static Command setToTargetCmd() {
        return new InstantCommand(() -> Robot.pivot.setNewPivotState(PivotState.TO_TARGET), Robot.pivot);
    }

    public static Command setHPIntakeCmd() {
        return setTargetAndRunCmd(PivotConfig.HP_INTAKE_ANGLE, 0.5);
    }

    public static Command setTargetAndRunCmd(double newTargetAngle, double newPwr) {
        return new SequentialCommandGroup(
            new InstantCommand(() -> Robot.pivot.setNewEncoderAngle(newTargetAngle, newPwr)),
            setToTargetCmd()
        );
    }

    public static Command setLowAndRunCmd() {
        return new SequentialCommandGroup(
            setTargetAndRunCmd(55, 0.75),
            new WaitUntilCommand(() -> Robot.pivot.isStopped()),
            setTargetAndRunCmd(45, 0.25)
        );
    }

    public static Command setHighAndRunCmd() {
        return new SequentialCommandGroup(
            setTargetAndRunCmd(200, 0.75),
            new WaitUntilCommand(() -> Robot.pivot.isStopped()),
            setTargetAndRunCmd(195, 0.1)
        );
    }
    // public static Command setMidAndRunCmd() { return setTargetAndRunCmd(PivotConfig.PIVOT_MID_ANGLE); }
    // public static Command setHighAndRunCmd() { return setTargetAndRunCmd(PivotConfig.PIVOT_MAX_ANGLE); }
}
