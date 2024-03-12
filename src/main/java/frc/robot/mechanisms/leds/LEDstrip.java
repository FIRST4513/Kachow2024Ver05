package frc.robot.mechanisms.leds;

// This replaces the Spectrum LEDs subsystem and moves its periodic routine
// to the LEDsSubSys

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;

public class LEDstrip {
    private AddressableLED leds;
    private AddressableLEDBuffer buffer;

    // ---- CONSTRUCTOR ----
    public LEDstrip( int port, int length) {
        leds = new AddressableLED( port);
        leds.setLength( length);
        buffer = new AddressableLEDBuffer( length);
        leds.setData( buffer);
        leds.start();
    }

    /**
     * Allows to reconfigure the LEDs after boot. This can be helpful if different robot
     * configurations have different LEDs
     */
    public void setLEDsPortLength(int port, int length) {
        leds.stop();
        leds.close();
        leds = new AddressableLED(port);
        leds.setLength(length);
        buffer = new AddressableLEDBuffer(length);
        leds.start();
    }

    public void update() {
        leds.setData( buffer);
    }

    public void setLED(int i, Color c) {
        buffer.setLED(i, c);
    }

    public void setLED(int i, int r, int g, int b) {
        buffer.setLED(i, new Color(r, g, b));
    }

    public void setHSV(int i, int h, int s, int v) {
        buffer.setHSV(i, h, s, v);
    }
}