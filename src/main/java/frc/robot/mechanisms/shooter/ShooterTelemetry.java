package frc.robot.mechanisms.shooter;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class ShooterTelemetry {
    protected ShuffleboardTab tab;

    public ShooterTelemetry( ShooterSubSys shooter) {
        // todo: better tabing

        tab = Shuffleboard.getTab("Shooter");
        tab.addNumber("Motor Speed 1:",         () -> shooter.getMotorSpeed1());
        tab.addNumber("Motor Speed 2:",         () -> shooter.getMotorSpeed2());
        tab.addString("Current State", () -> shooter.getStateString());
        tab.add("Current Commands:",          shooter);
    }
}
