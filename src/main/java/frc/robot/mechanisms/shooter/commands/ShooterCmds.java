package frc.robot.mechanisms.shooter.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Robot;
import frc.robot.mechanisms.shooter.ShooterSubSys.FireState;

public class ShooterCmds {
    public static void setupDefaultCommand() {
        // Robot.shooter.setDefaultCommand(shooterByJoyCmd());
        stopShooterCmd();
    }

    // ----- Intake Stop -----
    public static Command stopShooterCmd() {
        return new InstantCommand(() -> Robot.shooter.stopMotors());
    }

    public static Command shooterSetStateCmd(FireState newState) {
        return new InstantCommand(() -> Robot.shooter.setNewState(newState));
    }

    public static Command shooterSetAmpCmd() {
        return shooterSetStateCmd(FireState.AMP);
    }
    
    public static Command shooterSetSpeakerCmd() {
        return shooterSetStateCmd(FireState.SPEAKER);
    }

    public static Command shooterSetRetractCmd() {
        return shooterSetStateCmd(FireState.RETRACT);
    }

    public static Command shooterSetManualCmd() {
        return shooterSetStateCmd(FireState.MANUAL);
    }

    // ---- TEST COMMAND
    public static Command ampWhileHeld() {
        return new SequentialCommandGroup(
            // set shooter to amp
            shooterSetAmpCmd(),
            // wait until timeout or interrupted to stop shooter?
            // new WaitCommand(5).finallyDo(() -> stopShooterCmd())
            new WaitCommand(2),
            stopShooterCmd()
        );
    }
}
