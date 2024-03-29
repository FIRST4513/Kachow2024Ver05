package frc.robot.mechanisms.exampleElevator;

import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import frc.lib.MotorConfigurations.motorFXConfig;
import frc.lib.mechanisms.Elevator.ElevatorControlConfig;
import frc.robot.RobotConfig;

public class ElevatorConfig {
    public static final double randomCustomRotation = 15;

    public static final ElevatorControlConfig elevConfig = new ElevatorControlConfig()
            .withLowPos(1)
            .withMidPos(20)
            .withTopPos(24)
            .withMMFeedForward(0.05)
            .withToBottomSpeed(-0.1)
            .withMMTolerance(0.1)
            .withPWMTolerance(0.1);
    
    public static final motorFXConfig motor1Config = new motorFXConfig()
            .withCanID(RobotConfig.Motors.exElev1)
            .withInvert(InvertedValue.Clockwise_Positive)
            .withCruiseVelocity(35)
            .withAcceleration(75)
            .withJerk(225)
            .withkP(16.0)
            .withkV(0.12)
            .withkS(0.05)
            .withNeutralMode(NeutralModeValue.Brake);
    
    public static final motorFXConfig motor2Config = new motorFXConfig()
            .withCanID(RobotConfig.Motors.exElev2)
            .withInvert(InvertedValue.CounterClockwise_Positive)
            .withCruiseVelocity(35)
            .withAcceleration(75)
            .withJerk(225)
            .withkP(8.0)
            .withkV(0.12)
            .withkS(0.05)
            .withNeutralMode(NeutralModeValue.Brake);

    public static final motorFXConfig motor3Config = new motorFXConfig()
            .withCanID(RobotConfig.Motors.exElev3)
            .withInvert(InvertedValue.CounterClockwise_Positive)
            .withCruiseVelocity(35)
            .withAcceleration(75)
            .withJerk(225)
            .withkP(8.0)
            .withkV(0.12)
            .withkS(0.05)
            .withNeutralMode(NeutralModeValue.Brake);
}
