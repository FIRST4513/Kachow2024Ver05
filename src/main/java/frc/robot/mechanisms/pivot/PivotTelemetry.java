package frc.robot.mechanisms.pivot;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class PivotTelemetry {
    protected ShuffleboardTab tab;

    public PivotTelemetry( PivotSubSys pivot) {
        tab = Shuffleboard.getTab("Pivot");

        // Pivot Left Column
        tab.addString("Pivot State",        () -> pivot.getPivotStateString())        .withPosition(0, 0).withSize(2, 1);
        tab.addNumber("Pivot Raw Enc",      () -> pivot.getEncoderRawPosition())      .withPosition(0, 1).withSize(2, 1);
        tab.addNumber("Pivot Abs Enc",      () -> pivot.getEncoderAbsolutePosition()) .withPosition(0, 2).withSize(2, 1);
        tab.addNumber("Pivot Encoder Angle",() -> pivot.getEncoderAngle())            .withPosition(0, 3).withSize(2, 1);
        
        // Pivot Right Column - Shooter Angle, Target Angle, At Target Y/N, and motor power
        tab.addNumber("Pivot Shooter Angle",() -> pivot.getShooterAngle())            .withPosition(2, 0).withSize(2, 1);
        tab.addNumber("Target angle",       () -> pivot.getTargetAngle()).withPosition(2, 1).withSize(2, 1);
        tab.addBoolean("At Tgt",            () -> pivot.isAtTarget())    .withPosition(2, 2).withSize(2, 1);
        tab.addNumber("Pivot Power",        () -> pivot.getPivotPower()) .withPosition(2, 3).withSize(2, 1);
    }
}

