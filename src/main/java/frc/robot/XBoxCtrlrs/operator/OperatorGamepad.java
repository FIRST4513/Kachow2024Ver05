package frc.robot.XBoxCtrlrs.operator;

import frc.lib.gamepads.Gamepad;
import frc.lib.gamepads.mapping.ExpCurve;
import frc.robot.Robot;
import frc.robot.RobotConfig;
import frc.robot.XBoxCtrlrs.operator.commands.OperatorGamepadCmds;
import frc.robot.mechanisms.pivot.commands.PivotCmds;
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
        /* ----- Overrides ----- */
        gamepad.leftBumper.onTrue(OperatorGamepadCmds.stopAllCmd());
        gamepad.rightBumper.onTrue(OperatorGamepadCmds.manualAllCmd()).onFalse(OperatorGamepadCmds.stopAllCmd());

        /* ----- Intaking ----- */
        gamepad.yButton.onTrue(OperatorGamepadCmds.hpIntakeUntilGamepiece());
        gamepad.aButton.onTrue(OperatorGamepadCmds.groundIntakeUntilGamepieceCmd());

        /* ----- Ejecting ----- */
        //         .onTrue(OperatorGamepadCmds.noAutoPosSpeakerShot());   // manually spool up shooter for anticipation
        gamepad.xButton.onTrue(OperatorGamepadCmds.noAutoPosSpeakerShot());
        gamepad.Dpad.Down.onTrue(OperatorGamepadCmds.readyForBumperShotCmd());  // shoot speaker when bumpered up or close
        gamepad.Dpad.Up.onTrue(OperatorGamepadCmds.readyForFarShotCmd());     // shoot speaker from far at max angle

        /* ----- Pivot Preset Positions ----- */
        //gamepad.xButton.and(gamepad.Dpad.Down)  .onTrue(PivotCmds.setZeroAndRunCmd());  // 0º angle
        //gamepad.xButton.and(gamepad.Dpad.Left)  .onTrue(PivotCmds.setLowAndRunCmd());   // 45º angle
        //gamepad.xButton.and(gamepad.Dpad.Up)    .onTrue(PivotCmds.setHighAndRunCmd());  // 195º angle
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
