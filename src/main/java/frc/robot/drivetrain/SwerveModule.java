package frc.robot.drivetrain;

import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.StatusSignal;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import frc.lib.swerve.CTREModuleState;
import frc.robot.drivetrain.config.AngleFalconConfig;
import frc.robot.drivetrain.config.CanCoderConfig;
import frc.robot.drivetrain.config.DriveFalconConfig;
import frc.robot.drivetrain.config.DrivetrainConfig;


/**
 * A Swerve Module object, a piece of a broader Swerve Drive Subsystem.
 * <p>
 * A Swerve Module object contains a TalonFX drive motor, a TalonFX steer motor, and a CAN Coder absolute encoder.
 * <p>
 * This object also provides ways to drive the swerve module with ChassisSpeeds and get information from the devices like angles and distances.
 */
public class SwerveModule {
    // Module's Identification
    public final String modName;
    public final int modNumber;

    // Module's Devices
    private TalonFX mAngleMotor;
    private TalonFX mDriveMotor;
    private CANcoder mAngleEncoder;

    // Module's Control Requests
    private VelocityVoltage mDrivePIDReq;   // drive by PID (closedloop)
    private VoltageOut mDriveVoltageReq;    // drive by Voltage out (open loop)
    private PositionVoltage mAnglePIDReq;   // angle/turn
    private SwerveModulePosition m_internalState = new SwerveModulePosition();

    private BaseStatusSignal[] m_signals;
    private StatusSignal<Double> m_drivePosition;
    private StatusSignal<Double> m_driveVelocity;
    private StatusSignal<Double> m_steerPosition;
    private StatusSignal<Double> m_steerVelocity;

     // ----- Constructor -----
    /**
     * Construct a Swerve Module object, a piece of a broader Swerve Drive Subsystem.
     * <p>
     * Given a module number/ID, this instantiates the devices associated with this swerve module, and configures the motors to be run.
     * 
     * @param moduleNumber the integer ID number of the Swerve Module to be made. This is used to get information about CAN IDs for devices
     */
    public SwerveModule(int moduleNumber) {
        // Set Name, ID, and Angle Offset
        modNumber = moduleNumber;
        modName = DrivetrainConfig.getModName(moduleNumber);

        // Instantiate Devices and configure them from config file

        // high speed bus config
        mAngleMotor   = new TalonFX(DrivetrainConfig.getModAngleCanID(moduleNumber), "CANFD");
        mDriveMotor   = new TalonFX(DrivetrainConfig.getModDriveCanID(moduleNumber), "CANFD");
        mAngleEncoder = new CANcoder(DrivetrainConfig.getModCanCoderID(moduleNumber), "CANFD");
        configureDevices(moduleNumber);
        
        // Setup Control Request Objects
        setupControlReqObjects();
    }


    // ----- Drive Methods -----
    /**
     * Stop the motors!
     */
    public void stopMotors() {
        mDriveMotor.stopMotor();
        mAngleMotor.stopMotor();
    }

    /**
     * Tell the steer and drive motors to go to the appropriate positions and velocities.
     * 
     * @param desiredState the SwerveModuleState object that represents what the swerve module should be doing
     * @param isOpenloop whether or not to run in Openloop. True = DON'T use PID; False = DO use PID
     */
    public void setDesiredState(SwerveModuleState desiredState, boolean isOpenloop) {
        desiredState = CTREModuleState.optimize(desiredState, getSteerAngleRotation2d());  // get can coder degrees
        setAngle(desiredState);
        setSpeed(desiredState, isOpenloop);
    }

    /**
     * Set the angle of the steer motor.
     * 
     * @param desiredState the SwerveModuleState object that represents what the swerve module should be doing
     */
    public void setAngle(SwerveModuleState desiredState) {
        // Tell Steer motor to go to the required rotation angle from desired state
        double angleRotation = desiredState.angle.getRotations();
        
        if (( Math.abs(desiredState.speedMetersPerSecond) < (DrivetrainConfig.maxVelocity * 0.01))) {
            // Prevent rotating module if speed is less than 1% of max speed. (Jitter prevention)
            angleRotation = getSteerAngle();
        }
        
        mAngleMotor.setControl(mAnglePIDReq.withPosition(angleRotation).withSlot(0));
    }

