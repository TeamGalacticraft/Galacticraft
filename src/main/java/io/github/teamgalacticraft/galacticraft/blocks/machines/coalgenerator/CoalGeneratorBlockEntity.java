package io.github.teamgalacticraft.galacticraft.blocks.machines.coalgenerator;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.cottonmc.energy.api.EnergyAttribute;
import io.github.cottonmc.energy.api.EnergyType;
import io.github.cottonmc.energy.api.Observable;
import io.github.prospector.silk.util.ActionType;
import io.github.teamgalacticraft.galacticraft.energy.GalacticraftEnergyType;
import io.github.teamgalacticraft.galacticraft.entity.GalacticraftBlockEntities;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class CoalGeneratorBlockEntity extends BlockEntity implements EnergyAttribute, Observable, Tickable {
    private final List<Runnable> listeners = Lists.newArrayList();
    BasicInventory inventory = new BasicInventory(1);

    private int currentEnergy;
    boolean isBurning = false;
    public CoalGeneratorStatus status = CoalGeneratorStatus.INACTIVE;
    private float heat = 0.0f;
    public int fuelTimeMax;
    public int fuelTimeCurrent;
    public int fuelEnergyPerTick;

    public CoalGeneratorBlockEntity() {
        super(GalacticraftBlockEntities.COAL_GENERATOR_BLOCK_BLOCK_ENTITY_TYPE);
    }

    public static Map<Item, Integer> createFuelTimeMap() {
        Map<Item, Integer> map_1 = Maps.newLinkedHashMap();
        addFuel(map_1,  Items.LAVA_BUCKET, 20000);
        addFuel(map_1,  Blocks.COAL_BLOCK, 16000);
        addFuel(map_1, Items.BLAZE_ROD, 2400);
        addFuel(map_1, Items.COAL, 1600);
        addFuel(map_1, Items.CHARCOAL, 1600);
        addFuel(map_1,  ItemTags.LOGS, 300);
        addFuel(map_1, ItemTags.PLANKS, 300);
        addFuel(map_1, ItemTags.WOODEN_STAIRS, 300);
        addFuel(map_1, ItemTags.WOODEN_SLABS, 150);
        addFuel(map_1, ItemTags.WOODEN_TRAPDOORS, 300);
        addFuel(map_1, ItemTags.WOODEN_PRESSURE_PLATES, 300);
        addFuel(map_1, Blocks.OAK_FENCE, 300);
        addFuel(map_1, Blocks.BIRCH_FENCE, 300);
        addFuel(map_1, Blocks.SPRUCE_FENCE, 300);
        addFuel(map_1, Blocks.JUNGLE_FENCE, 300);
        addFuel(map_1, Blocks.DARK_OAK_FENCE, 300);
        addFuel(map_1, Blocks.ACACIA_FENCE, 300);
        addFuel(map_1, Blocks.OAK_FENCE_GATE, 300);
        addFuel(map_1, Blocks.BIRCH_FENCE_GATE, 300);
        addFuel(map_1, Blocks.SPRUCE_FENCE_GATE, 300);
        addFuel(map_1, Blocks.JUNGLE_FENCE_GATE, 300);
        addFuel(map_1, Blocks.DARK_OAK_FENCE_GATE, 300);
        addFuel(map_1, Blocks.ACACIA_FENCE_GATE, 300);
        addFuel(map_1, Blocks.NOTE_BLOCK, 300);
        addFuel(map_1, Blocks.BOOKSHELF, 300);
        addFuel(map_1, Blocks.LECTERN, 300);
        addFuel(map_1, Blocks.JUKEBOX, 300);
        addFuel(map_1, Blocks.CHEST, 300);
        addFuel(map_1, Blocks.TRAPPED_CHEST, 300);
        addFuel(map_1, Blocks.CRAFTING_TABLE, 300);
        addFuel(map_1, Blocks.DAYLIGHT_DETECTOR, 300);
        addFuel(map_1, ItemTags.BANNERS, 300);
        addFuel(map_1, Items.BOW, 300);
        addFuel(map_1, Items.FISHING_ROD, 300);
        addFuel(map_1, Blocks.LADDER, 300);
        addFuel(map_1, ItemTags.SIGNS, 200);
        addFuel(map_1, Items.WOODEN_SHOVEL, 200);
        addFuel(map_1, Items.WOODEN_SWORD, 200);
        addFuel(map_1, Items.WOODEN_HOE, 200);
        addFuel(map_1, Items.WOODEN_AXE, 200);
        addFuel(map_1, Items.WOODEN_PICKAXE, 200);
        addFuel(map_1, ItemTags.WOODEN_DOORS, 200);
        addFuel(map_1, ItemTags.BOATS, 200);
        addFuel(map_1, ItemTags.WOOL, 100);
        addFuel(map_1, ItemTags.WOODEN_BUTTONS, 100);
        addFuel(map_1, Items.STICK, 100);
        addFuel(map_1, ItemTags.SAPLINGS, 100);
        addFuel(map_1, Items.BOWL, 100);
        addFuel(map_1, ItemTags.CARPETS, 67);
        addFuel(map_1, Blocks.DRIED_KELP_BLOCK, 4001);
        addFuel(map_1, Items.CROSSBOW, 300);
        addFuel(map_1, Blocks.BAMBOO, 50);
        addFuel(map_1, Blocks.DEAD_BUSH, 100);
        addFuel(map_1, Blocks.SCAFFOLDING, 50);
        addFuel(map_1, Blocks.LOOM, 300);
        addFuel(map_1, Blocks.BARREL, 300);
        addFuel(map_1, Blocks.CARTOGRAPHY_TABLE, 300);
        addFuel(map_1, Blocks.FLETCHING_TABLE, 300);
        addFuel(map_1, Blocks.SMITHING_TABLE, 300);
        addFuel(map_1, Blocks.COMPOSTER, 300);
        addFuel(map_1, Blocks.OBSIDIAN, 1);
        return map_1;
    }

    private static void addFuel(Map<Item, Integer> map_1, net.minecraft.tag.Tag<Item> tag_1, int int_1) {
        Iterator var3 = tag_1.values().iterator();

        while(var3.hasNext()) {
            Item item_1 = (Item)var3.next();
            map_1.put(item_1, int_1);
        }
    }

    private static void addFuel(Map<Item, Integer> map_1, ItemProvider itemProvider_1, int int_1) {
        map_1.put(itemProvider_1.getItem(), int_1);
    }

    public static boolean canUseAsFuel(ItemStack itemStack) {
        return createFuelTimeMap().containsKey(itemStack.getItem());
    }

    @Override
    public void tick() {
        int prev = this.currentEnergy;

        if(canUseAsFuel(inventory.getInvStack(0)) && (status == CoalGeneratorStatus.INACTIVE || status == CoalGeneratorStatus.IDLE) && this.getCurrentEnergy() < this.getMaxEnergy()) {
            if(status == CoalGeneratorStatus.INACTIVE) {
                this.status = CoalGeneratorStatus.WARMING;
            } else {
                this.status = CoalGeneratorStatus.ACTIVE;
            }
            this.fuelTimeMax = 200;
            this.fuelTimeCurrent = 0;
            this.fuelEnergyPerTick = createFuelTimeMap().get(inventory.getInvStack(0).getItem());

            inventory.getInvStack(0).setAmount(inventory.getInvStack(0).getAmount() - 1);
        }

        if(this.status == CoalGeneratorStatus.WARMING) {
            if(this.heat >= 10.0f) {
                this.status = CoalGeneratorStatus.ACTIVE;
            }
            this.heat += 0.1f;
        }

        if(status == CoalGeneratorStatus.ACTIVE) {
            fuelTimeCurrent++;
            currentEnergy = Math.min(getMaxEnergy(), currentEnergy + fuelEnergyPerTick);

            if(fuelTimeCurrent >= fuelTimeMax) {
                this.status = CoalGeneratorStatus.IDLE;
                this.fuelTimeCurrent = 0;
            }
        }

        for(Direction direction : Direction.values()) {
            if(world.getBlockEntity(pos.offset(direction)) instanceof EnergyAttribute) {
                EnergyAttribute energyAttribute = (EnergyAttribute)world.getBlockEntity(pos.offset(direction));
                if(energyAttribute.canInsertEnergy()) {
                    this.currentEnergy = energyAttribute.insertEnergy(new GalacticraftEnergyType(),1, ActionType.PERFORM);
                }
            }
        }

        if(prev != currentEnergy) onChanged();
    }

    @Override
    public int getMaxEnergy() {
        return 250000;
    }

    @Override
    public int getCurrentEnergy() {
        return this.currentEnergy;
    }

    @Override
    public boolean canInsertEnergy() {
        return false;
    }

    @Override
    public int insertEnergy(EnergyType energyType, int i, ActionType actionType) {
        return 0;
    }

    @Override
    public boolean canExtractEnergy() {
        return true;
    }

    @Override
    public int extractEnergy(EnergyType energyType, int amount, ActionType actionType) {

        int extractAmount = (amount <= currentEnergy)? amount : currentEnergy;

        if (actionType == ActionType.PERFORM) {
            currentEnergy -= extractAmount;
            if (extractAmount != 0) onChanged();
        }

        return extractAmount;
    }

    @Override
    public EnergyType getPreferredType() {
        return new GalacticraftEnergyType();
    }

    @Override
    public void listen(Runnable runnable) {
        listeners.add(runnable);
    }

    @Override
    public CompoundTag toTag(CompoundTag compoundTag_1) {
        super.toTag(compoundTag_1);

        compoundTag_1.putInt("CurrentEnergy", this.currentEnergy);

        ItemStack invStack = inventory.getInvStack(0);
        compoundTag_1.put("Fuel", invStack.toTag(new CompoundTag()));

        return compoundTag_1;
    }


    @Override
    public void fromTag(CompoundTag compoundTag_1) {
        super.fromTag(compoundTag_1);

        this.currentEnergy = compoundTag_1.getInt("CurrentEnergy");

        ItemStack invStack = ItemStack.fromTag((CompoundTag)compoundTag_1.getTag("Fuel"));
        this.inventory.setInvStack(0, invStack);
    }

    public void onChanged() {
        listeners.forEach(Runnable::run);
    }
}
