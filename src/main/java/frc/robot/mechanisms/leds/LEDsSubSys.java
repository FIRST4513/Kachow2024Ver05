package frc.robot.mechanisms.leds;

import java.util.List;
import java.util.Random;

import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Robot;
import frc.robot.RobotTelemetry;
import frc.robot.mechanisms.leds.LEDsConfig.Section;
import frc.robot.mechanisms.passthrough.PassthroughSubSys.PassthroughState;
import frc.robot.mechanisms.shooter.ShooterSubSys.FireState;
import frc.robot.mechanisms.leds.LEDsConfig.LEDDisplayMode;
import frc.robot.mechanisms.intake.IntakeSubSys.IntakeState;
import frc.robot.mechanisms.leds.LEDsConfig.Dest;

public class LEDsSubSys extends SubsystemBase {
    private int counter                = 0;
    private double time                = 0;
    private Color allianceColor        = Color.kRed; // for fail fast!
    private Color notAllianceColor     = Color.kGold; // for fail fast!
    private boolean isSparkleActive    = false;
    private boolean isLightningActive  = false;
    private double brightness          = 0;
    private int length                 = 0;
    private LEDDisplayMode displayMode = LEDDisplayMode.NONE;
    private LEDstrip ledstrip;


    // ---- CONSTRUCTOR ----
    public LEDsSubSys () {
        ledstrip            = new LEDstrip( LEDsConfig.port, LEDsConfig.length);
        isSparkleActive     = LEDsConfig.isSparkleActive;
        isLightningActive   = LEDsConfig.isLightningActive;
        brightness          = LEDsConfig.brightness;
        displayMode         = LEDDisplayMode.NONE;

        counter             = 0;
        time                = 0;
        RobotTelemetry.print("LEDs Subsystem Initialized: ");
    } //end constructor

