package frc.robot.mechanisms.rotarySwitch;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

// ------------------------------------------------------------
// ------------   RotarySwitch Telemetry Class   --------------
// ------------------------------------------------------------

public class RotarySwitchTelemetry {
    protected ShuffleboardTab tab;


    public RotarySwitchTelemetry(RotarySwitchSubSys rotarySwitch) {
        tab = Shuffleboard.getTab("RotarySwitch");
        tab.addNumber("Switch Volts", ()-> rotarySwitch.GetRotaryVolts()) .withPosition(0, 2).withSize(2, 2);
        tab.addNumber("Switch Pos", ()-> rotarySwitch.GetRotaryPos()) .withPosition(0, 4).withSize(2, 2);
    }
}
