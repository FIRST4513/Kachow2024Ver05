package frc.robot.mechanisms.pivot;

public class PivotConfig {
        // Shooter Pivot Angles for Presets
        public static final double HP_INTAKE_ANGLE = 0;
        
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
