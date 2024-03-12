package frc.robot.vision;
import java.io.IOException;
import java.util.List;

import org.littletonrobotics.junction.Logger;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonUtils;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Robot;
import frc.util.PoseAndTimestamp;

public class VisionSubSys extends SubsystemBase {
    // Objects representing physical cameras, pose estimation results, and field april tag info
    protected VisionPhotonCamera   cameras[] = new VisionPhotonCamera[VisionConfig.cameraTransforms.length];

    protected PhotonCamera frontCamera = new PhotonCamera("Front Camera");

    protected AprilTagFieldLayout  aprilTagFieldLayout;

    // The PoseAndTimestamp holds ( <Pose3d> pose, <double> timestamp (latency), <boolean> isNew )
    protected PoseAndTimestamp robotPoseAndTimestamp =     new PoseAndTimestamp(new Pose3d(), 0, false);
    protected PoseAndTimestamp lastRobotPoseAndTimestamp = new PoseAndTimestamp(new Pose3d(), 0, false);

    protected int     bestCameraID = -1;
    protected int     bestTagID = 0;
    protected double  bestTagAmbiguity = -1;

   
    // Constructor for PhotonVision to proccess all cameras to determine Current Robot Pose on the Field
    public VisionSubSys() {
        loadAprilTagFieldLayout();

        // Instantiate Array of all the cameras.
        cameras[0] =  new VisionPhotonCamera(VisionConfig.frontCamName,
                                             VisionConfig.frontCamToRobotTrsfm, aprilTagFieldLayout);
        // cameras[1] =  new VisionPhotonCamera(VisionConfig.backLeftCamName,
        //                                      VisionConfig.backLeftCamToRobotTrsfm, aprilTagFieldLayout);

        updatePose(new Pose3d(), 0, false);     // Initialize the PoseAndTimestamps     to basically 0
        storeLastPose(robotPoseAndTimestamp);   // Initialize the LasePoseAndTimestamps to basically 0
    }

    // look for April Tag and Calculate an updated Robot Pose on the Field
    @Override
    public void periodic() {
        processVision();

        // Pose3d exRobotCenterPose = new Pose3d(100, 100, 0, new Rotation3d(0, 0, 0));
        // Robot.print("Example pose at robot center: x(" + exRobotCenterPose.getX() + ") y(" + exRobotCenterPose.getY() + ") z(" + exRobotCenterPose.getZ() + ") yaw(" + exRobotCenterPose.getRotation().getZ());
        // Pose3d exCameraPose = exRobotCenterPose.transformBy(VisionConfig.frontCamToRobotTrsfm);
        // Robot.print("Example pose at camera: x(" + exCameraPose.getX() + ") y(" + exCameraPose.getY() + ") z(" + exCameraPose.getZ() + ") yaw(" + exCameraPose.getRotation().getZ());
    }

    /* This is where the work gets done !!!!:
    
            This method will iterate through each individual camera's results looking for the best April Tag target
            and updating the camera's individual best results file. 
            This method then iterates through these best April tags to determine the best one overall for all the cameras.
            This is then used to calculate a best estimate of Robot Pose on the field. 
            This estimated Robot position is then checked for resonablenace.

            If all is good then a NEW updated PoseTimestamp object is recorded. Otherwise the the last valid estimate
            is used.
            
            A synchronized method updates this object's values, and a synchronized method is used to get this object.
            This object can then be used with the robots pose/odometry methods to update it's position on field.
        
            NOTE: For accuracy's sake, make sure that- when applying the pose to odometry we only use vision to update position
            when the isNew flag is true, for fresh data.
    */

    public void processVision() {

        // Step 1 - Let each camera acquire new current data
        for (int i = 0; i < VisionConfig.cameraTransforms.length; i++) {
            cameras[i].getUpdatedPhotonPoseEstimate();
        }

        // Step 2   - Find camera with best tag data (lowest ambiguity)
        bestTagAmbiguity = 1000;
        bestTagID = 0;
        bestCameraID = -1;
        for (int i = 0; i < cameras.length; i++) {
            if (cameras[i].ambiguity < bestTagAmbiguity){
                bestTagAmbiguity = cameras[i].ambiguity;
                bestCameraID = i;
                bestTagID = cameras[i].aprilTagID;
            }
        }

        // Step 2.3 - If we DONT have a valid tag. We can get out
        if ( ( bestCameraID < 0) || 
             ( !aprilTagFieldLayout.getTagPose(bestTagID).isPresent()) ||
             ( bestTagAmbiguity > 0.3) ) {
                // No valid targets found, were done store the last valid pose and get out
                updatePose(lastRobotPoseAndTimestamp, false ); 
                logPoseEst(robotPoseAndTimestamp,bestTagID );
                return;
        }

        // Step 3 - We have a good Tag , lets Calculate transforms to Create a Robot Pose Estimate
        Transform3d bestCamToTagTrsfm = cameras[bestCameraID].cameraToTagTrsfm;
        Pose3d robotPose3d = PhotonUtils.estimateFieldToRobotAprilTag(
                                         bestCamToTagTrsfm,
                                         aprilTagFieldLayout.getTagPose(bestTagID).get(),
                                         VisionConfig.cameraTransforms[bestCameraID] );

        // Step 4 - Check for Resonablence and Send it to drivetrain to update odometry
        if (checkForVisionUpdateValid(robotPose3d) ){
            // Target Pose is resonable - Lets store the new Estimate
            updatePose(robotPose3d, cameras[bestCameraID].timestamp, true );
            storeLastPose(robotPoseAndTimestamp);

            // Different approach. Instead of saving value and letting odometry look it uo. We just send the updsate
            // to the drivetrain and let it send it to odometry for update.

            //System.out.println("Vision sending to Odom x=" + robotPose3d.getX() +" Y=" + robotPose3d.getY());
            if (robotPoseAndTimestamp.isNew) {
                Robot.swerve.updateOdometryVisionPose( robotPose3d.toPose2d(),  cameras[bestCameraID].timestamp);
                robotPoseAndTimestamp.isNew = false;
            }

        }   else {
            // Target Pose in NOT resonable lets leave as last estimate
                updatePose(lastRobotPoseAndTimestamp, false ); 
        }

        logPoseEst(robotPoseAndTimestamp, bestTagID);     
    }


