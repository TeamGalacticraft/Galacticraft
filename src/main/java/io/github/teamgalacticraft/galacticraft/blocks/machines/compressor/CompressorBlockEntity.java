package io.github.teamgalacticraft.galacticraft.blocks.machines.compressor;

import alexiil.mc.lib.attributes.DefaultedAttribute;
import alexiil.mc.lib.attributes.SearchOptions;
import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.impl.PartialInventoryFixedWrapper;
import alexiil.mc.lib.attributes.item.impl.SimpleFixedItemInv;
import io.github.teamgalacticraft.galacticraft.entity.GalacticraftBlockEntities;
import io.github.teamgalacticraft.galacticraft.recipes.GalacticraftRecipes;
import io.github.teamgalacticraft.galacticraft.recipes.ShapedCompressingRecipe;
import io.github.teamgalacticraft.galacticraft.recipes.ShapelessCompressingRecipe;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

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

    public CompressorStatus status = CompressorStatus.INACTIVE;
    public int fuelTime;
    public int maxFuelTime;

    public CompressorBlockEntity() {
        super(GalacticraftBlockEntities.COMPRESSOR_TYPE);
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


        if (this.fuelTime <= 0) {
            ItemStack fuel = inventory.getInvStack(FUEL_INPUT_SLOT);
            if (fuel.isEmpty()) {
                // Machine out of fuel and no fuel present.
                status = CompressorStatus.INACTIVE;
                return;
            } else if (isValidRecipe(inv) && canPutStackInResultSlot(getResultFromRecipeStack(inv))) {
                this.maxFuelTime = AbstractFurnaceBlockEntity.createFuelTimeMap().get(fuel.getItem());
                this.fuelTime = maxFuelTime;
                fuel.subtractAmount(1);
                status = CompressorStatus.PROCESSING;
            } else {
                // Can't start processing any new materials anyway, dont waste fuel.
                status = CompressorStatus.INACTIVE;
                return;
            }
        }
        this.fuelTime--;

        if (status == CompressorStatus.PROCESSING && !isValidRecipe(inv)) {
            status = CompressorStatus.IDLE;
            return;
        }

        if (status == CompressorStatus.PROCESSING && !isValidRecipe(inv) && canPutStackInResultSlot(getResultFromRecipeStack(inv))) {
            ItemStack resultStack = getResultFromRecipeStack(inv);
            this.progress++;
            System.out.println("Progress: " + this.progress);

            if (this.progress == maxProgress) {
                this.progress = 0;

                for (int i = 0; i < 9; i++) {
                    inventory.getInvStack(i).subtractAmount(1);
                }

                ItemStack output = inventory.getInvStack(OUTPUT_SLOT);
                if (output.isEmpty()) {
                    inventory.setInvStack(OUTPUT_SLOT, resultStack, Simulation.ACTION);
                } else {
                    inventory.getInvStack(OUTPUT_SLOT).addAmount(resultStack.getAmount());
                }
            }
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
            return (inventory.getInvStack(OUTPUT_SLOT).getAmount() + itemStack.getAmount()) <= itemStack.getMaxAmount();
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
        return getShapelessRecipe(input).isPresent() || getShapedRecipe(input).isPresent();
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
