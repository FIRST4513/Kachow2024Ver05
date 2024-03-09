package frc.robot.mechanisms.leds;

public class LEDsConfig {
    public static final int port = 0;
    public static final double strobeFastDuration = 0.1;
    public static final double strobeSlowDuration = 0.2;
    public static final double breathDuration = 1.0;
    public static final double rainbowCycleLength = 25.0;
    public static final double rainbowDuration = 0.25;
    public static final double waveExponent = 0.4;
    public static final double waveFastCycleLength = 25.0;
    public static final double waveFastDuration = 0.25;
    public static final double waveSlowCycleLength = 25.0;
    public static final double waveSlowDuration = 3.0;
    public static final double waveAllianceCycleLength = 15.0;
    public static final double waveAllianceDuration = 2.0;
    public static final double autoFadeTime = 2.5;
    public static final double autoFadeMaxTime = 5.0;
    public static final boolean isSparkActive = true;
    public static final boolean isLightningActive = true;

    public static final double setupErrorFlashPeriod = 0.25; //s
    public static final double teleopReadyFlashPeriod = 0.25; //s
    public static final double teleopAutoDriveEngagedFlashPeriod = 0.5; //s
    public static final double marqueePeriod = 0.5; //s
    public static final double colorMarqueePeriod = 0.5; //s
    public static final double kitPeriod = 4.0;
    public static final int kitWidth = 10;
    public static final double lightningProbability = 0.1;
    public static final double sparkleProbability = 0.2;


    /*
     * Robot has three strips of LEDs connected in series: left, top, right
     * The sides are divided into either thirds: upper, middle, lower; or four
     * quarters: upper, mid upper, mid lower, and lower. The top is divided into four
     * quarters: left, center left, center right, and right.
     * 
     */
    private static final int lenLeftFront  = 45;
    private static final int lenLeftBack   = 45;
    private static final int lenRightFront = 45;
    private static final int lenRightBack  = 45;
    public static final int length = lenLeftFront + lenLeftBack +
                                     lenRightFront + lenRightBack;

    private static final int offLeftFront   = 0;
    private static final int offLeftBack    = lenLeftFront;
    private static final int offRightFront  = lenLeftFront + lenLeftBack;
    private static final int offRightBack   = lenLeftFront + lenLeftBack +
                                              lenRightFront;

    /*
     * Robot perspective
     * 
     *             right
     * front                   back
     * shooter                 pickup
     *       control feed
     *              left
     * 
     */
    // The LED sections are defined below
    // use only whole and half sections...
    public enum Section {
        //section name      ( start pixel                   , last pixel + 1)
        all                 (0                        , length),

        leftFront           (offLeftFront                   , offLeftFront+lenLeftFront),
        leftFrontLowerHalf  (offLeftFront                   , offLeftFront+lenLeftFront /2),
        leftFrontUpperHalf  (offLeftFront+lenLeftFront /2   , offLeftFront+lenLeftFront),

        leftBack            (offLeftBack                    , offLeftBack+lenLeftBack),
        leftBackLowerHalf   (offLeftBack                    , offLeftBack+lenLeftBack /2),
        leftBackUpperHalf   (offLeftBack+lenLeftBack /2     , offLeftBack+lenLeftBack),

        rightFront          (offRightFront                  , offRightFront+lenRightFront),
        rightFrontLowerHalf (offRightFront                  , offRightFront+lenRightFront /2),
        rightFrontUpperHalf (offRightFront+lenRightFront /2 , offRightFront+lenRightFront),

        rightBack           (offRightBack                   , offRightBack+lenRightFront),
        rightBackLowerHalf  (offRightBack                   , offRightBack+lenRightFront /2),
        rightBackUpperHalf  (offRightBack+lenRightBack /2   , offRightBack+lenRightFront);


        private final int start;
        private final int end;
        Section (int start, int end) {
            this.start = start;
            this.end = end;
        }
        public int start()   { return start; }
        public int end() { return end; }

    }

    public enum LEDDisplayMode { // LED display modes
        SETUP_STATUS,
        TELEOP_STATUS,
        BREATH,
        RAINBOW,
        RAINBOW_WAVE,
        KIT,
        METEOR,
        MARQUEE,
        COLOR_MARQUEE,
        NONE;
    }

    // this may not belong here
    // perspective depends on alliance and side of robot being used (i.e., driving direction)
    public enum Dest { // destinations field relative
        PICKUP_BLUE_LEFT,
        PICKUP_BLUE_CENTER,
        PICKUP_BLUE_RIGHT,
        PICKUP_RED_LEFT,
        PICKUP_RED_CENTER,
        PICKUP_RED_RIGHT,

        SHOOTER_PICKUP_BLUE_LEFT,
        SHOOTER_PICKUP_BLUE_CENTER,
        SHOOTER_PICKUP_BLUE_RIGHT,
        SHOOTER_PICKUP_RED_LEFT,
        SHOOTER_PICKUP_RED_CENTER,
        SHOOTER_PICKUP_RED_RIGHT,

        SCORE_BLUE_AMP,
        SCORE_BLUE_SPEAKER_LEFT,
        SCORE_BLUE_SPEAKER_CENTER,
        SCORE_BLUE_SPEAKER_RIGHT,
        SCORE_RED_AMP,
        SCORE_RED_SPEAKER_LEFT,
        SCORE_RED_SPEAKER_CENTER,
        SCORE_RED_SPEAKER_RIGHT,

        NONE;
    }
}
