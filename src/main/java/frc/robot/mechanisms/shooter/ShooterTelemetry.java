package frc.robot.mechanisms.shooter;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class ShooterTelemetry {
    protected ShuffleboardTab tab;

    public ShooterTelemetry( ShooterSubSys shooter) {
        tab = Shuffleboard.getTab("Shooter");
        
        // Top Motor Info
        tab.addNumber("Top Power:", () -> shooter.getBottomPWM()).withPosition(0, 0).withSize(2, 1);
        tab.addNumber("Top RPS",    () -> shooter.getTopRPS())   .withPosition(2, 0).withSize(2, 1);

        // Bottom Motor Info
        tab.addNumber("Bottom Power:", () -> shooter.getTopPWM())   .withPosition(0, 1).withSize(2, 1);
        tab.addNumber("Bottom RPS",    () -> shooter.getBottomRPS()).withPosition(2, 1).withSize(2, 1);

        // State and At Velocity Y/N
        tab.addString("Fire State",       () -> shooter.getFireStateString())       .withPosition(0, 2).withSize(2, 1);
        tab.addBoolean("At Velocity Tgt", () -> shooter.areMotorsAtVelocityTarget()).withPosition(2, 2).withSize(2, 1);
    }
}
