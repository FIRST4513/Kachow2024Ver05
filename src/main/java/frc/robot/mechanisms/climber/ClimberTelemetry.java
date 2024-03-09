package frc.robot.mechanisms.climber;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class ClimberTelemetry {
    protected ShuffleboardTab tab;

    public ClimberTelemetry( ClimberSubSys Climber) {
        // todo: clean up tabs, add withSize and withPosition
        
        tab = Shuffleboard.getTab("Climber");

        // Left Motor Pos and Switch
        tab.addNumber("Left Enc Pos", () -> Climber.getLeftRotations()).withPosition(0, 0).withSize(2, 1);
        tab.addBoolean("Left Limit Sw", () -> Climber.getLeftLowerSw()).withPosition(0, 1).withSize(2, 1);

        // Right Motor Pos and Switch
        tab.addNumber("Right Enc Pos", () -> Climber.getRightRotations()).withPosition(2, 0).withSize(2, 1);
        tab.addBoolean("Right Limit Sw", () -> Climber.getRightLowerSw()).withPosition(2, 1).withSize(2, 1);

        // General State
        tab.addString("State", () -> Climber.getStateString()).withPosition(4, 0).withSize(2, 2);
    }
}
