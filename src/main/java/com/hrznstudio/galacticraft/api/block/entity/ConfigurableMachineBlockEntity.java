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
import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.compat.InventoryFixedWrapper;
import alexiil.mc.lib.attributes.item.filter.ConstantItemFilter;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import alexiil.mc.lib.attributes.item.impl.FullFixedItemInv;
import alexiil.mc.lib.attributes.misc.Reference;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.accessor.WorldRendererAccessor;
import com.hrznstudio.galacticraft.api.block.ConfigurableMachineBlock;
import com.hrznstudio.galacticraft.api.block.ConfiguredSideOption;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.util.BlockFace;
import com.hrznstudio.galacticraft.api.internal.data.MinecraftServerTeamsGetter;
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
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.Inventory;
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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public abstract class ConfigurableMachineBlockEntity extends BlockEntity implements BlockEntityClientSerializable, Tickable {
    private final SecurityInfo security = new SecurityInfo();
    private final SideConfiguration sideConfiguration = new SideConfiguration(this, this.validSideOptions(), 1, this.getInventorySize(), this.getFluidTankSize());

    private MachineStatus status = MachineStatus.NULL;
    private RedstoneState redstone = RedstoneState.IGNORE;
    private boolean noDrop = false;

    private final @NotNull SimpleCapacitor capacitor = new SimpleCapacitor(DefaultEnergyType.INSTANCE, this.getEnergyCapacity());

    private final @NotNull FullFixedItemInv inventory = new FullFixedItemInv(this.getInventorySize());

    private final @NotNull SimpleFixedFluidInv tank = new SimpleFixedFluidInv(this.getFluidTankSize(), this.getFluidTankCapacity()) {
        @Override
        public FluidFilter getFilterForTank(int tank) {
            return ConfigurableMachineBlockEntity.this.getFilterForTank(tank).or(key -> key.getRawFluid() == Fluids.EMPTY);
        }
    };

    private final InventoryFixedWrapper wrappedInventory = new InventoryFixedWrapper(this.getInventory()) {
        @Override
        public boolean canPlayerUse(PlayerEntity player) {
            return ConfigurableMachineBlockEntity.this.getSecurity().hasAccess(player);
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
    public int getEnergyCapacity() {
        return Galacticraft.configManager.get().machineEnergyStorageSize();
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
            if (cso.getOption().isEnergy()) {
                if (cso.getOption().isOutput()) {
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
            if (cso.getOption().isEnergy()) {
                if (cso.getOption().isInput()) {
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
            if (cso.getOption().isEnergy()) {
                return this.getCapacitor();
            }
            return null;
        }
        return this.getCapacitor();
    }

    public final @Nullable FixedItemInv getInventory(@NotNull BlockState state, @Nullable Direction direction) { //DIRECTION IS POINTING AWAY FROM MACHINE TO THE SEARCHER
        if (direction != null) {
            ConfiguredSideOption cso = this.getSideConfiguration().get(BlockFace.toFace(state.get(Properties.HORIZONTAL_FACING), direction));
            if (cso.getOption().isItem()) {
                if (cso.isWildcard()) {
                    IntArrayList list = new IntArrayList();

                    if (cso.getOption().isInput()) {
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
            if (cso.getOption().isFluid()) {
                if (cso.isWildcard()) {
                    IntArrayList list = new IntArrayList();
                    if (cso.getOption().isInput()) {
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
            if (cso.getOption().isFluid()) {
                if (cso.getOption().isInput()) {
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
            if (cso.getOption().isFluid()) {
                if (cso.getOption().isOutput()) {
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
        return security;
    }

    public final @NotNull RedstoneState getRedstone() {
        return redstone;
    }

    public final @NotNull ConfigurableMachineBlockEntity.SideConfiguration getSideConfiguration() {
        return sideConfiguration;
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
        if (!world.isClient) this.sync();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void fromClientTag(CompoundTag tag) {
        this.sideConfiguration.fromTag(tag);
        ((WorldRendererAccessor) MinecraftClient.getInstance().worldRenderer).addChunkToRebuild(pos);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        this.sideConfiguration.toTag(tag);
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
                if (option.getOption() == SideOption.POWER_OUTPUT) {
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
                if (option.getOption().isFluid() && option.getOption().isOutput()) {
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
        this.world.setBlockState(this.pos, state.with(ConfigurableMachineBlock.ARBITRARY_BOOLEAN_PROPERTY, !state.get(ConfigurableMachineBlock.ARBITRARY_BOOLEAN_PROPERTY)), 11);
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
        MachineStatus NULL = new MachineStatus() {
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

    public static class SideConfiguration {
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
        private final ConfigurableMachineBlockEntity machine;

        public SideConfiguration(ConfigurableMachineBlockEntity machine, List<SideOption> values, int capacitors, int invSize, int tanks) {
            if (!values.contains(SideOption.DEFAULT)) throw new RuntimeException();
            this.values = new ArrayList<>(values);
            this.values.sort(Enum::compareTo);
            this.capacitors = capacitors;
            this.invSize = invSize;
            this.tanks = tanks;
            this.machine = machine;

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
            if (!machine.world.isClient()) machine.sync();
        }

        public void setBackOption(SideOption option) {
            back.setOption(option, this.getMax(option));
            if (!machine.world.isClient()) machine.sync();
        }

        public void setLeftOption(SideOption option) {
            left.setOption(option, this.getMax(option));
            if (!machine.world.isClient()) machine.sync();
        }

        public void setRightOption(SideOption option) {
            right.setOption(option, this.getMax(option));
            if (!machine.world.isClient()) machine.sync();
        }

        public void setTopOption(SideOption option) {
            top.setOption(option, this.getMax(option));
            if (!machine.world.isClient()) machine.sync();
        }

        public void setBottomOption(SideOption option) {
            bottom.setOption(option, this.getMax(option));
            if (!machine.world.isClient()) machine.sync();
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
