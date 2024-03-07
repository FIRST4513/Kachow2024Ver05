package frc.robot.drivetrain;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.Utils;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.Vector;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import frc.lib.swerve.Request;
import frc.lib.swerve.Request.ControlRequestParameters;
import edu.wpi.first.math.filter.LinearFilter;
import edu.wpi.first.math.filter.MedianFilter;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.math.numbers.N5;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.Threads;
import frc.robot.Robot;
import frc.robot.drivetrain.config.DrivetrainConfig;
import frc.util.DriveState;
import frc.util.PoseAndTimestamp;

/* Perform swerve module updates in a separate thread to minimize latency */
public class OdometryThread extends Thread {

    private final int START_THREAD_PRIORITY =
            1; // Testing shows 1 (minimum realtime) is sufficient for tighter
    // odometry loops.
    // If the odometry period is far away from the desired frequency,
    // increasing this may help

    private BaseStatusSignal[] m_allSignals;
    public int SuccessfulDaqs = 0;
    public int FailedDaqs = 0;
    MedianFilter peakRemover = new MedianFilter(3);
    LinearFilter lowPass = LinearFilter.movingAverage(50);
    protected Rotation2d m_fieldRelativeOffset = new Rotation2d();
    double lastTime = 0;
    double currentTime = 0;
    double averageLoopTime = 0;
    protected final boolean IsOnCANFD = true;
    protected SwerveDrivePoseEstimator m_odometry;
    protected ControlRequestParameters m_requestParameters = new ControlRequestParameters();
    protected Matrix<N3, N1> visionMeasurementStdDevs;

    int lastThreadPriority = START_THREAD_PRIORITY;
    int threadPriorityToSet = START_THREAD_PRIORITY;

    protected ReadWriteLock m_stateLock = new ReentrantReadWriteLock();
    protected Request m_requestToApply = new Request.Idle();

    protected final int ModuleCount = 4;
    protected final double UpdateFrequency = 120;

    protected final DrivetrainSubSys drive;
    protected SwerveModulePosition[] m_modulePositions = new SwerveModulePosition[4];
    protected DriveState m_cachedState = new DriveState();


    // -------------- Constructor --------------------
    public OdometryThread( DrivetrainSubSys drive ) {
        super();
        this.drive = drive;

        // 4 signals for each module + 2 for Pigeon2
        m_allSignals = new BaseStatusSignal[(ModuleCount * 4) + 2];
        for (int i = 0; i < ModuleCount; ++i) {
            BaseStatusSignal[] signals = drive.swerveMods[i].getSignals();
            m_allSignals[(i * 4) + 0] = signals[0];
            m_allSignals[(i * 4) + 1] = signals[1];
            m_allSignals[(i * 4) + 2] = signals[2];
            m_allSignals[(i * 4) + 3] = signals[3];
        }
        m_allSignals[m_allSignals.length - 2] = drive.gyro.yawGetter;
        m_allSignals[m_allSignals.length - 1] = drive.gyro.angularZVelGetter;

        for (int i = 0; i < ModuleCount; ++i) {
            m_modulePositions[i] = drive.swerveMods[i].getPosition();
        }

        // createStateStdDevs(
        //     DrivetrainConfig.kPositionStdDevX,
        //     DrivetrainConfig.kPositionStdDevY,
        //     DrivetrainConfig.kPositionStdDevTheta),
        visionMeasurementStdDevs = createVisionMeasurementStdDevs(
                                                DrivetrainConfig.kVisionStdDevX,
                                                DrivetrainConfig.kVisionStdDevY,
                                                DrivetrainConfig.kVisionStdDevTheta);


        m_odometry =
            new SwerveDrivePoseEstimator(
                    DrivetrainConfig.getKinematics(), new Rotation2d(), m_modulePositions, new Pose2d());
    }

