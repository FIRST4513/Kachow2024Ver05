package frc.robot.mechanisms.intake.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import frc.robot.Robot;

public class IntakeCmds {
    public void setupDefaultCommand() {
        Robot.intake.setDefaultCommand(intakeStopCmd());
    }

    /* ----- Intake Stop Command ----- */
    public static Command intakeStopCmd() {
        return new InstantCommand( () -> Robot.intake.stop(), Robot.intake);
    }

    /* ----- Intake Simple Set Commands ----- */
    public static Command intakeSetGroundCmd() { return new InstantCommand(() -> Robot.intake.setGround()); }
    public static Command intakeSetFeedCmd()   { return new InstantCommand(() -> Robot.intake.setShooterFeed()); }
    public static Command intakeSetHPCmd()     { return new InstantCommand(() -> Robot.intake.setHumanPlayer()); }
    public static Command intakeSetManualCmd() { return new InstantCommand(() -> Robot.intake.setManual()); }

    /* ----- Intake Command with Until Conditions */

    /**
     * Runs the intake at the ground intake speed until a gamepiece is detected. Will run indefinitely until Interrupted.
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
            new WaitCommand(secondsAfterGamepieceDeparture),
            intakeStopCmd()
        );
    }
}
