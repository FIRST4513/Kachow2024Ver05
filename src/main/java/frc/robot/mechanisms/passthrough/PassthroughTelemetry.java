package frc.robot.mechanisms.passthrough;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class PassthroughTelemetry {
    protected ShuffleboardTab tab;

    public PassthroughTelemetry( PassthroughSubSys passthrough) {
        tab = Shuffleboard.getTab("Passthrough");
        
        tab.addNumber("PS Motor Power:",          () -> passthrough.getMotorPower())      .withPosition(0, 2).withSize(3, 2);
    }
}
