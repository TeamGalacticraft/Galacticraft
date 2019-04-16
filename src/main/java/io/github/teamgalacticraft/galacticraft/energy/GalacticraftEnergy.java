package io.github.teamgalacticraft.galacticraft.energy;

import io.github.cottonmc.energy.CottonEnergy;
import io.github.cottonmc.energy.api.EnergyType;
import io.github.teamgalacticraft.galacticraft.Constants;
import io.github.teamgalacticraft.galacticraft.api.EnergyHolderItem;
import io.github.teamgalacticraft.galacticraft.items.OxygenTankItem;
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
    public static final EnergyType GALACTICRAFT_OXYGEN = new OxygenEnergyType();

    public static void register() {
        Registry.register(CottonEnergy.ENERGY_REGISTRY, new Identifier(Constants.MOD_ID, Constants.Energy.GALACTICRAFT_JOULES), GALACTICRAFT_JOULES);
        Registry.register(CottonEnergy.ENERGY_REGISTRY, new Identifier(Constants.MOD_ID, Constants.Energy.GALACTICRAFT_OXYGEN), GALACTICRAFT_OXYGEN);
    }

    public static boolean isEnergyItem(ItemStack itemStack) {
        return itemStack.getItem() instanceof EnergyHolderItem;
    }

    public static int getBatteryEnergy(ItemStack battery) {
        if (!isEnergyItem(battery)) {
            throw new IllegalArgumentException("Provided argument is not an energy item!");
        }

        return battery.hasTag() && battery.getTag().containsKey("Energy") ? battery.getTag().getInt("Energy") : Integer.MAX_VALUE;
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

    public static boolean isOxygenItem(ItemStack itemStack) {
        if (!itemStack.hasTag()) {
            return false;
        }

        CompoundTag tag = itemStack.getTag();
        return tag.containsKey(OxygenTankItem.OXYGEN_NBT_KEY) && tag.containsKey(OxygenTankItem.MAX_OXYGEN_NBT_KEY);
    }
}
