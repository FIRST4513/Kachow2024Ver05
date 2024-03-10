package frc.robot.mechanisms.shooter;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class ShooterTelemetry {
    protected ShuffleboardTab tab;

    public ShooterTelemetry( ShooterSubSys shooter) {
        tab = Shuffleboard.getTab("Shooter");
        
        tab.addNumber("Top Power:",         () -> shooter.getBottomPWM()).withPosition(0, 0).withSize(2, 1);
        tab.addNumber("Top RPS", () -> shooter.getTopRPS()).withPosition(2, 0).withSize(2, 1);

        tab.addNumber("Bottom Power:",         () -> shooter.getTopPWM()).withPosition(0, 1).withSize(2, 1);
        tab.addNumber("Bottom RPS", () -> shooter.getBottomRPS()).withPosition(2, 1).withSize(2, 1);

        tab.addString("Fire State", () -> shooter.getFireStateString()).withPosition(0, 2).withSize(2, 1);
        tab.addBoolean("At Velocity Tgt", () -> shooter.getIsAtVelocityTgt()).withPosition(2, 2).withSize(2, 1);

        tab.addNumber("Enc", () -> shooter.getEncoderPosition()).withPosition(5, 0).withSize(1, 1);
        tab.addNumber("Deg", () -> shooter.getAngle()).withPosition(6, 0).withSize(1, 1);
        tab.addString("Pivot State", () -> shooter.getPivotStateString()).withPosition(7, 0).withSize(2, 1);

        tab.addNumber("Target angle", () -> shooter.getTargetAngle()).withPosition(5, 1).withSize(2, 1);
        tab.addBoolean("At Tgt", () -> shooter.getAtTarget()).withPosition(6, 1).withSize(1, 1);
        tab.addNumber("Power", () -> shooter.getPivotPower()).withPosition(7, 1).withSize(2, 1);
    }
}
