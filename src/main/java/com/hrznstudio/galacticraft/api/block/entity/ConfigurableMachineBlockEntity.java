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

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.accessor.WorldRendererAccessor;
import com.hrznstudio.galacticraft.api.block.ConfiguredSideOption;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.util.BlockFace;
import com.hrznstudio.galacticraft.api.internal.data.MinecraftServerTeamsGetter;
import com.hrznstudio.galacticraft.component.SubTankComponent;
import com.hrznstudio.galacticraft.component.SubInventoryComponent;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.util.EnergyUtils;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.api.ComponentHelper;
import io.github.cottonmc.component.compat.vanilla.InventoryWrapper;
import io.github.cottonmc.component.energy.CapacitorComponent;
import io.github.cottonmc.component.energy.impl.SimpleCapacitorComponent;
import io.github.cottonmc.component.fluid.TankComponent;
import io.github.cottonmc.component.fluid.TankComponentHelper;
import io.github.cottonmc.component.fluid.impl.SimpleTankComponent;
import io.github.cottonmc.component.item.InventoryComponent;
import io.github.cottonmc.component.item.impl.SimpleInventoryComponent;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public abstract class ConfigurableMachineBlockEntity extends BlockEntity implements BlockEntityClientSerializable, SidedInventory, Tickable {
    private final InventoryWrapper wrappedInventory = new InventoryWrapper() {
        @Override
        public boolean canPlayerUse(PlayerEntity player) {
            return ConfigurableMachineBlockEntity.this.canUse(player);
        }

        @Override
        public InventoryComponent getComponent() {
            return ConfigurableMachineBlockEntity.this.getInventory();
        }
    };

    private final SecurityInfo security = new SecurityInfo();
    private final SideConfigInfo sideConfigInfo = new SideConfigInfo(this, validSideOptions(), 1, this.getInventorySize(), this.getFluidTankSize());

    private MachineStatus status = MachineStatus.EMPTY;
    private RedstoneState redstone = RedstoneState.IGNORE;
    private boolean noDrop = false;

    private final @NotNull SimpleCapacitorComponent capacitor = new SimpleCapacitorComponent(this.getMaxEnergy(), GalacticraftEnergy.GALACTICRAFT_JOULES) {
        @Override
        public boolean canExtractEnergy() {
            return ConfigurableMachineBlockEntity.this.canExtractEnergy();
        }

        @Override
        public boolean canInsertEnergy() {
            return ConfigurableMachineBlockEntity.this.canInsertEnergy();
        }

        @Override
        public void readFromNbt(CompoundTag tag) {
            if (tag.getBoolean("disabled")) return;
            super.readFromNbt(tag);
        }

        @Override
        public void writeToNbt(CompoundTag tag) {
            if (ConfigurableMachineBlockEntity.this.getMaxEnergy() == 0) {
                tag.putBoolean("disabled", true);
                return;
            }
            super.writeToNbt(tag);
        }
    };

    private final @NotNull SimpleInventoryComponent inventory = new SimpleInventoryComponent(this.getInventorySize()) {
        @Override
        public boolean isAcceptableStack(int slot, ItemStack stack) {
            return ConfigurableMachineBlockEntity.this.getFilterForSlot(slot).test(stack) || stack.isEmpty();
        }

        @Override
        public void readFromNbt(CompoundTag tag) {
            if (tag.getBoolean("disabled")) return;
            super.readFromNbt(tag);
        }

        @Override
        public void writeToNbt(CompoundTag tag) {
            if (ConfigurableMachineBlockEntity.this.getMaxEnergy() == 0) {
                tag.putBoolean("disabled", true);
                return;
            }
            super.writeToNbt(tag);
        }
    };

    private final @NotNull SimpleTankComponent tank = new SimpleTankComponent(this.getFluidTankSize(), this.getFluidTankMaxCapacity()) {
        @Override
        public boolean canExtract(int slot) {
            return ConfigurableMachineBlockEntity.this.canPipeExtractFluid(slot);
        }

        @Override
        public boolean canInsert(int slot) {
            return ConfigurableMachineBlockEntity.this.canPipeInsertFluid(slot);
        }

        @Override
        public FluidVolume insertFluid(FluidVolume fluid, ActionType action) {
            for (int i = 0; i < contents.size(); i++) {
                if (isAcceptableFluid(i, fluid)) {
                    fluid = super.insertFluid(i, fluid, action);
                    if (fluid.isEmpty()) return fluid;
                }
            }

            return fluid;
        }

        @Override
        public FluidVolume insertFluid(int tank, FluidVolume fluid, ActionType action) {
            if (isAcceptableFluid(tank, fluid)) {
                return super.insertFluid(tank, fluid, action);
            }
            return fluid;
        }

        public boolean isAcceptableFluid(int tank, FluidVolume volume) {
            return ConfigurableMachineBlockEntity.this.isAcceptableFluid(tank, volume) || volume.isEmpty();
        }

        @Override
        public void setFluid(int slot, FluidVolume stack) {
            if (isAcceptableFluid(slot, stack)) super.setFluid(slot, stack);
        }

        @Override
        public boolean isAcceptableFluid(int tank) {//how are you supposed to check if its acceptable if you *only* get the tank and no fluid?! also currently unused?
            return canInsert(tank);
        }

        @Override
        public void readFromNbt(CompoundTag tag) {
            if (tag.getBoolean("disabled")) return;
            super.readFromNbt(tag);
        }

        @Override
        public void writeToNbt(CompoundTag tag) {
            if (ConfigurableMachineBlockEntity.this.getMaxEnergy() == 0) {
                tag.putBoolean("disabled", true);
                return;
            }
            super.writeToNbt(tag);
        }
    };


    public ConfigurableMachineBlockEntity(BlockEntityType<? extends ConfigurableMachineBlockEntity> blockEntityType) {
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
        if (getStatus().getType().isActive()) return getBaseEnergyConsumption();
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
        if (getStatus().getType().isActive()) return getBaseEnergyGenerated();
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

    public boolean isAcceptableFluid(int tank, FluidVolume volume) {
        return false;
    }

    public int getInventorySize() {
        return 0;
    }

    public int getFluidTankSize() {
        return 0;
    }

    public Fraction getFluidTankMaxCapacity() {
        return Fraction.ZERO;
    }

    public abstract List<SideOption> validSideOptions();

    public void setRedstone(@NotNull RedstoneState redstone) {
        this.redstone = redstone;
    }

    public final @NotNull MachineStatus getStatus() {
        return status;
    }

    public final void setStatus(MachineStatus status) {
        this.status = status;
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
    public int getMaxEnergy() {
        return Galacticraft.configManager.get().machineEnergyStorageSize();
    }

    /**
     * @return The {@link Predicate} for the given slot of {@link #getInventory()}.
     */
    public Predicate<ItemStack> getFilterForSlot(int slot) {
        return Constants.Misc.alwaysTrue();
    }

    /**
     * @return The maximum amount of energy that can be transferred to or from a battery in this machine per call to
     * {@link #attemptChargeFromStack(int)} or {@link #attemptDrainPowerToStack(int)}
     */
    protected int getBatteryTransferRate() {
        return 50;
    }

    public final @NotNull SimpleCapacitorComponent getCapacitor() {
        return this.capacitor;
    }

    public final @NotNull SimpleInventoryComponent getInventory() {
        return this.inventory;
    }

    public final @NotNull SimpleTankComponent getFluidTank() {
        return this.tank;
    }

    public final @Nullable CapacitorComponent getCapacitor(@NotNull BlockState state, @Nullable Direction direction) {
        if (direction != null) {
            ConfiguredSideOption sideOption = this.getSideConfigInfo().get(BlockFace.toFace(state.get(Properties.HORIZONTAL_FACING), direction));
            if (sideOption.getOption().isEnergy()) {
                return this.getCapacitor();
            }
            return null;
        }
        return this.getCapacitor();
    }

    public final @Nullable InventoryComponent getInventory(@NotNull BlockState state, @Nullable Direction direction) {
        if (direction != null) {
            ConfiguredSideOption sideOption = this.getSideConfigInfo().get(BlockFace.toFace(state.get(Properties.HORIZONTAL_FACING), direction));
            if (sideOption.getOption().isItem()) {
                if (sideOption.isWildcard()) {
                    IntArrayList list = new IntArrayList();
                    if (sideOption.getOption().isInput()) {
                        for (int i = 0; i < tank.getTanks(); i++) {
                            if (tank.canInsert(i)) {
                                list.add(i);
                            }
                        }
                    } else {
                        for (int i = 0; i < tank.getTanks(); i++) {
                            if (tank.canExtract(i)) {
                                list.add(i);
                            }
                        }
                    }
                    return new SubInventoryComponent(this.getInventory(), list.toArray(new int[0]));
                } else {
                    return new SubInventoryComponent(this.getInventory(), new int[]{sideOption.getValue()});
                }
            }
            return null;
        }
        return this.getInventory();
    }

    public final @Nullable TankComponent getFluidTank(@NotNull BlockState state, @Nullable Direction direction) {
        if (direction != null) {
            ConfiguredSideOption sideOption = this.getSideConfigInfo().get(BlockFace.toFace(state.get(Properties.HORIZONTAL_FACING), direction));
            if (sideOption.getOption().isFluid()) {
                if (sideOption.isWildcard()) {
                    IntArrayList list = new IntArrayList();
                    if (sideOption.getOption().isInput()) {
                        for (int i = 0; i < tank.getTanks(); i++) {
                            if (tank.canInsert(i)) {
                                list.add(i);
                            }
                        }
                    } else {
                        for (int i = 0; i < tank.getTanks(); i++) {
                            if (tank.canExtract(i)) {
                                list.add(i);
                            }
                        }
                    }
                    return new SubTankComponent(this.getFluidTank(), list.toArray(new int[0]));
                } else {
                    return new SubTankComponent(this.getFluidTank(), new int[]{sideOption.getValue()});
                }
            }
            return null;
        }
        return this.getFluidTank();
    }

    public final @NotNull SecurityInfo getSecurity() {
        return security;
    }

    public final @NotNull RedstoneState getRedstone() {
        return redstone;
    }

    public final @NotNull SideConfigInfo getSideConfigInfo() {
        return sideConfigInfo;
    }

    public final boolean canUse(PlayerEntity player) {
        return this.getSecurity().hasAccess(player);
    }

    protected void decrement(int slot, int amount) {
        ItemStack stack = this.getInventory().getStack(slot);
        stack.decrement(amount);
        this.getInventory().setStack(slot, stack);
    }

    /**
     * Whether the current machine is enabled
     *
     * @return The state of the machine
     */
    public boolean disabled() {
        switch (this.redstone) {
            case OFF:
                return this.getWorld().isReceivingRedstonePower(pos);
            case ON:
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
                    this.getCapacitor().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, getEnergyConsumption(), ActionType.PERFORM);
                if (getBaseEnergyGenerated() > 0)
                    this.getCapacitor().insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, getEnergyGenerated(), ActionType.PERFORM);
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
        return this.getCapacitor().getCurrentEnergy() >= this.getBaseEnergyConsumption();
    }

    public boolean isTankFull(int tank) {
        return this.getFluidTank().getContents(tank).getAmount().compareTo(this.getFluidTank().getMaxCapacity(tank)) >= 0;
    }

    @NotNull
    public <C extends Inventory, T extends Recipe<C>> Optional<T> getRecipe(RecipeType<T> type, C inventory) {
        return this.world.getRecipeManager().getFirstMatch(type, inventory, this.world);
    }

    public boolean canInsert(int slot, Recipe<?> recipe) {
        return canInsert(slot, recipe, 1);
    }

    public boolean canInsert(int slot, Recipe<?> recipe, int multiplier) {
        ItemStack stack = recipe.getOutput().copy();
        stack.setCount(stack.getCount() * multiplier);
        return canInsert(slot, stack);
    }

    public boolean canInsert(int[] slots, Recipe<?> recipe, int multiplier) {
        ItemStack stack = recipe.getOutput().copy();
        stack.setCount(stack.getCount() * multiplier);
        return canInsert(slots, stack);
    }

    public boolean canInsert(int[] slots, ItemStack stack) {
        stack = stack.copy();
        for (int slot : slots) {
            stack = this.getInventory().insertStack(slot, stack, ActionType.TEST);
            if (stack.isEmpty()) return true;
        }
        return stack.isEmpty();
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        if (this.getMaxEnergy() > 0) this.getCapacitor().writeToNbt(tag);
        if (this.getInventorySize() > 0) this.getInventory().writeToNbt(tag);
        if (this.getFluidTankSize() > 0) this.getFluidTank().writeToNbt(tag);
        this.getSecurity().toTag(tag);
        this.getSideConfigInfo().toTag(tag);
        this.getRedstone().toTag(tag);
        tag.putBoolean("NoDrop", this.noDrop);
        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        if (this.getMaxEnergy() > 0) this.getCapacitor().readFromNbt(tag);
        if (this.getInventorySize() > 0) this.getInventory().readFromNbt(tag);
        if (this.getFluidTankSize() > 0) this.getFluidTank().readFromNbt(tag);
        this.getSecurity().fromTag(tag);
        this.getSideConfigInfo().fromTag(tag);
        this.setRedstone(RedstoneState.fromTag(tag));
        this.noDrop = tag.getBoolean("NoDrop");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void fromClientTag(CompoundTag tag) {
        this.sideConfigInfo.fromTag(tag);
        ((WorldRendererAccessor) MinecraftClient.getInstance().worldRenderer).addChunkToRebuild(pos);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        this.sideConfigInfo.toTag(tag);
        return tag;
    }

    public boolean canInsert(int slot, ItemStack stack) {
        return this.getInventory().insertStack(slot, stack, ActionType.TEST).isEmpty();
    }

    public void insert(int slot, ItemStack stack) {
        if (canInsert(slot, stack)) {
            this.getInventory().insertStack(slot, stack, ActionType.PERFORM);
        } else {
            throw new RuntimeException();
        }
    }

    public void trySpreadEnergy() {
        if (this.canExtractEnergy()) {
            for (BlockFace face : BlockFace.values()) {
                ConfiguredSideOption option = this.getSideConfigInfo().get(face);
                if (option.getOption() == SideOption.POWER_OUTPUT) {
                    Direction dir = face.toDirection(this.world.getBlockState(pos).get(Properties.HORIZONTAL_FACING));
                    CapacitorComponent component = ComponentHelper.CAPACITOR.getComponent(this.world, pos.offset(dir), dir.getOpposite());
                    if (component != null) {
                        if (component.canInsertEnergy()) {
                            int i = this.getCapacitor().insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, component.insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, this.getCapacitor().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, component.getMaxEnergy() - component.getCurrentEnergy(), ActionType.PERFORM), ActionType.PERFORM), ActionType.PERFORM);
                            if (i != 0) {
                                Galacticraft.logger.debug( i + "gJ wasted?!");
                            }
                        }
                    }
                }
            }
        }
    }

    public void trySpreadFluids(int tank) {
        if (this.canPipeExtractFluid(tank) && !this.getFluidTank().getContents(tank).isEmpty()) {
            for (BlockFace face : BlockFace.values()) {
                ConfiguredSideOption option = this.getSideConfigInfo().get(face);
                if (option.getOption().isFluid() && option.getOption().isOutput()) {
                    Direction dir = face.toDirection(this.world.getBlockState(pos).get(Properties.HORIZONTAL_FACING));
                    TankComponent component = ComponentHelper.TANK.getComponent(this.world, pos.offset(dir), dir.getOpposite());
                    if (component != null) {
                        for (int i = 0; i < component.getTanks(); i++) {
                            if (component.canInsert(i)) {
                                FluidVolume a = this.getFluidTank().insertFluid(tank, component.insertFluid(this.getFluidTank().takeFluid(tank, component.getMaxCapacity(i), ActionType.PERFORM), ActionType.PERFORM), ActionType.PERFORM);
                                if (!a.isEmpty()) {
                                    Galacticraft.logger.debug(a.getAmount().toString() + " " + Registry.FLUID.getId(a.getFluid()) + " wasted?!");
                                }
                                if (this.getFluidTank().getContents(tank).isEmpty()) return;
                            }
                        }
                    }
                }
            }
        }
    }

    public void idleEnergyDecrement(boolean off) {
        if (this.world.random.nextInt(off ? 40 : 20) == 1) {
            if (this.getBaseEnergyConsumption() > 0) {
                this.getCapacitor().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, this.getBaseEnergyConsumption() / 20, ActionType.PERFORM);
            }
        }
    }

    /**
     * Tries to charge this machine from the item in the given slot in this {@link #getInventory}.
     */
    protected void attemptChargeFromStack(int slot) {
        if (this.getCapacitor().getCurrentEnergy() >= this.getCapacitor().getMaxEnergy()) {
            return;
        }
        ItemStack stack = this.getInventory().getStack(slot);
        int neededEnergy = Math.min(this.getBatteryTransferRate(), this.getCapacitor().getMaxEnergy() - this.getCapacitor().getCurrentEnergy());
        if (EnergyUtils.isEnergyItem(stack)) {
            this.getCapacitor().insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, EnergyUtils.extractEnergy(stack, neededEnergy, ActionType.PERFORM), ActionType.PERFORM);
            this.getInventory().setStack(slot, stack);
        }
    }

    /**
     * Tries to drain some of this machine's power into the item in the given slot in this {@link #getInventory}.
     *
     * @param slot The slot id of the item
     */
    protected void attemptDrainPowerToStack(int slot) {
        int available = Math.min(this.getBatteryTransferRate(), this.getCapacitor().getCurrentEnergy());
        if (available <= 0) {
            return;
        }
        ItemStack stack = this.getInventory().getStack(slot);
        if (EnergyUtils.isEnergyItem(stack)) {
            if (EnergyUtils.getEnergy(stack) < EnergyUtils.getMaxEnergy(stack)) {
                int i = EnergyUtils.insertEnergy(stack, available, ActionType.PERFORM);
                this.getCapacitor().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, available - i, ActionType.PERFORM);
                this.getInventory().setStack(slot, stack);
            }
        }
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        ConfiguredSideOption configuredSideOption = this.getSideConfigInfo().get(BlockFace.toFace(this.world.getBlockState(pos).get(Properties.HORIZONTAL_FACING), side));
        if (configuredSideOption.isWildcard()) {
            return IntStream.range(0, this.getInventorySize()).toArray();
        } else {
            return new int[]{configuredSideOption.getValue()};
        }
    }

    @Override
    public final boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if (!canHopperExtract(slot)) return false;
        int[] slots = this.getAvailableSlots(dir);
        boolean accessible = false;
        for (int i : slots) {
            if (slot == i) {
                accessible = true;
                break;
            }
        }

        return accessible && this.getInventory().canExtract(slot) && this.getInventory().getStack(slot).isItemEqual(stack);
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        if (!canHopperInsert(slot)) return false;
        int[] slots = this.getAvailableSlots(dir);
        boolean accessible = false;
        for (int i : slots) {
            if (slot == i) {
                accessible = true;
                break;
            }
        }

        return accessible && this.getInventory().insertStack(stack, ActionType.TEST).isEmpty() && canInsert(slot, stack);
    }

    /**
     * Returns a list of non-configurable machine faces.
     * @return a list of non-configurable machine faces.
     */
    public List<BlockFace> getLockedFaces() {
        return Collections.emptyList();
    }

    public InventoryWrapper getWrappedInventory() {
        return wrappedInventory;
    }

    @Override
    public int getMaxCountPerStack() {
        return this.getWrappedInventory().getMaxCountPerStack();
    }

    @Override
    public void onOpen(PlayerEntity player) {
    }

    @Override
    public void onClose(PlayerEntity player) {
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return this.getWrappedInventory().isValid(slot, stack);
    }

    @Override
    public int count(Item item) {
        return this.getWrappedInventory().count(item);
    }

    @Override
    public boolean containsAny(Set<Item> items) {
        return this.getWrappedInventory().containsAny(items);
    }

    @Override
    public int size() {
        return this.getWrappedInventory().size();
    }

    @Override
    public boolean isEmpty() {
        return this.getWrappedInventory().isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.getWrappedInventory().getStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return this.getWrappedInventory().removeStack(slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return this.getWrappedInventory().removeStack(slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.getWrappedInventory().setStack(slot, stack);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return this.security.hasAccess(player);
    }

    @Override
    public void clear() {
        this.getWrappedInventory().clear();
    }

    @Override
    public void sync() {
        BlockEntityClientSerializable.super.sync();
        this.world.updateNeighbors(pos, this.getCachedState().getBlock());
    }

    public enum RedstoneState implements StringIdentifiable {
        /**
         * Ignores redstone entirely.
         */
        IGNORE,

        /**
         * When powered with redstone, the machine turns off.
         */
        OFF,

        /**
         * When powered with redstone, the machine turns on.
         */
        ON;

        public static RedstoneState fromString(String string) {
            return RedstoneState.valueOf(string.toUpperCase(Locale.ROOT));
        }

        @Override
        public String asString() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        public void toTag(CompoundTag tag) {
            tag.putString("Redstone", this.asString());
        }

        public static RedstoneState fromTag(CompoundTag tag) {
            return fromString(tag.getString("Redstone"));
        }
    }

    public interface MachineStatus {
        MachineStatus EMPTY = new MachineStatus() {
            @Override
            public @NotNull Text getName() {
                return Constants.Misc.EMPTY_TEXT;
            }

            @Override
            public @NotNull StatusType getType() {
                return StatusType.OTHER;
            }

            @Override
            public int getIndex() {
                return 0;
            }
        };

        @NotNull Text getName();

        @NotNull StatusType getType();

        int getIndex();

        enum StatusType {
            /**
             * The machine is active
             */
            WORKING(true),
            /**
             * THe machine is active, but at reduced efficiency.
             */
            PARTIALLY_WORKING(true),
            /**
             * The machine is missing a resource it needs to function.
             * Should not be an item, fluid or energy.
             *
             * @see #MISSING_ENERGY
             * @see #MISSING_FLUIDS
             * @see #MISSING_ITEMS
             */
            MISSING_RESOURCE(false),
            /**
             * The machine is missing a fluid it needs to function.
             * Should be preferred over {@link #MISSING_RESOURCE}
             */
            MISSING_FLUIDS(false),
            /**
             * The machine does not have the amount of energy needed to function.
             * Should be preferred over {@link #MISSING_RESOURCE}
             */
            MISSING_ENERGY(false),
            /**
             * The machine does not have the items needed to function.
             * Should be preferred over {@link #MISSING_RESOURCE}
             */
            MISSING_ITEMS(false),
            /**
             * The machine's output is blocked/full.
             */
            OUTPUT_FULL(false),
            /**
             *
             */
            OTHER(false);

            final boolean active;

            StatusType(boolean active) {
                this.active = active;
            }

            public boolean isActive() {
                return this.active;
            }
        }
    }

    public static class SecurityInfo {
        private UUID owner;
        private String username;
        private Identifier team;
        private Publicity publicity;

        protected SecurityInfo() {
            this.owner = null;
            this.publicity = Publicity.PUBLIC;
            this.team = null;
            this.username = "";
        }

        public boolean isOwner(PlayerEntity player) {
            return isOwner(player.getUuid());
        }

        public boolean isOwner(UUID uuid) {
            if (owner == null) owner = uuid;
            return this.owner.equals(uuid);
        }

        public boolean hasAccess(PlayerEntity player) {
            switch (publicity) {
                case PUBLIC:
                    return true;
                case SPACE_RACE:
                    return (((MinecraftServerTeamsGetter) player.getServer()).getSpaceRaceTeams().getTeam(player.getUuid()) != null)
                            && ((MinecraftServerTeamsGetter) player.getServer()).getSpaceRaceTeams().getTeam(player.getUuid()).players.containsKey(owner);
                case PRIVATE:
                    return isOwner(player);
            }
            return false;
        }

        public Publicity getPublicity() {
            return publicity;
        }

        public void setPublicity(Publicity publicity) {
            this.publicity = publicity;
        }

        public boolean hasOwner() {
            return this.owner != null;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public UUID getOwner() {
            return this.owner;
        }

        public void setOwner(PlayerEntity owner) {
            if (!this.hasOwner()) {
                this.owner = owner.getUuid();
            }
            this.username = owner.getEntityName();
        }

        public Identifier getTeam() {
            return team;
        }

        public boolean hasTeam() {
            return team != null;
        }

        public CompoundTag toTag(CompoundTag tag) {
            CompoundTag compoundTag = new CompoundTag();
            if (this.hasOwner()) {
                compoundTag.putUuid("owner", this.owner);
            }
            compoundTag.putString("username", this.username);
            compoundTag.putString("publicity", this.publicity.asString());
            if (this.hasTeam()) {
                compoundTag.putString("team", team.toString());
            }
            tag.put("security", compoundTag);
            return tag;
        }

        public void fromTag(CompoundTag tag) {
            CompoundTag compoundTag = tag.getCompound("security");

            if (compoundTag.contains("owner")) {
                if (!this.hasOwner()) {
                    this.owner = compoundTag.getUuid("owner");
                }
            }

            if (compoundTag.contains("team")) {
                if (!this.hasTeam()) {
                    this.team = new Identifier(compoundTag.getString("team"));
                }
            }

            this.username = compoundTag.getString("username");
            this.publicity = Publicity.valueOf(compoundTag.getString("publicity"));
        }


        public enum Publicity implements StringIdentifiable {
            PUBLIC,
            SPACE_RACE,
            PRIVATE;

            @Override
            public String asString() {
                return this.toString();
            }
        }
    }

    public static class SideConfigInfo {
        private final ConfiguredSideOption front;
        private final ConfiguredSideOption back;
        private final ConfiguredSideOption left;
        private final ConfiguredSideOption right;
        private final ConfiguredSideOption top;
        private final ConfiguredSideOption bottom;
        private final List<SideOption> values;
        private final int capacitors;
        private final int invSize;
        private final int tanks;
        private final ConfigurableMachineBlockEntity blockEntity;

        public SideConfigInfo(ConfigurableMachineBlockEntity blockEntity, List<SideOption> values, int capacitors, int invSize, int tanks) {
            if (!values.contains(SideOption.DEFAULT)) throw new RuntimeException();
            this.values = new ArrayList<>(values);
            this.values.sort(Enum::compareTo);
            this.capacitors = capacitors;
            this.invSize = invSize;
            this.tanks = tanks;
            this.blockEntity = blockEntity;

            this.front = new ConfiguredSideOption(SideOption.DEFAULT, 0, 1);
            this.back = new ConfiguredSideOption(SideOption.DEFAULT, 0, 1);
            this.left = new ConfiguredSideOption(SideOption.DEFAULT, 0, 1);
            this.right = new ConfiguredSideOption(SideOption.DEFAULT, 0, 1);
            this.top = new ConfiguredSideOption(SideOption.DEFAULT, 0, 1);
            this.bottom = new ConfiguredSideOption(SideOption.DEFAULT, 0, 1);
        }

        public SideOption getFrontOption() {
            return front.getOption();
        }

        public SideOption getBackOption() {
            return back.getOption();
        }

        public SideOption getLeftOption() {
            return left.getOption();
        }

        public SideOption getRightOption() {
            return right.getOption();
        }

        public SideOption getUpOption() {
            return top.getOption();
        }

        public SideOption getDownOption() {
            return bottom.getOption();
        }

        public void setFrontOption(SideOption option) {
            front.setOption(option, this.getMax(option));
            if (!blockEntity.world.isClient()) blockEntity.sync();
        }

        public void setBackOption(SideOption option) {
            back.setOption(option, this.getMax(option));
            if (!blockEntity.world.isClient()) blockEntity.sync();
        }

        public void setLeftOption(SideOption option) {
            left.setOption(option, this.getMax(option));
            if (!blockEntity.world.isClient()) blockEntity.sync();
        }

        public void setRightOption(SideOption option) {
            right.setOption(option, this.getMax(option));
            if (!blockEntity.world.isClient()) blockEntity.sync();
        }

        public void setTopOption(SideOption option) {
            top.setOption(option, this.getMax(option));
            if (!blockEntity.world.isClient()) blockEntity.sync();
        }

        public void setBottomOption(SideOption option) {
            bottom.setOption(option, this.getMax(option));
            if (!blockEntity.world.isClient()) blockEntity.sync();
        }

        public int getFrontValue() {
            return front.getValue();
        }

        public int getBackValue() {
            return back.getValue();
        }

        public int getLeftValue() {
            return left.getValue();
        }

        public int getRightValue() {
            return right.getValue();
        }

        public int getTopValue() {
            return top.getValue();
        }

        public int getBottomValue() {
            return bottom.getValue();
        }

        public int incrementFront() {
            return front.increment();
        }

        public int incrementBack() {
            return back.increment();
        }

        public int incrementLeft() {
            return left.increment();
        }

        public int incrementRight() {
            return right.increment();
        }

        public int incrementUp() {
            return top.increment();
        }

        public int incrementDown() {
            return bottom.increment();
        }

        public int decrementFront() {
            return front.decrement();
        }

        public int decrementBack() {
            return back.decrement();
        }

        public int decrementLeft() {
            return left.decrement();
        }

        public int decrementRight() {
            return right.decrement();
        }

        public int decrementUp() {
            return top.decrement();
        }

        public int decrementDown() {
            return bottom.decrement();
        }

        private int getMax(SideOption option) {
            if (option.isEnergy()) return capacitors;
            if (option.isFluid()) return tanks;
            if (option.isItem()) return invSize;
            return 1;
        }

        public CompoundTag toTag(CompoundTag tag) {
            tag.put("front", front.toTag(new CompoundTag()));
            tag.put("back", back.toTag(new CompoundTag()));
            tag.put("left", left.toTag(new CompoundTag()));
            tag.put("right", right.toTag(new CompoundTag()));
            tag.put("top", top.toTag(new CompoundTag()));
            tag.put("bottom", bottom.toTag(new CompoundTag()));
            return tag;
        }

        public void fromTag(CompoundTag tag) {
            front.fromTag(tag.getCompound("front"));
            back.fromTag(tag.getCompound("back"));
            left.fromTag(tag.getCompound("left"));
            right.fromTag(tag.getCompound("right"));
            top.fromTag(tag.getCompound("top"));
            bottom.fromTag(tag.getCompound("bottom"));
        }

        /**
         * Please do not modify the returned {@link ConfiguredSideOption}
         * @param face the block face to pull the option from
         * @return a {@link ConfiguredSideOption} assignd to the given face.
         */
        public ConfiguredSideOption get(@NotNull BlockFace face) {
            switch (face) {
                case FRONT:
                    return front;
                case TOP:
                    return top;
                case BACK:
                    return back;
                case RIGHT:
                    return right;
                case LEFT:
                    return left;
                case BOTTOM:
                    return bottom;
            }
            throw new RuntimeException();
        }

        public void set(@NotNull BlockFace face, SideOption option) {
            switch (face) {
                case FRONT:
                    setFrontOption(option);
                    break;
                case TOP:
                    setTopOption(option);
                    break;
                case BACK:
                    setBackOption(option);
                    break;
                case RIGHT:
                    setRightOption(option);
                    break;
                case LEFT:
                    setLeftOption(option);
                    break;
                case BOTTOM:
                    setBottomOption(option);
                    break;
                default:
                    throw new RuntimeException();
            }
        }

        public void increment(@NotNull BlockFace face) {
            switch (face) {
                case FRONT:
                    front.increment();
                    break;
                case TOP:
                    top.increment();
                    break;
                case BACK:
                    back.increment();
                    break;
                case RIGHT:
                    right.increment();
                    break;
                case LEFT:
                    left.increment();
                    break;
                case BOTTOM:
                    bottom.increment();
                    break;
            }
        }
        public void decrement(@NotNull BlockFace face) {
            switch (face) {
                case FRONT:
                    front.decrement();
                    break;
                case TOP:
                    top.decrement();
                    break;
                case BACK:
                    back.decrement();
                    break;
                case RIGHT:
                    right.decrement();
                    break;
                case LEFT:
                    left.decrement();
                    break;
                case BOTTOM:
                    bottom.decrement();
                    break;
            }
        }
    }
}
