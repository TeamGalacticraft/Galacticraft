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
import io.github.teamgalacticraft.galacticraft.api.configurable.SideOptions;
import io.github.teamgalacticraft.galacticraft.energy.GalacticraftEnergy;
import io.github.teamgalacticraft.galacticraft.energy.GalacticraftEnergyType;
import io.github.teamgalacticraft.galacticraft.entity.GalacticraftBlockEntities;
import io.github.teamgalacticraft.galacticraft.util.BlockOptionUtils;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.SidedInventory;
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
    SimpleFixedItemInv inventory = new SimpleFixedItemInv(2);
    SimpleEnergyAttribute energy = new SimpleEnergyAttribute(10000, GalacticraftEnergy.GALACTICRAFT_JOULES);

    public CoalGeneratorStatus status = CoalGeneratorStatus.INACTIVE;
    private float heat = 0.0f;
    public int fuelTimeMax;
    public int fuelTimeCurrent;
    public int fuelEnergyPerTick;

    public SideOptions[] sideOptions = {SideOptions.BLANK, SideOptions.POWER_OUTPUT};
    public Map<Direction, SideOptions> selectedOptions = BlockOptionUtils.getDefaultSideOptions();

    public CoalGeneratorBlockEntity() {
        super(GalacticraftBlockEntities.COAL_GENERATOR_BLOCK_ENTITY_TYPE);
        //automatically mark dirty whenever the energy attribute is changed
        this.energy.listen(this::markDirty);
        selectedOptions.put(Direction.SOUTH, SideOptions.POWER_OUTPUT);
    }

    public static Map<Item, Integer> createFuelTimeMap() {
        Map<Item, Integer> map = Maps.newLinkedHashMap();
        map.put(Blocks.COAL_BLOCK.getItem(), 160000);
        map.put(Items.COAL, 1600);
        map.put(Items.CHARCOAL, 1600);
        return map;
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
            this.fuelEnergyPerTick = createFuelTimeMap().get(this.inventory.getInvStack(0).getItem());

            this.inventory.getInvStack(0).setAmount(this.inventory.getInvStack(0).getAmount() - 1);
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
            if (selectedOptions.get(direction).equals(SideOptions.POWER_OUTPUT)) {
                EnergyAttribute energyAttribute = getNeighborAttribute(EnergyAttribute.ENERGY_ATTRIBUTE, direction);
                if (energyAttribute.canInsertEnergy()) {
                    energy.setCurrentEnergy(energyAttribute.insertEnergy(new GalacticraftEnergyType(), 1, ActionType.PERFORM));
                }
            }
        }

        if (inventory.getInvStack(1).getTag() != null && getEnergy().getCurrentEnergy() > 0) {
            if (GalacticraftEnergy.isEnergyItem(inventory.getInvStack(1))) {
                if (inventory.getInvStack(1).getTag().getInt("Energy") < inventory.getInvStack(1).getTag().getInt("MaxEnergy")) {
                    energy.extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 1, ActionType.PERFORM);
                    inventory.getInvStack(1).getTag().putInt("Energy", this.inventory.getInvStack(1).getTag().getInt("Energy") + 1);
                    inventory.getInvStack(1).setDamage(this.inventory.getInvStack(1).getDamage() - 1);
                }
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
        super.toTag(tag);
        tag.put("Inventory", inventory.toTag());
        tag.putInt("Energy", energy.getCurrentEnergy());
        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        this.inventory.fromTag(tag.getCompound("Inventory"));
        this.energy.setCurrentEnergy(tag.getInt("Energy"));
    }
}
