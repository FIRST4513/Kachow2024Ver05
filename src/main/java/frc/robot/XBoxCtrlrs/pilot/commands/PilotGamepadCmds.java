package frc.robot.XBoxCtrlrs.pilot.commands;

import java.util.List;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.path.GoalEndState;
import com.pathplanner.lib.path.PathConstraints;
import com.pathplanner.lib.path.PathPlannerPath;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Robot;
import frc.robot.auto.Auto;
import frc.robot.auto.comands.AutoCmds;
import frc.robot.drivetrain.commands.SwerveDriveCmd;
import frc.robot.mechanisms.shooter.commands.ShooterCmds;

/** Add your docs here. */
public class PilotGamepadCmds {

    /** Set default command to turn off the rumble */
    public static void setupDefaultCommand() {
        Robot.pilotGamepad.setDefaultCommand(RumblePilotCmd(0));
    }

    // ------------- Drive by TeleOp Commands ---------------

        // Drive to pose command
        public static Command driveToPose(Pose2d targetPose) {
        Pose2d currentPose = Robot.swerve.getPose();
        List<Translation2d> bezierPoints = PathPlannerPath.bezierFromPoses(currentPose, targetPose);
        PathPlannerPath path = new PathPlannerPath(bezierPoints,
                                            new PathConstraints(
                                                3.0,                            // Max Velocity MPS
                                                3.0,                            // Max Accel MPSS
                                                Units.degreesToRadians(180),    // Max Rotation Rate Rad/S
                                                Units.degreesToRadians(180)),   // Max Rot Acel Rad/SS
                                            new GoalEndState(
                                                0.0,                            // End State Vel MPS
                                                currentPose.getRotation()));    // End State Rotation
        path.preventFlipping = true;        // Don't let AutoBuidler invert this path ! (we could be blue or red)
        return AutoBuilder.followPath(path);
    }

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
            ShooterCmds.shooterSetSpeakerCmd(),
            new WaitCommand(1),
            ShooterCmds.stopShooterCmd(),
            AutoCmds.initAndFollowPath("Blueback1m"),
            new WaitCommand(3),
            AutoCmds.followPath("Bluefwd1m"),
            ShooterCmds.shooterSetSpeakerCmd(),
            new WaitCommand(1),
            ShooterCmds.stopShooterCmd()
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
