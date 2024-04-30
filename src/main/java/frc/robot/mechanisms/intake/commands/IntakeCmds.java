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
    public static Command intakeSetRetract() { return new InstantCommand(() -> Robot.intake.setRetract()); }
    public static Command intakeSetEject()   { return new InstantCommand(() -> Robot.intake.setEject()); }
    public static Command intakeSetFeed()    { return new InstantCommand(() -> Robot.intake.setShooterFeed()); }
    public static Command intakeSetManual()  { return new InstantCommand(() -> Robot.intake.setManual()); }

    public static Command intakeRetractUntilGamepiece(double extraTime) {
        return new SequentialCommandGroup(
            intakeSetRetract(),
            new WaitUntilCommand(() -> Robot.intake.getGamepieceDetected()),
            new WaitCommand(extraTime),
            intakeStopCmd()
        );
    }
}
