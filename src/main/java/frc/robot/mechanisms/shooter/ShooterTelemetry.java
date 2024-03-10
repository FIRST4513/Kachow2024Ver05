package frc.robot.mechanisms.shooter;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class ShooterTelemetry {
    protected ShuffleboardTab tab;

    public ShooterTelemetry( ShooterSubSys shooter) {
        tab = Shuffleboard.getTab("Shooter");
        
        tab.addNumber("Top Power:",         () -> shooter.getBottomPWM());
        tab.addNumber("Bottom Power:",         () -> shooter.getTopPWM());
        tab.addNumber("Top RPS", () -> shooter.getTopRPS());
        tab.addNumber("Bottom RPS", () -> shooter.getBottomRPS());

        tab.addNumber("Pivot Enc", () -> shooter.getEncoderPosition());
        tab.addNumber("Target angle", () -> shooter.getTargetAngle());
        tab.addNumber("Pivot Power", () -> shooter.getPivotPower());

        tab.addString("Fire State", () -> shooter.getFireStateString());
        tab.addString("Pivot State", () -> shooter.getPivotStateString());
    }
}
