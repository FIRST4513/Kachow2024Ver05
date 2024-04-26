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
            .withMMFeedForward(0.2)
            .withToBottomSpeed(-0.1)
            .withMMTolerance(0.1)
            .withPWMTolerance(0.1);
    
    public static final motorFXConfig motor1Config = new motorFXConfig()
            .withCanID(RobotConfig.Motors.exElevator)
            .withInvert(InvertedValue.CounterClockwise_Positive)
            .withCruiseVelocity(200)
            .withAcceleration(200)
            .withJerk(500)
            .withkP(50.0)
            .withkV(0.12)
            .withkS(0.05)
            .withNeutralMode(NeutralModeValue.Brake);
}
