package frc.robot.mechanisms.intake;

import frc.lib.mechanisms.Intake.Intake_FX;
import frc.robot.Robot;
import frc.robot.RobotConfig;

public class IntakeSubSys extends Intake_FX {

    /* ----- Constructor ----- */
    public IntakeSubSys() { 
        super(IntakeConfig.motorConfig,
              RobotConfig.AnalogPorts.intakeGamepieceSensor,
              IntakeConfig.retractSpeed,
              IntakeConfig.ejectSpeed,
              IntakeConfig.gamepieceDetectDistance);
    }

    /* ----- methods for custom speed settings for this year's game + robot ----- */
    public void setShooterFeed() {
        setCustom(IntakeConfig.shooterFeedSpeed);
    }

    public void setManual() {
        setCustom(() -> Robot.pilotGamepad.getClimberAdjustInput());
    }
}
