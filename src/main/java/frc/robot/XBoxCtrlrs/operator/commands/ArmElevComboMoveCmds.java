package frc.robot.XBoxCtrlrs.operator.commands;

import java.util.function.DoubleSupplier;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.PrintCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import frc.robot.Robot;
import frc.robot.mechanisms.arm.ArmConfig;
import frc.robot.mechanisms.arm.commands.ArmCmds;
import frc.robot.mechanisms.elevator.commands.ElevatorCmds;

public class ArmElevComboMoveCmds{
    public static enum TargetPosition {
        SAFE_TRAVEL,
        SHOOTER,
        GROUND,
        TRAP,
        AMP
    }

    public static Command ArmAndElevMoveCmd(DoubleSupplier armAngleGetter, TargetPosition positionTo) {
        return new ConditionalCommand(
            // Case 1 : Out to Out - Is a Simple process
            outToOutCmd(positionTo),
            new ConditionalCommand(
                // Case 2 : In to In - Is a Simple process
                inToInCmd(positionTo),
                new ConditionalCommand(
                    // Case 3 : Out to In - Not quit as Simple of a process
                    outToInCmd(positionTo),
                    new ConditionalCommand(
                        // Case 4 : In to Out - Not quit as Simple of a process
                        inToOutCmd(positionTo),
                        // Default: Do nothing, print error message
                        new PrintCommand("ERROR"),
                        () -> isCaseInToOut(armAngleGetter, positionTo)
                    ),
                    () -> isCaseOutToIn(armAngleGetter, positionTo)
                ),
                () -> isCaseInToIn(armAngleGetter, positionTo)
            ),
            () -> isCaseOutToOut(armAngleGetter, positionTo)
        );
    }

    /* ----- Case Condition Boolean Methods ----- */
    // case 1
    public static boolean isCaseOutToOut(DoubleSupplier angleFrom, TargetPosition positionTo) {
        return (isOutside(angleFrom.getAsDouble()) && isOutside(positionTo));
    }

    // case 2
    public static boolean isCaseInToIn(DoubleSupplier angleFrom, TargetPosition positionTo) {
        return (isInside(angleFrom.getAsDouble()) && isInside(positionTo));
    }

    // case 3
    public static boolean isCaseOutToIn(DoubleSupplier angleFrom, TargetPosition positionTo) {
        return (isOutside(angleFrom.getAsDouble()) && isInside(positionTo));
    }

    // case 4
    public static boolean isCaseInToOut(DoubleSupplier angleFrom, TargetPosition positionTo) {
        return (isInside(angleFrom.getAsDouble()) && isOutside(positionTo));
    }

    /* ----- INSIDE vs OUTSIDE Boolean Methods ----- */
    public static boolean isInside(double angle) {
        return angle < ArmConfig.SAFE_TRAVEL_MIN;
    }

    public static boolean isInside(TargetPosition position) {
        switch (position) {
            case SAFE_TRAVEL: return false;
            case SHOOTER:     return true;
            case GROUND:      return false;
            case AMP:         return false;
            case TRAP:        return false;
            default:          return true;
        }
    }

    public static boolean isOutside(double angle) {
        return angle > ArmConfig.SAFE_TRAVEL_MIN;
    }

    public static boolean isOutside(TargetPosition position) {
        return !isInside(position);
    }

    /* ----- GND vs NOT Bolean Methods ----- */
    public static boolean isGround(String position) {
        if (position == "GROUND") return true;
        return false;
    }

    /* ----- Commands to run for Cases ----- */
    public static Command outToOutCmd(TargetPosition positionTo) {
        // simply set arm and elevator to their new target poses, there should be no interferences
        // return new PrintCommand("Case 1 : Out To Out !!!!");
        return new ParallelCommandGroup(
            new PrintCommand("setting arm and elev"),
            setArm(positionTo),
            setElev(positionTo),
            new PrintCommand("done")
        );
    }

