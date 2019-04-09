package io.github.teamgalacticraft.galacticraft.blocks.machines.basicsolarpanel;

import alexiil.mc.lib.attributes.DefaultedAttribute;
import alexiil.mc.lib.attributes.SearchOptions;
import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.impl.SimpleFixedItemInv;
import com.google.common.collect.Lists;
import io.github.cottonmc.energy.api.EnergyAttribute;
import io.github.cottonmc.energy.impl.SimpleEnergyAttribute;
import io.github.prospector.silk.util.ActionType;
import io.github.teamgalacticraft.galacticraft.api.configurable.SideOptions;
import io.github.teamgalacticraft.galacticraft.energy.GalacticraftEnergy;
import io.github.teamgalacticraft.galacticraft.energy.GalacticraftEnergyType;
import io.github.teamgalacticraft.galacticraft.entity.GalacticraftBlockEntities;
import io.github.teamgalacticraft.galacticraft.items.GalacticraftItems;
import io.github.teamgalacticraft.galacticraft.util.BlockOptionUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class BasicSolarPanelBlockEntity extends BlockEntity implements Tickable {
    private final List<Runnable> listeners = Lists.newArrayList();
    SimpleFixedItemInv inventory = new SimpleFixedItemInv(1);
    SimpleEnergyAttribute energy = new SimpleEnergyAttribute(250000, GalacticraftEnergy.GALACTICRAFT_JOULES);

    boolean isBurning = false;
    public BasicSolarPanelStatus status = BasicSolarPanelStatus.NIGHT;
    private float heat = 0.0f;
    public int fuelTimeMax;
    public int fuelTimeCurrent;
    public int fuelEnergyPerTick;

    public SideOptions[] sideOptions = {SideOptions.BLANK, SideOptions.POWER_OUTPUT};
    public Map<Direction, SideOptions> selectedOptions = BlockOptionUtils.getDefaultSideOptions();

    public BasicSolarPanelBlockEntity() {
        super(GalacticraftBlockEntities.BASIC_SOLAR_PANEL_BLOCK_ENTITY_TYPE);
        //automatically mark dirty whenever the energy attribute is changed
        this.energy.listen(this::markDirty);
        selectedOptions.put(Direction.SOUTH, SideOptions.POWER_OUTPUT);
    }

    @Override
    public void tick() {
        long time = world.getTimeOfDay();
        while (true) {
            if (time <= -1) {
                time += 24000;
                break;
            }
            time -= 24000;
        }

        System.out.println(time);

        if ((time > 250 && time < 12000)) {
                if (energy.getCurrentEnergy() <= energy.getMaxEnergy()) {
                    status = BasicSolarPanelStatus.COLLECTING;
                } else {
                    energy.setCurrentEnergy(energy.getMaxEnergy());
                    status = BasicSolarPanelStatus.FULL;
                }
            } else if (world.isRaining() || world.isThundering()) {
            status = BasicSolarPanelStatus.RAIN;
        }

        if (time <= 250 || time >= 12000) {
            status = BasicSolarPanelStatus.NIGHT;
        }

        if (status == BasicSolarPanelStatus.COLLECTING) {
            if (time > 6000) {
                energy.insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, (int)((6000D-((double)time-6000D))/133.3333333333D), ActionType.PERFORM);
            } else {
                energy.insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, (int)(((double)time/133.3333333333D)), ActionType.PERFORM);
            }
        }

        if (inventory.getInvStack(0) != ItemStack.EMPTY && inventory.getInvStack(0).getItem() == GalacticraftItems.BATTERY) {
            if (energy.getCurrentEnergy() >= 150 && inventory.getInvStack(0).getDamage() != 0) {
                energy.setCurrentEnergy(energy.getCurrentEnergy() - 150);
                inventory.getInvStack(0).setDamage(inventory.getInvStack(0).getDurability() - 1);
            }
        }

        for (Direction direction : Direction.values()) {
            if (selectedOptions.get(direction).equals(SideOptions.POWER_OUTPUT)) {
                EnergyAttribute energyAttribute = getNeighborAttribute(EnergyAttribute.ENERGY_ATTRIBUTE, direction);
                if (energyAttribute.canInsertEnergy()) {
                    this.energy.setCurrentEnergy(energyAttribute.insertEnergy(new GalacticraftEnergyType(), 1, ActionType.PERFORM));
                }
            }
        }

    }

    public <T> T getNeighborAttribute(DefaultedAttribute<T> attr, Direction dir) {
        return attr.getFirst(getWorld(), getPos().offset(dir), SearchOptions.inDirection(dir));
    }

    public EnergyAttribute getEnergy() {
        return this.energy;
    }

    public FixedItemInv getItems() {
        return this.inventory;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.put("Inventory", inventory.toTag());
        tag.put("Energy", energy.toTag());
        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        inventory.fromTag(tag.getCompound("Inventory"));
        energy.fromTag(tag.getTag("Energy"));
    }
}
