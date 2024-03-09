package frc.robot.mechanisms.passthrough;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class PassthroughTelemetry {
    protected ShuffleboardTab tab;

    public PassthroughTelemetry( PassthroughSubSys passthrough) {
        tab = Shuffleboard.getTab("Passthrough");
        tab.addString("PS - Gamepiece Detected?", () -> passthrough.isGamepieceDetected()).withPosition(0, 0).withSize(3, 2);
        tab.addNumber("PS Motor Power:",          () -> passthrough.getMotorPower())      .withPosition(0, 2).withSize(3, 2);
        tab.addNumber("PS Sensor Value:",         () -> passthrough.getSensorVal())       .withPosition(0, 4).withSize(3, 2);
    }
}
