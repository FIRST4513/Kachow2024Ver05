package frc.robot.mechanisms.exampleElevator;

import frc.lib.mechanisms.Elevator.SimpleElevatorFX;
import frc.robot.Robot;
import frc.robot.RobotConfig;

public class exElevator extends SimpleElevatorFX {
    /* ----- Constructor ----- */
    public exElevator() {
        super(ElevatorConfig.elevConfig,  // general config for elevator
              RobotConfig.LimitSwitches.exElevatorLimitSw,  // port for limit switch
              ElevatorConfig.motor1Config  // config for first motor
        );
    }

    /* ----- Methods for custom actions ----- */
    public void setRandomHeight() {
        setCustomMM(ElevatorConfig.randomCustomRotation);
    }

    public void setManualControl() {
        setManual(() -> Robot.pilotGamepad.getClimberAdjustInput());
    }
}
