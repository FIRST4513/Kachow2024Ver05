package frc.robot.XBoxCtrlrs.operator.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.PrintCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Robot;

import frc.robot.mechanisms.intake.commands.IntakeCmds;
import frc.robot.mechanisms.shooter.commands.ShooterCmds;

public class OperatorGamepadCmds {
    /** Set default command to turn off the rumble */
    public static void setupDefaultCommand() {
    //    Robot.pilotGamepad.setDefaultCommand(ProcessAndSetRumbleCmd());
    }

    /* ----- Intake and Shooter only commands ----- */

    public static Command stopIntakeAndShooterCmd() {
        return new ParallelCommandGroup(
            IntakeCmds.intakeStopCmd(),
            ShooterCmds.stopShooterCmd()
        );
    }

    // Shoot Speaker
    public static Command intakeFeedSpeaker() {
        return new SequentialCommandGroup(
            IntakeCmds.intakeFeedCmd(5),
            ShooterCmds.stopShooterCmd(),
            IntakeCmds.intakeStopCmd()
        );
    }


    // Intake from Human Player Station
    public static Command intakeShooterIntakeHPStation() {
        return new SequentialCommandGroup(
            ShooterCmds.shooterSetRetractCmd(),
            IntakeCmds.intakeGroundUntilGamepieceCmd(),
            ShooterCmds.stopShooterCmd()
        );
    }
   
    // -------------------- Rumble Controller  ---------------

    public static Command RumbleOperatorCmd(double intensity) {
        return new RunCommand(() -> Robot.operatorGamepad.rumble(intensity), Robot.operatorGamepad);
    }
}
