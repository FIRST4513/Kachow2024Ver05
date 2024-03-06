package frc.robot.drivetrain;

import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.hardware.Pigeon2;

import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.RobotConfig.Gyros;

public class PigeonGyro {
    protected Pigeon2 pigeon2;
    protected StatusSignal<Double> yawGetter;
    protected StatusSignal<Double> angularZVelGetter;

    /**
     * Creates a new Gyro, which is a wrapper for the Pigeon IMU and stores an offset so we don't
     * have to directly zero the gyro
     */
    public PigeonGyro() {
        // pigeon2 = new Pigeon2(Gyros.Pigeon2ID, "CANFD");
        pigeon2 = new Pigeon2(Gyros.Pigeon2ID);
        yawGetter = pigeon2.getYaw().clone();   // Degrees +CCW
        angularZVelGetter = pigeon2.getAngularVelocityZWorld().clone();
    }

    // Get Yaw (Heading) in Rotation2d format (+CCW -CW)
    public Rotation2d getYawRotation2d()            { return pigeon2.getRotation2d(); }

    // Get Yaw (Heading) in Degreees
    public Double getYawDegrees()                   { return getYawRotation2d().getDegrees(); }

    //  Set Yaw (Heading) to 0
    public void yawReset()                          { pigeon2.reset(); }
    
    //  Set Yaw (Heading) to Angle in degrees
    public void yawReset(double angle)              { pigeon2.setYaw(angle); }

    public void yawReset(Rotation2d angle)          { pigeon2.setYaw(angle.getDegrees()); }

    
}
