package frc.robot.mechanisms.pivot;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class PivotTelemetry {
    protected ShuffleboardTab tab;

    public PivotTelemetry( PivotSubSys pivot) {
        tab = Shuffleboard.getTab("Pivot");

        // Pivot Top Row - Enc Val, Deg, and Pivot State
        tab.addNumber("Pivot Enc",          () -> pivot.getEncoderPosition()) .withPosition(5, 0).withSize(1, 1);
        tab.addNumber("Pivot Deg",          () -> pivot.getAngle())           .withPosition(6, 0).withSize(1, 1);
        tab.addString("Pivot State",        () -> pivot.getPivotStateString()).withPosition(7, 0).withSize(2, 1);

        // Pivot Bottom Row - Target Angle, At Target Y/N, and motor power
        tab.addNumber("Target angle",       () -> pivot.getTargetAngle()).withPosition(5, 1).withSize(2, 1);
        tab.addBoolean("At Tgt",            () -> pivot.getAtTarget())   .withPosition(6, 1).withSize(1, 1);
        tab.addNumber("Pivot Power",        () -> pivot.getPivotPower()) .withPosition(7, 1).withSize(2, 1);
    }
}

