package io.github.teamgalacticraft.galacticraft.blocks.machines.basicsolarpanel;

import alexiil.mc.lib.attributes.DefaultedAttribute;
import alexiil.mc.lib.attributes.SearchOptions;
import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.impl.SimpleFixedItemInv;
import com.google.common.collect.Lists;
import io.github.cottonmc.energy.api.EnergyAttribute;
import io.github.cottonmc.energy.impl.SimpleEnergyAttribute;
import io.github.prospector.silk.util.ActionType;
import io.github.teamgalacticraft.galacticraft.api.configurable.SideOptions;
import io.github.teamgalacticraft.galacticraft.energy.GalacticraftEnergy;
import io.github.teamgalacticraft.galacticraft.energy.GalacticraftEnergyType;
import io.github.teamgalacticraft.galacticraft.entity.GalacticraftBlockEntities;
import io.github.teamgalacticraft.galacticraft.util.BlockOptionUtils;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
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
public class BasicSolarPanelBlockEntity extends BlockEntity implements Tickable, BlockEntityClientSerializable {
    private final List<Runnable> listeners = Lists.newArrayList();
    private SimpleFixedItemInv inventory = new SimpleFixedItemInv(1);
    SimpleEnergyAttribute energy = new SimpleEnergyAttribute(15000, GalacticraftEnergy.GALACTICRAFT_JOULES);

    public BasicSolarPanelStatus status = BasicSolarPanelStatus.NIGHT;
    public SideOptions[] sideOptions = {SideOptions.BLANK, SideOptions.POWER_OUTPUT};
    public Map<Direction, SideOptions> selectedOptions = BlockOptionUtils.getDefaultSideOptions();

    public BasicSolarPanelBlockEntity() {
        super(GalacticraftBlockEntities.BASIC_SOLAR_PANEL_TYPE);
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

        if ((time > 250 && time < 12000)) {
            if (energy.getCurrentEnergy() <= energy.getMaxEnergy()) {
                status = BasicSolarPanelStatus.COLLECTING;
            } else {
                energy.setCurrentEnergy(energy.getMaxEnergy());
                status = BasicSolarPanelStatus.FULL;
            }
        } else if (world.isRaining() || world.isThundering()) {
            status = BasicSolarPanelStatus.RAINING;
        }

        if (time <= 250 || time >= 12000) {
            status = BasicSolarPanelStatus.NIGHT;
        }

        if (status == BasicSolarPanelStatus.COLLECTING) {
            if (time > 6000) {
                energy.insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, (int) ((6000D - ((double) time - 6000D)) / 133.3333333333D), ActionType.PERFORM);
            } else {
                energy.insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, (int) (((double) time / 133.3333333333D)), ActionType.PERFORM);
            }
        }
        if (world.isClient) return;

        if (inventory.getInvStack(0).getTag() != null && getEnergy().getCurrentEnergy() > 0) {
            if (GalacticraftEnergy.isEnergyItem(inventory.getInvStack(0))) {
                if (inventory.getInvStack(0).getTag().getInt("Energy") < inventory.getInvStack(0).getTag().getInt("MaxEnergy")) {
                    energy.extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 1, ActionType.PERFORM);
                    inventory.getInvStack(0).getTag().putInt("Energy", this.inventory.getInvStack(0).getTag().getInt("Energy") + 1);
                    inventory.getInvStack(0).setDamage(this.inventory.getInvStack(1).getDamage() - 1);
                }
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

    public SimpleFixedItemInv getItems() {
        return this.inventory;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.put("Inventory", inventory.toTag());
        tag.putInt("Energy", energy.getCurrentEnergy());
        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        inventory.fromTag(tag.getCompound("Inventory"));
        this.energy.setCurrentEnergy(tag.getInt("Energy"));
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        this.fromTag(tag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        return this.toTag(tag);
    }
}