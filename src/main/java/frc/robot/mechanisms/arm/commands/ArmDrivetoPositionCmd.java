package frc.robot.mechanisms.arm.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Robot;
import frc.robot.mechanisms.arm.ArmConfig;
import frc.robot.mechanisms.arm.ArmConfig.ARMPOS;
import frc.robot.mechanisms.elevator.ElevatorConfig;
import frc.robot.mechanisms.elevator.ElevatorConfig.ELEVPOS;

public class ArmDrivetoPositionCmd extends Command {

    double targetAngle;
    double angleDifference;
    double timeout;
    Timer runTimer = new Timer();

    public ArmDrivetoPositionCmd(double angle, double timeout) {
        targetAngle = angle;
        this.timeout = timeout;
    }

    @Override
    public void initialize() {
        runTimer.reset();
        runTimer.start();
        addRequirements(Robot.arm);
    }

    @Override
    public void execute() {
        angleDifference = Robot.arm.getArmCurrentAngle() - targetAngle;

        // Robot.arm.driveToAngle(targetAngle);
    }

    @Override
    public void end(boolean interrupted) {
        Robot.arm.stopMotors();
    }

    // Returns true when the command should end.
    @Override
    public boolean isFinished() {
        // if within threshold, stop arm
        return true;
        // if (runTimer.get() > timeout)  { System.out.println("cmd TIMED-OUT"); return true; }
        // if (Math.abs(angleDifference) < ArmConfig.isAtTargetError) {
            // return true;
        // }
        // return false;
    }
}
