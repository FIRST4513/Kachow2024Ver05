package frc.util;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class FieldConstants {
    /* Blue Field Positions */
    public static final Pose2d BLUE_SPEAKER_LEFT  = new Pose2d(0.72, 6.67, Rotation2d.fromDegrees(-120));
    public static final Pose2d BLUE_SPEAKER_CTR   = new Pose2d(1.37, 5.53, Rotation2d.fromDegrees(-180));
    public static final Pose2d BLUE_SPEAKER_RIGHT = new Pose2d(0.72, 4.41, Rotation2d.fromDegrees(120));
    
    public static final double BLUE_SPEAKER_LEFT_GYRO  = -134.5;
    public static final double BLUE_SPEAKER_CTR_GYRO   = -180;
    public static final double BLUE_SPEAKER_RIGHT_GYRO = 134.6;

    public static final Pose2d BLUE_AMP = new Pose2d(1.81, 7.63, Rotation2d.fromDegrees(90));

    public static final Pose2d BLUE_HP_LEFT  = new Pose2d(15.87, 1.25, Rotation2d.fromDegrees(-60));
    public static final Pose2d BLUE_HP_CTR   = new Pose2d(15.44, 1.0, Rotation2d.fromDegrees(-60));
    public static final Pose2d BLUE_HP_RIGHT = new Pose2d(14.94, 0.69, Rotation2d.fromDegrees(-60));
    
    /* Red Fiel Positions */
    public static final Pose2d RED_SPEAKER_LEFT  = new Pose2d(15.81, 4.41, Rotation2d.fromDegrees(60));
    public static final Pose2d RED_SPEAKER_CTR   = new Pose2d(15.19, 5.54, Rotation2d.fromDegrees(0));
    public static final Pose2d RED_SPEAKER_RIGHT = new Pose2d(15.81, 6.69, Rotation2d.fromDegrees(-60));

    public static final double RED_SPEAKER_LEFT_GYRO  = 224.6;
    public static final double RED_SPEAKER_CTR_GYRO   = -180;
    public static final double RED_SPEAKER_RIGHT_GYRO = 134.6;

    public static final Pose2d RED_AMP = new Pose2d(14.71, 7.63, Rotation2d.fromDegrees(90));

    public static final Pose2d RED_HP_LEFT  = new Pose2d(1.67, 0.69, Rotation2d.fromDegrees(-120));
    public static final Pose2d RED_HP_CTR   = new Pose2d(1.17, 0.95, Rotation2d.fromDegrees(-120));
    public static final Pose2d RED_HP_RIGHT = new Pose2d(0.69, 1.23, Rotation2d.fromDegrees(-120));
}
