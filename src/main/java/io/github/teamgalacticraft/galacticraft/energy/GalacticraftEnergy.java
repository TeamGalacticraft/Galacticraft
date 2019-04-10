package io.github.teamgalacticraft.galacticraft.energy;

import io.github.cottonmc.energy.CottonEnergy;
import io.github.cottonmc.energy.api.EnergyType;
import io.github.teamgalacticraft.galacticraft.Constants;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftEnergy {
    private static final Marker ENERGY = MarkerManager.getMarker("Energy");

    public static final EnergyType GALACTICRAFT_JOULES = new GalacticraftEnergyType();

    public static void register() {
        Registry.register(CottonEnergy.ENERGY_REGISTRY, new Identifier(Constants.MOD_ID, Constants.Energy.GALACTICRAFT_JOULES), GALACTICRAFT_JOULES);
    }

    public static boolean isEnergyItem(ItemStack itemStack) {
        CompoundTag tag = itemStack.getOrCreateTag();
        return itemStack.hasTag() && tag.containsKey("Energy") && tag.containsKey("MaxEnergy");
    }
}
