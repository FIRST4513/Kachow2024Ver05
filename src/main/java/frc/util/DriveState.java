package frc.util;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;

/**
 * Plain-Old-Data class holding the state of the swerve drivetrain. This encapsulates most data
 * that is relevant for telemetry or decision-making from the Swerve Drive.
 */

public class DriveState {
        public int SuccessfulDaqs;
        public int FailedDaqs;
        public Pose2d Pose = new Pose2d();
        public SwerveModuleState[] ModuleStates = new SwerveModuleState[] {};
        public SwerveModulePosition[] ModulePositions = new SwerveModulePosition[] {};
        public double OdometryPeriod;
}