    // ------------------- Thread Run Method ---------------------------
    @Override
    public void run() {
        /* Make sure all signals update at around 250hz */
        for (BaseStatusSignal sig : m_allSignals) {
            sig.setUpdateFrequency(UpdateFrequency);
        }
        Threads.setCurrentThreadPriority(true, START_THREAD_PRIORITY);

        /* Run as fast as possible, our signals will control the timing */
        while (true) {
            /* Synchronously wait for all signals in drivetrain */
            /* Wait up to twice the period of the update frequency */
            StatusCode status;
            if (DrivetrainConfig.CANFDBus) {
                status = BaseStatusSignal.waitForAll(2.0 / UpdateFrequency, m_allSignals); //CANivore only !!!!
            } else {
                try {
                    /* Wait for the signals to update */
                    Thread.sleep((long) ((1.0 / UpdateFrequency) * 1000.0));
                } catch (InterruptedException ex) {
                }
                status = BaseStatusSignal.refreshAll(m_allSignals);
            }
            m_stateLock.writeLock().lock();

            lastTime = currentTime;
            currentTime = Utils.getCurrentTimeSeconds();
            /* We don't care about the peaks, as they correspond to GC events, and we want the period generally low passed */
            averageLoopTime = lowPass.calculate(peakRemover.calculate(currentTime - lastTime));

            /* Get status of first element */
            if (status.isOK()) {
                SuccessfulDaqs++;
            } else {
                FailedDaqs++;
            }

            /* Now update odometry */
            /* Keep track of the change in azimuth rotations */
            for (int i = 0; i < ModuleCount; ++i) {
                m_modulePositions[i] = drive.swerveMods[i].getPosition();
            }
            
            // Assume Pigeon2 is flat-and-level so latency compensation can be performed
            double yawDegrees =
                    BaseStatusSignal.getLatencyCompensatedValue(drive.gyro.yawGetter, drive.gyro.angularZVelGetter);

            /* Keep track of previous and current pose to account for the carpet vector */
            m_odometry.update(Rotation2d.fromDegrees(yawDegrees), m_modulePositions);

            /* And now that we've got the new odometry, update the controls */
            m_requestParameters.currentPose =
                    m_odometry
                            .getEstimatedPosition()
                            .relativeTo(new Pose2d(0, 0, m_fieldRelativeOffset));
            m_requestParameters.kinematics = DrivetrainConfig.getKinematics();
            m_requestParameters.swervePositions = DrivetrainConfig.moduleLocations;
            m_requestParameters.timestamp = currentTime;
            m_requestParameters.updatePeriod = 1.0 / UpdateFrequency;

            m_requestToApply.apply(m_requestParameters, drive.swerveMods);

            /* Update our cached state with the newly updated data */
            m_cachedState.FailedDaqs = FailedDaqs;
            m_cachedState.SuccessfulDaqs = SuccessfulDaqs;
            m_cachedState.ModuleStates = new SwerveModuleState[drive.swerveMods.length];
            m_cachedState.ModulePositions = new SwerveModulePosition[drive.swerveMods.length];

            for (int i = 0; i < drive.swerveMods.length; ++i) {
                m_cachedState.ModuleStates[i] = drive.swerveMods[i].getState();
                m_cachedState.ModulePositions[i] = m_modulePositions[i];
            }
            
            m_cachedState.Pose = m_odometry.getEstimatedPosition();
            m_cachedState.OdometryPeriod = averageLoopTime;

            // ------- Add Vision Updates Here --------
            PoseAndTimestamp estPose = Robot.vision.getVisionPoseEst();

            if (estPose.isNew()) {
                // "Consume" the pose, aka set isNew to False since it is old now
                System.out.println("Odometry NEW pose found !!!!!!!!!!!!!!");
                Robot.vision.consumePoseEst();

                // Method 1
                addVisionMeasurement( estPose.pose.toPose2d(), estPose.timestamp );

                System.out.println(" x = " + estPose.pose.getX() + "y= " + estPose.pose.getY() );

                // Method 2 Std deviation matrix - not fully implemented yet
                //addVisionMeasurement( estPose.pose.toPose2d(), estPose.timestamp, visionMeasurementStdDevs) ;
            } else {
                System.out.println("Odometry NO new pose found !!!!!!");
            }

            m_stateLock.writeLock().unlock();
            /**
             * This is inherently synchronous, since lastThreadPriority is only written here and
             * threadPriorityToSet is only read here
             */
            if (threadPriorityToSet != lastThreadPriority) {
                Threads.setCurrentThreadPriority(true, threadPriorityToSet);
                lastThreadPriority = threadPriorityToSet;
            }
        }

    }

