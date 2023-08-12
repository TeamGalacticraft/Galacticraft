package dev.galacticraft.mod.content;

import dev.galacticraft.mod.content.entity.ai.sensor.NearestArchGreySensor;
import net.minecraft.world.entity.ai.sensing.AdultSensor;
import net.minecraft.world.entity.ai.sensing.SensorType;

public class GCEntitySensorTypes {

    public static final SensorType<NearestArchGreySensor> NEAREST_ARCH_GREY_SENSOR = SensorType.register("nearest_arch_grey", NearestArchGreySensor::new);

    public static void register() {

    }

}
