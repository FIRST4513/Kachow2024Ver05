package frc.robot.auto;

import java.lang.reflect.Field;

import javax.swing.Action;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.Robot;
import frc.robot.Robot.TeamAlliance;
import frc.robot.auto.comands.AutoCmds;
import frc.util.FieldConstants;

public class Auto {
    public static final SendableChooser<String> actionChooser = new SendableChooser<>();
    public static final SendableChooser<String> positionChooser = new SendableChooser<>();

    public static String actionSelect;
    public static String positionSelect;
    private static Pose2d startPose;
    
    // ----- Autonomous Subsystem Constructor -----
    public Auto() {
        configureAutoBuilder();
        registerNamedCommands();
        setupSelectors();                // Setup on screen slection menus
    }

    public static void setupSelectors() {
        // Selector for Robot Starting Position on field
        positionChooser.setDefaultOption("Left Speaker",        AutoConfig.kSpkrLeftSelect);
        positionChooser.addOption(       "Center Speaker",      AutoConfig.kSpkrCtrSelect);
        positionChooser.addOption(       "Right Speaker",       AutoConfig.kSpkrRightSelect);
        // Selector for Autonomous Desired Action
        actionChooser.setDefaultOption(  "Do Nothing",          AutoConfig.kActionDoNothing);
        actionChooser.addOption(         "One Note",            AutoConfig.kActionOneNoteOnly);
        actionChooser.addOption ("sick 360 (BLUE LEFT ONLY)", AutoConfig.kSick360);
        actionChooser.addOption("logan practice cone path", AutoConfig.kLoganConePath);
        // actionChooser.addOption(         "Two Note",            AutoConfig.kActionTwoNote);
        actionChooser.addOption(         "One Note and Crossline", AutoConfig.kOneNoteCrossOnlySelect);
        actionChooser.addOption(         "Crossline Only",      AutoConfig.kCrossOnlySelect);
    }

    // ------ Get operator selected responses from shuffleboard -----
    public static void getAutoSelections() {
        actionSelect =     actionChooser.getSelected();
        positionSelect =  positionChooser.getSelected();
        Robot.print("Action Select = " +     actionSelect);
        Robot.print("Position Select = " +     positionSelect);
    }
    
