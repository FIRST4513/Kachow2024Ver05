package frc.lib.mechanisms.Elevator;

public class ElevatorControlConfig {
    /* ----- Preset Heights for MotionMagic ----- */
    public double lowRot = 2;
    public double midRot = 6;
    public double topRot = 10;

    /* ----- Go-To-Bottom Speed for PWM */
    public double toBottomSpeed = -0.25;

    /* ----- Feed Forward For Gravity Compensation ----- */
    public double mmFeedForward = 0;

    /* ----- Target Tolerances ----- */
    public double pwmIsAtTargetTolerance = 0.05;
    public double mmIsAtTargetTolerance = 0.25;

    /* ----- With-Methods for configuration chaining ----- */

    public ElevatorControlConfig withLowPos(double newPos) {
        this.lowRot = newPos;
        return this;
    }

    public ElevatorControlConfig withMidPos(double newPos) {
        this.midRot = newPos;
        return this;
    }

    public ElevatorControlConfig withTopPos(double newPos) {
        this.topRot = newPos;
        return this;
    }

    public ElevatorControlConfig withToBottomSpeed(double newSpeed) {
        this.toBottomSpeed = newSpeed;
        return this;
    }

    public ElevatorControlConfig withMMFeedForward(double newFeedForward) {
        this.mmFeedForward = newFeedForward;
        return this;
    }

    public ElevatorControlConfig withPWMTolerance(double newTolerance) {
        this.pwmIsAtTargetTolerance = newTolerance;
        return this;
    }

    public ElevatorControlConfig withMMTolerance(double newTolerance) {
        this.mmIsAtTargetTolerance = newTolerance;
        return this;
    }
}
