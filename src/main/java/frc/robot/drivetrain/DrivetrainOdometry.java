package frc.robot.drivetrain;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import frc.robot.drivetrain.config.DrivetrainConfig;

public class DrivetrainOdometry {
    private SwerveDriveOdometry swerveOdometry;
    private DrivetrainSubSys drivetrain;

    // ----- Constructor -----
    public DrivetrainOdometry(DrivetrainSubSys drive) {
        drivetrain = drive;
        swerveOdometry = new SwerveDriveOdometry(
                DrivetrainConfig.getKinematics(),
                drivetrain.gyro.getYawRotation2d(),
                drivetrain.getModPositions()
        );
    }

    // --------------------- Update Odometry ----------------
    public void update() {
        // Called in drivetrain periodic - every 20ms
        swerveOdometry.update(drivetrain.gyro.getYawRotation2d(), drivetrain.getModPositions());
    }

    // --------------------- Reset/Init Odometry ----------------
    public void resetOdometryPose(Pose2d pose) { 
        // Resets Odometry to new Pose with the current Encoder Positions and current Gyro Angle
        // swerveOdometry.resetPosition(drivetrain.gyro.getGyroHeading(), drivetrain.getPositions(), pose);
        swerveOdometry.resetPosition(  drivetrain.gyro.getYawRotation2d(), drivetrain.getModPositions(), pose);
    }

    public void resetHeading(Rotation2d newHeading) {
        // This zeroes out the current pose ???????
        resetOdometryPose(new Pose2d(getTranslationMeters(), newHeading));
    }

    // ------------- Odometry Getters -----------------
    public SwerveDriveOdometry getSwerveDriveOdometry() {
        return swerveOdometry;
    }

    public Pose2d getPoseMeters() {
        return swerveOdometry.getPoseMeters();
    }

    public Translation2d getTranslationMeters() {
        return swerveOdometry.getPoseMeters().getTranslation();
    }

    public Rotation2d getHeading() {
        return getPoseMeters().getRotation();
    }
}