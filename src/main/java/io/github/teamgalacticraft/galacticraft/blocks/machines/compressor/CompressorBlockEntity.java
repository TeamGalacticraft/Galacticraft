package io.github.teamgalacticraft.galacticraft.blocks.machines.compressor;

import alexiil.mc.lib.attributes.DefaultedAttribute;
import alexiil.mc.lib.attributes.SearchOptions;
import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.impl.SimpleFixedItemInv;
import io.github.teamgalacticraft.galacticraft.api.configurable.SideOptions;
import io.github.teamgalacticraft.galacticraft.blocks.machines.circuitfabricator.CircuitFabricatorStatus;
import io.github.teamgalacticraft.galacticraft.entity.GalacticraftBlockEntities;
import io.github.teamgalacticraft.galacticraft.items.GalacticraftItems;
import io.github.teamgalacticraft.galacticraft.recipes.CompressingRecipe;
import io.github.teamgalacticraft.galacticraft.recipes.FabricationRecipe;
import io.github.teamgalacticraft.galacticraft.recipes.GalacticraftRecipes;
import io.github.teamgalacticraft.galacticraft.util.BlockOptionUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

import java.util.Map;
import java.util.Optional;

public class CompressorBlockEntity extends BlockEntity implements Tickable {
    SimpleFixedItemInv inventory = new SimpleFixedItemInv(12);
    private final int maxProgress = 300;
    private int progress;

    public CircuitFabricatorStatus status = CircuitFabricatorStatus.INACTIVE;

    public Map<Direction, SideOptions> selectedOptions = BlockOptionUtils.getDefaultSideOptions();

    public CompressorBlockEntity() {
        super(GalacticraftBlockEntities.COMPRESSOR_BLOCK_ENTITY_TYPE);
        //automatically mark dirty whenever the energy attribute is changed
    }

    @Override
    public void tick() {
        /*
        if (status == CircuitFabricatorStatus.INACTIVE) {
            this.energy.extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 1, ActionType.PERFORM);
            return;
        }
        */

        DefaultedList<ItemStack> inv = DefaultedList.create(9, ItemStack.EMPTY);
        for (int i = 0; i < 9; i++) {
            inv.set(i, this.inventory.getInvStack(i));
        }
        if (isValidRecipe(inv)) {
            if (canPutStackInResultSlot(getResultFromRecipe())) {
                this.status = CircuitFabricatorStatus.PROCESSING;
            }
        } else {
            this.status = CircuitFabricatorStatus.IDLE;
        }

        if (status == CircuitFabricatorStatus.PROCESSING) {
            ItemStack resultStack = getResultFromRecipe();
            if (inventory.getInvStack(9).isEmpty() || inventory.getInvStack(9).getItem() == resultStack.getItem()) {
                if (inventory.getInvStack(9).getAmount() < resultStack.getMaxAmount()) {
                    if (progress <= maxProgress) {
                        ++progress;
                    } else {
                        System.out.println("Finished crafting an item.");
                        progress = 0;

                        for (int i = 0; i < 9; i++) {
                            ItemStack invStack = inventory.getInvStack(i);
                            if (!invStack.isEmpty()) {
                                invStack.subtractAmount(1);
                            }
                        }

                        if (!inventory.getInvStack(9).isEmpty()) {
                            inventory.getInvStack(9).addAmount(resultStack.getAmount());
                        } else {
                            inventory.setInvStack(9, resultStack, Simulation.ACTION);
                        }
                    }
                }
            }
        }
//        System.out.println("Status: " + status.name());
    }

    private ItemStack getResultFromRecipe() {
        return getRecipe(DefaultedList.create(ItemStack.EMPTY,
                this.inventory.getInvStack(1), this.inventory.getInvStack(2), this.inventory.getInvStack(3),
                this.inventory.getInvStack(4), this.inventory.getInvStack(5), this.inventory.getInvStack(6),
                this.inventory.getInvStack(7), this.inventory.getInvStack(8), this.inventory.getInvStack(9))
        ).orElseThrow(() -> new IllegalStateException("No recipe present????")).getOutput();
    }

    private boolean canPutStackInResultSlot(ItemStack itemStack) {
        if (inventory.getInvStack(9).isEmpty()) {
            return true;
        } else if (inventory.getInvStack(9).getItem() == itemStack.getItem()) {
            return inventory.getInvStack(9).getAmount() < itemStack.getMaxAmount();
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

    private Optional<CompressingRecipe> getRecipe(DefaultedList<ItemStack> input) {
        return this.world.getRecipeManager().getFirstMatch(GalacticraftRecipes.COMPRESSING_TYPE, new BasicInventory(input.toArray(new ItemStack[0])), this.world);
    }


    int i = 0;
    private boolean isValidRecipe(DefaultedList<ItemStack> inv) {
        boolean present = getRecipe(inv).isPresent();
        if (!world.isClient) {
            if (i == 100) {
                for (ItemStack itemStack : inv) {
                    System.out.println(Registry.ITEM.getId(itemStack.getItem()));
                }
                i = 0;
            }
            i++;
        }

//        return !input.isEmpty() && hasMandatoryMaterials();
//        System.out.println((present ? "Valid" : "Invalid") + " recipe.");
        return present;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.put("Inventory", inventory.toTag());
        tag.putInt("Progress", progress);
        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        inventory.fromTag(tag.getCompound("Inventory"));
        this.progress = tag.getInt("Progress");
    }
}
