package frc.robot.mechanisms.exampleElevator;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class ElevatorTelemetry {
    protected ShuffleboardTab tab;

    public ElevatorTelemetry( exElevator elev) {
        tab = Shuffleboard.getTab("Elevator");
        tab.addNumber("Speed", () -> elev.getSpeed()).withPosition(0, 0).withSize(2, 1);
        tab.addNumber("Rotations", () -> elev.getRotations()).withPosition(0, 1).withSize(2, 1);
    }
}
