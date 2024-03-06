# Pilot and CoPilot Controller Layouts

How the Controls are laid out on the Pilot and CoPilot controllers

## Pilot

One-axis Movement and Controls
- `LS X & Y` - Translational movement on the Field
- `Triggers` - Rotation of the Robot
- `Start` - Reset Gyro Heading to 0
- `RS Y` - Manually adjust Climber height
    - Up: climber down, robot up
    - Down: climber up, robot down
- `Dpad UP` - Climber Preset Pose: Clear [Climber down, Robot Up]
- `Dpad DOWN` - Climber Preset Pose: Bottom [Climber up, Robot Down]
- `Dpad LEFT` - Climber Preset Pose: Hook [Climber Mid, Robot Down]

Two-Button Control Combinations for Autonomous Drive-To Positions
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

Driver should also be able to manually override Drive-To positions by giving an input on a Left Stick or on the Triggers

## CoPilot

- Manual Controls (may not make it to competition, just for testing)
- `LS Y` - Move Elevator
- `RS Y` - Move Arm
- `Y + Triggers` - Intake speed control
- `Y + Dpad UP` - [on hold] set shooter to speaker shoot speed
- `Y + Dpad Down` - [on hold] set shooter to intake speed

Two-Button Control Combinations
- `A + Dpad UP` - Intake at Human Player Station
- `A + Dpad Down` - Intake from Ground
- `B + Dpad Up` - Shoot to Speaker
- `B + Dpad Down` - Shoot to Amp
- `B + Dpad (Left/Right)` - Shoot to Trap

***

Pilot has control of:
- climber

CoPilot has control of:
- shooter
- intake
- arm
- elevator