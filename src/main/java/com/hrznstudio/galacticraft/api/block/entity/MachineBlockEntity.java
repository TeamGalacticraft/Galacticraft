/*
 * Copyright (c) 2019-2021 HRZN LTD
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.hrznstudio.galacticraft.api.block.entity;

import alexiil.mc.lib.attributes.SearchOptions;
import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FixedFluidInv;
import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.FluidExtractable;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.filter.ConstantFluidFilter;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.impl.SimpleFixedFluidInv;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.compat.InventoryFixedWrapper;
import alexiil.mc.lib.attributes.item.filter.ConstantItemFilter;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import alexiil.mc.lib.attributes.item.impl.FullFixedItemInv;
import alexiil.mc.lib.attributes.misc.Reference;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.accessor.WorldRendererAccessor;
import com.hrznstudio.galacticraft.api.block.ConfiguredSideOption;
import com.hrznstudio.galacticraft.api.block.MachineBlock;
import com.hrznstudio.galacticraft.api.block.util.BlockFace;
import com.hrznstudio.galacticraft.api.machine.*;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.energy.api.Capacitor;
import com.hrznstudio.galacticraft.energy.api.EnergyExtractable;
import com.hrznstudio.galacticraft.energy.api.EnergyInsertable;
import com.hrznstudio.galacticraft.energy.impl.DefaultEnergyType;
import com.hrznstudio.galacticraft.energy.impl.RejectingEnergyInsertable;
import com.hrznstudio.galacticraft.energy.impl.SimpleCapacitor;
import com.hrznstudio.galacticraft.util.EnergyUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public abstract class MachineBlockEntity extends BlockEntity implements BlockEntityClientSerializable, Tickable, ExtendedScreenHandlerFactory {
    private final MachineConfiguration configuration = new MachineConfiguration(new SideConfiguration(this), new SecurityInfo());

    private boolean noDrop = false;
    private boolean loaded = false;

    private final @NotNull SimpleCapacitor capacitor = new SimpleCapacitor(DefaultEnergyType.INSTANCE, this.getEnergyCapacity());

    private final @NotNull FullFixedItemInv inventory = new FullFixedItemInv(this.getInventorySize());

    private final @NotNull SimpleFixedFluidInv tank = new SimpleFixedFluidInv(this.getFluidTankSize(), this.getFluidTankCapacity()) {
        @Override
        public FluidFilter getFilterForTank(int tank) {
            return MachineBlockEntity.this.getFilterForTank(tank).or(FluidKey::isEmpty);
        }
    };

    private final InventoryFixedWrapper wrappedInventory = new InventoryFixedWrapper(this.getInventory()) {
        @Override
        public boolean canPlayerUse(PlayerEntity player) {
            return MachineBlockEntity.this.getSecurity().hasAccess(player);
        }
    };

    public MachineBlockEntity(BlockEntityType<? extends MachineBlockEntity> blockEntityType) {
        super(blockEntityType);
    }

    /**
     * Returns whether this machine may have energy extracted from it.
     * @return whether this machine may have energy extracted from it.
     */
    public boolean canExtractEnergy() {
        return false;
    }

    /**
     * Returns whether this machine may have energy inserted into it.
     * @return whether this machine may have energy inserted into it.
     */
    public boolean canInsertEnergy() {
        return false;
    }

    /**
     * The amount of energy that the machine consumes in a tick.
     * @return The amount of energy that the machine consumes in a tick.
     */
    protected int getBaseEnergyConsumption() {
        return 0;
    }

    /**
     * The amount of energy that the machine consumes in a tick, in the current context.
     * @return The amount of energy that the machine consumes in a tick, in the current context.
     */
    public int getEnergyConsumption() {
        if (this.getStatus().getType().isActive()) return getBaseEnergyConsumption();
        return 0;
    }

    /**
     * The amount of energy that the machine generates in a tick.
     * @return The amount of energy that the machine generates in a tick.
     */
    public int getBaseEnergyGenerated() {
        return 0;
    }

    /**
     * The amount of energy that the machine generates in a tick, in the current context.
     * @return The amount of energy that the machine generates in a tick, in the current context.
     */
    public int getEnergyGenerated() {
        if (this.getStatus().getType().isActive()) return getBaseEnergyGenerated();
        return 0;
    }

    /**
     * Returns whether a hopper may extract items from the given slot.
     * @param slot The slot to test
     * @return whether a hopper may extract items from the given slot.
     */
    public boolean canHopperExtract(int slot) {
        return false;
    }

    public boolean canHopperInsert(int slot) {
        return false;
    }

    public boolean canPipeExtractFluid(int tank) {
        return false;
    }

    public boolean canPipeInsertFluid(int tank) {
        return false;
    }

    public FluidFilter getFilterForTank(int tank) {
        return ConstantFluidFilter.NOTHING;
    }

    public int getInventorySize() {
        return 0;
    }

    public int getFluidTankSize() {
        return 0;
    }

    public FluidAmount getFluidTankCapacity() {
        return FluidAmount.ZERO;
    }

    public void setRedstone(@NotNull RedstoneState redstone) {
        this.configuration.setRedstone(redstone);
    }

    public final @NotNull MachineStatus getStatus() {
        return this.configuration.getStatus();
    }

    public final void setStatus(MachineStatus status) {
        this.configuration.setStatus(status);
    }

    public final void setStatusById(int index) {
        this.setStatus(this.getStatusById(index));
    }

    protected abstract MachineStatus getStatusById(int index);

    /**
     * The max energy that this machine can hold. Override for machines that should hold more.
     *
     * @return Energy capacity of this machine.
     */
    public int getEnergyCapacity() {
        return Galacticraft.CONFIG_MANAGER.get().machineEnergyStorageSize();
    }

    /**
     * @return The {@link ItemFilter} for the given slot of {@link #getInventory()}.
     */
    public ItemFilter getFilterForSlot(int slot) {
        return ConstantItemFilter.ANYTHING;
    }

    /**
     * @return The maximum amount of energy that can be transferred to or from a battery in this machine per call to
     * {@link #attemptChargeFromStack(int)} or {@link #attemptDrainPowerToStack(int)}
     */
    protected int getBatteryTransferRate() {
        return 50;
    }

    public final @NotNull SimpleCapacitor getCapacitor() {
        return this.capacitor;
    }

    public final @NotNull FullFixedItemInv getInventory() {
        return this.inventory;
    }

    public final @NotNull SimpleFixedFluidInv getFluidTank() {
        return this.tank;
    }

    public final @Nullable EnergyExtractable getEnergyExtractable(@NotNull BlockState state, @Nullable Direction direction) {
        if (direction != null) {
            ConfiguredSideOption cso = this.getSideConfiguration().get(BlockFace.toFace(state.get(Properties.HORIZONTAL_FACING), direction));
            if (cso.getAutomationType().isEnergy()) {
                if (cso.getAutomationType().isOutput()) {
                    return this.getCapacitor().getExtractable();
                }
            }
            return null;
        }
        return this.getCapacitor().getExtractable();
    }

    public final @Nullable EnergyInsertable getEnergyInsertable(@NotNull BlockState state, @Nullable Direction direction) {
        if (direction != null) {
            ConfiguredSideOption cso = this.getSideConfiguration().get(BlockFace.toFace(state.get(Properties.HORIZONTAL_FACING), direction));
            if (cso.getAutomationType().isEnergy()) {
                if (cso.getAutomationType().isInput()) {
                    return this.getCapacitor().getInsertable();
                }
            }
            return null;
        }
        return this.getCapacitor().getInsertable();
    }

    public final @Nullable Capacitor getCapacitor(@NotNull BlockState state, @Nullable Direction direction) {
        if (direction != null) {
            ConfiguredSideOption cso = this.getSideConfiguration().get(BlockFace.toFace(state.get(Properties.HORIZONTAL_FACING), direction));
            if (cso.getAutomationType().isEnergy()) {
                return this.getCapacitor();
            }
            return null;
        }
        return this.getCapacitor();
    }

    public final @Nullable FixedItemInv getInventory(@NotNull BlockState state, @Nullable Direction direction) { //DIRECTION IS POINTING AWAY FROM MACHINE TO THE SEARCHER
        if (direction != null) {
            ConfiguredSideOption cso = this.getSideConfiguration().get(BlockFace.toFace(state.get(Properties.HORIZONTAL_FACING), direction));
            if (cso.getAutomationType().isItem()) {
                if (cso.isWildcard()) {
                    IntArrayList list = new IntArrayList();

                    if (cso.getAutomationType().isInput()) {
                        for (int i = 0; i < inventory.getSlotCount(); i++) {
                            if (this.canHopperInsert(i)) {
                                list.add(i);
                            }
                        }
                    } else {
                        for (int i = 0; i < inventory.getSlotCount(); i++) {
                            if (this.canHopperExtract(i)) {
                                list.add(i);
                            }
                        }
                    }
                    return this.getInventory().getMappedInv(list.toIntArray());
                } else {
                    return this.getInventory().getMappedInv(cso.getValue());
                }
            }
            return null;
        }
        return this.getInventory();
    }

    public final @Nullable FixedFluidInv getFluidTank(@NotNull BlockState state, @Nullable Direction direction) {
        if (direction != null) {
            ConfiguredSideOption cso = this.getSideConfiguration().get(BlockFace.toFace(state.get(Properties.HORIZONTAL_FACING), direction));
            if (cso.getAutomationType().isFluid()) {
                cso.getMatching(this)
                if (cso.isWildcard()) {
                    IntArrayList list = new IntArrayList();
                    if (cso.getAutomationType().isInput()) {
                        for (int i = 0; i < tank.getTankCount(); i++) {
                            if (this.canPipeInsertFluid(i)) {
                                list.add(i);
                            }
                        }
                    } else {
                        for (int i = 0; i < tank.getTankCount(); i++) {
                            if (this.canPipeExtractFluid(i)) {
                                list.add(i);
                            }
                        }
                    }
                    return this.getFluidTank().getMappedInv(list.toIntArray());
                } else {
                    return this.getFluidTank().getMappedInv(cso.getValue());
                }
            }
            return null;
        }
        return this.getFluidTank();
    }

    public final @Nullable FluidInsertable getFluidInsertable(@NotNull BlockState state, @Nullable Direction direction) {
        if (direction != null) {
            ConfiguredSideOption cso = this.getSideConfiguration().get(BlockFace.toFace(state.get(Properties.HORIZONTAL_FACING), direction));
            if (cso.getAutomationType().isFluid()) {
                if (cso.getAutomationType().isInput()) {
                    if (cso.isWildcard()) {
                        IntArrayList list = new IntArrayList();
                        for (int i = 0; i < this.getFluidTankSize(); i++) {
                            if (this.canPipeInsertFluid(i)) {
                                list.add(i);
                            }
                        }

                        return this.getFluidTank().getMappedInv(list.toIntArray()).getInsertable();
                    } else {
                        return this.getFluidTank().getMappedInv(cso.getValue()).getInsertable();
                    }
                }
            }
            return null;
        }
        return this.getFluidTank().getInsertable();
    }

    public final @Nullable FluidExtractable getFluidExtractable(@NotNull BlockState state, @Nullable Direction direction) {
        if (direction != null) {
            ConfiguredSideOption cso = this.getSideConfiguration().get(BlockFace.toFace(state.get(Properties.HORIZONTAL_FACING), direction));
            if (cso.getAutomationType().isFluid()) {
                if (cso.getAutomationType().isOutput()) {
                    if (cso.isWildcard()) {
                        IntArrayList list = new IntArrayList();
                        for (int i = 0; i < tank.getTankCount(); i++) {
                            if (this.canPipeExtractFluid(i)) {
                                list.add(i);
                            }
                        }
                        return this.getFluidTank().getMappedInv(list.toIntArray()).getExtractable();
                    } else {
                        return this.getFluidTank().getMappedInv(cso.getValue()).getExtractable();
                    }
                }
            }
            return null;
        }
        return this.getFluidTank().getExtractable();
    }

    public final @NotNull SecurityInfo getSecurity() {
        return this.configuration.getSecurity();
    }

    public final @NotNull RedstoneState getRedstone() {
        return this.configuration.getRedstone();
    }

    public final @NotNull SideConfiguration getSideConfiguration() {
        return this.configuration.getConfiguration();
    }

    public final boolean canUse(PlayerEntity player) {
        return this.getSecurity().hasAccess(player);
    }

    protected ItemStack decrement(int slot, int amount) {
        return this.getInventory().extractStack(slot, ConstantItemFilter.ANYTHING, ItemStack.EMPTY, amount, Simulation.ACTION);
    }

    /**
     * Whether the current machine is enabled
     *
     * @return The state of the machine
     */
    public boolean disabled() {
        switch (this.getRedstone()) {
            case LOW:
                return this.getWorld().isReceivingRedstonePower(pos);
            case HIGH:
                return !this.getWorld().isReceivingRedstonePower(pos);
            default:
                return false;
        }
    }

    @Override
    public final void tick() {
        assert this.world != null;
        if (!this.world.isClient) {
            this.updateComponents();
            if (disabled()) {
                idleEnergyDecrement(true);
                return;
            }
            this.setStatus(this.updateStatus());
            this.tickWork();
            if (this.getStatus().getType().isActive()) {
                if (getBaseEnergyConsumption() > 0)
                    this.getCapacitor().extract(getEnergyConsumption());
                if (getBaseEnergyGenerated() > 0)
                    this.getCapacitor().insert(getEnergyGenerated());
            }

        }
    }

    /**
     * Returns the updated machine status
     * Should not have any side effects
     * @return The updated status
     */
    @Contract(pure = true)
    public abstract @NotNull MachineStatus updateStatus();

    /**
     * Update the work/progress and/or create the outputted items in this method
     */
    public abstract void tickWork();

    public void updateComponents() {
        this.trySpreadEnergy();
    }

    public boolean hasEnergyToWork() {
        return this.getCapacitor().getEnergy() >= this.getBaseEnergyConsumption();
    }

    public boolean isTankFull(int tank) {
        return this.getFluidTank().getInvFluid(tank).getAmount_F().isGreaterThanOrEqual(this.getFluidTank().getMaxAmount_F(tank));
    }

    @NotNull
    public <C extends Inventory, T extends Recipe<C>> Optional<T> getRecipe(RecipeType<T> type, C inventory) {
        if (this.world == null) return Optional.empty();
        return this.world.getRecipeManager().getFirstMatch(type, inventory, this.world);
    }

    public boolean canInsert(int slot, Recipe<?> recipe) {
        return this.canInsert(slot, recipe, 1);
    }

    public boolean canInsert(int slot, Recipe<?> recipe, int multiplier) {
        ItemStack stack = recipe.getOutput().copy();
        stack.setCount(stack.getCount() * multiplier);
        return this.canInsert(slot, stack);
    }

    public boolean canInsert(int[] slots, Recipe<?> recipe, int multiplier) {
        ItemStack stack = recipe.getOutput().copy();
        stack.setCount(stack.getCount() * multiplier);
        return this.canInsert(slots, stack);
    }

    public boolean canInsert(int[] slots, ItemStack stack) {
        stack = stack.copy();
        for (int slot : slots) {
            stack = this.getInventory().insertStack(slot, stack, Simulation.SIMULATE);
            if (stack.isEmpty()) return true;
        }
        return stack.isEmpty();
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        if (this.getEnergyCapacity() > 0) this.getCapacitor().toTag(tag);
        if (this.getInventorySize() > 0) this.getInventory().toTag(tag);
        if (this.getFluidTankSize() > 0) this.getFluidTank().toTag(tag);
        this.getSecurity().toTag(tag);
        this.getSideConfiguration().toTag(tag);
        this.getRedstone().toTag(tag);
        tag.putBoolean("NoDrop", this.noDrop);
        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        if (this.getEnergyCapacity() > 0) this.getCapacitor().fromTag(tag);
        if (this.getInventorySize() > 0) this.getInventory().fromTag(tag);
        if (this.getFluidTankSize() > 0) this.getFluidTank().fromTag(tag);
        this.getSecurity().fromTag(tag);
        this.getSideConfiguration().fromTag(tag);
        this.setRedstone(RedstoneState.fromTag(tag));
        this.noDrop = tag.getBoolean("NoDrop");
        if (loaded && !world.isClient) this.sync();
        if (!loaded) loaded = true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void fromClientTag(CompoundTag tag) {
        this.getSideConfiguration().fromTag(tag);
        ((WorldRendererAccessor) MinecraftClient.getInstance().worldRenderer).addChunkToRebuild(pos);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        this.getSideConfiguration().toTag(tag);
        return tag;
    }

    public boolean canInsert(int slot, ItemStack stack) {
        return this.getInventory().insertStack(slot, stack, Simulation.SIMULATE).isEmpty();
    }

    public void insert(int slot, ItemStack stack) {
        if (this.canInsert(slot, stack)) {
            this.getInventory().insertStack(slot, stack, Simulation.ACTION);
        } else {
            throw new RuntimeException();
        }
    }

    public void trySpreadEnergy() {
        if (this.canExtractEnergy()) {
            for (BlockFace face : Constants.Misc.BLOCK_FACES) {
                ConfiguredSideOption option = this.getSideConfiguration().get(face);
                if (option.getAutomationType().isEnergy() && option.getAutomationType().isOutput()) {
                    Direction dir = face.toDirection(this.world.getBlockState(pos).get(Properties.HORIZONTAL_FACING));
                    EnergyInsertable insertable = GalacticraftEnergy.INSERTABLE.getFirst(world, pos.offset(dir), SearchOptions.inDirection(dir.getOpposite()));
                    if (insertable != RejectingEnergyInsertable.NULL) {
                        this.getCapacitor().insert(insertable.tryInsert(DefaultEnergyType.INSTANCE, this.getCapacitor().extract(2048), Simulation.ACTION));
                    }
                }
            }
        }
    }

    public void trySpreadFluids(int tank) {
        if (this.canPipeExtractFluid(tank) && !this.getFluidTank().getInvFluid(tank).isEmpty()) {
            for (BlockFace face : Constants.Misc.BLOCK_FACES) {
                ConfiguredSideOption option = this.getSideConfiguration().get(face);
                if (option.getAutomationType().isFluid() && option.getAutomationType().isOutput()) {
                    Direction dir = face.toDirection(this.world.getBlockState(pos).get(Properties.HORIZONTAL_FACING));
                    FluidInsertable insertable = FluidAttributes.INSERTABLE.getFromNeighbour(this, dir);
                    this.getFluidTank().insertFluid(tank, insertable.attemptInsertion(this.getFluidTank().extractFluid(tank, ConstantFluidFilter.ANYTHING, null, FluidAmount.ONE, Simulation.ACTION), Simulation.ACTION), Simulation.ACTION);
                }
            }
        }
    }

    public void idleEnergyDecrement(boolean off) {
        if (this.world.random.nextInt(off ? 40 : 20) == 1) {
            if (this.getBaseEnergyConsumption() > 0) {
                this.getCapacitor().extract(this.getBaseEnergyConsumption() / 20);
            }
        }
    }

    /**
     * Tries to charge this machine from the item in the given slot in this {@link #getInventory}.
     */
    protected void attemptChargeFromStack(int slot) {
        if (this.getCapacitor().getEnergy() >= this.getCapacitor().getMaxCapacity()) return;

        Reference<ItemStack> stack = this.getInventory().getSlot(slot);
        int neededEnergy = Math.min(this.getBatteryTransferRate(), this.getCapacitor().getMaxCapacity() - this.getCapacitor().getEnergy());
        if (EnergyUtils.isEnergyExtractable(stack)) {
            this.getCapacitor().insert(EnergyUtils.extractEnergy(stack, neededEnergy));
        }
    }

    /**
     * Tries to drain some of this machine's power into the item in the given slot in this {@link #getInventory}.
     *
     * @param slot The slot id of the item
     */
    protected void attemptDrainPowerToStack(int slot) {
        int available = Math.min(this.getBatteryTransferRate(), this.getCapacitor().getEnergy());
        if (available <= 0) {
            return;
        }
        Reference<ItemStack> stack = this.getInventory().getSlot(slot);
        if (EnergyUtils.isEnergyInsertable(stack)) {
            this.getCapacitor().insert(EnergyUtils.insert(stack, this.getCapacitor().extract(available)));
        }
    }

    /**
     * Returns a list of non-configurable machine faces.
     * @return a list of non-configurable machine faces.
     */
    public List<BlockFace> getLockedFaces() {
        return Collections.emptyList();
    }

    public Inventory getWrappedInventory() {
        return this.wrappedInventory;
    }

    @Override
    public void sync() {
        BlockEntityClientSerializable.super.sync();
        BlockState state = this.world.getBlockState(this.pos);
        this.world.setBlockState(this.pos, state.with(MachineBlock.ARBITRARY_BOOLEAN_PROPERTY, !state.get(MachineBlock.ARBITRARY_BOOLEAN_PROPERTY)), 11);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(this.getPos());
    }

    @Override
    public Text getDisplayName() {
        return LiteralText.EMPTY;
    }

}
