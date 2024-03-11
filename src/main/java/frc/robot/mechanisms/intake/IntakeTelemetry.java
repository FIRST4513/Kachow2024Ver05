package frc.robot.mechanisms.intake;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class IntakeTelemetry {
    protected ShuffleboardTab tab;

    public IntakeTelemetry( IntakeSubSys intake) {
        tab = Shuffleboard.getTab("Intake");
        tab.addBoolean("Gamepiece Detected?", () -> intake.getGamepieceDetected()).withPosition(0, 0).withSize(2, 1);
        tab.addNumber("Motor Speed:",         () -> intake.getMotorSpeed())       .withPosition(0, 1).withSize(2, 2);
        tab.addNumber("IR Sensor Value:",     () -> intake.getSensorVal())        .withPosition(0, 3).withSize(2, 2);
        tab.addString("State",                () -> intake.getStateString())      .withPosition(0, 4).withSize(2, 1);
    }
}