    public static Command inToInCmd(TargetPosition positionTo) {
        // simply set arm and elevator to their new target poses, there should be no interferences
        // return new PrintCommand("Case 2 : In To In !!!!");
        return new SequentialCommandGroup(
            new PrintCommand("In to in"),
            new PrintCommand("Setting Elevator"),
            setElev(positionTo),
            new WaitUntilCommand(() -> Robot.elevator.isAtTarget()),
            new PrintCommand("Elevator at target, setting arm"),
            setArm(positionTo),
            new PrintCommand("Done")
        );
    }

    public static Command outToInCmd(TargetPosition positionTo) {
        // move arm to safe travel, move elevator to bottom, then move arm to its intended pos
        // return new PrintCommand("Case 3 : Out To In !!!!");
        return new SequentialCommandGroup(
            new PrintCommand("Setting arm to safe travel"),
            // ArmCmds.armToSafeTravelCmd().onlyIf(() -> isGround(positionTo)),
            setArm(TargetPosition.SAFE_TRAVEL),
            new WaitUntilCommand(() -> Robot.arm.isAtSafeTravelPos()),
            new PrintCommand("arm done setting elev to bottom"),
            setElev(TargetPosition.SHOOTER),  // same as bottom
            new WaitUntilCommand(() -> Robot.elevator.isAtTarget()),
            new PrintCommand("elevator done setting arm"),
            setArm(positionTo),
            new PrintCommand("done")
        );
    }

    public static Command inToOutCmd(TargetPosition positionTo) {
        // send elev to bottom, move arm to safe, then move arm and elev to intended pos
        // return new PrintCommand("Case 4 : In To Out !!!!");
        return new SequentialCommandGroup(
            new PrintCommand("In to out, setting elevator"),
            setElev(TargetPosition.SHOOTER),  // same as bottom
            new WaitUntilCommand(() -> Robot.elevator.isAtTarget()),
            new PrintCommand("Eelvator done setting arm"),
            setArm(TargetPosition.SAFE_TRAVEL),
            new WaitUntilCommand(() -> Robot.arm.isAtSafeTravelPos()),
            new PrintCommand("arm done setting both to final"),
            setElev(positionTo),
            setArm(positionTo)
        );
    }

    /* ----- SubSystem Set Methods ----- */
    public static Command setArm(TargetPosition tgtPose) {
        switch (tgtPose) {
            case SAFE_TRAVEL: return ArmCmds.armToSafeTravelCmd();
            case SHOOTER:     return ArmCmds.armToShooterCmd();
            case GROUND:      return ArmCmds.armToGroundCmd();
            case AMP:         return ArmCmds.armToAmpCmd();
            case TRAP:        return ArmCmds.armToTrapCmd();
            default:          return ArmCmds.armToSafeTravelCmd();
        }
    }

    public static Command setElev(TargetPosition tgtPose) {
        switch (tgtPose) {
            case SAFE_TRAVEL: return ElevatorCmds.elevatorStopCmd();
            case SHOOTER:     return ElevatorCmds.elevatorSetBottomCmd();  // shooter = bottom
            case GROUND:      return ElevatorCmds.elevatorSetGroudCmd();
            case AMP:         return ElevatorCmds.elevatorSetTopCmd();
            case TRAP:        return ElevatorCmds.elevatorSetTopCmd();
            default:          return ElevatorCmds.elevatorStopCmd();
        }
    }

    /* ----- Mr. Eby's Idea of having a class for more dynamicness ----- */

    // public class ArmElevState {
    //     public enum PositionRange {
    //         SHOOTER,
    //         STOW,
    //         SAFE_TO_TRAVEL,
    //         GROUND,
    //         UNSAFE_OUTSIDE
    //     }

    //     private boolean isInside;
    //     private PositionRange positionRange;

    //     public ArmElevState(boolean isInside, PositionRange positionRange) {
    //         this.isInside = isInside;
    //         this.positionRange = positionRange;
    //     }

    //     public ArmElevState fromAngle(double armAngle) {
    //         return this(isInsideFromAngle(armAngle), positionRangeFromAngle(armAngle));
    //     }

    //     public boolean isInsideFromAngle(double angle) {
    //         return angle < 5;
    //     }

    //     public PositionRange positionRangeFromAngle(double angle) {
    //         if (angle < -5) { return PositionRange.SHOOTER; }
    //         else if (angle < 5) { return PositionRange.STORE}
    //     }
    // }
}