    public static Command getAutonomousCommand() {
        getAutoSelections();
        setStartPose();                 // Initialize Robot Pose on Field

        // ------------------------------- Do Nothing ---------------------------
        if (doNothing()) {
            System.out.println("********* DO Nothing Selection *********");
            return AutoCmds.DoNothingCmd();
        }

        // ----------------------------- One Note Only ---------------------------
        if (oneNoteOnly()) {
            System.out.println("********* One Note Cross Line Selection *********");
            if ( spkrLeft() )           { return AutoCmds.SpeakerShootCmd(); }
            if ( spkrCtr() )            { return AutoCmds.SpeakerShootCmd(); }
            if ( spkrRight() )          { return AutoCmds.SpeakerShootCmd(); }
            // should never get here
            return AutoCmds.SpeakerShootCmd();
        }
        
        // --------------------------- Cross Line Only ----------------------------
        if (crossOnly()) {
            System.out.println("********* One Note Cross Line Selection *********");
            if (red()) {
                Robot.print("REEDDDDDD");
                if ( spkrLeft() )           { return AutoCmds.CrossLineOnlyCmd("RedSpkrLeft" ); }
                if ( spkrCtr() )            { return AutoCmds.CrossLineOnlyCmd("RedSpkrCtr"); }
                if ( spkrRight() )          { return AutoCmds.CrossLineOnlyCmd("RedSpkrRight"); }
            } else {
                Robot.print("BLUEEEEE");
                if ( spkrLeft() )           { return AutoCmds.CrossLineOnlyCmd("BlueSpkrLeft" ); }
                if ( spkrCtr() )            { return AutoCmds.CrossLineOnlyCmd("BlueSpkrCtr"); }
                if ( spkrRight() )          { return AutoCmds.CrossLineOnlyCmd("BlueSpkrRight"); }
            }
        }

        // ----------------------- One Note and Cross Line Only -------------------
        if (oneNoteCross()) {
            System.out.println("********* One Note Cross Line Selection *********");
            if ( spkrLeft() )           { return AutoCmds.ShootAndCrossCmd("SpkrLeft1" ); }
            if ( spkrCtr() )            { return AutoCmds.ShootAndCrossCmd("SpkrCtr1"); }
            if ( spkrRight() )          { return AutoCmds.ShootAndCrossCmd("SpkrRight1"); }
        } 

        // ------------------------------ Two Note  -------------------------------
        if (twoNote()) {
            System.out.println("********* One Note Cross Line Selection *********");
            if ( spkrLeft() )           { return AutoCmds.TwoNoteCmd("SpkrLeft1",    "SpkrLeft2" ); }
            if ( spkrCtr() )            { return AutoCmds.TwoNoteCmd("SpkrCtr1",     "SpkrCtr2"); }
            if ( spkrRight() )          { return AutoCmds.TwoNoteCmd("SpkrRight1",   "SpkrRight2"); }
        }
        
        if (sick360()) {
            System.out.println("youre going to kill the robot, please ESTOP");
            if ( spkrLeft() )           { return AutoCmds.CrossLineOnlyCmd("360path");}
            if ( spkrCtr() )            { return AutoCmds.DoNothingCmd();}
            if ( spkrRight() )          { return AutoCmds.DoNothingCmd();}
        }
        if (loganconepath()) {
            System.out.println("youre going to kill the robot, please ESTOP");
            if ( spkrLeft() )           { return AutoCmds.DoNothingCmd();}
            if ( spkrCtr() )            { return AutoCmds.ShootAndCrossCmd("loganconepath");}
            if ( spkrRight() )          { return AutoCmds.DoNothingCmd();}
        }
        // should never get here
        return AutoCmds.DoNothingCmd();
    }

    // ----- Configuration and Setup Methods -----

    // Configures the auto builder to use to run paths in autonomous and in teleop
    public static void configureAutoBuilder() {
        // Configure the AutoBuilder settings
        AutoBuilder.configureHolonomic(
            Robot.swerve::getPose,                // Supplier<Pose2d> ------------> Robot pose supplier
            Robot.swerve::resetPose,              // Consumer<Pose2d> ------------> Method to reset odometry (will be called if your auto has a starting pose)
            Robot.swerve::getChassisSpeeds,       // Supplier<ChassisSpeeds> -----> MUST BE ROBOT RELATIVE
            Robot.swerve::driveByChassisSpeeds,   // Consumer<ChassisSpeeds> -----> Set robot relative speeds (drive)
            AutoConfig.AutoPathFollowerConfig,    // HolonomicPathFollowerConfig -> config for configuring path commands
            // ()->getAllianceFlip(),                // BooleanSupplier -------------> Should mirror/flip path
            () -> false,                        // BooleanSupplier -------------> Should mirror/flip path
            Robot.swerve                          // Subsystem: ------------------> required subsystem (usually swerve)
        );
    }

    public static Boolean getAllianceFlip(){
        // Boolean supplier that controls when the path will be mirrored for the red alliance
        // This will flip the path being followed to the red side of the field.

        if ((DriverStation.isAutonomous()) && (Robot.alliance == TeamAlliance.RED) ) {
            // Were in Autonomous Mode and Alliance is Red, so invert field
            return true;
        } else {
        // Not Autonomous or Alliance is Blue so dont invert field
        return false;
        }
    }


    // Setup Named Commands for Events in PathPlanner To Call
    private static void registerNamedCommands() {
        // an example named command
        NamedCommands.registerCommand("MidPoint", Commands.print("Midpoint reached!!"));
    }

    // ------------------------------------------------------------------------
    //     Setup proper Arm/Elevator position to Place Cone/Cube
    // ------------------------------------------------------------------------

