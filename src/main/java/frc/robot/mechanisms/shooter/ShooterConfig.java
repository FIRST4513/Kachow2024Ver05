package frc.robot.mechanisms.shooter;

public class ShooterConfig {
    // Shooter Pivot Angles for Presets
    public static final double HP_INTAKE_ANGLE = 0;

    // falcon rpm at 1: 6380
    // pulley gear ratio: 0.625 from falcon-to-wheel
    // wheel rpm at 1: 6380 * 0.625 = 3975
    // wheel rps at 1: 106

    // retract/eject speeds (rps)
    protected static final double SPEAKER_BOTTOM = 106; //.425
    protected static final double SPEAKER_TOP = 106; //.45
    
    protected static final double RETRACT_BOTTOM = 40; //-106;
    protected static final double RETRACT_TOP = 40; //-106;

    protected static final double SHOT_VELOCITY_TOLERANCE = 1;  // ±1 rps tolerance

    // pivot limits
    protected static final double PIVOT_MAX_ENC = 4000;
    protected static final double PIVOT_MIN_ENC = 2000;

    // pivot move to angle speed
    protected static final double PIVOT_MOVE_SPEED = 0.5;

    // pivot angle tolerance and encoder conversions
    public static final double PIVOT_DEG_TO_ENC = 11.3777777778;  // (4096/360)
    public static final double PIVOT_ENC_TO_DEG = 0.087890625;  // (360/4096)
    protected static final double PIVOT_ANGLE_TOLDERANCE = 3;
    protected static final double PIVOT_ENC_OFFSET = 0;

    // Speaker Shot Distances
    public static final double SPEAKER_MIN_SHOT_DISTANCE = 1.016;  // meters
    public static final double SPEAKER_MIX_SHOT_DISTANCE = 1.887;  // meters
    public static final double SPEAKER_MIN_SHOT_ANGLE = 0;         // degrees
    public static final double SPEAKER_MIX_SHOT_ANGLE = 13;        // degrees
}
