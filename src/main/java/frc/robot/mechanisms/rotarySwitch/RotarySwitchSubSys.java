package frc.robot.mechanisms.rotarySwitch;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.RobotConfig;
import frc.robot.RobotTelemetry;


public class RotarySwitchSubSys extends SubsystemBase {
    public  AnalogInput              rotarySwitch;

    // Constructor
    public RotarySwitchSubSys() {
        rotarySwitch = new AnalogInput(RobotConfig.AnalogPorts.rotarySwitch);
        RobotTelemetry.print("RotarySwitch Subsystem Initialized: ");
    }
        
    public int GetRotaryPos() {
        double range = RotarySwitchConfig.RANGE;
        double volts = GetRotaryVolts();
        if (volts <= RotarySwitchConfig.POSITION0 + range )                                                        return 0;
        if ((volts >= RotarySwitchConfig.POSITION1 - range ) && (volts <= RotarySwitchConfig.POSITION1 + range ))  return 1;
        if ((volts >= RotarySwitchConfig.POSITION2 - range ) && (volts <= RotarySwitchConfig.POSITION2 + range ))  return 2;
        if ((volts >= RotarySwitchConfig.POSITION3 - range ) && (volts <= RotarySwitchConfig.POSITION3 + range ))  return 3;
        if ((volts >= RotarySwitchConfig.POSITION4 - range ) && (volts <= RotarySwitchConfig.POSITION4 + range ))  return 4;
        if ((volts >= RotarySwitchConfig.POSITION5 - range ) && (volts <= RotarySwitchConfig.POSITION5 + range ))  return 5;
        if ((volts >= RotarySwitchConfig.POSITION6 - range ) && (volts <= RotarySwitchConfig.POSITION6 + range ))  return 6;
        if ((volts >= RotarySwitchConfig.POSITION7 - range ) && (volts <= RotarySwitchConfig.POSITION7 + range ))  return 7;
        if ((volts >= RotarySwitchConfig.POSITION8 - range ) && (volts <= RotarySwitchConfig.POSITION8 + range ))  return 8;
        if ((volts >= RotarySwitchConfig.POSITION9 - range ) && (volts <= RotarySwitchConfig.POSITION9 + range ))  return 9;
        return 0;
    }
    
    public double GetRotaryVolts() {
        return rotarySwitch.getVoltage();
    }
}
