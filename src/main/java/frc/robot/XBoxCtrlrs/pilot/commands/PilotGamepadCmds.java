package frc.robot.XBoxCtrlrs.pilot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Robot;
import frc.robot.XBoxCtrlrs.operator.commands.OperatorGamepadCmds;
import frc.robot.auto.Auto;
import frc.robot.auto.comands.AutoCmds;
import frc.robot.drivetrain.commands.SwerveDriveCmd;
import frc.robot.mechanisms.shooter.commands.ShooterAimAndFireCmd;
import frc.robot.mechanisms.shooter.commands.ShooterCmds;

/** Add your docs here. */
public class PilotGamepadCmds {

    /** Set default command to turn off the rumble */
    public static void setupDefaultCommand() {
        Robot.pilotGamepad.setDefaultCommand(RumblePilotCmd(0));
    }

    // ------------- Drive by TeleOp Commands ---------------

    /** Field Oriented Drive */
    public static Command FpvPilotSwerveCmd() {
        return new SwerveDriveCmd(
                        () -> Robot.pilotGamepad.getDriveFwdPositive(),
                        () -> Robot.pilotGamepad.getDriveLeftPositive(),
                        () -> Robot.pilotGamepad.getDriveRotationCCWPositive(),
                        true,
                        false)
                .withName("FpvPilotSwerveCmd");
    }

    /** Robot Oriented Drive */
    public static Command RpvPilotSwerveCmd() {
        return new SwerveDriveCmd(
                        () -> Robot.pilotGamepad.getDriveFwdPositive(),
                        () -> Robot.pilotGamepad.getDriveLeftPositive(),
                        () -> Robot.pilotGamepad.getDriveRotationCCWPositive(),
                        // () -> Robot.pilotGamepad.getDriveFwdPositiveSlow(),
                        // () -> Robot.pilotGamepad.getDriveLeftPositiveSlow(),
                        // () -> Robot.pilotGamepad.getDriveRotationCCWPositiveSlow(),
                        false,
                        false)
                .withName("RpvPilotSwerveCmd");
    }






    // ---- test aut0 cdsnlfeafgeabjksge
    public static Command Blueback1mPathCmd() {
        return AutoCmds.initAndFollowPath("Blueback1m");
    }

    public static Command BluefwdPathCmd() {
        return AutoCmds.initAndFollowPath("Bluefwd1m");
    }

    public static Command BluebackandfwdCmd() {
        return new SequentialCommandGroup(
            new ShooterAimAndFireCmd(2),
            AutoCmds.initAndFollowPath("Blueback1m"),
            OperatorGamepadCmds.groundIntakeUntilGamepieceCmd(),
            AutoCmds.followPath("Bluefwd1m"),
            new ShooterAimAndFireCmd(2),
            OperatorGamepadCmds.stopAllCmd()
        );
    }

    // public static Command driveToCentralSpeaker() {
    //     return AutoCmds.driveToPose()
    // }

    // public static Command ToggleBasedSwerveCmd() {
    //     return new ConditionalCommand(
    //         RpvPilotSwerveCmd(),
    //         FpvPilotSwerveCmd(),
    //         () -> Robot.swerve.getPilotPOV());
    // }

    // public static Command TogglePOV() {
    //     return new InstantCommand(() -> Robot.swerve.togglePilotPOV());
    // }

    /** Field Oriented Drive With Auto Rotate to Snapped 90 oreintation */
    // public static Command FpvDriveAndAutoRotateCmd() {
    //     return new TurnToAngleCmd(
    //                     () -> Robot.pilotGamepad.getDriveFwdPositive(),
    //                     () -> Robot.pilotGamepad.getDriveLeftPositive(),
    //                     () -> Robot.swerve.getSnap90Angle())
    //             .withName("FpvDriveAndAutoRotateCmd")
    //             .withTimeout(5.0);
    // }

    // /** Static (Stationary) Snap to 90 oreintation */
    // public static Command BasicSnapCmd() {
    //     return new TurnToAngleCmd(() -> Robot.swerve.getSnap90Angle())
    //         .withName("BasicSnapCmd")
    //         .withTimeout(5.0);
    // }

    
    
    // -------------------- Rumble Controller -------------

    public static Command RumblePilotCmd(double intensity) {
        return new RunCommand(() -> Robot.pilotGamepad.rumble(intensity), Robot.pilotGamepad);
    }
}
