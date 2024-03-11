package frc.robot.XBoxCtrlrs.operator.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import frc.robot.Robot;
import frc.robot.mechanisms.intake.commands.IntakeCmds;
import frc.robot.mechanisms.passthrough.commands.PassthroughCmds;
import frc.robot.mechanisms.shooter.commands.ShooterCmds;

public class OperatorGamepadCmds {
    /** Set default command to turn off the rumble */
    public static void setupDefaultCommand() {
    //    Robot.pilotGamepad.setDefaultCommand(ProcessAndSetRumbleCmd());
    }

    /* ----- Overrides ----- */
    public static Command manualAllCmd() {
        return new SequentialCommandGroup(
            IntakeCmds.intakeSetManualCmd(),
            PassthroughCmds.setManualCmd(),
            ShooterCmds.setShooterAndPivotManualCmd()
        );
    }

    public static Command stopAllCmd() {
        return new SequentialCommandGroup(
            IntakeCmds.intakeStopCmd(),
            PassthroughCmds.stopPassthroughCmd(),
            ShooterCmds.stopShooterAndPivotCmd()
        );
    }

    /* ----- Combo Commands ----- */
    public static Command hpIntakeUntilGamepiece() {
        return new SequentialCommandGroup(
            PassthroughCmds.setHPIntakeCmd(),  
            ShooterCmds.setShooterAndPivotHPIntakeCmd(),
            new WaitUntilCommand(() -> Robot.passthrough.getGamepieceDetected()),
            stopAllCmd()
        );
    }

    public static Command groundIntakeUntilGamepieceCmd() {
        return new SequentialCommandGroup(
            PassthroughCmds.setGroundIntakeCmd(),
            IntakeCmds.intakeSetGroundCmd(),
            new WaitUntilCommand(() -> Robot.passthrough.getGamepieceDetected()),
            stopAllCmd()
        );
    }

    // -------------------- Rumble Controller  ---------------
    public static Command RumbleOperatorCmd(double intensity) {
        return new RunCommand(() -> Robot.operatorGamepad.rumble(intensity), Robot.operatorGamepad);
    }
}
