package frc.robot.vision;

import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.util.Units;

public class VisionConfig {
    
    public static String frontCamName = ("Arducam_OV2311_USB_Camera");
    public static Transform3d frontCamToRobotTrsfm = new Transform3d(
                            new Translation3d(
                                    Units.inchesToMeters( 15.0),    // Distance Fwd  from Robot Ctr.
                                    Units.inchesToMeters( 0.0),     // Distance Left from Robot Ctr.
                                    Units.inchesToMeters( 6.0)),    // Distance above ground
                             new Rotation3d(0, 25, 0));             // roll, pitch, yaw CCW +

    public static String backLeftCamName = ("Back Left Camera");                             
    public static Transform3d backLeftCamToRobotTrsfm = new Transform3d(
                            new Translation3d(
                                    Units.inchesToMeters( -15.0),
                                    Units.inchesToMeters( 15.0),
                                    Units.inchesToMeters( 6.0)),
                             new Rotation3d(0, 25.0, 135.0));

    public static String backRtCamName = ("Back Right Camera");
    public static Transform3d backRtCamToRobotTrsfm = new Transform3d(
                            new Translation3d(
                                    Units.inchesToMeters( -15.0),
                                    Units.inchesToMeters( -15.0),
                                    Units.inchesToMeters( 6.0)),
                             new Rotation3d(0, 25, -135.0));

    public static Transform3d cameraTransforms[] = {};  // frontCamToRobotTrsfm, backLeftCamToRobotTrsfm, backRtCamToRobotTrsfm

    // ---------------------------------
    public enum RobotPoseStrategy {
        LOWEST_AMBIGUITY,
        CLOSEST_TO_CAMERA_HEIGHT,
        CLOSEST_TO_REFERENCE_POSE,
        CLOSEST_TO_LAST_POSE,
        AVERAGE_BEST_TARGETS,
        MUTI_TAG
    }
    public enum CameraPoseStrategy {
        LOWEST_AMBIGUITY,
        CLOSEST_TO_CAMERA_HEIGHT,
        CLOSEST_TO_REFERENCE_POSE,
        CLOSEST_TO_LAST_POSE,
        AVERAGE_BEST_TARGETS,
        MULTI_TAG
    }

    public static RobotPoseStrategy robotPoseStrategy = RobotPoseStrategy.LOWEST_AMBIGUITY;
    public static CameraPoseStrategy cameraPoseStrategy = CameraPoseStrategy.LOWEST_AMBIGUITY;
    // -----------------------------------

    // Front Camera Positions on the Robot
    public static final double FC_X_OFFSET = Units.inchesToMeters(0.0);         // forward (+)
    public static final double FC_Y_OFFSET = Units.inchesToMeters(0.0);         // left (+)
    public static final double FC_Z_OFFSET = Units.inchesToMeters(6.0);         // Above Floor (+)
    public static final double FC_PITCH_ANGLE = Units.degreesToRadians(25.0);   // Angle Up (+)
    public static final double FC_YAW_ANGLE = Units.degreesToRadians(0.0);      // CCW (+)
    
    public static final Translation3d FC_OFFSETS = new Translation3d(FC_X_OFFSET, FC_Y_OFFSET, FC_Z_OFFSET);
    public static final Rotation3d FC_ANGLES = new Rotation3d(0.0, FC_PITCH_ANGLE, FC_YAW_ANGLE);

    // This Transform is used in Photon vision Robot Pose Estimater Method 2
    public static final Transform3d CAMERA_TO_ROBOT = new Transform3d(FC_OFFSETS, FC_ANGLES);

    // This Transform is used in Photon vision Robot Pose Estimater Method 1
    public static final Transform3d ROBOT_TO_CAMERA = CAMERA_TO_ROBOT.inverse();

}