    public boolean odometryIsValid() {
        return SuccessfulDaqs > 2; // Wait at least 3 daqs before saying the odometry is valid
    }

    /**
     * Sets the DAQ thread priority to a real time priority under the specified priority level
     *
     * @param priority Priority level to set the DAQ thread to. This is a value between 0 and
     *     99, with 99 indicating higher priority and 0 indicating lower priority.
     */
    public void setThreadPriority(int priority) {
        threadPriorityToSet = priority;
    }

    /**
     * Takes the current orientation of the robot plus an angle offset and makes it X forward for
     * field-relative maneuvers.
     */
    // public void reorientPose(double offsetDegrees) {
    //     //public void seedFieldRelative(double offsetDegrees) {
    //     try {
    //         m_stateLock.writeLock().lock();
    //         m_fieldRelativeOffset =
    //                 drive.getDriveState().Pose.getRotation().plus(Rotation2d.fromDegrees(offsetDegrees));
    //     } finally {
    //         m_stateLock.writeLock().unlock();
    //     }
    // }

    /**
     * Takes the specified location Pose and makes it the current pose for field-relative maneuvers
     * also reset the odometer heading to the current gyro heading
     *
     * @param location Pose to make the current pose at.
     */
    public void resetOdometryPose(Pose2d location) {
        try {
            m_stateLock.writeLock().lock();
            m_odometry.resetPosition(drive.gyro.getYawRotation2d(), m_modulePositions, location);
        } finally {
            m_stateLock.writeLock().unlock();
        }
    }

    /**
     * Zero's this swerve drive's odometry entirely. 
     *
     * <p>This will zero the entire odometry, and place the robot at 0,0
     */
    public void zeroEverything() {
        try {
            m_stateLock.writeLock().lock();
            for (int i = 0; i < ModuleCount; ++i) {
                drive.swerveMods[i].resetModulePostion();                       // Resets the Drive Motor position to zero
                m_modulePositions[i] = drive.swerveMods[i].getPosition(true);   // Reloads the cached state to current zero pos
            }
            m_odometry.resetPosition(drive.gyro.getYawRotation2d(), m_modulePositions, new Pose2d());
        } finally {
            m_stateLock.writeLock().unlock();
        }

    }

    /**
     * Gets the current state of the swerve drivetrain.
     *
     * @return Current state of the drivetrain
     */
    public DriveState getDriveState() {
        try {
            m_stateLock.readLock().lock();
            return m_cachedState;
        } finally {
            m_stateLock.readLock().unlock();
        }
    }



    /**
     * Gets a reference to the data acquisition thread.
     *
     * @return DAQ thread
     */
    public OdometryThread getDaqThread()            { return this; }

        // for (int i = 0; i < ModuleCount; ++i) {
        //     m_modulePositions[i] = drive.swerveMods[i].getPosition();
        // }





    /**
     * Adds a vision measurement to the Kalman Filter. This will correct the odometry pose estimate
     * while still accounting for measurement noise.
     *
     * <p>This method can be called as infrequently as you want, as long as you are calling {@link
     * SwerveDrivePoseEstimator#update} every loop.
     *
     * <p>To promote stability of the pose estimate and make it robust to bad vision data, we
     * recommend only adding vision measurements that are already within one meter or so of the
     * current pose estimate.
     *
     * <p>Note that the vision measurement standard deviations passed into this method will continue
     * to apply to future measurements until a subsequent call to {@link
     * SwerveDrivePoseEstimator#setVisionMeasurementStdDevs(Matrix)} or this method.
     *
     * @param visionRobotPoseMeters The pose of the robot as measured by the vision camera.
     * @param timestampSeconds The timestamp of the vision measurement in seconds. Note that if you
     *     don't use your own time source by calling {@link
     *     SwerveDrivePoseEstimator#updateWithTime(double,Rotation2d,SwerveModulePosition[])}, then
     *     you must use a timestamp with an epoch since FPGA startup (i.e., the epoch of this
     *     timestamp is the same epoch as {@link edu.wpi.first.wpilibj.Timer#getFPGATimestamp()}).
     *     This means that you should use {@link edu.wpi.first.wpilibj.Timer#getFPGATimestamp()} as
     *     your time source in this case.
     * @param visionMeasurementStdDevs Standard deviations of the vision pose measurement (x
     *     position in meters, y position in meters, and heading in radians). Increase these numbers
     *     to trust the vision pose measurement less.
     */
    public void addVisionMeasurement(
            Pose2d visionRobotPoseMeters,
            double timestampSeconds,
            Matrix<N3, N1> visionMeasurementStdDevs) {
        try {
            m_stateLock.writeLock().lock();
            m_odometry.addVisionMeasurement(
                    visionRobotPoseMeters, timestampSeconds, visionMeasurementStdDevs);
        } finally {
            m_stateLock.writeLock().unlock();
        }
    }

