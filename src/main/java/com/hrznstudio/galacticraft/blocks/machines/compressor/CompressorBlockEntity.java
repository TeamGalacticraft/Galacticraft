package com.hrznstudio.galacticraft.blocks.machines.compressor;

import alexiil.mc.lib.attributes.DefaultedAttribute;
import alexiil.mc.lib.attributes.SearchOptions;
import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.impl.PartialInventoryFixedWrapper;
import alexiil.mc.lib.attributes.item.impl.SimpleFixedItemInv;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.recipes.GalacticraftRecipes;
import com.hrznstudio.galacticraft.recipes.ShapedCompressingRecipe;
import com.hrznstudio.galacticraft.recipes.ShapelessCompressingRecipe;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

import java.util.Optional;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CompressorBlockEntity extends BlockEntity implements Tickable, BlockEntityClientSerializable {
    public static final int FUEL_INPUT_SLOT = 9;
    public static final int OUTPUT_SLOT = 10;
    private final int maxProgress = 200; // In ticks, 100/20 = 10 seconds
    public CompressorStatus status = CompressorStatus.INACTIVE;
    public int fuelTime;
    public int maxFuelTime;
    protected SimpleFixedItemInv inventory;
    private int progress;

    public CompressorBlockEntity() {
        this(GalacticraftBlockEntities.COMPRESSOR_TYPE);
    }

    public CompressorBlockEntity(BlockEntityType<?> electricCompressorType) {
        super(electricCompressorType);
        this.inventory = new SimpleFixedItemInv(getInventorySize());
    }

    protected int getInventorySize() {
        return 11;
    }

    @Override
    public void tick() {
        PartialInventoryFixedWrapper inv = new PartialInventoryFixedWrapper(inventory.getSubInv(0, 9)) {
            @Override
            public void markDirty() {
                CompressorBlockEntity.this.markDirty();
            }

            @Override
            public boolean canPlayerUseInv(PlayerEntity var1) {
                return true;
            }
        };

        if (shouldUseFuel()) {
            if (this.fuelTime <= 0) {
                ItemStack fuel = inventory.getInvStack(FUEL_INPUT_SLOT);
                if (fuel.isEmpty()) {
                    // Machine out of fuel and no fuel present.
                    status = CompressorStatus.INACTIVE;
                    return;
                } else if (isValidRecipe(inv) && canPutStackInResultSlot(getResultFromRecipeStack(inv))) {
                    this.maxFuelTime = AbstractFurnaceBlockEntity.createFuelTimeMap().get(fuel.getItem());
                    this.fuelTime = maxFuelTime;
                    fuel.decrement(1);
                    status = CompressorStatus.PROCESSING;
                } else {
                    // Can't start processing any new materials anyway, dont waste fuel.
                    status = CompressorStatus.INACTIVE;
                    return;
                }
            }
            this.fuelTime--;
        }

        if (status == CompressorStatus.PROCESSING && !isValidRecipe(inv)) {
            status = CompressorStatus.IDLE;
//            System.out.println("IDLE. RETURNING");
        }

        if (status == CompressorStatus.PROCESSING && isValidRecipe(inv) && canPutStackInResultSlot(getResultFromRecipeStack(inv))) {
            ItemStack resultStack = getResultFromRecipeStack(inv);
            this.progress++;

            if (this.progress % 40 == 0 && this.progress > maxProgress / 2) {
                this.world.playSound(null, this.getPos(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.3F, this.world.random.nextFloat() * 0.1F + 0.9F);
            }

            if (this.progress == maxProgress) {
                this.progress = 0;

                craftItem(resultStack);
            }
        }
    }

    protected boolean shouldUseFuel() {
        return true;
    }

    protected void craftItem(ItemStack craftingResult) {
        for (int i = 0; i < 9; i++) {
            inventory.getInvStack(i).decrement(1);
        }

        ItemStack output = inventory.getInvStack(OUTPUT_SLOT);
        if (output.isEmpty()) {
            inventory.setInvStack(OUTPUT_SLOT, craftingResult, Simulation.ACTION);
        } else {
            inventory.getInvStack(OUTPUT_SLOT).increment(craftingResult.getCount());
        }
    }

    private ItemStack getResultFromRecipeStack(Inventory inv) {
        // Once this method has been called, we have verified that either a shapeless or shaped recipe is present with isValidRecipe. Ignore the warning on getShapedRecipe(inv).get().

        Optional<ShapelessCompressingRecipe> shapelessRecipe = getShapelessRecipe(inv);
        if (shapelessRecipe.isPresent()) {
            return shapelessRecipe.get().craft(inv);
        }
        return getShapedRecipe(inv).orElseThrow(() -> new IllegalStateException("Neither a shapeless recipe or shaped recipe was present when getResultFromRecipeStack was called. This should never happen, as isValidRecipe should have been called first. That would have prevented this.")).craft(inv);
    }

    private Optional<ShapelessCompressingRecipe> getShapelessRecipe(Inventory input) {
        Optional<ShapelessCompressingRecipe> firstMatch = this.world.getRecipeManager().getFirstMatch(GalacticraftRecipes.SHAPELESS_COMPRESSING_TYPE, input, this.world);
        return firstMatch;
    }

    private Optional<ShapedCompressingRecipe> getShapedRecipe(Inventory input) {
        Optional<ShapedCompressingRecipe> firstMatch = this.world.getRecipeManager().getFirstMatch(GalacticraftRecipes.SHAPED_COMPRESSING_TYPE, input, this.world);
        return firstMatch;
    }

    private boolean canPutStackInResultSlot(ItemStack itemStack) {
        if (inventory.getInvStack(OUTPUT_SLOT).isEmpty()) {
            return true;
        } else if (inventory.getInvStack(OUTPUT_SLOT).getItem() == itemStack.getItem()) {
            return (inventory.getInvStack(OUTPUT_SLOT).getCount() + itemStack.getCount()) <= itemStack.getMaxCount();
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

    private boolean isValidRecipe(Inventory input) {
        Optional<ShapelessCompressingRecipe> shapelessRecipe = getShapelessRecipe(input);
        Optional<ShapedCompressingRecipe> shapedRecipe = getShapedRecipe(input);

        return shapelessRecipe.isPresent() || shapedRecipe.isPresent();
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);

        tag.put("Inventory", inventory.toTag());
        tag.putInt("Progress", this.progress);

        if (this.shouldUseFuel()) {
            tag.putInt("FuelTime", this.fuelTime);
        }

        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);

        this.inventory.fromTag(tag.getCompound("Inventory"));
        this.progress = tag.getInt("Progress");

        if (this.shouldUseFuel()) {
            this.fuelTime = tag.getInt("FuelTime");
        }
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
