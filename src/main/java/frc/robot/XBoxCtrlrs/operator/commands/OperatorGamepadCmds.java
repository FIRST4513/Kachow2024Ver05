package frc.robot.XBoxCtrlrs.operator.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import frc.robot.Robot;
import frc.robot.mechanisms.exampleElevator.ElevatorCmds;
import frc.robot.mechanisms.intake.commands.IntakeCmds;
import frc.robot.mechanisms.passthrough.commands.PassthroughCmds;
import frc.robot.mechanisms.pivot.commands.PivotCmds;
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
            ShooterCmds.setManualCmd(),
            PivotCmds.setManualCmd()
        );
    }

    public static Command stopAllCmd() {
        return new SequentialCommandGroup(
            IntakeCmds.intakeStopCmd(),
            PassthroughCmds.stopPassthroughCmd(),
            ShooterCmds.stopCmd(),
            PivotCmds.stopCmd(),
            ElevatorCmds.stopElevator()
        );
    }

    /* ----- Combo Commands ----- */
    // public static Command hpIntakeUntilGamepiece() {
    //     return new SequentialCommandGroup(
    //         PassthroughCmds.setHPIntakeCmd(),  
    //         PivotCmds.setHPIntakeCmd(),
    //         new WaitUntilCommand(() -> Robot.pivot.isAtTarget()),
    //         ShooterCmds.setHPIntakeCmd(),
    //         new WaitUntilCommand(() -> Robot.intake.getGamepieceDetected()),
    //         stopAllCmd()
    //     );
    // }

    // public static Command groundIntakeUntilGamepieceCmd() {
    //     return new SequentialCommandGroup(
    //         PassthroughCmds.setGroundIntakeCmd(),
    //         IntakeCmds.intakeSetGroundCmd(),
    //         new WaitUntilCommand(() -> Robot.intake.getGamepieceDetected()),
    //         new WaitCommand(0.15),
    //         stopAllCmd(),
    //         new WaitCommand(0.25),
    //         PassthroughCmds.setHPIntakeCmd(),
    //         new WaitUntilCommand(() -> Robot.intake.getGamepieceDetected()),
    //         stopAllCmd()
    //     );
    // }

    public static Command readyForBumperShotCmd() {
        return new ParallelCommandGroup(
            PivotCmds.setLowAndRunCmd(),
            ShooterCmds.setSpeakerSpeedCmd()
        );
    }

    public static Command readyForFarShotCmd() {
        return new ParallelCommandGroup(
            PivotCmds.setHighAndRunCmd(),
            ShooterCmds.setSpeakerSpeedCmd()
        );
    }

    // public static Command noAutoPosSpeakerShot() {
    //     return new SequentialCommandGroup(
    //         ShooterCmds.setSpeakerSpeedCmd(),
    //         new ParallelRaceGroup(
    //             new WaitUntilCommand(() -> Robot.shooter.areMotorsAtVelocityTarget()),
    //             new WaitCommand(0.5)
    //         ),
    //         PassthroughCmds.setEjectCmd(),
    //         new WaitCommand(0.75),
    //         stopAllCmd()
    //     );
    // }

    // -------------------- Rumble Controller  ---------------
    public static Command RumbleOperatorCmd(double intensity) {
        return new RunCommand(() -> Robot.operatorGamepad.rumble(intensity), Robot.operatorGamepad);
    }
}