    /**
     * Add a vision measurement to the PoseEstimator. This will correct the odometry pose estimate
     * while still accounting for measurement noise.
     *
     * <p>This method can be called as infrequently as you want, as long as you are calling {@link
     * SwerveDrivePoseEstimator#update} every loop.
     *
     * <p>To promote stability of the pose estimate and make it robust to bad vision data, we
     * recommend only adding vision measurements that are already within one meter or so of the
     * current pose estimate.
     *
     * @param visionRobotPoseMeters The pose of the robot as measured by the vision camera.
     * @param timestampSeconds The timestamp of the vision measurement in seconds. Note that if you
     *     don't use your own time source by calling {@link SwerveDrivePoseEstimator#updateWithTime}
     *     then you must use a timestamp with an epoch since FPGA startup (i.e. the epoch of this
     *     timestamp is the same epoch as Timer.getFPGATimestamp.) This means that you should use
     *     Timer.getFPGATimestamp as your time source or sync the epochs.
     */
    public void addVisionMeasurement(Pose2d visionRobotPoseMeters, double timestampSeconds) {
        try {
            m_stateLock.writeLock().lock();
            m_odometry.addVisionMeasurement(visionRobotPoseMeters, timestampSeconds);
        } finally {
            m_stateLock.writeLock().unlock();
        }
    }

    /**
     * Sets the pose estimator's trust of global measurements. This might be used to change trust in
     * vision measurements after the autonomous period, or to change trust as distance to a vision
     * target increases.
     *
     * @param visionMeasurementStdDevs Standard deviations of the vision measurements. Increase
     *     these numbers to trust global measurements from vision less. This matrix is in the form
     *     [x, y, theta]ᵀ, with units in meters and radians.
     */
    public void setVisionMeasurementStdDevs(Matrix<N3, N1> visionMeasurementStdDevs) {
        try {
            m_stateLock.writeLock().lock();
            m_odometry.setVisionMeasurementStdDevs(visionMeasurementStdDevs);
        } finally {
            m_stateLock.writeLock().unlock();
        }
    }


    /**
     * Creates a vector of standard deviations for the states. Standard deviations of model states.
     * Increase these numbers to trust your model's state estimates less.
     *
     * @param x in meters
     * @param y in meters
     * @param theta in degrees
     * @return the Vector of standard deviations need for the poseEstimator
     */
    public Vector<N3> createStateStdDevs(double x, double y, double theta) {
        return VecBuilder.fill(x, y, Units.degreesToRadians(theta));
    }

    /**
     * Creates a vector of standard deviations for the local measurements. Standard deviations of
     * encoder and gyro rate measurements. Increase these numbers to trust sensor readings from
     * encoders and gyros less.
     *
     * @param theta in degrees per second
     * @param s std for all module positions in meters per sec
     * @return the Vector of standard deviations need for the poseEstimator
     */
    public Vector<N5> createLocalMeasurementStdDevs(double theta, double p) {
        return VecBuilder.fill(Units.degreesToRadians(theta), p, p, p, p);
    }

    /**
     * Creates a vector of standard deviations for the vision measurements. Standard deviations of
     * global measurements from vision. Increase these numbers to trust global measurements from
     * vision less.
     *
     * @param x in meters
     * @param y in meters
     * @param theta in degrees
     * @return the Vector of standard deviations need for the poseEstimator
     */
    public Vector<N3> createVisionMeasurementStdDevs(double x, double y, double theta) {
        return VecBuilder.fill(x, y, Units.degreesToRadians(theta));
    }







}

