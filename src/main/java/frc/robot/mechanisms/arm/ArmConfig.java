package frc.robot.mechanisms.arm;

public class ArmConfig {
    public static final double maxArmSpeed = 0.75;
    public static final boolean armMotorsInvert = false;

    //   Enc Ct  | Enc º | Real º
    // ----------┼-------┼-------
    //  3969.0   |  250º |  100º  ⏎
    //     0.0   |    0º | -150º  ⏎
    //  1706.666 |  150º |    0º  ⏎
    //  2844.444 |  150º |  100º  ⏎

    public static final double encCountsPerDeg = 11.3777777778;  // (4096/360)
    public static final double encDegsPerCount = 0.087890625;  // (360/4096)
    // public static final double encCountOffset = (260 * encCountsPerDeg);
    public static final double encCountOffset = 2105;

    // 0 at -1285 from forward
    // 0 at 2811

    public static final int armRetractLimit = 200;
    public static final int armExtendLimit = -33;

    // verify need and value for these
    public static final int armGroundAngle = 139;
    public static final int armStowAngle = 0;
    public static final int armShooterAngle = -32;

    public static final int armAmpAngle = 125;
    public static final int armTrapAngle = 45;

    public static final int armSafeToTravelAngle = 60;
    public static final int SAFE_TRAVEL_MIN = 55;
    public static final int SAFE_TRAVEL_MAX = 85;
    public static final int SAFE_TRAP_MIN = 35;

    public static final double isAtTargetError = 3;  // degrees

    public static enum ARMPOS { GROUND, STOW, SHOOTER, AMP, TRAP, UNKOWN; }

    
}
