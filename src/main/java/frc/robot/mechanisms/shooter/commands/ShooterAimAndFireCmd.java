package frc.robot.mechanisms.shooter.commands;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Robot;
import frc.robot.Robot.TeamAlliance;
import frc.robot.mechanisms.passthrough.PassthroughSubSys.PassthroughState;
import frc.robot.mechanisms.shooter.ShooterConfig;
import frc.robot.mechanisms.shooter.ShooterSubSys.FireState;
import frc.robot.mechanisms.pivot.PivotConfig;
import frc.robot.mechanisms.pivot.PivotSubSys.PivotState;

public class ShooterAimAndFireCmd extends Command {
    // Variables
    TeamAlliance currentAlliance;
    double extraTime;
    Timer timer = new Timer();
    boolean shotStarted = false;
    
    // Constructor
    public ShooterAimAndFireCmd(double secondsRunAfterGamepiece) {
        this.extraTime = secondsRunAfterGamepiece;

        addRequirements(Robot.shooter, Robot.pivot);

    }

    // Initialize
    @Override
    public void initialize() {
        Robot.print("Shoot init");
        timer.reset();
        timer.start();
        Robot.print("Current Alliance: " + Robot.alliance);
        this.currentAlliance = Robot.alliance;

        // Spool up shooter
        Robot.shooter.setNewFireState(FireState.SPEAKER);

        // Set Pivot to To Target mode
        Robot.pivot.setNewPivotState(PivotState.TO_TARGET);
    }

    // Periodic
    @Override
    public void execute() {
        if (!shotStarted) {
            double distance = getDistanceToSpeaker();
            Robot.print("Distance to Speaker: " + distance);

            if (distance < 0) { return; }  // less than 0 distance not possible, no target found or math error

            // drive shooter pivot to correct shot angle
            Robot.pivot.setNewTargetAngle(distanceToPivotAngle(distance));

            // if out of range for shot, return
            if (distance > PivotConfig.SPEAKER_MIX_SHOT_DISTANCE) { return; }

            // if shooter pivot not at correct angle, return
            if (!Robot.pivot.isAtTarget()) { return; }

            // if shooter motors not at correct velocity, return
            if (!Robot.shooter.areMotorsAtVelocityTarget()) { return; }

            Robot.print("Everything good, starting passthrough to shooter for shot");

            // Else everything good, start passthrough feed
            Robot.passthrough.setNewState(PassthroughState.EJECT);

            // toggle flag and start after-gamepiece timer
            shotStarted = true;
            timer.reset();
            timer.start();
        } else {
            Robot.print("Waiting for timeout...");
        }
    }

    // Is Finished
    @Override
    public boolean isFinished() {
        if (timer.get() > extraTime) {
            Robot.print("Shoot Cmd Timed out");
            return true;
        }
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        Robot.print("Shoot Cmd Ended with interrupted status = " + interrupted);

        // Stop Shooter, Pivot, and Passthrough
        Robot.shooter.setNewFireState(FireState.STOPPED);
        Robot.pivot.setNewPivotState(PivotState.STOPPED);
        Robot.passthrough.setNewState(PassthroughState.STOPPED);
    }

    /* ----- Custom Methods ----- */
    public double getDistanceToSpeaker() {
        Pose3d poseToSpeaker;

        if (currentAlliance == TeamAlliance.BLUE) {
            poseToSpeaker = Robot.vision.getSpeakerTag(7);
        } else if (currentAlliance == TeamAlliance.RED) {
            poseToSpeaker = Robot.vision.getSpeakerTag(4);
        } else {
            Robot.print("No Speaker Target Seen");
            return -1;
        }

        return (Math.sqrt(Math.pow(poseToSpeaker.getX(), 2) + Math.pow(poseToSpeaker.getY(), 2)));
    }

    // There is definitely a better way to do this I know, but for now it should work
    // This is just a linear scale of the angle.... NOT correct
    // I would rather do a trig calculation but not rn........
    // This will overshoot at most angles except bumpered up and max distance back
    public double distanceToPivotAngle(double distance) {
        // min: 0º  @ 1.016m offset to 0m
        // max: 13º @ 1.887m offset to 0.871m

        // example: 1.4514 -> 50% or 0.5 -> 8.5º

        // Constrain distance to min and max range
        distance = Math.min(Math.max(distance, PivotConfig.SPEAKER_MIN_SHOT_DISTANCE), PivotConfig.SPEAKER_MIX_SHOT_DISTANCE);

        // Get range of possible distances
        double range = PivotConfig.SPEAKER_MIX_SHOT_DISTANCE - PivotConfig.SPEAKER_MIN_SHOT_DISTANCE;
        // Get offset distance by subtracting min of range
        double distanceFromMin = distance - PivotConfig.SPEAKER_MIN_SHOT_DISTANCE;
        // Get percent of range the current distance is, used for linearly scaling angle
        double percentOfRange = distanceFromMin / range;

        // Get range of angles we can shoot at
        double angleRange = PivotConfig.SPEAKER_MIX_SHOT_ANGLE - PivotConfig.SPEAKER_MIN_SHOT_ANGLE;
        // Linearly scale angle by percent of range we're at
        double finalAngle = angleRange * percentOfRange;

        return finalAngle;
    }
}