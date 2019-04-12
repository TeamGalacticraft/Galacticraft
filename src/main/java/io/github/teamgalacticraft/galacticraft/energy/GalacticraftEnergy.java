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
        if (!itemStack.hasTag()) {
            return false;
        }

        CompoundTag tag = itemStack.getTag();
        return tag.containsKey("Energy") && tag.containsKey("MaxEnergy");
    }

    public static int getBatteryEnergy(ItemStack battery) {
        if (!isEnergyItem(battery)) {
            throw new IllegalArgumentException("Provided argument is not an energy item!");
        }

        return battery.getTag().getInt("Energy");
    }

    public static int getMaxBatteryEnergy(ItemStack battery) {
        if (!isEnergyItem(battery)) {
            throw new IllegalArgumentException("Provided argument is not an energy item!");
        }

        return battery.getTag().getInt("MaxEnergy");
    }

    public static void incrementEnergy(ItemStack battery, int energyToAdd) {
        int newEnergy = getBatteryEnergy(battery);
        newEnergy = Math.min(newEnergy + energyToAdd, getMaxBatteryEnergy(battery));

        setEnergy(battery, newEnergy);
    }

    public static void decrementEnergy(ItemStack battery, int energyToRemove) {
        int newEnergy = getBatteryEnergy(battery);
        newEnergy = Math.max(newEnergy - energyToRemove, 0);

        setEnergy(battery, newEnergy);
    }

    public static void setEnergy(ItemStack battery, int newEnergy) {
        if (!isEnergyItem(battery)) {
            throw new IllegalArgumentException("Provided argument is not an energy item!");
        }

        CompoundTag tag = battery.getOrCreateTag();
        tag.putInt("Energy", newEnergy);
        battery.setTag(tag);
        battery.setDamage(battery.getDurability() - newEnergy);
    }
}
