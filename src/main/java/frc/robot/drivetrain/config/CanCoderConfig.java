package frc.robot.drivetrain.config;

import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.configs.MagnetSensorConfigs;
import com.ctre.phoenix6.signals.AbsoluteSensorRangeValue;

public class CanCoderConfig {
    /**
     * Get a configuration object configured for a CAN Coder on a MK4i Swerve Module.
     * 
     * @param modID the integer ID num of the module requesting the config object
     * @return A CANcoderConfiguration object
     */
    public static CANcoderConfiguration getConfig(int modID) {
        CANcoderConfiguration config = new CANcoderConfiguration();

        MagnetSensorConfigs magnetSensor = config.MagnetSensor;
        magnetSensor.AbsoluteSensorRange = AbsoluteSensorRangeValue.Signed_PlusMinusHalf;  // is default
        magnetSensor.MagnetOffset = DrivetrainConfig.getModAngleOffset(modID);

        return config;
    }
}
