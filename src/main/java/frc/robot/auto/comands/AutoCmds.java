package frc.robot.auto.comands;

import java.util.List;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.path.GoalEndState;
import com.pathplanner.lib.path.PathConstraints;
import com.pathplanner.lib.path.PathPlannerPath;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Robot;
import frc.robot.XBoxCtrlrs.operator.commands.OperatorGamepadCmds;
import frc.robot.auto.Auto;
import frc.robot.mechanisms.shooter.commands.ShooterCmds;

public class AutoCmds {

    // ------------------------------------ Do Nothing ---------------------------------------
    public static Command DoNothingCmd() {
        // Set position and Gyro Heading based on position
        return new SequentialCommandGroup(
            new InstantCommand(() -> System.out.println("do nothing cmd")),
            new InstantCommand(() -> Auto.setStartPose())
        );
    }

    // ----------------------------- Speaker Shoot Commands ----------------------------------
    public static Command SpeakerShootCmd() {
        return new SequentialCommandGroup(  
                new InstantCommand(() -> System.out.println("Auto Shoot Speaker")),
                // add commands here to shoot 
               ShooterCmds.shooterSetSpeakerCmd(),
                new WaitCommand(2.0f),
                ShooterCmds.stopShooterCmd()
            );
        }

    // ----------------------------- Cross Line Commands -------------------------------------
    public static Command CrossLineOnlyCmd( String pathName ) {
        return new SequentialCommandGroup(
                new InstantCommand( ()-> Robot.print( "Auto Shoot Left Speaker with path " + pathName)),
                initAndFollowPath( pathName )
                // lock wheels
        );
    }

    // ------------------------- One Note and Cross Line Only --------------------------------
    public static Command ShootAndCrossCmd(String pathName ) {
        return new SequentialCommandGroup(  
            SpeakerShootCmd(),
            CrossLineOnlyCmd( pathName)
        );    
    }

    // ----------------------------------- Two Note ------------------------------------------
    public static Command TwoNoteCmd(String pathName, String pathNameBack ) {
        return new SequentialCommandGroup(  
            new InstantCommand( ()-> Robot.print( "Two Note Cmd ")),
            SpeakerShootCmd(),
            // Intake to ground and on on Cmd here
            initAndFollowPath(pathName),
            // Intake to shoot position
            followPath(pathNameBack),
            SpeakerShootCmd()
            // Intake to store pos cmd
        );    
    }


    // ----- PathPlanning-Interfacing Methods -----

    public static Command followPath(PathPlannerPath path) {
        return AutoBuilder.followPath(path);                                    // Return Cmd controller to run Path
    }

    // Get a Command that Follows a Path
    public static Command followPath(String name) {
        PathPlannerPath path = PathPlannerPath.fromPathFile(name);              // Get Path
        return followPath(path);                                                // Return Cmd controller to run Path
    }

    public static Command initAndFollowPath(String name) {
        // Init Robot pose from auto Selections. We can't pull initial Pose from path because we 
        // only build blue paths, and then rely on AutoBuilder to flip path at run time as needed for red.
        Robot.print("Loading path name: " + name);
        return new SequentialCommandGroup(
            new InstantCommand(() -> Auto.setStartPose()),      // Init Robot Pose on Field                 
            followPath(name)                                    // Run Path
        );
    }

    // Build a path from current position to a new position and then run the path.  If this is only run duringteleop we 
    // dont need the line for preventflippin true. this is automatically handled in the AutoBuilder.
    // If this is only used in teleop we should move this method to drivercmds not here in autocmds
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
}
