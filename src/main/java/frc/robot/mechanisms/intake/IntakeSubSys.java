package frc.robot.mechanisms.intake;

import frc.lib.mechanisms.Intake.Intake_SRX;
import frc.robot.Robot;
import frc.robot.RobotConfig;

public class IntakeSubSys extends Intake_SRX {

    /* ----- Constructor ----- */
    public IntakeSubSys() { 
        super(IntakeConfig.motorConfig,
              RobotConfig.AnalogPorts.intakeGamepieceSensor,
              IntakeConfig.retractSpeed,
              IntakeConfig.ejectSpeed,
              IntakeConfig.gamepieceDetectDistance);
    }

    /* ----- methods for custom speed settings for this year's game + robot ----- */
    public void setGround() {
        setRetract();
    }

    public void setShooterFeed() {
        setRetract();
    }

    public void setHumanPlayer() {
        stop();
    }

    public void setManual() {
        setCustom(() -> Robot.pilotGamepad.getClimberAdjustInput());
    }
}
