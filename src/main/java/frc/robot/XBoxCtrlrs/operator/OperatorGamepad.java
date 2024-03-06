package frc.robot.XBoxCtrlrs.operator;

import frc.lib.gamepads.Gamepad;
import frc.lib.gamepads.mapping.ExpCurve;
import frc.robot.Robot;
import frc.robot.RobotConfig;
import frc.robot.XBoxCtrlrs.operator.commands.ArmElevComboMoveCmds;
import frc.robot.XBoxCtrlrs.operator.commands.OperatorGamepadCmds;
import frc.robot.XBoxCtrlrs.operator.commands.ArmElevComboMoveCmds.TargetPosition;
import frc.robot.mechanisms.intake.commands.IntakeCmds;
import frc.robot.mechanisms.shooter.commands.ShooterCmds;

public class OperatorGamepad extends Gamepad {
    public static ExpCurve intakeThrottleCurve = new ExpCurve(
        OperatorGamepadConfig.intakeSpeedExp,
        OperatorGamepadConfig.intakeSpeedOffset,
        OperatorGamepadConfig.intakeSpeedScaler,
        OperatorGamepadConfig.intakeSpeedDeadband);

    public static ExpCurve elevThrottleCurve = new ExpCurve(
        OperatorGamepadConfig.elevSpeedExp,
        OperatorGamepadConfig.elevSpeedOffset,
        OperatorGamepadConfig.elevSpeedScaler,
        OperatorGamepadConfig.elevSpeedDeadband);

    public static ExpCurve armThrottleCurve = new ExpCurve(
        OperatorGamepadConfig.armSpeedExp,
        OperatorGamepadConfig.armSpeedOffset,
        OperatorGamepadConfig.armSpeedScaler,
        OperatorGamepadConfig.armSpeedDeadband);

    /* Contstructor */
    public OperatorGamepad() {
        super("Operator", RobotConfig.Gamepads.operatorGamepadPort);
    }
    
    // ----- Gamepad specific methods for button assignments -----
    public void setupTeleopButtons() {
        /* ----- Competition Buttons, including Combos */

        /* Intaking Button Combos */
        gamepad.aButton.and(gamepad.Dpad.Up).onTrue(OperatorGamepadCmds.positionAndIntakeHPStation());
        // gamepad.aButton.and(gamepad.Dpad.Down).onTrue(OperatorGamepadCmds.positionAndIntakeGroundCmd());

        /* Ejecting/Scoring Button Combos */
        gamepad.bButton.and(gamepad.Dpad.Up).onTrue(OperatorGamepadCmds.intakeFeedSpeaker());
        gamepad.bButton.and(gamepad.Dpad.Left).onTrue(OperatorGamepadCmds.positionAndPrepShooterCmd());
        gamepad.bButton.and(gamepad.Dpad.Down).onTrue(OperatorGamepadCmds.positionAndAmpScoreCmd());

        gamepad.xButton.onTrue(ArmElevComboMoveCmds.ArmAndElevMoveCmd(() -> Robot.arm.getArmCurrentAngle(), TargetPosition.SHOOTER));
        // gamepad.bButton.and(gamepad.Dpad.Left).or(gamepad.Dpad.Right).onTrue(trap cmd here);
        
        /* Manual Control of Arm Elevator and Intake */
        gamepad.rightBumper.whileTrue(OperatorGamepadCmds.ControlArmElevIntakeShooterByJoysticksCmd()).onFalse(OperatorGamepadCmds.stopIntakeAndShooterCmd());
        gamepad.leftBumper.whileTrue(OperatorGamepadCmds.stopSubSysCmd());
        
        // TESTING BUTTONS
        // gamepad.xButton.onTrue(ArmElevComboMoveCmds.ArmAndElevMoveCmd(() -> Robot.arm.getArmCurrentAngle(), TargetPosition.SHOOTER));
        // gamepad.aButton.onTrue(ArmElevComboMoveCmds.ArmAndElevMoveCmd(() -> Robot.arm.getArmCurrentAngle(), TargetPosition.SAFE_TRAVEL));
        // gamepad.bButton.onTrue(ArmElevComboMoveCmds.ArmAndElevMoveCmd(() -> Robot.arm.getArmCurrentAngle(), TargetPosition.GROUND));
        
        // other cmds (old/unused)
        
        // // Intake from Human Player (Y + Dpad Down)
        // gamepad.yButton.and(gamepad.Dpad.Down).onTrue(OperatorGamepadCmds.hpIntakeCmd())
        //             .onFalse(ShooterCmds.stopShooterCmd());
        // // gamepad.aButton.and(gamepad.Dpad.Down).onTrue(OperatorGamepadCmds.groundIntake());

        // // Shoot Speaker and Amp
        // gamepad.yButton.and(gamepad.Dpad.Up).onTrue(OperatorGamepadCmds.shootSpeakerCmd());
        // gamepad.yButton.and(gamepad.Dpad.Right).onTrue(OperatorGamepadCmds.shootDownIntoAmpFromIntakeCmd());
    }

    @Override
    public void setupTestButtons() {}

    public void setupDisabledButtons() {}

    // ---- value getters -----
    public double getElevInput() {
        double input = gamepad.leftStick.getY();
        if (Math.abs(input) > 0.3) return input / 6.0;
        return 0;
    }

    public double getArmInput() {
        return gamepad.rightStick.getY() / 4.0 ;
    }

    public double getTriggerTwist() {
        return intakeThrottleCurve.calculateMappedVal(gamepad.triggers.getTwist());
    }

    // ---- rumble method -----

    public void rumble(double intensity) {
        this.gamepad.setRumble(intensity, intensity);
    }

    /*   "the sparkle" -madi   */
}
