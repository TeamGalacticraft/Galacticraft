package io.github.teamgalacticraft.galacticraft.blocks.machines.coalgenerator;

import alexiil.mc.lib.attributes.DefaultedAttribute;
import alexiil.mc.lib.attributes.SearchOptions;
import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.impl.SimpleFixedItemInv;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.cottonmc.energy.api.EnergyAttribute;
import io.github.cottonmc.energy.impl.SimpleEnergyAttribute;
import io.github.prospector.silk.util.ActionType;
import io.github.teamgalacticraft.galacticraft.energy.GalacticraftEnergy;
import io.github.teamgalacticraft.galacticraft.energy.GalacticraftEnergyType;
import io.github.teamgalacticraft.galacticraft.entity.GalacticraftBlockEntities;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class CoalGeneratorBlockEntity extends BlockEntity implements Tickable {
    private final List<Runnable> listeners = Lists.newArrayList();
    SimpleFixedItemInv inventory = new SimpleFixedItemInv(1);
    SimpleEnergyAttribute energy = new SimpleEnergyAttribute(250000, GalacticraftEnergy.GALACTICRAFT_JOULES);

    boolean isBurning = false;
    public CoalGeneratorStatus status = CoalGeneratorStatus.INACTIVE;
    private float heat = 0.0f;
    public int fuelTimeMax;
    public int fuelTimeCurrent;
    public int fuelEnergyPerTick;

    public CoalGeneratorBlockEntity() {
        super(GalacticraftBlockEntities.COAL_GENERATOR_BLOCK_BLOCK_ENTITY_TYPE);
        //automatically mark dirty whenever the energy attribute is changed
        energy.listen(this::markDirty);
    }

    public static Map<Item, Integer> createFuelTimeMap() {
        Map<Item, Integer> map_1 = Maps.newLinkedHashMap();
        map_1.put(Blocks.COAL_BLOCK.getItem(), 160000);
        map_1.put(Items.COAL, 1600);
        map_1.put(Items.CHARCOAL, 1600);
        return map_1;
    }

    public static boolean canUseAsFuel(ItemStack itemStack) {
        return createFuelTimeMap().containsKey(itemStack.getItem());
    }

    @Override
    public void tick() {
        int prev = energy.getCurrentEnergy();

        if (canUseAsFuel(inventory.getInvStack(0)) && (status == CoalGeneratorStatus.INACTIVE || status == CoalGeneratorStatus.IDLE) && energy.getCurrentEnergy() < energy.getMaxEnergy()) {
            if (status == CoalGeneratorStatus.INACTIVE) {
                this.status = CoalGeneratorStatus.WARMING;
            } else {
                this.status = CoalGeneratorStatus.ACTIVE;
            }
            this.fuelTimeMax = 200;
            this.fuelTimeCurrent = 0;
            this.fuelEnergyPerTick = createFuelTimeMap().get(inventory.getInvStack(0).getItem());

            inventory.getInvStack(0).setAmount(inventory.getInvStack(0).getAmount() - 1);
        }

        if (this.status == CoalGeneratorStatus.WARMING) {
            if (this.heat >= 10.0f) {
                this.status = CoalGeneratorStatus.ACTIVE;
            }
            this.heat += 0.1f;
        }

        if (status == CoalGeneratorStatus.ACTIVE) {
            fuelTimeCurrent++;
            energy.setCurrentEnergy(Math.min(energy.getMaxEnergy(), energy.getCurrentEnergy() + fuelEnergyPerTick));

            if (fuelTimeCurrent >= fuelTimeMax) {
                this.status = CoalGeneratorStatus.IDLE;
                this.fuelTimeCurrent = 0;
            }
        }

        for (Direction direction : Direction.values()) {
            EnergyAttribute energyAttribute = getNeighborAttribute(EnergyAttribute.ENERGY_ATTRIBUTE, direction);
            if (energyAttribute.canInsertEnergy()) {
                energy.setCurrentEnergy(energyAttribute.insertEnergy(new GalacticraftEnergyType(), 1, ActionType.PERFORM));
            }
        }

    }

    public <T> T getNeighborAttribute(DefaultedAttribute<T> attr, Direction dir) {
        return attr.getFirst(getWorld(), getPos().offset(dir), SearchOptions.inDirection(dir));
    }

    public EnergyAttribute getEnergy() {
        return energy;
    }

    public FixedItemInv getItems() {
        return inventory;
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
