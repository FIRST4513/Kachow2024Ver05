package frc.robot.mechanisms.exampleElevator;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.Robot;

public class ElevatorCmds {
    public static Command stopElevator() {
        return new InstantCommand(() -> Robot.elevator.stop());
    }

    public static Command setLow() {
        return new InstantCommand(() -> Robot.elevator.setLow());
    }

    public static Command setMid() {
        return new InstantCommand(() -> Robot.elevator.setMid());
    }

    public static Command setTop() {
        return new InstantCommand(() -> Robot.elevator.setTop());
    }

    public static Command setManual() {
        return new InstantCommand(() -> Robot.elevator.setManual(() -> Robot.operatorGamepad.getPivotAdjust()));
    }

    public static Command setCustomCommand() {
        return new InstantCommand(() -> Robot.elevator.setCustomMM(ElevatorConfig.randomCustomRotation));
    }
}