    public static void setStartPose() {
        // Set Robot position (Odometry) and Heading (Gyro) based on selected autonomous starting position
        startPose = new Pose2d(new Translation2d(0,0), new Rotation2d(0)); // Should never use
        double gyroHeading = 0;
        if (red()) {
            Robot.print("1. We are red");
            if (spkrLeft())         { 
                startPose = FieldConstants.RED_SPEAKER_LEFT;
                gyroHeading = FieldConstants.RED_SPEAKER_LEFT_GYRO; 
                Robot.print("2. We are Speaker Left"); }
            
            if (spkrCtr())          { 
                startPose = FieldConstants.RED_SPEAKER_CTR;  
                gyroHeading = FieldConstants.RED_SPEAKER_CTR_GYRO; 
                Robot.print("2. We are Speaker Center"); }
            
            if (spkrRight())        { 
                startPose = FieldConstants.RED_SPEAKER_RIGHT; 
                gyroHeading = FieldConstants.RED_SPEAKER_RIGHT_GYRO;
                 Robot.print("2. We are Speaker Right"); }

        } else {
            Robot.print("1. We are Blue");
            if (spkrLeft())         {
                 startPose = FieldConstants.BLUE_SPEAKER_LEFT;  
                 gyroHeading = FieldConstants.BLUE_SPEAKER_LEFT_GYRO;
                 Robot.print("2. We are Speaker Left"); }
            
             if (spkrCtr())          { 
                startPose = FieldConstants.BLUE_SPEAKER_CTR;   
                gyroHeading = FieldConstants.BLUE_SPEAKER_CTR_GYRO;
                Robot.print("2. We are Speaker Center"); }
            
            if (spkrRight())        { 
                startPose = FieldConstants.BLUE_SPEAKER_RIGHT; 
                gyroHeading = FieldConstants.BLUE_SPEAKER_RIGHT_GYRO;
                Robot.print("2. We are Speaker Right"); }
        }
        // Robot.swerve.resetOdometryAndGyroFromPose(startPose);
        Robot.swerve.resetOdometryAndGyroFromPose(startPose, gyroHeading);
    }


    // ------------------------------------------------------------------------
    //            Simple Checks to make above routines cleaner
    // ------------------------------------------------------------------------
    private static boolean doNothing() {
        if (actionSelect.equals(AutoConfig.kActionDoNothing)) { return true; }
        return false;
    }

    private static boolean oneNoteOnly() {
        if (actionSelect.equals(AutoConfig.kActionOneNoteOnly)) { return true; }
        return false;
    }

    private static boolean twoNote() {
        if (actionSelect.equals(AutoConfig.kActionTwoNote)) { return true; }
        return false;
    }

    private static boolean oneNoteCross() {
        if (actionSelect.equals(AutoConfig.kOneNoteCrossOnlySelect)) { return true; }
        return false;
    }

    private static boolean crossOnly() {
        if (actionSelect.equals(AutoConfig.kCrossOnlySelect)) { return true; }
        return false;
    }
    private static boolean sick360() {
        if (actionSelect.equals(AutoConfig.kSick360)) {return true;}
        return false;
    }
    private static boolean loganconepath() {
        if (actionSelect.equals(AutoConfig.kLoganConePath)) {return true;}
        return false;
    }
    private static boolean red() {
        if (Robot.alliance == TeamAlliance.RED) { return true; }
        return false;
    }

    // private static boolean blue() {
    //     if (Robot.alliance == "Blue") { return true; }
    //     return false;
    // }

    private static boolean spkrLeft() {
         if (positionSelect.equals(AutoConfig.kSpkrLeftSelect)) { return true; }
        return false;
    }

    private static boolean spkrCtr() {
         if (positionSelect.equals(AutoConfig.kSpkrCtrSelect)) { return true; }
        return false;
    }

    private static boolean spkrRight() {
         if (positionSelect.equals(AutoConfig.kSpkrRightSelect)) { return true; }
        return false;
    }

}
