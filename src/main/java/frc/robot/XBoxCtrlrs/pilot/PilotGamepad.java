package frc.robot.XBoxCtrlrs.pilot;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.lib.gamepads.Gamepad;
import frc.lib.gamepads.mapping.ExpCurve;
import frc.robot.Robot;
import frc.robot.Robot.TeamAlliance;
import frc.robot.RobotConfig;
import frc.robot.XBoxCtrlrs.pilot.PilotGamepadConfig.MaxSpeeds;
import frc.robot.mechanisms.exampleElevator.commands.ElevatorCommands;
import frc.robot.mechanisms.intake.commands.IntakeCmds;
// import frc.robot.mechanisms.climber.commands.ClimberCmds;
import frc.util.FieldConstants;

/** Used to add buttons to the pilot gamepad and configure the joysticks */
public class PilotGamepad extends Gamepad {
    //public final PilotGamepadTelemetry telemetry;
    public static ExpCurve forwardSpeedCurve =
            new ExpCurve(
                    PilotGamepadConfig.forwardSpeedExp,
                    PilotGamepadConfig.forwardSpeedOffset,
                    PilotGamepadConfig.forwardSpeedScaler,
                    PilotGamepadConfig.forwardSpeedDeadband);
    public static ExpCurve sidewaysSpeedCurve =
            new ExpCurve(
                    PilotGamepadConfig.sidewaysSpeedExp,
                    PilotGamepadConfig.sidewaysSpeedOffset,
                    PilotGamepadConfig.sidewaysSpeedScaler,
                    PilotGamepadConfig.sidewaysSpeedDeadband);
    public static ExpCurve rotationCurve =
            new ExpCurve(
                    PilotGamepadConfig.rotationSpeedExp,
                    PilotGamepadConfig.rotationSpeedOffset,
                    PilotGamepadConfig.rotationSpeedScaler,
                    PilotGamepadConfig.rotationSpeedDeadband);
    public SendableChooser<String> speedChooser = new SendableChooser<String>();
    
    // ----- Constructor -----
    public PilotGamepad() {
        super("Pilot", RobotConfig.Gamepads.pilotGamepadPort);
        setupSpeedMenu();
    }

    // ----- `Gamepad` Lib Required Methods for Button Assignment -----
    public void setupTeleopButtons() {
        /* ----- Competition Button Assignments ----- */
        // "Start" Button - Reset Gyro to 0
        gamepad.startButton.onTrue(new InstantCommand(() -> Robot.swerve.zeroGyroHeading()));
        
        // "Select" Button - Reset Odometry to (0, 0) & 0º [FOR TESTING, DON'T USE IN COMP]
        // gamepad.selectButton.onTrue(new InstantCommand(() -> Robot.swerve.resetPose()));

        // "Select" Button - Reset Gyro to 180
        gamepad.selectButton.onTrue(new InstantCommand(() -> Robot.swerve.setGyroHeading(180)));

        // gamepad.aButton.onTrue(IntakeCmds.intakeSetManualCmd()).onFalse(IntakeCmds.intakeStopCmd());
        gamepad.aButton.onTrue(ElevatorCommands.setManualPWM()).onFalse(ElevatorCommands.stop());
        gamepad.bButton.onTrue(ElevatorCommands.setLow()).onFalse(ElevatorCommands.stop());

        // gamepad.Dpad.Up.onTrue(ClimberCmds.climberSetTop());
        // gamepad.Dpad.Down.onTrue(ClimberCmds.climberSetBottom());
        // gamepad.Dpad.Left.onTrue(ClimberCmds.climberSetOnChain());
        // gamepad.Dpad.Right.onTrue(ClimberCmds.climberSetManual());
    }

    public void setupDisabledButtons() {}

    public void setupTestButtons() {}

    // ----- Custom Methods for Getting Gamepad Values and Inputs -----

    // forward/backward down the field
    public double getDriveFwdPositive() {
        return forwardSpeedCurve.calculateMappedVal(this.gamepad.leftStick.getY());
    }

    // side-to-side across the field
    public double getDriveLeftPositive() {
        return sidewaysSpeedCurve.calculateMappedVal(this.gamepad.leftStick.getX());
    }

    //Positive is counter-clockwise, left Trigger is positive
    public double getDriveRotationCCWPositive() {
		double value = this.gamepad.triggers.getTwist();
		value = rotationCurve.calculateMappedVal(value);
		return value;        
    }

    public double getClimberAdjustInput() {
        return gamepad.rightStick.getY() / 1.5;
    }

    // ----- Getters and Setters for Speed Selections -----

    public MaxSpeeds getSelectedSpeed(){
        String speed = speedChooser.getSelected();;
        if ( speed == "Fast")    return MaxSpeeds.FAST;
        if ( speed == "Medium") return MaxSpeeds.MEDIUM;
        return MaxSpeeds.SLOW;
    }

    public void setupSpeedMenu(){
            // Setup Speed Selector
            speedChooser.addOption        ("1. Slow",      "Slow");
            speedChooser.addOption        ("2. Medium", "Medium");
            speedChooser.setDefaultOption ("4. Fast", 	   "Fast");
            SmartDashboard.putData(speedChooser);
    }
    
    public void setMaxSpeeds(MaxSpeeds speed){
        switch (speed) { 
            case FAST:
                System.out.println("Driver Speeds set to FAST !!!");
                forwardSpeedCurve.setScalar(PilotGamepadConfig.FastfowardVelocity);
                sidewaysSpeedCurve.setScalar(PilotGamepadConfig.FastsidewaysVelocity);
                rotationCurve.setScalar(PilotGamepadConfig.FastrotationVelocity);
                // forwardSpeedCurve.setExpVal(PilotGamepadConfig.FastForwardExp);
                // sidewaysSpeedCurve.setExpVal(PilotGamepadConfig.FastSidewaysExp);
                // rotationCurve.setExpVal(PilotGamepadConfig.FastRotationExp);
                break;
            case MEDIUM:
                System.out.println("Driver Speeds set to MEDIUM !!!");
                forwardSpeedCurve.setScalar(PilotGamepadConfig.MediumForwardVelocity);
                sidewaysSpeedCurve.setScalar(PilotGamepadConfig.MediumSidewaysVelocity);
                rotationCurve.setScalar(PilotGamepadConfig.MediumSidewaysVelocity);
                // forwardSpeedCurve.setExpVal(PilotGamepadConfig.MedSlowForwardExp);
                // sidewaysSpeedCurve.setExpVal(PilotGamepadConfig.MedSlowSidewaysExp);
                // rotationCurve.setExpVal(PilotGamepadConfig.FastRotationExp);
                break;
            default:
                System.out.println("Driver Speeds set to SLOW !!!");
                forwardSpeedCurve.setScalar(PilotGamepadConfig.SlowforwardVelocity);
                sidewaysSpeedCurve.setScalar(PilotGamepadConfig.SlowsidewaysVelocity);
                rotationCurve.setScalar(PilotGamepadConfig.SlowsidewaysVelocity);
                // forwardSpeedCurve.setExpVal(PilotGamepadConfig.SlowForwardExp);
                // sidewaysSpeedCurve.setExpVal(PilotGamepadConfig.SlowSidewaysExp);
                // rotationCurve.setExpVal(PilotGamepadConfig.SlowRotationExp);
                break;
        }
    }

    // ----- Misc. Gamepad Methods -----

    public void rumble(double intensity) {
        this.gamepad.setRumble(intensity, intensity);
    }
}