    /**
     * Set the velocity of the drive motor.
     * 
     * @param desiredState the SwerveModuleState object that represents what the swerve module should be doing
     */
    public void setSpeed(SwerveModuleState desiredState, boolean isOpenloop) {
        // Tell Drive motor to go to the required velocity MPS from desired state
        double velocityMPS = desiredState.speedMetersPerSecond;

        if (isOpenloop) {
            // Openloop, straight voltage
            // divide target speed by max speed at 12v, giving us a percentage of top speed [0, 1]
            double targetSpeedPercent = velocityMPS / DrivetrainConfig.getMaxVelocity(); // Percent
            double targetVoltage = targetSpeedPercent * 12;     // convert to volts from percent

            // tell motor to drive at that voltage, no PID or anything
            mDriveMotor.setControl(mDriveVoltageReq.withOutput(targetVoltage));
        } else {
            // Closedloop, use PID to go to velocity
            // calculate velocity by multiplying target speed by the ratio the wheel and gear ratios imply on the motor
            double velocityToSet = velocityMPS * DriveFalconConfig.driveRotationsPerMeter;
            
            // tell motor to drive at that velocity, using PID
            mDriveMotor.setControl(mDrivePIDReq.withVelocity(velocityToSet).withSlot(0));
        }
    }

    // ----- Falcon and CAN Coder Rotation Getters -----

    /**
     * Get the steer angle of the wheel, taken from the CAN Coder with its method `getAbsolutePosition().getValueAsDouble()`
     * 
     * @return A Rotation2d representing the angle in Degrees, Radians, and Rotations at the same time 1 rotation only
     */
    public Rotation2d getSteerAngleRotation2d() {
        return Rotation2d.fromRotations(getSteerAngle());
    }

    /**
     * Get the steer angle of the wheel, taken from the CAN Coder
     * 
     * @return A double representing the angle in rotations Truncated to 1 rotation Not accumulative
     */
    public double getSteerAngle() {
        return mAngleEncoder.getAbsolutePosition().getValueAsDouble();
    }

    // ----- Odometry and Swerve Module State/Position Getters -----

    /**
     * Gets the position/velocity signals of the drive and steer
     *
     * @return Array of BaseStatusSignals for this module in the following order: 0 - Drive Position
     *     1 - Drive Velocity 2 - Steer Position 3 - Steer Velocity
     */
    BaseStatusSignal[] getSignals() {
        return m_signals;
    }
    /**
     * --------------------------------Position (Meters , Roation2d) ----------------------------
     * Get the position of the swerve module, including the position (distance in meters)
     * and the rotation of the wheel in the steer direction 
     * 
     * @return A SwerveModulePosition object representing the position of the module [double, Rotation2d]
     */
    public SwerveModulePosition getPosition() {
        return new SwerveModulePosition(
                getModulePositionMeters(),
                getSteerAngleRotation2d()
        );
    }

    public double getModulePositionMeters() {
        double driveRotations = mDriveMotor.getPosition().getValueAsDouble();
        return ( driveRotations / DriveFalconConfig.driveRotationsPerMeter );
    }
  
    /**
     * ------------------------ State (MetersPerSec, Rotation2d) ----------------------------
     * Get the state of the swerve module, including it's velocity on the field
     * and the rotation of the wheel in the steer direction
     * 
     * @return A SwerveModuleState object representing the state of the module [double, Rotation2d]
     */
    public SwerveModuleState getState() {
        return new SwerveModuleState( getModuleVelocityMPS(), getSteerAngleRotation2d() );
    }  

