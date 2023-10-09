package dev.galacticraft.mod.content;

import dev.galacticraft.mod.content.entity.ai.sensor.NearestArchGreySensor;
import dev.galacticraft.mod.content.entity.ai.sensor.TimeNearPlayerSensor;
import net.minecraft.world.entity.ai.sensing.AdultSensor;
import net.minecraft.world.entity.ai.sensing.SensorType;

public class GCEntitySensorTypes {

    public static final SensorType<NearestArchGreySensor> NEAREST_ARCH_GREY_SENSOR = SensorType.register("nearest_arch_grey", NearestArchGreySensor::new);

    public static final SensorType<TimeNearPlayerSensor> TIME_NEAR_PLAYER_SENSOR = SensorType.register("time_near_player", TimeNearPlayerSensor::new);
    public static void register() {

    }

}
