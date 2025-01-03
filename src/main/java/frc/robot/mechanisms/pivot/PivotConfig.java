package frc.robot.mechanisms.pivot;

public class PivotConfig {
        // Shooter Pivot Angles for Presets
        public static final double HP_INTAKE_ANGLE = 0;
        public static final double PRESET_SHOT_BUMPER = 40;  // Degrees of pivot angle (on encoder)
        
        // pivot limits
        protected static final double PIVOT_ENC_OFFSET = 1550;

        protected static final double PIVOT_MAX_ADJUSTED_ENC = 4000;
        protected static final double PIVOT_MIN_ADJUSTED_ENC = 2000;

        public static final double PIVOT_MAX_ANGLE = 190.0;  // Degrees of pivot angle (on encoder)
        public static final double PIVOT_MID_ANGLE = 95;     // temp mid angle based on pivot degrees
        public static final double PIVOT_MIN_ANGLE = 0.0;     
        
        
        protected static final double SHOOTER_MAX_ANGLE = 17.0;  // Degrees of shooter angle (run by cam)
        protected static final double SHOOTER_MID_ANGLE = 8.5;   // Degrees
        protected static final double SHOOTER_MIN_ANGLE = 0.0;

        // pivot move to angle speed
        protected static final double PIVOT_CCW_SPEED = 0.75;
        protected static final double PIVOT_CW_SPEED = -0.75;
        protected static final double PIVOT_CW_DIR =   -1.0;
        protected static final double PIVOT_CCW_DIR =   1.0;

        // pivot angle tolerance and encoder conversions

        public static final double PIVOT_ENC_INVERT = 1.0; // (Counter Clockwise positive)

        public static final double PIVOT_DEG_TO_ENC = 11.3777777778;  // (4096/360)
        public static final double PIVOT_ENC_TO_DEG = 0.087890625;  // (360/4096)
        protected static final double PIVOT_ANGLE_TOLDERANCE = 3;


        protected static final double PIVOT_ANGLE_TO_SHOOTER_ANGLE = SHOOTER_MAX_ANGLE / PIVOT_MAX_ANGLE; // 0.154545 (17.0 degrees / 195.0 degrees);
        protected static final double SHOOTER_ANGLE_TO_PIVOT_ANGLE = 1.0 / PIVOT_ANGLE_TO_SHOOTER_ANGLE; // 6.470607  

        // Speaker Shot Distance
        public static final double SPEAKER_MIN_SHOT_DISTANCE = 1.016;  // meters
        public static final double SPEAKER_MAX_SHOT_DISTANCE = 1.887;  // meters
        public static final double SPEAKER_MIN_SHOT_ANGLE = 0;         // degrees
        public static final double SPEAKER_MAX_SHOT_ANGLE = 13;        // degrees
}
