# Pilot and CoPilot Controller Layouts

How the Controls are laid out on the Pilot and CoPilot controllers

## Pilot

One-axis Movement and Controls
- `LS X & Y` - Translational movement on the Field
- `Triggers` - Rotation of the Robot
- `Start` - Reset Gyro Heading to 0
- `Select` - Reset Odometry all to 0
- `Dpad UP` - Climber Preset Pose: Clear [Climber Up, Robot Down]
- `Dpad DOWN` - Climber Preset Pose: Bottom [Climber Down, Robot Up]
- `Dpad LEFT` - Climber Preset Pose: Hook On chain [Climber Mid, Robot Still Down]
- `Dpad RIGHT` - Climber Override: Manual Control with `RS Y`
    - `RS Y` movement explained:
        - Up: climber down, robot up
        - Down: climber up, robot down

Two-Button Control Combinations for Autonomous Drive-To Positions (all currently disabled as of 03122024)
- Position and Rotation
    - `LB + X` - Speaker Left
    - `LB + Y` - Speaker Mid
    - `LB + B` - Speaker Right
    - `LB + A` - Amp
    - `RB + X` - Human Player Left
    - `RB + Y` - Human Player Mid
    - `RB + B` - Human Player Right
- Rotation Only
    - `RB + A`- Stage positions:
        - `1xA` - Stage Left
        - `2xA` - Stage Center
        - `3xA` - Stage Right

Driver should also be able to manually override Drive-To positions by giving an input on a Left Stick or on the Triggers (not important to know as of 03122024)

## CoPilot

- Manual Overrides (for both testing and competition use)
- `Left Bumper` - Stop all subsystems (Intake, Passthrough, Shooter, and Pivot)
- `Right Bumper` - While held, manually override the subsystems
    - `Trigger Twist Difference` - Drives Intake, Passthrough, and Shooter at same speed all at once
    - `RS Y` - Adjust Shooter Pivot angle by PWM

Two-Button Control Combinations
- `A` - Intaking Commands
    - `A + Dpad UP` - Human Player Intake until gamepiece, no timeout
    - `A + Dpad DOWN` - Ground Intake until gamepiece detected, no timeout
- `B` - Ejecting Commands
    - `B + Dpad LEFT` - Shoot Speaker, doesn't change Pivot angle, and feeds shooter with gamepiece once the motors have reached the required speed
- `X` - Pivot Preset Angle Commands
    - `X + Dpad UP` - Set Pivot to low angle - ~40º
    - `X + Dpad DOWN` - Set Pivot to high angle - ~180º

***

Pilot has control of:
- climber

CoPilot has control of:
- shooter
- intake
- arm
- elevator