package frc.robot.drivetrain;

import java.util.Map;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.SuppliedValueWidget;
import frc.lib.util.Rmath;
import frc.robot.Robot;

public class DrivetrainTelemetry {
    protected ShuffleboardTab tab;
    private DrivetrainSubSys swerve;
    String Mod0Name;
    String Mod1Name;
    String Mod2Name;
    String Mod3Name;

    public DrivetrainTelemetry(DrivetrainSubSys swerve) {
        this.swerve = swerve;
        tab = Shuffleboard.getTab("Swerve");
        tab.addNumber("Heading from Pose", () -> swerve.getPoseHdgDegrees()).withPosition(7, 0).withSize(2, 1);
        tab.addNumber("Odometry X", () -> swerve.getPose().getX()).withPosition(7, 1).withSize(2, 1);
        tab.addNumber("Odometry Y", () -> swerve.getPose().getY()).withPosition(7, 2).withSize(2, 1);
        tab.addNumber("Gyro Yaw", () -> Rmath.mRound(swerve.getGyroYawDegrees(), 2)).withPosition(7, 3).withSize(2, 1);

        tab.addNumber("Pilot Input X", () -> Robot.pilotGamepad.getDriveFwdPositive()).withPosition(9,0).withSize(2, 1);
        tab.addNumber("Pilot Input Y", () -> Robot.pilotGamepad.getDriveLeftPositive()).withPosition(9,1).withSize(2, 1);
        
        tab.addBoolean("VisionValid", () -> Robot.swerve.isVisionPoseValid()).withPosition(9, 2).withSize(2, 2);
        tab.addString("VisionPose", () -> Robot.swerve.getVisionPose().toString()).withPosition(7, 4).withSize(6, 1);
        Mod0Name = swerve.swerveMods[0].modName;
        Mod1Name = swerve.swerveMods[1].modName;
        Mod2Name = swerve.swerveMods[2].modName;
        Mod3Name = swerve.swerveMods[3].modName;

        moduleLayout(Mod0Name, 0, tab).withPosition(0, 0).withSize(3, 3);
        moduleLayout(Mod1Name, 1, tab).withPosition(3, 0).withSize(3, 3);
        moduleLayout(Mod2Name, 2, tab).withPosition(0, 3).withSize(3, 3);
        moduleLayout(Mod3Name, 3, tab).withPosition(3, 3).withSize(3, 3);
    }

    public ShuffleboardLayout moduleLayout(String name, int moduleNum, ShuffleboardTab tab) {
        Robot.print("Swerve Module Layout being made with name: " + name + " and module id of: " + moduleNum);

        ShuffleboardLayout modLayout = tab.getLayout(name, BuiltInLayouts.kGrid);
        // m_mod0Layout.withSize(1, 2);
        modLayout.withProperties(Map.of("Label position", "TOP"));

        // Wheel Angle in Degrees
        SuppliedValueWidget<Double> wheelAngle =
        modLayout.addNumber(
                "Wheel Angle º",
                () -> swerve.swerveMods[moduleNum].getSteerAngleRotation2d().getDegrees());
        wheelAngle.withPosition(0, 0).withSize(1, 2);

        // Wheel Velocity in Meters per Second
        SuppliedValueWidget<Double> wheelVelocity =
        modLayout.addNumber(
                "Wheel Velocity MPS",
                () -> swerve.swerveMods[moduleNum].getModuleVelocityMPS());
        wheelVelocity.withPosition(2, 0).withSize(1, 2);

        // Target Angle to go to
        SuppliedValueWidget<Double> tgtAngle =
        modLayout.addNumber(
                "Target Angle º",
                () -> swerve.swerveMods[moduleNum].getModuleAngleDegrees());
        tgtAngle.withPosition(0, 1).withSize(1, 2);

        // CAN ABS Value
        SuppliedValueWidget<Double> canABS =
        modLayout.addNumber(
                "Can ABS Raw",
                () -> swerve.swerveMods[moduleNum].getSteerAngle());
        canABS.withPosition(2, 1).withSize(1, 2);

        // // Target Velocity to go to
        // SuppliedValueWidget<Double> tgtVelocity =
        // modLayout.addNumber(
        //         "Target Velocity MPS",
        //         () -> swerve.swerveMods[moduleNum].getModuleVelocityMPS());
        // tgtVelocity.withPosition(2, 1).withSize(1, 2);

        return modLayout;
    }
}
