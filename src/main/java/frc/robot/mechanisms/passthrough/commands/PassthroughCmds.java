package frc.robot.mechanisms.passthrough.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import frc.robot.Robot;
import frc.robot.mechanisms.passthrough.PassthroughSubSys.PassthroughState;

public class PassthroughCmds {
    public static final double TIMEOUT = 10;

    public static void setupDefaultCommand() {
        Robot.passthrough.setDefaultCommand(stopPassthroughCmd());
    }

    // ------------ Intake Stop ------------------
    public static Command stopPassthroughCmd() {
        return new InstantCommand( () -> Robot.passthrough.stopMotors(), Robot.passthrough);
    }

    // ----- Intake Set State Commands -----
    public static Command passthroughSetState(PassthroughState newState) {
        return new InstantCommand(() -> Robot.passthrough.setNewState(newState));
    }

    // ----- Intake Set State Command Shortcuts -----
    public static Command setGroundIntakeCmd() { return passthroughSetState(PassthroughState.GROUND_INTAKE); }
    public static Command setHPIntakeCmd() { return passthroughSetState(PassthroughState.HP_INTAKE); }
    public static Command setEjectCmd() { return passthroughSetState(PassthroughState.EJECT); }
    public static Command setManualCmd() { return passthroughSetState(PassthroughState.MANUAL); }

    // ----- Commands with custom timing and until conditions -----
    public static Command groundIntakeUntilGamepieceCmd() {
        return new SequentialCommandGroup(
            setGroundIntakeCmd(),
            new WaitUntilCommand(() -> Robot.passthrough.getGamepieceDetected()),
            stopPassthroughCmd()
        );
    }

    public static Command hpIntakeUntilGamepieceCmd() {
        return new SequentialCommandGroup(
            setHPIntakeCmd(),
            new WaitUntilCommand(() -> Robot.passthrough.getGamepieceDetected()),
            stopPassthroughCmd()
        );
    }

    public static Command speakerShootUntilNoGamepieceCmd() {
        return new SequentialCommandGroup(
            setEjectCmd(),
            new WaitUntilCommand(() -> Robot.passthrough.getGamepieceNotDetected()),
            stopPassthroughCmd()
        );
    }
}
