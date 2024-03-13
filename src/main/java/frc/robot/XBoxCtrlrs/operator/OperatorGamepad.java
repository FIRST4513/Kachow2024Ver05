package frc.robot.XBoxCtrlrs.operator;

import frc.lib.gamepads.Gamepad;
import frc.lib.gamepads.mapping.ExpCurve;
import frc.robot.Robot;
import frc.robot.RobotConfig;
import frc.robot.XBoxCtrlrs.operator.commands.OperatorGamepadCmds;
import frc.robot.mechanisms.pivot.commands.PivotCmds;
import frc.robot.mechanisms.shooter.commands.ShooterAimAndFireCmd;

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
        /* ----- Overrides ----- */
        gamepad.leftBumper.onTrue(OperatorGamepadCmds.stopAllCmd());
        gamepad.rightBumper.onTrue(OperatorGamepadCmds.manualAllCmd()).onFalse(OperatorGamepadCmds.stopAllCmd());

        /* ----- Intaking ----- */
        gamepad.aButton.and(gamepad.Dpad.Up)    .onTrue(OperatorGamepadCmds.hpIntakeUntilGamepiece());
        gamepad.aButton.and(gamepad.Dpad.Down)  .onTrue(OperatorGamepadCmds.groundIntakeUntilGamepieceCmd());

        /* ----- Ejecting ----- */
        // gamepad.bButton.and(gamepad.Dpad.Up)    .onTrue(new ShooterAimAndFireCmd(5));
        gamepad.bButton.and(gamepad.Dpad.Left)  .onTrue(OperatorGamepadCmds.noAutoPosSpeakerShot());

        // gamepad.xButton.and(gamepad.Dpad.Up)    .onTrue(PivotCmds.setHighAndRunCmd());
        // gamepad.xButton.and(gamepad.Dpad.Right) .onTrue(PivotCmds.setMidAndRunCmd());
        gamepad.xButton.and(gamepad.Dpad.Down)  .onTrue(PivotCmds.setLowAndRunCmd());
        gamepad.xButton.and(gamepad.Dpad.Up).onTrue(PivotCmds.setHighAndRunCmd());
    }

    @Override
    public void setupTestButtons() {}

    public void setupDisabledButtons() {}

    // ---- value getters -----
    public double getTriggerTwist() {
        return intakeThrottleCurve.calculateMappedVal(gamepad.triggers.getTwist());
    }

    public double getPivotAdjust() {
        return gamepad.rightStick.getY();
    }

    // ---- rumble method -----

    public void rumble(double intensity) {
        this.gamepad.setRumble(intensity, intensity);
    }

    /*   "the sparkle" -madi   */
}
