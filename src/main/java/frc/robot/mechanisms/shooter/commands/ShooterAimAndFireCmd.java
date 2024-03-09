package frc.robot.mechanisms.shooter.commands;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.Command;
import frc.lib.util.Rmath;
import frc.robot.Robot;
import frc.robot.Robot.TeamAlliance;

public class ShooterAimAndFireCmd extends Command {
    // Variables
    TeamAlliance currentAlliance;
    double timeout;
    Timer timer = new Timer();
    
    // Constructor
    public ShooterAimAndFireCmd(double timeout) {
        this.timeout = timeout;
    }

    // Initialize
    @Override
    public void initialize() {
        Robot.print("Shoot init");
        timer.reset();
        timer.start();
        Robot.print("Current Alliance: " + Robot.alliance);
        this.currentAlliance = Robot.alliance;
    }

    // Periodic
    @Override
    public void execute() {
        Pose3d poseToSpeaker;

        if (currentAlliance == TeamAlliance.BLUE) {
            // Robot.print("Shoot Cmd Getting Blue");
            poseToSpeaker = Robot.vision.getSpeakerTag(7);
        } else if (currentAlliance == TeamAlliance.RED) {
            // Robot.print("Shoot Cmd Getting Red");
            poseToSpeaker = Robot.vision.getSpeakerTag(4);
        } else {
            Robot.print("Shoot Cmd Getting NONE");
            return;
        }

        double hypotenuseDistance = (Math.sqrt(Math.pow(poseToSpeaker.getX(), 2) + Math.pow(poseToSpeaker.getY(), 2)));
        Robot.print("Shoot Cmd Distance To Speaker: " + hypotenuseDistance);
    }

    // Is Finished
    @Override
    public boolean isFinished() {
        if (timer.get() > timeout) {
            Robot.print("Shoot Cmd Timed out");
            return true;
        }
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        Robot.print("Shoot Cmd Ended with interrupted status = " + interrupted);
    }
}
