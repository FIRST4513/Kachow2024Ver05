# -- Kachow 2024 Robot Outline --

Outline and layout of subsystems, devices, commands, etc. for this year's code and robot.

## Table of Contents
- [Subsystems](#swerve-drivetrain)
    - [Chassis And Navigation](#chassis-and-navigation)
        - [Swerve Drivetrain](#swerve-drivetrain)
        - [Autonomous](#autonomous)
        - [Vision](#vision)
    - [Gamepiece Manipulation](#gamepiece-manipulation)
        - [Arm](#arm)
        - [Climber](#climber)
        - [Elevator](#elevator)
        - [Intake](#intake)
        - [Shooter](#shooter)
    - [Controllers/Gamepads](#controllers-and-gamepads)
        - [Pilot](#pilot-gamepad)
        - [Copilot](#copilot-gamepad)
    - [Misc](#misc)
        - [Rotary Switch](#rotary-switch)
        - [LEDs](#leds)
- [Physical Devices](#physical-devices)
    - [CAN Bus Devices](#can-bus-devices)
    - [DIO Devices](#dio-devices)
    - [ANALOG Devices](#analog-devices)
- [Naming Conventions](#naming-conventions)

# Subsystems

<sup>[(Back to top)](#table-of-contents)</sup>

> Subsystems defined in code that control physical motors and sensors to make the robot act like... well, a _robot_.

## Chassis and Navigation

> Subsystems that are critical to the base robot, allowing us to drive the chassis and navigate the field.

### Swerve Drivetrain

Organized under the `drivetrain` folder, the `DrivetrainSubSys.java` class defines a four-module swerve drivetrain, run by 8 Falcon 500's and 4 CTRE CanCoders. Our particular modules are [SDS Mk4i's](https://www.swervedrivespecialties.com/products/mk4i-swerve-module). Something to note, our Falcons and CANCoders have been updated to the Phoenix 6 api's, and we are in fact taking advantage of certain features. It's great!

Our Drivetrain is laid out in a folder under `robot`, as so (not necessarily grouped by alphabetical, more by functional grouping):

- drivetrain
    - commands
        - `DrivetrainCmds.java`: The main, simple commands you can call to control the drivetrain
        - `SwerveDriveCmd.java`: The main command used to drive the swerve modules and therefore the robot
    - config
        - `AngleFalconConfig.java`: info for Angle Motor config (`getConfig` takes in module ID number)
        - `CanCoderConfig.java`: info for CANCoder config (`getConfig` takes in module ID number)
        - `DriveFalconConfig.java`: info for Drive Motor config
        - `DrivetrainConfig.java`: info for more general drivetrain configuration including kinematics definitions and device CAN ID getters
    - `Alignment Controller.java`: Theoretically, this class should be able to drive our robot to a specified position (X and Y) and rotation on the field, with PID on all 3 axis. It is untested
    - `RotationController.java`: Theoretially, this class should be able to drive our robot with manual control on the X and Y axis, but keep our robot pointed to a given angle in the rotation axis, with PID.
    - `DrivetrainSubSys.java`: The main file. This should be the main way other subsystems interact with the drivetrain, through methods in here. It includes way to drive the robot, get odometry data, etc.
    - `SwerveModule.java`: The class that defines a swerve module and how it can be used. 4 of these are made by the drivetrain subsystem, and include way to get their position and set their desired state.
    - `DrivetrainOdometry.java`: The old, simple way we used to do odometry. Keeps track of position, nothing else.
    - `OdometryThread.java`: The new way of keeping track of odometry. This thread runs at 120hz, and factors in vision as well. Thread Safety is an important thing here, and has been accounted for.
    - `DrivetrainTelemetry.java`: Telemetry file, makes ShuffleBoard layouts.
    - `NavxGyro.java`: The old gyro file, used for interacting with the NavX gyro on the RoboRio
    - `PigeonGyro.java`: The new gyro file, used for interacting with the Pigeon2.0's api's.

<!-- Usage:

1. In `Robot.java`, declare your drivetrain in the class, then instantiate the drivetrain in `robotInit()`.

```java
public class Robot extends TimedRobot {
    public static DrivetrainSubSys swerve;

    @Override
    public void robotInit() {
        swerve = new DrivetrainSubSys();
    }
}
```

2. When you need to drive the robot and therefore the drivetrain and its swerve modules, you can call the `SwerveDriveCmd.java` command under `drivetrain/commands/` in the following ways (I am simplifying the actual command calling and running for clarity's sake):

```java
// (pretend we're in a file that can call commands)
import frc.robot.drivetrain.commands.SwerveDriveCmd;

// the most common way to drive the robot:
SwerveDriveCmd(
    () -> Robot.pilot.getDriveFwdPositive(),          // get input for forward/backward, with forward being positive
    () -> Robot.pilot.getDriveLeftPositive(),         // get input for side to side, with left being positive
    () -> Robot.pilot.getDriveRotationCCWPositive(),  // get input for rotation, possibly from triggers
    true                                              // drive robot in field-relative mode.
);
```

3. And optionally, implement ways to activate nice-to-have features such as:
    - `DrivetrainCmds.LockSwerveCmd()`: 'lock' the wheels in place by turning them all to face towards the center of the robot
    - `DrivetrainCmds.ZeroGyroHeadingCmd()`: re-calibrate the gyro, with zero being the direction the robot is facing at the moment the command is issued  -->

Important Functions in `DrivetrainSubSys.java`:

- `drive()`: this method is used to drive the robot, and handles converting target x, y, and rotation speeds to speeds and angles the wheels should be at. Then it sends those speeds and angles to each corresponding swerve module.
- `stop()`: used to, obviously, stop the motors right where they are.
- `resetOdometryAndGyroFromPath(PathPlannerPath path)`: this method is used to initialize or reset the odometry to where the robot should be at based on the beginning of a given Path. For example, this may be called in an autonomous command to initialize the robot to the right place on the field so the path following works correctly.
- There used to be a method called `resetFalconAngles()` that would reset the angle motors to the angle of the wheel converted to falcon counts, but it no longer needed because we updated the firmware, libraries, and api's to Phoenix 6 and are now able to join the Angle Falcon and the CANCoder together. Simplicity!
- `getdriveState()`: this method returns a custom class of ours that holds data about the robot's odometry including position, wheel states, and others.
- `getPose()`, `getRotation()`, `resetPose(Pose2d pose)`, and others are important setters and getters of the drivetrain's odometry.

For transparency and clarity, our drivetrain has been morphed from many different forms and functions through the past few weeks. Last year, we got a good portion of our code (or at least the layout of it) from Spectrum3847, and it worked really well. Then this year in the past few weeks of build season, our code has changed dramatically. Studying and using samples from both Jack In The Bot 2910 and Spectrum3847, we have sort of settled on a balance between familiarity of the layout we had last year and the functionality we like, and the new, cutting-edge approaches of each of the other teams-- Jack in the Bot for their incredible complexity and Spectrum for their very modular and organized setup.

### Autonomous

> _[TBD]_

### Vision

> _[TBD]_

## Gamepiece Manipulation

<sup>[(Back to top)](#table-of-contents)</sup>

> Subsystems tied to physical mechanisms that allow us to interact with the field and manipulate game pieces during matches.

### Arm

Organized under the `arm` folder, `ArmSubSys.java` defines an arm controlled by either (1 or 2) Redlines or Vex Pro motors, that swings from inside the robot to outside the robot. This year, the arm is mounted to a [2-stage elevator](#elevator) for vertical motion, and has an [intake](#intake) mounted on one end.

Responsibilities of the Arm:

- Ground-pickup gamepieces
- Score in Amp
- Score in Trap
- Feed gamepiece to [Shooter](#shooter) for scoring in Speaker

> [insert code stuff for arm here]

### Climber

Organized under the `climber` folder, `ClimberSubSys.java` defines a climber articulated by 1 Falcon 500 motor. Mechanically, the climber is made up of two ["Climber-In-A-Box"-es](https://www.andymark.com/products/climber-in-a-box) with a CF (Constant-Force) spring inside, winched by the 1 motor.

Responsibilities of the Climber:

- Raise to let go of chains on the Stages
- Raise to lower robot to the ground
- Lower to grab hold of chains on the Stages
- Lower to raise robot up in the air

> [insert code stuff for climber here]

### Elevator

Organized under the `elevator` folder, `ElevatorSubSys.java` defines a 2-stage Elevator raised and lowered by either (1 Falcon 500) or (2 Redlines). Mechanically, the Elevator is a cascading elevator. The [Arm](#arm) is attached to the Elevator.

Responsibilities of the Elevator:

- raise to allow [Arm](#arm) and [Intake](#intake) to score in Amp
- raise to allow [Arm](#arm) and [Intake](#intake) to score in Trap
- lower to allow [Intake](#intake) to pick up gamepieces off of the ground
- lower to allow [Intake](#intake) to feed the [Shooter](#shooter) a gamepiece to score in Speaker

> [insert code stuff for elevator here]

### Intake

Organized under the `intake` folder, `IntakeSubSys.java` defines an intake run by 1 motor. The Intake is attached to one end of the [Arm](#arm) and can run in both directions. The Intake can manipulate game pieces by spinning the sets of horizontal rollers. There is also a small cage to hold the gamepiece, with a sensor to tell us if a gamepiece has been captured or not.

Responsibilities of the Intake:

- Suck in gamepieces from the ground or possibly Human Player Station
- Eject gamepieces into the Amp or Trap
- Feed gamepieces into the shooter for scoring in the Speaker

> [insert code stuff for intake here]

### Shooter

Organized under the `shooter` folder, `ShooterSubSys.java` defines a fixed shooter, that only ejects gamepieces with a single motor, at a single angle, possibly at a single speed. The Shooter stays in one place and is fed by the [Intake](#intake).

Responsibilities of the Shooter:

- Shoot gamepieces fed by the [Intake](#intake) to score in the Speaker.

> [insert code stuff for shooter here]

## Controllers and Gamepads

<sup>[(Back to top)](#table-of-contents)</sup>

> Controllers used by the Human Drivers to control the robot together.

### Pilot Gamepad

> [pilot text here]

### Copilot Gamepad

> [coplilot text here]

## Misc

### Rotary Switch

> [rotary switch text here]

### LEDs

> [led text here]

## Physical Devices

<sup>[(Back to top)](#table-of-contents)</sup>

### CAN Bus Devices

> [can text here]

### DIO Devices

> [dio text here]

### ANALOG Devices

> [analog text here]

## Naming Conventions

<sup>[(Back to top)](#table-of-contents)</sup>