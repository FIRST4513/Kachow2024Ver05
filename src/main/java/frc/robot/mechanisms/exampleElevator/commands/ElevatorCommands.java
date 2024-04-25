package frc.robot.mechanisms.exampleElevator.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.Robot;

public class ElevatorCommands {
    public static Command stop() { return new InstantCommand(() -> Robot.elevator.stop()); }

    public static Command setBottom()    { return new InstantCommand(() -> Robot.elevator.setBottom()); }
    public static Command setLow()       { return new InstantCommand(() -> Robot.elevator.setLow()); }
    public static Command setMid()       { return new InstantCommand(() -> Robot.elevator.setMid()); }
    public static Command setTop()       { return new InstantCommand(() -> Robot.elevator.setTop()); }
    public static Command setManualPWM() { return new InstantCommand(() -> Robot.elevator.setBottom()); }
    
    public static Command setCustomMM()  { return new InstantCommand(() -> Robot.elevator.setBottom()); }
}
