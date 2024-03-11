package frc.robot.mechanisms.pivot;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class PivotTelemetry {
    protected ShuffleboardTab tab;

    public PivotTelemetry( PivotSubSys pivot) {
        tab = Shuffleboard.getTab("Pivot");

        // Pivot Top Row - Enc Val, Deg, and Pivot State
        tab.addString("Pivot State",        () -> pivot.getPivotStateString())        .withPosition(0, 0).withSize(2, 1);
        tab.addNumber("Pivot Raw Enc",      () -> pivot.getEncoderRawPosition())      .withPosition(0, 3).withSize(2, 1);
        tab.addNumber("Pivot Abs Enc",      () -> pivot.getEncoderAbsolutePosition()) .withPosition(0, 5).withSize(2, 1);
        tab.addNumber("Pivot Encoder Angle",() -> pivot.getEncoderAngle())            .withPosition(0, 7).withSize(2, 1);
        tab.addNumber("Pivot Shooter Angle",() -> pivot.getShooterAngle())            .withPosition(0, 9).withSize(2, 1);



        // Pivot Bottom Row - Target Angle, At Target Y/N, and motor power
        tab.addNumber("Target angle",       () -> pivot.getTargetAngle()).withPosition(5, 0).withSize(2, 1);
        tab.addBoolean("At Tgt",            () -> pivot.isAtTarget())    .withPosition(5, 3).withSize(2, 1);
        tab.addNumber("Pivot Power",        () -> pivot.getPivotPower()) .withPosition(5, 7).withSize(2, 1);
    }
}

