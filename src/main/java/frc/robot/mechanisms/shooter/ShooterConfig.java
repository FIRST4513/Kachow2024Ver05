package frc.robot.mechanisms.shooter;

public class ShooterConfig {

    // falcon rpm at 1: 6380
    // pulley gear ratio: 0.625 from falcon-to-wheel
    // wheel rpm at 1: 6380 * 0.625 = 3975
    // wheel rps at 1: 106

    // retract/eject speeds (rps)
    protected static final double SPEAKER_BOTTOM = -104; //.425
    protected static final double SPEAKER_TOP = -104; //.45
    
    protected static final double RETRACT_BOTTOM = 25; //-106;
    protected static final double RETRACT_TOP = 25; //-106;

    protected static final double SHOT_VELOCITY_TOLERANCE = 1.5;  // ±1 rps tolerance


}
