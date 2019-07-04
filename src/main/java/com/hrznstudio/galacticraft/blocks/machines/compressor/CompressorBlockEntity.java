package com.hrznstudio.galacticraft.blocks.machines.compressor;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.compat.InventoryFixedWrapper;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergyType;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.recipes.GalacticraftRecipes;
import com.hrznstudio.galacticraft.recipes.ShapedCompressingRecipe;
import com.hrznstudio.galacticraft.recipes.ShapelessCompressingRecipe;
import io.github.cottonmc.energy.impl.SimpleEnergyAttribute;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Tickable;

import java.util.Optional;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CompressorBlockEntity extends ConfigurableElectricMachineBlockEntity implements Tickable {
    public static final int FUEL_INPUT_SLOT = 9;
    public static final int OUTPUT_SLOT = 10;
    private final int maxProgress = 200; // In ticks, 100/20 = 10 seconds
    public CompressorStatus status = CompressorStatus.INACTIVE;
    public int fuelTime;
    public int maxFuelTime;
    int progress;

    @Override
    public SimpleEnergyAttribute getEnergy() {
        return new SimpleEnergyAttribute(0, GalacticraftEnergy.GALACTICRAFT_JOULES);
    }

    public CompressorBlockEntity() {
        this(GalacticraftBlockEntities.COMPRESSOR_TYPE);
    }

    public CompressorBlockEntity(BlockEntityType<?> electricCompressorType) {
        super(electricCompressorType);
    }

    @Override
    protected int getInvSize() {
        return 11;
    }

    @Override
    protected ItemFilter getFilterForSlot(int slot) {
        if (slot == FUEL_INPUT_SLOT) {
            return AbstractFurnaceBlockEntity::canUseAsFuel;
        } else {
            return super.getFilterForSlot(slot);
        }
    }

    @Override
    public int getMaxEnergy() {
        return 0;
    }

    @Override
    public void tick() {
        if (!this.active()) {
            return;
        }
        InventoryFixedWrapper inv = new InventoryFixedWrapper(getInventory().getSubInv(0, 9)) {
            @Override
            public boolean canPlayerUseInv(PlayerEntity var1) {
                return true;
            }
        };

        if (shouldUseFuel()) {
            if (this.fuelTime <= 0) {
                ItemStack fuel = getInventory().getInvStack(FUEL_INPUT_SLOT);
                if (fuel.isEmpty()) {
                    // Machine out of fuel and no fuel present.
                    status = CompressorStatus.INACTIVE;
                    progress = 0;
                    return;
                } else if (isValidRecipe(inv) && canPutStackInResultSlot(getResultFromRecipeStack(inv))) {
                    this.maxFuelTime = AbstractFurnaceBlockEntity.createFuelTimeMap().get(fuel.getItem());
                    this.fuelTime = maxFuelTime;
                    getInventory().getSlot(FUEL_INPUT_SLOT).extract(1);
                    status = CompressorStatus.PROCESSING;
                } else {
                    // Can't start processing any new materials anyway, dont waste fuel.
                    status = CompressorStatus.INACTIVE;
                    progress = 0;
                    return;
                }
            }
            this.fuelTime--;
        }

        if (status == CompressorStatus.PROCESSING && !isValidRecipe(inv)) {
            status = CompressorStatus.IDLE;
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

    private boolean shouldUseFuel() {
        return true;
    }

    private void craftItem(ItemStack craftingResult) {
        for (int i = 0; i < 9; i++) {
            getInventory().getSlot(i).extract(1);
        }
        getInventory().getSlot(OUTPUT_SLOT).insert(craftingResult);
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
        return this.world.getRecipeManager().getFirstMatch(GalacticraftRecipes.SHAPELESS_COMPRESSING_TYPE, input, this.world);
    }

    private Optional<ShapedCompressingRecipe> getShapedRecipe(Inventory input) {
        return this.world.getRecipeManager().getFirstMatch(GalacticraftRecipes.SHAPED_COMPRESSING_TYPE, input, this.world);
    }

    protected boolean canPutStackInResultSlot(ItemStack itemStack) {
        return getInventory().getSlot(OUTPUT_SLOT).attemptInsertion(itemStack, Simulation.SIMULATE).isEmpty();
    }

    public int getProgress() {
        return this.progress;
    }

    public int getMaxProgress() {
        return this.maxProgress;
    }

    public boolean isValidRecipe(Inventory input) {
        Optional<ShapelessCompressingRecipe> shapelessRecipe = getShapelessRecipe(input);
        Optional<ShapedCompressingRecipe> shapedRecipe = getShapedRecipe(input);

        return shapelessRecipe.isPresent() || shapedRecipe.isPresent();
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);

        tag.putInt("Progress", this.progress);

        if (this.shouldUseFuel()) {
            tag.putInt("FuelTime", this.fuelTime);
        }

        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);

        this.progress = tag.getInt("Progress");

        if (this.shouldUseFuel()) {
            this.fuelTime = tag.getInt("FuelTime");
        }
    }
}
