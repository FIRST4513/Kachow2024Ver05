package frc.robot.mechanisms.climber;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class ClimberTelemetry {
    protected ShuffleboardTab tab;

    public ClimberTelemetry( ClimberSubSys Climber) {
        // todo: clean up tabs, add withSize and withPosition
        
        tab = Shuffleboard.getTab("Climber");
        tab.addNumber("Motor Power", () -> Climber.getSpeed());
        tab.addBoolean("Lower Switch?", () -> Climber.getLowerLimitSw());
        tab.addBoolean("Mid Switch?", () -> Climber.getMidLimitSw());
        tab.addNumber("Motor 1 Pos", () -> Climber.getRotations());
        tab.addString("State", () -> Climber.getStateString());
        tab.addNumber("Vel", () -> Climber.climbMotor1.getVelocity().getValueAsDouble());
    }
}
