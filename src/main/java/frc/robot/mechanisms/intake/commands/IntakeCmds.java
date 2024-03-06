package frc.robot.mechanisms.intake.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import frc.robot.Robot;
import frc.robot.mechanisms.intake.IntakeSubSys.IntakeState;

public class IntakeCmds {
    public static final double TIMEOUT = 10;

    public static void setupDefaultCommand() {
        Robot.intake.setDefaultCommand(intakeStopCmd());
    }

    // ------------ Intake Stop ------------------
    public static Command intakeStopCmd() {
        return new InstantCommand( () -> Robot.intake.stopMotors(), Robot.intake);
    }

    // ----- Intake Set State Commands -----
    public static Command intakeSetState(IntakeState newState) {
        return new InstantCommand(() -> Robot.intake.setNewState(newState));
    }

    // ----- Intake Set State Command Shortcuts -----
    public static Command intakeSetGroundCmd() { return intakeSetState(IntakeState.GROUND); }
    public static Command intakeSetFeedCmd() { return intakeSetState(IntakeState.SHOOTER_FEED); }
    public static Command intakeSetTrapCmd() { return intakeSetState(IntakeState.TRAP); }
    public static Command intakeSetAmpCmd() { return intakeSetState(IntakeState.AMP); }
    public static Command intakeSetManualCmd() { return intakeSetState(IntakeState.MANUAL); }

    // ----- Intake Commands with Until Conditions -----
    /**
     * Runs the intake at the ground intake speed until a gamepiece is detected. Will timeout after 10 seconds and quit.
     * @return A SequentialCommandGroup
     */
    public static Command intakeGroundUntilGamepieceCmd() {
        return new SequentialCommandGroup(
            intakeSetGroundCmd(),
            new WaitUntilCommand(() -> Robot.intake.getGamepieceDetected()),
            intakeStopCmd()
        );
    }

    /**
     * Run the intake at the shooter feed gamepiece speed until the gamepiece has left the intake, plus a given amount of time.
     * <p>
     * Will timeout after 10 seconds, plus your given time.
     * @param secondsAfterGamepieceDeparture Time to keep running after the gamepiece has left the intake's sensor
     * @return A SequentialCommandGroup
     */
    public static Command intakeFeedCmd(double secondsAfterGamepieceDeparture) {
        return new SequentialCommandGroup(
            intakeSetFeedCmd(),
            // new WaitCommand(TIMEOUT).until(() -> Robot.intake.not()),  // possible change to WaitUntilCommand
            new WaitCommand(secondsAfterGamepieceDeparture),
            intakeStopCmd()
        );
    }

    /**
     * Run the intake at the amp eject speed until the gamepiece has left the intake, plus a given amount of time.
     * <p>
     * Will timeout after 10 seconds, plus your given time.
     * @param secondsAfterGamepieceDeparture Time to keep running after the gamepiece has left the intake's sensor
     * @return A SequentialCommandGroup
     */
    public static Command intakeAmpCmd(double secondsAfterGamepieceDeparture) {
        return new SequentialCommandGroup(
            intakeSetFeedCmd(),
            new WaitCommand(TIMEOUT).until(() -> Robot.intake.getGamepieceDetected()),  // possible change to WaitUntilCommand
            new WaitCommand(secondsAfterGamepieceDeparture),
            intakeStopCmd()
        );
    }
}
