package frc.robot.vision;

import java.util.List;
import java.util.Optional;
import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonPoseEstimator.PoseStrategy;
import org.photonvision.targeting.PhotonTrackedTarget;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Transform3d;
import frc.robot.Robot; 
import frc.util.PoseAndTimestamp;

public class VisionPhotonCamera {
    private PhotonCamera        camera;
    private String              name;

    private PhotonPoseEstimator photonPoseEstimator;
    public Optional <EstimatedRobotPose> photonEstPoseOpt;      // Object containing optional  Pose,time stamp,other data from above
    public Pose3d photonEstRobotPose;
    public double photonEstPoseTimestamp;
    public boolean photonPoseEstValid;

    private Pose3d              robotPose3d;
    private double              robotPoseTimestamp;
    private PoseAndTimestamp    robotPoseAndTimestamp;

    public List<PhotonTrackedTarget> targetsUsed;       // List of tgts used from photonPoseEstimator
    PhotonTrackedTarget         target;                 // A specific tgt seen from photonPoseEstimator

    private Pose3d              lastRobotPose3d;
    private double              lastRobotPoseTimestamp;
    private PoseAndTimestamp    lastRobotPoseAndTimestamp;
    public List<PhotonTrackedTarget> lastTargetsUsed;

    public  Transform3d         camToRobotTrsfm;
    public  Transform3d         cameraToTagTrsfm;

    // --------------------- Camera Best April Tag Results -------------------- 
    public boolean  hasTarget = false;
    public int      aprilTagID = 0;
    public double   ambiguity  = 0;    // Lowest Abiguity is better
    public double   timestamp  = 0; 

    public double x = 0;            // Forward Distance to Tag (Meters)
    public double y = 0;            // Left/Right Distance of Tag (Meters)
    public double z = 0;            // Above/Below Tag (Meters)
    public double rotation = 0;     // Rotation to Tag

    public double yaw   = 0;        // Angle Left or Right to Tag
    public double pitch = 0;        // Angle Up/DN to Tag

    // Poses on field;
    public Pose3d aprilTagPose      = new Pose3d();
    public Pose3d cameraPose        = new Pose3d();
    public Pose3d robotEstPose      = new Pose3d();
    //---------------------------------------------


    // ---------------------------------- Constructor -------------------------------
    public VisionPhotonCamera(String camName, Transform3d camToRobotTrsfm, AprilTagFieldLayout fieldLayout) {
        
        this.name = camName;
        this.camera = new PhotonCamera(name);
        this.camToRobotTrsfm = camToRobotTrsfm;
        clearCamResults();

        // Setup this cameras Robot Pose Estimators, based on desired Camera Strategy
        switch (VisionConfig.cameraPoseStrategy) {
            case MULTI_TAG:
                photonPoseEstimator = new PhotonPoseEstimator(
                                        fieldLayout,                                  // Poses of all Tags
                                        PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR,    // Look at multiple tags
                                        camera,                                       // Camera doing the vision
                                        camToRobotTrsfm);                             // Transformation3d for Pose calc
                photonPoseEstimator.setMultiTagFallbackStrategy(PhotonPoseEstimator.PoseStrategy.LOWEST_AMBIGUITY);
            case LOWEST_AMBIGUITY:
                photonPoseEstimator = new PhotonPoseEstimator(
                                        fieldLayout,                                  // Poses of all Tags
                                        PoseStrategy.LOWEST_AMBIGUITY,                // Look for lowest ambiguity
                                        camera,                                       // Camera doing the vision
                                        camToRobotTrsfm);                             // Transformation3d for Pose calc
            default:
                photonPoseEstimator = new PhotonPoseEstimator(
                                        fieldLayout,                                  // Poses of all Tags
                                        PoseStrategy.LOWEST_AMBIGUITY,                // default to lowest ambiguity
                                        camera,                                       // Camera doing the vision
                                        camToRobotTrsfm);                             // Transformation3d for Pose calc
        }
    }

    //---------------------- Robot Pose Estimation Method 1 ---------------------------------
    // Call the update method in the "Estimator" to calculate a new robot Pose on the field.


