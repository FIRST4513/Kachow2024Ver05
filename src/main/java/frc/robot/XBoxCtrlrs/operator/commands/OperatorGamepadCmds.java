package frc.robot.XBoxCtrlrs.operator.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.PrintCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Robot;
import frc.robot.XBoxCtrlrs.operator.commands.ArmElevComboMoveCmds.TargetPosition;
import frc.robot.mechanisms.arm.commands.ArmCmds;
import frc.robot.mechanisms.elevator.commands.ElevatorCmds;
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

    // Shoot Speaker
    public static Command intakeShooterSpeakerShootCmd() {
        return new SequentialCommandGroup(
            ArmElevComboMoveCmds.ArmAndElevMoveCmd(() -> Robot.arm.getArmCurrentAngle(), TargetPosition.SHOOTER),
            ShooterCmds.shooterSetSpeakerCmd(),
            new WaitCommand(3),
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

    /* ----- Arm, Elevator, Intake, and Shoot Combo Commands - INTAKING ----- */
    /**
     * Move Arm and Elevator to shoot pos, then run Intake and Shooter in
     * retract until gamepiece detected.
     * @return The Command to run said action
     */
    public static Command positionAndIntakeHPStation() {
        return new SequentialCommandGroup(
            new PrintCommand("HP Station intaking"),
            ArmElevComboMoveCmds.ArmAndElevMoveCmd(() -> Robot.arm.getArmCurrentAngle(), TargetPosition.SHOOTER),
            new PrintCommand("At pos, intaking until gamepiece"),
            intakeShooterIntakeHPStation(),
            new PrintCommand("Done")
        );
    }

    /**
     * Move Arm and Elevator to Ground, run Intake until gamepiece detected,
     * then move Arm and Elevator back to shoot pos (default).
     * @return The Command to run said action
     */
    public static Command positionAndIntakeGroundCmd() {
        return new SequentialCommandGroup(
            ArmElevComboMoveCmds.ArmAndElevMoveCmd(() -> Robot.arm.getArmCurrentAngle(), TargetPosition.GROUND),
            IntakeCmds.intakeGroundUntilGamepieceCmd(),
            ArmElevComboMoveCmds.ArmAndElevMoveCmd(() -> Robot.arm.getArmCurrentAngle(), TargetPosition.SHOOTER)
        );
    }

    /* ----- Arm, Elevator, Intake, and Shoot Combo Commands - EJECTING/SCORING ----- */

    /**
     * Move Arm and Elevator to Shoot position, then run intake and shooter
     * until gamepiece has left plus timeout
     * @return The Command to run said action
     */
    public static Command positionAndPrepShooterCmd() {
        return new SequentialCommandGroup(
            ArmElevComboMoveCmds.ArmAndElevMoveCmd(() -> Robot.arm.getArmCurrentAngle(), TargetPosition.SHOOTER),
            ShooterCmds.shooterSetSpeakerCmd()
        );
    }

    /**
     * Move Arm and Elevator to Amp, run intake to score piece until it has
     * left the intake plus extra time, then move Arm and Elevator back to
     * shoot pos (default)
     * @return The Command to run said action
     */
    public static Command positionAndAmpScoreCmd() {
        return new SequentialCommandGroup(
            new PrintCommand("Starting to amp cmd, setting arm and elevator"),
            ArmElevComboMoveCmds.ArmAndElevMoveCmd(() -> Robot.arm.getArmCurrentAngle(), TargetPosition.AMP),
            new PrintCommand("Running itnake to eject"),
            // IntakeCmds.intakeAmpCmd(2),
            new PrintCommand("Moving Arm and Elevator baack to shoot")
            // ArmElevComboMoveCmds.ArmAndElevMoveCmd(() -> Robot.arm.getArmCurrentAngle(), TargetPosition.SHOOTER)
        );
    }

    // [shoot to trap cmd here]

    /* ---- MANUAL CONTROL STUFF ----- */

    // Arm/Elev by TeleOp Command
    public static Command ControlArmElevIntakeShooterByJoysticksCmd() {
        return new ParallelCommandGroup(
            ArmCmds.ArmByJoystickCmd(),
            ElevatorCmds.ElevByJoystickCmd(),
            IntakeCmds.intakeSetManualCmd(),
            ShooterCmds.shooterSetManualCmd()
            // add shooter?
        );
    }

    // Stop Arm and Elevator
    public static Command stopSubSysCmd() {
        return new ParallelCommandGroup(
            ArmCmds.stopArmCmd(),
            ElevatorCmds.elevatorStopCmd(),
            IntakeCmds.intakeStopCmd(),
            ShooterCmds.stopShooterCmd()
        );
    }

    // -------------------- Rumble Controller  ---------------

    public static Command RumbleOperatorCmd(double intensity) {
        return new RunCommand(() -> Robot.operatorGamepad.rumble(intensity), Robot.operatorGamepad);
    }
}
