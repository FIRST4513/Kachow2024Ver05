package frc.util;

import com.pathplanner.lib.path.PathPlannerPath;
import com.pathplanner.lib.path.PathPlannerTrajectory;
import com.pathplanner.lib.path.PathPoint;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;

public class PathTrajectories {
    // Setup Needed PathPlaner Paths
    public static ChassisSpeeds startingStoppedSpeed = new ChassisSpeeds(0,0,0);
    
    public static PathPlannerPath Blueback1mPath = PathPlannerPath.fromPathFile("Blueback1m");

    
    //public static PathPlannerTrajectory BluebTraj = Blueback1mPath.getTrajectory( startingStoppedSpeed,
    //                                                                               new Rotation2d(180));
    // PathPoint pt = Blueback1mPath.getPoint(0);

    // Translation2d pose = pt.position;
    // Pose2d pose2 = Blueback1mPath.getPreviewStartingHolonomicPose();
    // PathPlannerState s = (PathPlannerState) path.getStates().get(0);    // Starting pose                                                            
    
    // Blueback1mPath.getStates().get(0);
}
