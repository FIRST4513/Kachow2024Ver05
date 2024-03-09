package frc.robot.XBoxCtrlrs.operator;

import frc.lib.gamepads.Gamepad;
import frc.lib.gamepads.mapping.ExpCurve;
import frc.robot.Robot;
import frc.robot.RobotConfig;
import frc.robot.XBoxCtrlrs.operator.commands.OperatorGamepadCmds;
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
        gamepad.rightBumper.onTrue(ShooterCmds.shooterSetManualCmd()).onFalse(ShooterCmds.stopShooterCmd());

        gamepad.aButton.onTrue(ShooterCmds.shooterSetRetractCmd()).onFalse(ShooterCmds.stopShooterCmd());
        gamepad.bButton.onTrue(ShooterCmds.shooterSetSpeakerCmd()).onFalse(ShooterCmds.stopShooterCmd());
    }

    @Override
    public void setupTestButtons() {}

    public void setupDisabledButtons() {}

    // ---- value getters -----
    public double getTriggerTwist() {
        return intakeThrottleCurve.calculateMappedVal(gamepad.triggers.getTwist());
    }

    // ---- rumble method -----

    public void rumble(double intensity) {
        this.gamepad.setRumble(intensity, intensity);
    }

    /*   "the sparkle" -madi   */
}