    /**
     * @return A Double representing speed in meters per second
     */
    public double getModuleVelocityMPS() {
        // Get falcon rotations per second
        double rps = mDriveMotor.getVelocity().getValueAsDouble();
        return ( rps / DriveFalconConfig.driveRotationsPerMeter );       // Convert to MPS
    }
    
    // ----- Misc Getters -----

    public double getModuleAngleDegrees()       { return getState().angle.getDegrees(); }

    public double getCanCoderOffset()           { return DrivetrainConfig.getModAngleOffset(modNumber); }

        
    // ----- Misc setters -----
    public void resetModulePostion()            { mDriveMotor.setPosition(0.0); }
    public void resetModulePostion(double pos)  { mDriveMotor.setPosition( pos ); }

    // ------- Other Methods --------------

    public void configureDevices(int moduleNumber) {
        // Configure Devices (Falcons, CAN Coder) and print responses
        StatusCode response = mAngleMotor.getConfigurator().apply(AngleFalconConfig.getConfig(moduleNumber));
        printResponse(response, modName + " angle motor");

        response = mDriveMotor.getConfigurator().apply(DriveFalconConfig.getConfig(moduleNumber));
        printResponse(response, modName + " drive motor");

        response = mAngleEncoder.getConfigurator().apply(CanCoderConfig.getConfig(moduleNumber));
        printResponse(response, modName + " CAN Coder");
    }
 
    private void printResponse(StatusCode response, String motorName) {
        if (!response.isOK()) {
            System.out.println(
                    "Motor Name "
                            + motorName
                            + " failed config with error "
                            + response.toString());
        }
    }

    public void setupControlReqObjects(){
        // Instantiate Control Request Objects
        mDrivePIDReq = new VelocityVoltage(0);
        mDriveVoltageReq = new VoltageOut(0);
        mAnglePIDReq = new PositionVoltage(0);
        // mAngleMMVoltageReq = new MotionMagicVoltage(0);

        // Make control requests synchronous
        mDrivePIDReq.UpdateFreqHz = 0;
        mDriveVoltageReq.UpdateFreqHz = 0;
        mAnglePIDReq.UpdateFreqHz = 0;

        // TODO: leave out StatusSignals<Double> for motor positions and velocities
        // used by odometry (Spectrum file `Module.java` lines 40-43 and 189-192)

        m_drivePosition = mDriveMotor.getPosition().clone();
        m_driveVelocity = mDriveMotor.getVelocity().clone();
        m_steerPosition = mAngleMotor.getPosition().clone();
        m_steerVelocity = mAngleMotor.getVelocity().clone();

        m_signals = new BaseStatusSignal[4];
        m_signals[0] = m_drivePosition;
        m_signals[1] = m_driveVelocity;
        m_signals[2] = m_steerPosition;
        m_signals[3] = m_steerVelocity;
    }

    /**
     * Gets the state of this module and passes it back as a SwerveModulePosition object with
     * latency compensated values.
     *
     * @param refresh True if the signals should be refreshed
     * @return SwerveModulePosition containing this module's state.
     */
    public SwerveModulePosition getPosition(boolean refresh) {
        if (refresh) {
            /* Refresh all signals */
            m_drivePosition.refresh();
            m_driveVelocity.refresh();
            m_steerPosition.refresh();
            m_steerVelocity.refresh();
        }

        /* Now latency-compensate our signals */
        double drive_rot = BaseStatusSignal.getLatencyCompensatedValue(m_drivePosition, m_driveVelocity);
        double angle_rot = BaseStatusSignal.getLatencyCompensatedValue(m_steerPosition, m_steerVelocity);

        /*
         * Back out the drive rotations based on angle rotations due to coupling between
         * azimuth and steer
         */
        drive_rot -= angle_rot * AngleFalconConfig.angleGearRatio;

        /* And push them into a SwerveModuleState object to return */
        m_internalState.distanceMeters = getModulePositionMeters();
        /* Angle is already in terms of steer rotations */
        m_internalState.angle = Rotation2d.fromRotations(angle_rot);

        return m_internalState;
    }
}
