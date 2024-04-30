package frc.robot.mechanisms.intake;

import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import frc.lib.MotorConfigurations.motorFXConfig;
import frc.robot.RobotConfig;

public class IntakeConfig {
    public static final double retractSpeed = -0.5;
    public static final double ejectSpeed = 0.5;      // 
    public static final double shooterFeedSpeed = 1;  // Fast Out
    
    public static final double gamepieceDetectDistance = 1;

    public static final motorFXConfig motorConfig = new motorFXConfig()
            .withCanID(RobotConfig.Motors.exIntake)
            .withNeutralMode(NeutralModeValue.Brake)
            .withInvert(InvertedValue.CounterClockwise_Positive);
}