    public synchronized Pose3d getSpeakerTag(int tagId){
        Pose3d tgtPose = new Pose3d();
        var results = frontCamera.getLatestResult();
        if (results.hasTargets()) {
            // Find the tag we want to chase
            var targetOpt = results.getTargets().stream()
                .filter(t -> t.getFiducialId() == tagId)
                .filter(t -> t.getPoseAmbiguity() <= .3 && t.getPoseAmbiguity() != -1)
                .findFirst();
            if (targetOpt.isPresent()) {
                var target = targetOpt.get();

                // get best target 
                // double xDist = target.getBestCameraToTarget().getX();
                // Robot.print("X-Dist to speaker tag " + tagId + ": " + Units.metersToInches(xDist));

                // Transform the robot's pose to find the camera's pose
                Pose3d robotPose = new Pose3d();
                var cameraPose = robotPose.transformBy(VisionConfig.frontRobotToCamTrsfm);
        
                // Transform the camera's pose to the target's pose
                var camToTarget = target.getBestCameraToTarget();
                tgtPose = cameraPose.transformBy(camToTarget);
                // Robot.print("Tgt Pose: x(" + Units.metersToInches(tgtPose.getX()) + "), y(" + Units.metersToInches(tgtPose.getY()) + "), rot(" + tgtPose.getRotation().toRotation2d().getDegrees() + ")");
            }
        }
        return tgtPose;

    }

    // ---------------------------------------------------------------------------------
    // Synchronized for thread safe access. Dont want get fractured data in PoseData

    public synchronized PoseAndTimestamp getVisionPoseEst( ) {
         return robotPoseAndTimestamp;
        }

    public synchronized PoseAndTimestamp getVisionPoseEstWithConsume( ) {
        PoseAndTimestamp tempPoseTS = robotPoseAndTimestamp;
        robotPoseAndTimestamp.isNew = false;    // mark as consumed
        return tempPoseTS;
        }

    public synchronized void consumePoseEst() {
        robotPoseAndTimestamp.isNew = false;
        }

    public synchronized void updatePose(Pose3d pose, double timeStamp, boolean isNew){
          robotPoseAndTimestamp = new PoseAndTimestamp(
                pose,
                timeStamp,
                isNew);  // This records as New  
    }

    public synchronized void updatePose(PoseAndTimestamp poseTS, boolean isNew){
          robotPoseAndTimestamp = new PoseAndTimestamp(
                poseTS.pose,
                poseTS.timestamp,
                isNew);  // This records as New  
    }

    public synchronized void storeLastPose(PoseAndTimestamp poseData ){
          lastRobotPoseAndTimestamp = poseData;
    }

    public synchronized void logPoseEst(PoseAndTimestamp poseTS, int tagID) {
        Logger.recordOutput("Vision Tag ID", tagID);
        Logger.recordOutput("Vision RobotPose X", poseTS.getPose().getX());
        Logger.recordOutput("Vision RobotPose Y", poseTS.getPose().getY());
        Logger.recordOutput("Vision RobotPose TS", poseTS.getTimestamp());
        Logger.recordOutput("Vision RobotPose New", poseTS.isNew());
    }



    // ---------------------------------------------------------------------------------
    public boolean checkForVisionUpdateValid(Pose3d pose){

        // Case 1 - Is estimated pose a valid place on the Field
        if (( pose.getX() < 0 ) ||                                // Negative Field location Bad
            ( pose.getY() < 0 ) || 
            ( pose.getZ() < 0 ) ||                                // Below Ground I Don't think so
            ( pose.getX() > Units.inchesToMeters(650.0) ) ||      // Too far off field
            ( pose.getY() > Units.inchesToMeters(350.0) ) ||
            ( pose.getZ() > Units.inchesToMeters(48.0) ))
            {
                return false;
            }

        // Case 2 - Is estimated pose whithin 1 meter of current robot pose on Field
        // double xDifference, yDifference;
        // xDifference = pose.getX() - Robot.swerve.getPose().getX();
        // yDifference = pose.getY() - Robot.swerve.getPose().getY();
        // if (( Math.abs(xDifference) > 1.0 ) ||
        //     ( Math.abs(yDifference) > 1.0 ))
        //     {
        //         return false;
        //     }



        // All above condition are good so lets return TRUE !
        return true;
    }



    // ---------------------- Getters for Vision Telemetry ----------------
    protected int      getBestCamera()                             { return bestCameraID; }
    protected int      getBestTagId()                              { return bestTagID; }
    protected double   getBestAmbiguity()                          { return bestTagAmbiguity; }


    //  Load Field Data ( Contains all April Tag Pose data for the 2024 Comeptition )
    protected void loadAprilTagFieldLayout() {
        try {
            aprilTagFieldLayout = 
                    AprilTagFieldLayout.loadFromResource(AprilTagFields.k2024Crescendo.m_resourceFile);
            System.out.println("We have loaded the April Tag Field Layout");
        } catch (IOException e) {
            System.out.println("ERROR: We cAN not load the April Tag Field Layout");
            e.printStackTrace();
        }
    }

}
