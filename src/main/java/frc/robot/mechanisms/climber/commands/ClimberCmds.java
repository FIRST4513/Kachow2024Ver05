package frc.robot.mechanisms.climber.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.Robot;
import frc.robot.mechanisms.climber.ClimberSubSys.ClimbState;

public class ClimberCmds {
    public static void setupDefaultCommand() {
        // Robot.climber.setDefaultCommand(
        //     climberSetManual()
        //);
    }

    // ------------ Climber Stop ------------------
    public static Command climberStopCmd() {
        return new InstantCommand(() -> Robot.climber.stopMotors(), Robot.climber);
    }

    // ---------- Climber Set State Command ----------
    public static Command climberSetState(ClimbState newState) {
        return new InstantCommand(() -> Robot.climber.setNewState(newState), Robot.climber);
    }

    public static Command climberSetManual() { return climberSetState(ClimbState.MANUAL); }
    public static Command climberSetBottom() { return climberSetState(ClimbState.BOTTOM); }
    public static Command climberSetClear() { return climberSetState(ClimbState.CLEAR_CHAIN); }
    public static Command climberSetOnChain() { return climberSetState(ClimbState.ON_CHAIN); }
}
