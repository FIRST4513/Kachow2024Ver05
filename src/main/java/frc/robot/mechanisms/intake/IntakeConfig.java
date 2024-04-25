package frc.robot.mechanisms.intake;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import frc.lib.MotorConfigurations.motorSRXConfig;
import frc.robot.RobotConfig;

public class IntakeConfig {
    public static final double retractSpeed = 1;
    public static final double ejectSpeed = -1;  // unused
    public static final double shooterFeedSpeed = 1.2;  // ???
    
    public static final double gamepieceDetectDistance = 1;

    public static final motorSRXConfig motorConfig = new motorSRXConfig()
            .withCanID(RobotConfig.Motors.exIntake)
            .withNeutralMode(NeutralMode.Brake)
            .withContinuousCurrentLimit(30)
            .withPeakCurrentLimit(40)
            .withPeakCurrentDurration(100);
}