    public void periodic() {
        counter = (counter + 1);// % 500; // sets up timing for 10s loop
        // factors of 500: 2 2 5 5 5
        // factors of 24*20 = 480 = 2 2 2 2 2 3 5
        // factors of 4*9*25 = 900 - 2 2 3 3 5 5
        // factors of 50 = 2 5 5...fractions of a second
        if (counter % 5 == 0) { // update the LEDs every 5th cycle
            time = counter * 0.02;  // time is seconds in the 10s loop
                                    // it is really 0.1 increments

            // displayMode         = LEDDisplayMode.TELEOP_STATUS;

            // displayMode         = LEDDisplayMode.FLASH_TEST; // tested
            // displayMode         = LEDDisplayMode.KIT; // tested
            // displayMode         = LEDDisplayMode.METEOR; // tested
            // displayMode         = LEDDisplayMode.COLOR_MARQUEE; // tested
            // displayMode         = LEDDisplayMode.RAINBOW_WAVE; // tested
            // displayMode         = LEDDisplayMode.RAINBOW; // tested
            // displayMode         = LEDDisplayMode.SETUP_STATUS;
            // displayMode         = LEDDisplayMode.TELEOP_STATUS;
            // displayMode         = LEDDisplayMode.BREATH; // tested
            // displayMode         = LEDDisplayMode.CLOUDS; // tested
            // displayMode         = LEDDisplayMode.NONE;
            // displayMode         = LEDDisplayMode.MARQUEE; // tested

            switch( displayMode) {
                case SETUP_STATUS:
                    setupStatus();
                    break;
                case TELEOP_STATUS:
                    telopStatus();
                    break;
                case RAINBOW:
                    rainbow( Section.all, LEDsConfig.length,
                             LEDsConfig.rainbowDuration);
                    break;
                case RAINBOW_WAVE:
                    wave( Section.all, Color.kRed, Color.kBlue, LEDsConfig.length,
                          LEDsConfig.waveSlowDuration);
                    break;
                case METEOR:
                    /*
                    meteor( Section.leftFront,  allianceColor, 8, 0.5, 1.0, false);
                    meteor( Section.leftBack,   allianceColor, 8, 0.5, 1.0,  true);
                    meteor( Section.rightFront, allianceColor, 8, 0.5, 1.0, false);
                    meteor( Section.rightBack,  allianceColor, 8, 0.5, 1.0, true);
                    */
                    meteor( Section.leftFront,  allianceColor, 4, 0.75, 2.0, false);
                    meteor( Section.leftBack,   allianceColor, 4, 0.75, 2.0, true);
                    meteor( Section.rightFront, allianceColor, 4, 0.75, 2.0, false);
                    meteor( Section.rightBack,  allianceColor, 4, 0.75, 2.0, true);
                    break;
                case MARQUEE:
                    int [] pattern = {1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
                    marquee( Section.all, allianceColor, pattern,
                             LEDsConfig.marqueePeriod, true);
                    break;
                case COLOR_MARQUEE:
                    Color [] colorPattern = { Color.kRed, Color.kWhite, Color.kBlue};
                    colorMarquee( Section.all, colorPattern,
                                  LEDsConfig.colorMarqueePeriod,
                                  true);
                    break;
                case FLASH_TEST:
                    flash( Section.leftFront,  Color.kYellow, 1.0);
                    flash( Section.leftBack,   Color.kRed,    0.5);
                    flash( Section.rightFront, Color.kGreen,  0.2);
                    flash( Section.rightBack,  Color.kBlue,   0.1);
                    break;
                case BREATH:
                    breath( Section.all, Color.kRed, Color.kBlue, 5.0);
                    break;
                case CLOUDS:
                    clouds( Section.all, changeBrightness( Color.kWhite, 0.05));
                    break;
                case KIT:
                case NONE:
                default:
                    //TODO nice if kit could use discontinuous LEDs: front and back
		    // do it on all four strips for now
                    kit( Section.leftFront,  allianceColor, LEDsConfig.kitWidth, LEDsConfig.kitPeriod, false);
                    kit( Section.leftBack,   allianceColor, LEDsConfig.kitWidth, LEDsConfig.kitPeriod, true);
                    kit( Section.rightFront, allianceColor, LEDsConfig.kitWidth, LEDsConfig.kitPeriod, false);
                    kit( Section.rightBack,  allianceColor, LEDsConfig.kitWidth, LEDsConfig.kitPeriod, true);
            }

            // check on enchanted modes
            if ( isLightningActive) {
                flashRandomly( Section.all, notAllianceColor,
                               LEDsConfig.lightningProbability);
            }
            if ( isSparkleActive) {
                sparkleRandomly( Section.all, Color.kWhite,
                                 LEDsConfig.sparkleProbability);
            }
        }
        if (counter % 5 == 1) {
            ledstrip.update();
        }
    } // end periodic

    /**
     * Sets a section to a specified color
     * 
     */
    public void solid( Section section, Color color) {
        if (color != null) {
            Color solidColor = changeBrightness( color, brightness);
            for (int i = section.start(); i < section.end(); i++) {
                ledstrip.setLED( i, solidColor);
            }
        }
    }

    /**
     * Sets a percentage of the LEDs to a color
     *
     * @param section A section of the LEDs to use
     * @param percent The percentage of LEDs to set
     * @param color The color to set the LEDs to
     * @param isReversed The start and end is reversed
     * 
     */
    public void percent( Section section, double percent, Color color, boolean isReversed) {
        int litLength = (int) Math.round( (section.end() - section.start()) * percent);
        if (!isReversed) {
            for (int i = 0; i < section.end(); i++) {
                boolean on =  i <=  litLength;
                ledstrip.setLED( i, on ? color : Color.kBlack);
            }
        } else { // reeverse
            for (int i = section.end() - 1 ; i >= section.start(); i--) {
                boolean on =  i >=  (section.end() - litLength);
                ledstrip.setLED( i, on ? color : Color.kBlack);
            }
        }
    }

    /**
     * Set a section of the LEDs to strobe
     *
     * @param section The section of the LEDs to strobe
     * @param color The color to strobe
     * @param duration The duration of the strobe cycle
     * @param dutyCycle The fraction that the strobe on
     */
    public void strobe( Section section, Color color, double duration, double dutyCycle) {
        for (int i = section.start(); i < section.end(); i++) {
            boolean on = ((time % duration) / duration) <= dutyCycle;
            ledstrip.setLED( i, on ? changeBrightness(color, brightness) : Color.kBlack);
        }
    }

    /**
     * Set a section of the LEDs to flash repeatedly
     *
     * @param section The section of the LEDs to flash
     * @param color The color to flash
     * @param duration The duration of the flash cycle (on and off)
     */
    public void flash( Section section, Color color, double duration) {
        strobe( section, color, duration, 0.5);
    }
    
    /**
     * Set a section of the LEDs to flash randomly, intended to overwrite
     *
     * @param section The section of the LEDs to flash
     * @param color The color to flash
     * @param probability The probability of the flash
     */
    public void flashRandomly( Section section, Color color, double probability) {
        Random random = new Random();
        Color flashColor = changeBrightness( color, LEDsConfig.lightningBrightness);
        if (random.nextDouble() < probability) {
            for (int i = section.start(); i < section.end(); i++) {
                ledstrip.setLED( i, flashColor);
            }
        }
    }

    /**
     * Flash single LEDs in a section randomly intended to overwrite
     * 
     * @param section The section of the LEDs to flash
     * @param color The color to flash
     * @param probability The probability of the flash
     */
    public void sparkleRandomly( Section section, Color color, double probability) {
        Random random = new Random();
        int offset = 0;
        Color sparkleColor = changeBrightness( color, LEDsConfig.sparkleBrightness);
        if (random.nextDouble() < probability) {
            offset = (int) Math.round( (section.end() - section.start()) * random.nextDouble());
            if (offset >= (section.end() - section.start()) ) {
                offset = section.end() - section.start() - 1;
            }
            ledstrip.setLED( section.start() + offset, sparkleColor);
        }
    }

    /**
     * Change a section to morph between two colors sinusoidally
     *
     * @param section The section of the LEDs to flash
     * @param color Color on one limit
     * @param color Color on the other limit
     * @param duration The duration of the morphing
     */
    public void breath( Section section, Color c1, Color c2, double duration) {
        double x =
                ((time % LEDsConfig.breathDuration) / LEDsConfig.breathDuration)
                        * 2.0
                        * Math.PI; // radians
        double ratio = (Math.sin( x) + 1.0) / 2.0; // 0..1
        double red = (c1.red * (1 - ratio)) + (c2.red * ratio);
        double green = (c1.green * (1 - ratio)) + (c2.green * ratio);
        double blue = (c1.blue * (1 - ratio)) + (c2.blue * ratio);
        solid( section, new Color( red, green, blue));
    }

    /**
     * Sets the LEDs to a revolving rainbow pattern
     *
     * @param section The section of the LEDs to set
     * @param cycleLength The length of the rainbow cycle in LEDs
     * @param duration The duration of the rainbow cycle in seconds
     */
    public void rainbow( Section section, double cycleLength, double duration) {
        double x = (1 - ((time / duration) % 1.0)) * 180.0;
        double xDiffPerLed = 180.0 / cycleLength;
        for (int i = section.start(); i < section.end(); i++) {
            x = (x + xDiffPerLed) % 180;
            ledstrip.setHSV( i, (int) x, 255, 255);
        }
    }

    /**
     * Sets the LEDs to a wave pattern
     *
     * @param section The section of the LEDs to set
     * @param c1 The first color of the wave
     * @param c2 The second color of the wave
     * @param cycleLength The length of the wave cycle in LEDs
     * @param duration The duration of the wave
     */
    public void wave(
            Section section,
            Color c1,
            Color c2,
            double cycleLength,
            double duration) {
        double x = (1 - ((time % duration) / duration)) * 2.0 * Math.PI;
        double xDiffPerLed = (2.0 * Math.PI) / cycleLength;
        for (int i = section.start(); i < section.end(); i++) {
            x += xDiffPerLed;

            double ratio = (Math.pow( Math.sin( x), LEDsConfig.waveExponent) + 1.0) / 2.0;
            if (Double.isNaN(ratio)) {
                ratio = (-Math.pow( Math.sin( x + Math.PI), LEDsConfig.waveExponent) + 1.0) / 2.0;
            }
            if (Double.isNaN( ratio)) {
                ratio = 0.5;
            }
            double red = brightness * (c1.red * (1 - ratio)) + (c2.red * ratio);
            double green = brightness * (c1.green * (1 - ratio)) + (c2.green * ratio);
            double blue = brightness * (c1.blue * (1 - ratio)) + (c2.blue * ratio);
            ledstrip.setLED( i, new Color(red, green, blue));
        }
    }
    
    /**
     * Sets the LEDs to a cycling color stripe pattern
     *
     * @param section The section of the LEDs to set
     * @param List<color> A list of colors for the stripes
     * @param int The width of each stripe
     * @param int The duration of the cycle
     */
    public void stripes(
                Section section,
                List<Color> colors,
                int width,
                double duration) {
        int offset = (int) (time % duration / duration * width * colors.size());
        for (int i = section.start(); i < section.end(); i++) {
            int colorIndex =
                    (int) (Math.floor((double) (i - offset) / width) + colors.size())
                            % colors.size();
            colorIndex = colors.size() - 1 - colorIndex;
            ledstrip.setLED( i, colors.get( colorIndex));
        }
    }

    /**
     * Sets the LEDs to the Knight Rider Car "Kit" pattern
     *
     * @param section The section of the LEDs to set
     * @param color The moving color bar
     * @param width The width of the color bar
     * @param duration The duration of one cycle of movement
     */
    public void kit(
                        Section section,
                        Color color,
                        double width,
                        double duration,
                        boolean isReversed) {

        double ratio = time % duration / duration;
        if (ratio > 0.5) { // fold the ratio in half
            ratio = 1 - ratio;
        }
        Color kitColor = changeBrightness( color, brightness);
        length = section.end() - section.start();
        int position = (int) Math.round ( length * ratio * 2);
        if (!isReversed) {
            for( int i = 0; i < length; i++) {
                if (i >= position - width/2 && i < position + width/2){
                    ledstrip.setLED( section.start() + i, kitColor);
                } else {
                    ledstrip.setLED( section.start() + i, Color.kBlack);
                }
            }
        } else {
            for( int i = 0; i < length; i++) {
                if (i >= position - width/2 && i < position + width/2){
                    ledstrip.setLED( section.end() - i -1, kitColor);
                } else {
                    ledstrip.setLED( section.end() - i -1, Color.kBlack);
                }
            }

        }
    }


    /**
     * Sets the LEDs for a repeating meteor pattern
     *
     * @param section The section of the LEDs to set
     * @param color The color of the meteor
     * @param tailLength The length of the meteor
     * @param shotTime The duration of the meteor
     * @param cycleTime The time between meteors (or Roman candles)
     * @param isReversed The direction of the meteor is reversed.
     */
    public void meteor(
                        Section section,
                        Color   color,
                        double  tailLength,
                        double  shotTime,
                        double  cycleTime,
                        boolean isReversed) {

        double timeInCycle = time % cycleTime;
        if (timeInCycle < shotTime) {
            length = section.end() - section.start();
            double ratio = timeInCycle / shotTime;
            int position = (int) Math.round( ratio * length);
            for( int i = 0; i <= length; i++) {
                if (i >= position - tailLength && i <= position){
                    double meteorBrightness = 1.0 - ((position - i) / tailLength);
                    int r = (int) Math.round( brightness * meteorBrightness * color.red * 255);
                    int g = (int) Math.round( brightness * meteorBrightness * color.green * 255);
                    int b = (int) Math.round( brightness * meteorBrightness * color.red * 255);
                    if (!isReversed) {
                        Robot.print("Meteor Tail:" +  (section.start() + i) + " " +  r+ " " + g + " " + b);
                        ledstrip.setLED( section.start() + i, r, g, b);
                    } else {
                        ledstrip.setLED( section.end() - i - 1, r, g, b);
                    }
                } else {
                    if (!isReversed) {
                        ledstrip.setLED( section.start() + i, Color.kBlack);
                    } else {
                        ledstrip.setLED( section.end() - i - 1, Color.kBlack);
                    }
                }
            }
        } else {
            solid( section, Color.kBlack);
        }
    }

    /**
     * Sets the LEDs to a marquee pattern
     *
     * @param section  The section of the LEDs to set
     * @param color    The color of the LEDs
     * @param pattern  An array of integer 0 and 1s for the initial pattern
     * @param duration  The time it takes to go through one cycle:W
     */
    public void marquee(
            Section section,
            Color color,
            int [] pattern,
            double duration,
            boolean isReversed) {
        
        int offset; 
        double ratio; 
        Color marqueeColor = changeBrightness( color, brightness);

        ratio = time % duration / duration;
        if (!isReversed) {
            offset = (int) Math.round( ratio * pattern.length);
        } else {
            offset = pattern.length - (int) Math.round( ratio * pattern.length);
        }
        for (int i = section.start(); i < section.end(); i++) { 
            if (pattern[( (i + offset + pattern.length) % pattern.length)] == 1) {
                ledstrip.setLED( i, marqueeColor);
            } else {
                ledstrip.setLED( i, Color.kBlack);
            }
        }
    }


    /**
     * Sets the LEDs to a colored marquee pattern
     *
     * @param section   The section of the LEDs to set
     * @param List <Color>  A list of colors for the pattern
     * @param duration  The time it takes to go through one cycle
     * @param isReversed The circulation is reversed
     */

    public void colorMarquee(
                                Section section,
                                Color [] pattern,
                                double duration,
                                boolean isReversed) {
        
        double ratio;
        int offset;
        for (int i = 0; i < pattern.length; i++) {
            pattern[i] = changeBrightness( pattern[i], brightness);
        }
        ratio = time % duration / duration;
        if (isReversed) {
            offset = pattern.length - (int) Math.round( ratio * pattern.length);
        } else {
            offset = (int) Math.round( ratio * pattern.length);
        }
        //need an offset into pattern
        for (int i = section.start(); i < section.end(); i++) { 
            ledstrip.setLED( i, pattern[ (i + offset)  % pattern.length]);
        }
    }

    private void setupStatus() {
        // solid left, center, right corresponding to correct auto selected
        // flash left, center, right corresponding to incorrect auto selected
        int position = 0;  // get from Drive Station (assuming 0,1, or 2)
        Section section = Section.all;
        if (allianceColor == Color.kRed) {
            position = position + 3;
        }
        boolean isPositionCorrect = true; // get from somewhere?
        int numberOfPointsSelected = 0; // get this from Drive Station

        solid( Section.all, Color.kBlack);
        switch( position) {
            case 0: //blue speaker closest to scoring tables
            case 5: //red speaker furthest from scoring tables
                if (isPositionCorrect) {
                    solid( Section.rightFront, allianceColor);
                    solid( Section.rightBack, allianceColor);
                } else {
                    flash( Section.rightFront, allianceColor,
                        LEDsConfig.setupErrorFlashPeriod);
                    flash( Section.rightBack, allianceColor,
                        LEDsConfig.setupErrorFlashPeriod);
                }
                break;
            case 1: //blue center speaker
            case 4: //red center speaker
                if (isPositionCorrect) {
                    solid( Section.rightFrontUpperHalf, allianceColor);
                    solid( Section.rightBackUpperHalf, allianceColor);
                    solid( Section.leftFrontUpperHalf, allianceColor);
                    solid( Section.leftBackUpperHalf, allianceColor);
                } else {
                    flash( Section.rightFrontUpperHalf, allianceColor,
                        LEDsConfig.setupErrorFlashPeriod);
                    flash( Section.rightBackUpperHalf, allianceColor,
                        LEDsConfig.setupErrorFlashPeriod);
                    flash( Section.leftFrontUpperHalf, allianceColor,
                        LEDsConfig.setupErrorFlashPeriod);
                    flash( Section.leftBackUpperHalf, allianceColor,
                        LEDsConfig.setupErrorFlashPeriod);
                }
                break;
            case 2: //blue speaker furthest from scoring tables
            case 3: //red speaker closest to scoring tables
                if (isPositionCorrect) {
                    solid( Section.leftFront, allianceColor);
                    solid( Section.leftBack, allianceColor);
                } else {
                    flash( Section.leftFront, allianceColor,
                        LEDsConfig.setupErrorFlashPeriod);
                    flash( Section.leftBack, allianceColor,
                        LEDsConfig.setupErrorFlashPeriod);
                }
                break;
            default:
        }
        if (isPositionCorrect) {
            solid( section, allianceColor);
        } else {
            flash( section, allianceColor,
                    LEDsConfig.setupErrorFlashPeriod);
        }

        // sides show number of points selected from bottom, 0,1,2
        if (numberOfPointsSelected == 1) {
            solid( Section.leftFrontLowerHalf, allianceColor);
            solid( Section.leftBack, allianceColor);
            solid( Section.rightFrontLowerHalf, allianceColor);
            solid( Section.rightBack, allianceColor);
        } else if (numberOfPointsSelected == 2) {
            solid( Section.leftFrontUpperHalf, allianceColor);
            solid( Section.leftBackUpperHalf, allianceColor);
            solid( Section.rightFrontUpperHalf, allianceColor);
            solid( Section.rightBackUpperHalf, allianceColor);
        } else {
            solid( Section.leftFront, allianceColor);
            solid( Section.leftBack, allianceColor);
            solid( Section.rightFront, allianceColor);
            solid( Section.rightBack, allianceColor);
        }
    }

    private void telopStatus() {
        // this code has been modified from the example, with real life data and getters; thanks Kirk
        // gamepiece manipulation - intake conditions
        boolean isGroundIntaking = Robot.intake.getState() == IntakeState.GROUND;
        boolean isHPIntaking = Robot.shooter.getFireState() == FireState.HP_INTAKE;
        boolean isPieceInPickup = Robot.intake.getGamepieceDetected();

        // gamepiece manipulation - eject conditions
        boolean isShooterShootingSpeaker = Robot.shooter.getFireState() == FireState.SPEAKER;
        boolean isShooterAtVelocity = Robot.shooter.areMotorsAtVelocityTarget();
        boolean isFiring = (Robot.shooter.getFireState() == FireState.SPEAKER) && (Robot.passthrough.getState() == PassthroughState.EJECT);

        // robot manipulation - Climbing
        boolean isClimberAboveZero = Robot.climber.getAnyAboveZero();
        boolean isClimberLowering  = Robot.climber.getAveragePower() < 0;

        // robot auto drive (unused)
        boolean isAutoDriveEngaged = false; // get from drivetrain
        Dest destination = Dest.NONE;  // get from Drivestation

        solid( Section.all, Color.kBlack);
        if (isClimberAboveZero) {
            int [] pattern = {1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
            marquee( Section.all, Color.kBlue, pattern,
                        LEDsConfig.marqueePeriod, true);
        }
        // first check for eject conditions - top priority
        else if (isShooterShootingSpeaker) {
            if (isShooterAtVelocity) {
                // ready to fire, at speed
                flash( Section.all, Color.kGreen,
                       LEDsConfig.teleopReadyFlashPeriod * 4);
            } else if (isFiring) {
                // firing
                flash( Section.all, Color.kPurple,
                        LEDsConfig.teleopReadyFlashPeriod * 2);
            } else {
                // spooling up but going to shoot speaker
                int [] pattern = {1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
                marquee( Section.all, Color.kRed, pattern,
                            LEDsConfig.marqueePeriod, true);
            }
        }
        // otherwise check for intake conditions - second priority
        else if (isPieceInPickup) {
            breath(Section.all, Color.kBlack, Color.kGreen, 1.0);
        } else if (isGroundIntaking) {
            int [] pattern = {1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
            marquee( Section.all, Color.kPurple, pattern,
                        LEDsConfig.marqueePeriod, true);
        } else if (isHPIntaking) {
            int [] pattern = {1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
            marquee( Section.all, Color.kPurple, pattern,
                        LEDsConfig.marqueePeriod, true);
        }
        // default condition
        else {
            wave( Section.all, Color.kBlack, allianceColor, LEDsConfig.length,
                  LEDsConfig.waveSlowDuration);
        }

        // top is for piece readiness
        // slides are for destination information
        if (isAutoDriveEngaged) {
            switch( destination) {
                case SCORE_BLUE_AMP:
                case SCORE_RED_AMP:
                    flash( Section.leftFrontUpperHalf, allianceColor,
                            LEDsConfig.teleopAutoDriveEngagedFlashPeriod);
                    flash( Section.leftBackUpperHalf, allianceColor,
                            LEDsConfig.teleopAutoDriveEngagedFlashPeriod);
                    flash( Section.rightFrontUpperHalf, allianceColor,
                            LEDsConfig.teleopAutoDriveEngagedFlashPeriod);
                    flash( Section.rightBackUpperHalf, allianceColor,
                            LEDsConfig.teleopAutoDriveEngagedFlashPeriod);
                    break;

                case PICKUP_BLUE_RIGHT:
                case PICKUP_RED_RIGHT:
                case SCORE_BLUE_SPEAKER_RIGHT:
                case SCORE_RED_SPEAKER_RIGHT:
                case SHOOTER_PICKUP_BLUE_LEFT:
                case SHOOTER_PICKUP_RED_LEFT:
                    flash( Section.leftFront, allianceColor,
                            LEDsConfig.teleopAutoDriveEngagedFlashPeriod);
                    flash( Section.leftBack, allianceColor,
                            LEDsConfig.teleopAutoDriveEngagedFlashPeriod);
                    break;

                case PICKUP_BLUE_LEFT:
                case PICKUP_RED_LEFT:
                case SCORE_BLUE_SPEAKER_LEFT:
                case SCORE_RED_SPEAKER_LEFT:
                case SHOOTER_PICKUP_BLUE_RIGHT:
                case SHOOTER_PICKUP_RED_RIGHT:
                    flash( Section.rightFront, allianceColor,
                            LEDsConfig.teleopAutoDriveEngagedFlashPeriod);
                    flash( Section.rightBack, allianceColor,
                            LEDsConfig.teleopAutoDriveEngagedFlashPeriod);
                    break;

                default: //  include  SCORE_*_SPEAKER_CENTER
                    flash( Section.leftFront, allianceColor,
                            LEDsConfig.teleopAutoDriveEngagedFlashPeriod);
                    flash( Section.rightFront, allianceColor,
                            LEDsConfig.teleopAutoDriveEngagedFlashPeriod);
            }
        }
    }

    public void clouds ( Section section, Color color) {
        solid( section, color);
    }


    public Color changeBrightness ( Color color, double brightness) {
        return new Color(
                            color.red   * brightness,
                            color.green * brightness,
                            color.blue  * brightness);
    }

    public void setLEDDisplayMode ( LEDDisplayMode mode) {
        displayMode = mode;
    }

    public void setLEDTeamColor ( Color color) {
        allianceColor = color;

        if (color == Color.kRed) {
            notAllianceColor = Color.kBlue;
        } else if (color == Color.kBlue) {
            notAllianceColor = Color.kRed;
        } else if (color == Color.kMaroon) {
            notAllianceColor = Color.kGold;
        } else {
            notAllianceColor = Color.kPurple;
        }
    }

    public void setLightningActive(boolean enabled) {
        isLightningActive = enabled;
    }

    public void setSparkleActive(boolean enabled) {
        isSparkleActive = enabled;
    }
} //end LEDsSubSys
