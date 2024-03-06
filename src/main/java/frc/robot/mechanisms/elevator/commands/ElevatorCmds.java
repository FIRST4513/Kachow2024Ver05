package frc.robot.mechanisms.elevator.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.PrintCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Robot;
import frc.robot.mechanisms.elevator.ElevatorConfig;

public class ElevatorCmds {
    public void setupDefaultCommand() {
        // Robot.elevator.setDefaultCommand(
        //     new RunCommand(() -> Robot.elevator.adjustTargetHeight(Robot.operatorGamepad.getArmInput()))
        // );
    }

    // ------------ Elevator Stop ------------------
    public static Command elevatorStopCmd() {
        return new InstantCommand(() -> Robot.elevator.stopMotor(), Robot.elevator);
    }

    // ---------- Elevator Set Cmds ----------
    public static Command elevatorSetRotationsCmd(double rotations) {
        return new InstantCommand(() -> Robot.elevator.setTargetRotations(rotations));
    }

    // ---------- Elevator Set Cmds ----------
    public static Command ElevByJoystickCmd() {
        return new RunCommand(
        () -> Robot.elevator.adjustMMTarget(() -> Robot.operatorGamepad.getElevInput()), Robot.elevator);
    }

    // ----- Elevator Set Command Shortcuts -----
    // Elev to - Bottom
    public static Command elevatorSetBottomCmd()    { return elevatorSetRotationsCmd(0); }

    // Elev to Top - Should we only do this if arm angle > some value
    public static Command elevatorSetTopCmd()       { return elevatorSetRotationsCmd(ElevatorConfig.posTopRot); }

    // Elev to Ground Pickup Position
    public static Command elevatorSetGroudCmd()     { return elevatorSetRotationsCmd(ElevatorConfig.posGroundRot); }

    // Elev to Mid 
    public static Command elevatorSetMidCmd()       { return elevatorSetRotationsCmd(ElevatorConfig.posMidRot); }
}
