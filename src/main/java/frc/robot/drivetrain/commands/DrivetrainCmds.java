package frc.robot.drivetrain.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.Robot;
import frc.robot.XBoxCtrlrs.pilot.commands.PilotGamepadCmds;

public class DrivetrainCmds {
    public static void setupDefaultCommand() {
        Robot.swerve.setDefaultCommand(PilotGamepadCmds.FpvPilotSwerveCmd());
    }

    // ----------------------- Gyro & Odometry Commands  ------------------------
    public static Command ZeroGyroHeadingCmd() {
        return new InstantCommand( () -> Robot.swerve.zeroGyroHeading() );
    }

    public static Command SetGyroYawCmd(double deg){
        return new InstantCommand( () -> Robot.swerve.setGyroHeading(deg) );
    }

    // public static Command IntializeGyroAngleCmd(PathPlannerTrajectory path){
    //     PathPlannerState s = (PathPlannerState) path.getStates().get(0);    // Starting pose
    //     return SetGyroYawCmd(s.holonomicRotation.getDegrees());             // Set Gyro to Starting holonomicRotation Hdg +-180
    // }

    // public static Command ResetOdometryCmd(PathPlannerTrajectory path){
    //     Pose2d tempPose = path.getInitialPose();
    //     PathPlannerState s = (PathPlannerState) path.getStates().get(0) ;
    //     Pose2d tempPose2 = new Pose2d(tempPose.getTranslation(), s.holonomicRotation) ;
    //     return new InstantCommand(() -> Robot.swerve.resetOdometry(tempPose2));
    // }
}
