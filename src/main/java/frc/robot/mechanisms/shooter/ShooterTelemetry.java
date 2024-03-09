package frc.robot.mechanisms.shooter;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class ShooterTelemetry {
    protected ShuffleboardTab tab;

    public ShooterTelemetry( ShooterSubSys shooter) {
        // todo: better tabing

        tab = Shuffleboard.getTab("Shooter");
        
        tab.addNumber("Top Power:",         () -> shooter.getBottomPWM());
        tab.addNumber("Bottom Power:",         () -> shooter.getTopPWM());
        tab.addNumber("Top RPS", () -> shooter.getTopRPS());
        tab.addNumber("Bottom RPS", () -> shooter.getBottomRPS());

        tab.addString("Current State", () -> shooter.getStateString());
        tab.add("Current Commands:",          shooter);
    }
}
