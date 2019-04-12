package io.github.teamgalacticraft.galacticraft.blocks.machines.compressor;

import alexiil.mc.lib.attributes.DefaultedAttribute;
import alexiil.mc.lib.attributes.SearchOptions;
import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.impl.PartialInventoryFixedWrapper;
import alexiil.mc.lib.attributes.item.impl.SimpleFixedItemInv;
import io.github.teamgalacticraft.galacticraft.blocks.machines.circuitfabricator.CircuitFabricatorStatus;
import io.github.teamgalacticraft.galacticraft.entity.GalacticraftBlockEntities;
import io.github.teamgalacticraft.galacticraft.recipes.CompressingRecipe;
import io.github.teamgalacticraft.galacticraft.recipes.GalacticraftRecipes;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

import java.util.Map;
import java.util.Optional;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class CompressorBlockEntity extends BlockEntity implements Tickable, BlockEntityClientSerializable {
    public static final int FUEL_INPUT_SLOT = 9;
    public static final int OUTPUT_SLOT = 10;
    SimpleFixedItemInv inventory = new SimpleFixedItemInv(11);
    private final int maxProgress = 200; // In ticks, 100/20 = 10 seconds
    private int progress;

    public CircuitFabricatorStatus status = CircuitFabricatorStatus.INACTIVE;
    public int fuelTime;
    public int maxFuelTime;

    public CompressorBlockEntity() {
        super(GalacticraftBlockEntities.CIRCUIT_FABRICATOR_TYPE);
    }

    @Override
    public void tick() {
        if (fuelTime > 0) {
            --this.fuelTime;
        }

        if (status == CircuitFabricatorStatus.IDLE) {
            this.progress = 0;
        }

        if (this.fuelTime <= 0) {
            ItemStack fuel = this.inventory.getInvStack(FUEL_INPUT_SLOT);
            if (!fuel.isEmpty()) {
                //TODO this logic needs to move because it will cause the compressor to infinitely consume coal.
                status = CircuitFabricatorStatus.IDLE;
            } else {
                status = CircuitFabricatorStatus.INACTIVE;
            }
        } else {
            status = CircuitFabricatorStatus.IDLE;
        }


        if (status == CircuitFabricatorStatus.INACTIVE) {
            //this.energy.extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 1, ActionType.PERFORM);
            this.progress = 0;
            return;
        }

        if (isValidRecipe(new PartialInventoryFixedWrapper(this.inventory) {
            @Override
            public void markDirty() {
                CompressorBlockEntity.this.markDirty();
            }

            @Override
            public boolean canPlayerUseInv(PlayerEntity var1) {
                return true;
            }
        })) {
            if (canPutStackInResultSlot(getResultFromRecipeStack())) {
                this.status = CircuitFabricatorStatus.PROCESSING;
            }
        } else {
            if (this.status != CircuitFabricatorStatus.INACTIVE) {
                this.status = CircuitFabricatorStatus.IDLE;
            }
        }

        if (status == CircuitFabricatorStatus.PROCESSING) {
            if (this.fuelTime <= 0) {
                ItemStack fuel = this.inventory.getInvStack(FUEL_INPUT_SLOT);
                Map<Item, Integer> fuelMap = AbstractFurnaceBlockEntity.createFuelTimeMap();
                this.maxFuelTime = fuelMap.get(fuel.getItem());
                this.fuelTime = maxFuelTime;
                fuel.subtractAmount(1);
            }
            ItemStack resultStack = getResultFromRecipeStack();
            if (inventory.getInvStack(6).isEmpty() || inventory.getInvStack(6).getItem() == resultStack.getItem()) {
                if (inventory.getInvStack(6).getAmount() < resultStack.getMaxAmount()) {
                    if (this.progress < this.maxProgress) {
                        ++progress;
                        this.fuelTime--;
                    } else {
                        System.out.println("Finished crafting an item.");
                        this.progress = 0;

                        if (!world.isClient) {
                            for (int i = 0; i < 9; i++) {
                                inventory.getInvStack(i).subtractAmount(1);
                            }

                            if (!inventory.getInvStack(OUTPUT_SLOT).isEmpty()) {
                                inventory.getInvStack(OUTPUT_SLOT).addAmount(resultStack.getAmount());
                            } else {
                                inventory.setInvStack(OUTPUT_SLOT, resultStack, Simulation.ACTION);
                            }
                        }

                        markDirty();
                    }
                }
            }
        }
    }

    // This is just for testing purposes
    private ItemStack getResultFromRecipeStack() {
        BasicInventory inv = new BasicInventory(inventory.getInvStack(5));
        // This should under no circumstances not be present. If it is, this method has been called before isValidRecipe and you should feel bad.
        CompressingRecipe recipe = getRecipe(inv).orElseThrow(() -> new IllegalStateException("No recipe present????"));
        return recipe.craft(inv);
    }

    private Optional<CompressingRecipe> getRecipe(Inventory input) {
        return this.world.getRecipeManager().getFirstMatch(GalacticraftRecipes.COMPRESSING_TYPE, input, this.world);
    }

    private boolean canPutStackInResultSlot(ItemStack itemStack) {
        if (inventory.getInvStack(6).isEmpty()) {
            return true;
        } else if (inventory.getInvStack(6).getItem() == itemStack.getItem()) {
            return (inventory.getInvStack(6).getAmount() + itemStack.getAmount()) <= itemStack.getMaxAmount();
        } else {
            return false;
        }
    }

    public FixedItemInv getItems() {
        return this.inventory;
    }

    public int getProgress() {
        return this.progress;
    }

    public int getMaxProgress() {
        return this.maxProgress;
    }

    public <T> T getNeighborAttribute(DefaultedAttribute<T> attr, Direction dir) {
        return attr.getFirst(getWorld(), getPos().offset(dir), SearchOptions.inDirection(dir));
    }

    // This is just for testing
    private boolean isValidRecipe(Inventory input) {
        // TODO check up on this
        return getRecipe(input).isPresent();
//        return !input.isEmpty() && hasMandatoryMaterials();
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);

        tag.put("Inventory", inventory.toTag());
        tag.putInt("FuelTime", this.fuelTime);
        tag.putInt("Progress", this.progress);

        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);

        this.inventory.fromTag(tag.getCompound("Inventory"));
        this.fuelTime = tag.getInt("FuelTime");
        this.progress = tag.getInt("Progress");
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