    // for this particular camera, get the target list and process the best target
    // get image, iterate, find best tag for strategy (lowest ambiguity)
    public void getUpdatedPhotonPoseEstimate(){
        photonEstPoseOpt = photonPoseEstimator.update();

        if (photonEstPoseOpt.isPresent()) {
            photonPoseEstValid = true;
            // Update Robot Pose data with new values
            robotPose3d = photonEstPoseOpt.get().estimatedPose; // This is it ! (Robot POSE)
            robotPoseTimestamp = photonEstPoseOpt.get().timestampSeconds;
            robotPoseAndTimestamp = new PoseAndTimestamp(robotPose3d, robotPoseTimestamp, true );
            targetsUsed = photonEstPoseOpt.get().targetsUsed;
            // Save to Last Pose in case next time we cant see the target (Robot appears to freeze at last good spot)
            lastRobotPose3d = robotPose3d;
            lastRobotPoseTimestamp = robotPoseTimestamp;
            lastRobotPoseAndTimestamp = robotPoseAndTimestamp;
            lastTargetsUsed = targetsUsed;
            proccessCamResults(targetsUsed);     // Save various stats
        } else {
            // We dont have a new image lets use our last known spot (frozen at last spot) but reflect this is NOT New
            photonPoseEstValid = false;                               // No Tagas seen
            photonEstRobotPose = lastRobotPose3d;                     // Robot Pose set to 0,0,0
            photonEstPoseTimestamp = lastRobotPoseTimestamp;
            robotPoseAndTimestamp = lastRobotPoseAndTimestamp;
            // clearCamResults();       // Dont clear out last valid results let it it just stand
        }
    }

    // Update data for cameras best april tag target
    public void proccessCamResults(List<PhotonTrackedTarget> poseEstimatedTargets) {

        // Setup this cameras Robot Pose Estimators, based on desired Camera Strategy
        switch (VisionConfig.cameraPoseStrategy) {
            case LOWEST_AMBIGUITY:
                double lowestAmbiguityScore = 1000.0;   // So big
                int lowestAmbiguityTgtIdx = -1;         // 
                for (int j = 0; j < poseEstimatedTargets.size(); j++) {

                    if (poseEstimatedTargets.get(j).getPoseAmbiguity() < lowestAmbiguityScore) {
                        // This target is better than any before so record the index to this tgt
                        lowestAmbiguityTgtIdx = j;
                    }
                }
                // We have completed looking for Tgt with Lowest ambiguity so update fields
                updateCamResults(poseEstimatedTargets.get(lowestAmbiguityTgtIdx));
                break;
            default:
                // Bot sure how to deterine best tag so just clear them out
                clearCamResults();
        }
    }

    public void updateCamResults(PhotonTrackedTarget tgt) {
        hasTarget = true;
        aprilTagID = tgt.getFiducialId();
        ambiguity = tgt.getPoseAmbiguity();
        
        x = tgt.getBestCameraToTarget().getX();             // Forward Distance to Tag (Meters)
        y = tgt.getBestCameraToTarget().getY();             // Left/Right Distance of Tag (Meters)
        z = tgt.getBestCameraToTarget().getZ();             // Above/Below Tag (Meters)
        rotation = 0;                                       // Not sure where this data element is in tgt
        yaw   = tgt.getYaw();                               // Angle Left or Right to Tag
        pitch = tgt.getPitch();                             // Angle Up/DN to Tag

        timestamp = camera.getLatestResult().getLatencyMillis();

        if (Robot.vision.aprilTagFieldLayout.getTagPose(aprilTagID).isPresent())
            aprilTagPose = Robot.vision.aprilTagFieldLayout.getTagPose(aprilTagID).get();
        else {
            aprilTagPose = new Pose3d();
        }
        cameraToTagTrsfm = tgt.getBestCameraToTarget();
    }

    public void clearCamResults() {
        hasTarget = false;
        aprilTagID = 0;
        ambiguity = 10000;

        x = 0;                              // Forward Distance to Tag (Meters)
        y = 0;                              // Left/Right Distance of Tag (Meters)
        z = 0;                              // Above/Below Tag (Meters)
        rotation = 0;
        yaw   = 0;                          // Angle Left or Right to Tag
        pitch = 0;                          // Angle Up/DN to Tag
        aprilTagPose = new Pose3d();
        cameraToTagTrsfm = new Transform3d();
    }

    // --------------------------- Getters for VisionTelemetry ---------------------
    public String getName()                     { return name; }
    public boolean getHasTarget()               { return hasTarget; }
    public double getTimestamp()                { return timestamp; }
    public double getTargetID()                 { return aprilTagID; }
    public double getAmbiguity()                { return ambiguity; }
    public double getTargetYaw()                { return yaw; }
    public double getTargetPitch()              { return pitch; }
    public double getXDist()                    { return x; }
    public double getYDist()                    { return y; }
    public double getZDist()                    { return z; }
    public double getRotation()                 { return rotation; }
    public Pose3d getTagPose()                  { return aprilTagPose; }

    public Pose3d      getRobotPose()           { return robotEstPose; }
    public Transform3d getCamToTagTsfm()        { return cameraToTagTrsfm; }
    public Transform3d getRobotToCamTsfm()      { return VisionConfig.CAMERA_TO_ROBOT; }

}
