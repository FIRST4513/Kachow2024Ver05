package frc.robot.mechanisms.elevator;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.robot.Robot;

public class ElevatorTelemetry {
    protected ShuffleboardTab tab;

    public ElevatorTelemetry( ElevatorSubSys Elevator) {
        tab = Shuffleboard.getTab("Elevator");

        tab.addString("Elev At Position",    () -> Robot.elevator.getElevPosStr())  .withPosition(0, 0).withSize(3, 2);

        tab.addNumber("Target",         () -> Robot.elevator.getTargetRotations())  .withPosition(0, 2 ).withSize(3, 2);
        tab.addNumber("Curr Rotations", () -> Robot.elevator.getRotations())        .withPosition(0, 3).withSize(3, 2);
        tab.addNumber("Vel.",           () -> Robot.elevator.elevatorMotor.getVelocity().getValueAsDouble())
                                                                                    .withPosition(0, 6).withSize(3, 2);
        tab.addNumber("Power",          () -> Robot.elevator.getSpeed())            .withPosition(0, 8).withSize(3, 2);
        tab.addBoolean("Elev Limit Sw", () -> Robot.elevator.getLowerLimitSw())     .withPosition(0, 10).withSize(3, 2);

    }
}
