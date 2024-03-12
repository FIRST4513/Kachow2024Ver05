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
import frc.robot.mechanisms.climber.commands.ClimberCmds;
import frc.robot.mechanisms.shooter.commands.ShooterAimAndFireCmd;
import frc.robot.mechanisms.shooter.commands.ShooterCmds;
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

    private Pose2d spkrLeftPose;
    private Pose2d spkrCtrPose;
    private Pose2d spkrRightPose;
    private Pose2d ampPose;
    private Pose2d HPLeft;
    private Pose2d HPCtr;
    private Pose2d HPRight;
    
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
        gamepad.selectButton.onTrue(new InstantCommand(() -> Robot.swerve.resetPose()));

        gamepad.xButton.whileTrue(new ShooterAimAndFireCmd(2.0));

        gamepad.Dpad.Up.onTrue(ClimberCmds.climberSetTop());
        gamepad.Dpad.Down.onTrue(ClimberCmds.climberSetBottom());
        gamepad.Dpad.Left.onTrue(ClimberCmds.climberSetOnChain());
        gamepad.Dpad.Right.onTrue(ClimberCmds.climberSetManual());
        

        // buttons for testing shooter
        // gamepad.aButton.onTrue(ShooterCmds.stopShooterCmd());
        // gamepad.xButton.onTrue(ShooterCmds.shooterSetSpeakerCmd());
        // gamepad.bButton.onTrue(ShooterCmds.shooterSetRetractCmd());
        // gamepad.yButton.onTrue(ShooterCmds.shooterSetAmpCmd());

        /* ----- Example Ways to use Buttons in different ways ---- */

        // example combo button functionality:
        // gamepad.rightBumper.and(gamepad.aButton).whileTrue(new RunCommand(() -> Robot.print("Going to Toggling Angle")));

        // example go-while-held button functionality:
        
        // or:
        // gamepad.Dpad.Left.onTrue(IntakeCmds.intakeSetAmpCmd());
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
        // right will be priority in code, but not primarily used in driving practice
        if (Math.abs(this.gamepad.rightStick.getX()) > Math.abs(this.gamepad.leftStick.getX())) {
            return sidewaysSpeedCurve.calculateMappedVal(this.gamepad.rightStick.getX()) * 1;  // change later to scale
        } else {
            return sidewaysSpeedCurve.calculateMappedVal(this.gamepad.leftStick.getX());
        }
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

    public void setupFieldPoses(){
        if (Robot.alliance == TeamAlliance.BLUE) {
            spkrLeftPose = FieldConstants.BLUE_SPEAKER_LEFT;
            spkrCtrPose = FieldConstants.BLUE_SPEAKER_CTR;
            spkrRightPose =FieldConstants.BLUE_SPEAKER_RIGHT;
            ampPose =FieldConstants.BLUE_AMP;
            HPLeft = FieldConstants.BLUE_HP_LEFT;
            HPCtr = FieldConstants.BLUE_HP_CTR;
            HPRight =FieldConstants.BLUE_HP_RIGHT;
        } else {
            spkrLeftPose = FieldConstants.RED_SPEAKER_LEFT;
            spkrCtrPose = FieldConstants.RED_SPEAKER_CTR;
            spkrRightPose =FieldConstants.RED_SPEAKER_RIGHT;
            ampPose =FieldConstants.RED_AMP;
            HPLeft = FieldConstants.RED_HP_LEFT;
            HPCtr = FieldConstants.RED_HP_CTR;
            HPRight =FieldConstants.RED_HP_RIGHT;
        }
    }
}
