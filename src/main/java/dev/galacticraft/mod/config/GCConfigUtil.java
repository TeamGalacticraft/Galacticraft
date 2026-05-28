package dev.galacticraft.mod.config;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class GCConfigUtil {
    public static List<ResourceLocation> disabledCelestialScreenDestinations() {
        List<ResourceLocation> out = new ArrayList<>();

        for (String id : Galacticraft.CONFIG.disabledCelestialScreenDimensions()) {
            try {
                out.add(ResourceLocation.parse(id));
            } catch (Exception e) {
                Constant.LOGGER.warn("Ignoring invalid disabled celestial screen dimension id '{}'.", id);
            }
        }

        return out;
    }
}
