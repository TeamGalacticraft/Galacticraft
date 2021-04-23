/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.api.block.entity;

import alexiil.mc.lib.attributes.*;
import alexiil.mc.lib.attributes.fluid.FixedFluidInvView;
import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.FluidExtractable;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.filter.ConstantFluidFilter;
import alexiil.mc.lib.attributes.item.FixedItemInvView;
import alexiil.mc.lib.attributes.item.ItemExtractable;
import alexiil.mc.lib.attributes.item.ItemInsertable;
import alexiil.mc.lib.attributes.item.compat.InventoryFixedWrapper;
import alexiil.mc.lib.attributes.item.filter.ConstantItemFilter;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import alexiil.mc.lib.attributes.misc.Reference;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.energy.api.CapacitorView;
import com.hrznstudio.galacticraft.energy.api.EnergyExtractable;
import com.hrznstudio.galacticraft.energy.api.EnergyInsertable;
import com.hrznstudio.galacticraft.energy.api.EnergyType;
import com.hrznstudio.galacticraft.energy.impl.DefaultEnergyType;
import com.hrznstudio.galacticraft.energy.impl.RejectingEnergyInsertable;
import com.hrznstudio.galacticraft.energy.impl.SimpleCapacitor;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.accessor.WorldRendererAccessor;
import dev.galacticraft.mod.api.block.ConfiguredMachineFace;
import dev.galacticraft.mod.api.block.MachineBlock;
import dev.galacticraft.mod.api.block.util.BlockFace;
import dev.galacticraft.mod.api.machine.*;
import dev.galacticraft.mod.attribute.fluid.MachineFluidInv;
import dev.galacticraft.mod.attribute.item.MachineItemInv;
import dev.galacticraft.mod.util.EnergyUtil;
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
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public abstract class MachineBlockEntity extends BlockEntity implements BlockEntityClientSerializable, Tickable, ExtendedScreenHandlerFactory, AttributeProviderBlockEntity {
    private final MachineConfiguration configuration = new MachineConfiguration();

    private boolean noDrop = false;
    private boolean loaded = false;

    private final @NotNull SimpleCapacitor capacitor = new SimpleCapacitor(DefaultEnergyType.INSTANCE, this.getEnergyCapacity());
    private final @NotNull MachineItemInv inventory = this.createInventory(MachineItemInv.Builder.create()).build();

    private final @NotNull MachineFluidInv fluidInv = this.createFluidInv(MachineFluidInv.Builder.create(this.getFluidTankCapacity())).build();

    private final @NotNull CapacitorView capacitorView = new CapacitorView() {
        @Override
        public EnergyType getEnergyType() {
            return MachineBlockEntity.this.getCapacitor().getEnergyType();
        }

        @Override
        public int getEnergy() {
            return MachineBlockEntity.this.getCapacitor().getEnergy();
        }

        @Override
        public int getMaxCapacity() {
            return MachineBlockEntity.this.getCapacitor().getMaxCapacity();
        }

        @Override
        public @Nullable ListenerToken addListener(CapacitorListener listener, ListenerRemovalToken removalToken) {
            return MachineBlockEntity.this.getCapacitor().addListener(listener, removalToken);
        }
    };

    private final @NotNull FixedFluidInvView fluidInvView = this.getFluidInv().getFixedView();
    private final @NotNull FixedItemInvView invView = this.getInventory().getFixedView();

    private final InventoryFixedWrapper wrappedInventory = new InventoryFixedWrapper(this.getInventory()) {
        @Override
        public boolean canPlayerUse(PlayerEntity player) {
            return MachineBlockEntity.this.getSecurity().hasAccess(player);
        }
    };

    public MachineBlockEntity(BlockEntityType<? extends MachineBlockEntity> type) {
        super(type);
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

    protected MachineItemInv.Builder createInventory(MachineItemInv.Builder builder) {
        return builder;
    }

    protected MachineFluidInv.Builder createFluidInv(MachineFluidInv.Builder builder) {
        return builder;
    }

    public FluidAmount getFluidTankCapacity() {
        return FluidAmount.ZERO;
    }

    public void setRedstone(@NotNull RedstoneInteractionType redstone) {
        this.configuration.setRedstone(redstone);
    }

    public @NotNull MachineStatus getStatus() {
        return this.configuration.getStatus();
    }

    public void setStatus(MachineStatus status) {
        this.configuration.setStatus(status);
    }

    public void setStatusById(int index) {
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
    public final ItemFilter getFilterForSlot(int slot) {
        return this.getInventory().getFilterForSlot(slot);
    }

    /**
     * @return The maximum amount of energy that can be transferred to or from a battery in this machine per call to
     * {@link #attemptChargeFromStack(int)} or {@link #attemptDrainPowerToStack(int)}
     */
    protected int getBatteryTransferRate() {
        return 500;
    }

    public final @NotNull SimpleCapacitor getCapacitor() {
        return this.capacitor;
    }

    public final @NotNull MachineItemInv getInventory() {
        return this.inventory;
    }

    public final @NotNull MachineFluidInv getFluidInv() {
        return this.fluidInv;
    }

    public CapacitorView getCapacitorView() {
        return capacitorView;
    }

    public FixedFluidInvView getFluidInvView() {
        return fluidInvView;
    }

    public FixedItemInvView getInvView() {
        return invView;
    }

    public final @Nullable EnergyExtractable getEnergyExtractable(@NotNull BlockState state, @Nullable Direction direction) {
        if (direction != null) {
            ConfiguredMachineFace cso = this.getIOConfig().get(BlockFace.toFace(state.get(Properties.HORIZONTAL_FACING), direction.getOpposite()));
            if (cso.getAutomationType().isEnergy()) {
                if (cso.getAutomationType().isOutput()) {
                    return this.getCapacitor().getExtractable().asPureExtractable();
                }
            }
            return null;
        }
        return this.getCapacitor().getExtractable().asPureExtractable();
    }

    public final @Nullable EnergyInsertable getEnergyInsertable(@NotNull BlockState state, @Nullable Direction direction) {
        if (direction != null) {
            ConfiguredMachineFace cso = this.getIOConfig().get(BlockFace.toFace(state.get(Properties.HORIZONTAL_FACING), direction.getOpposite()));
            if (cso.getAutomationType().isEnergy()) {
                if (cso.getAutomationType().isInput()) {
                    return this.getCapacitor().getInsertable().asPureInsertable();
                }
            }
            return null;
        }
        return this.getCapacitor().getInsertable().asPureInsertable();
    }

    public final @Nullable ItemExtractable getItemExtractable(@NotNull BlockState state, @Nullable Direction direction) {
        if (direction != null) {
            ConfiguredMachineFace cso = this.getIOConfig().get(BlockFace.toFace(state.get(Properties.HORIZONTAL_FACING), direction.getOpposite()));
            if (cso.getAutomationType().isItem()) {
                if (cso.getAutomationType().isOutput()) {
                    return this.getInventory().getMappedInv(cso.getMatching(this.getInventory())).getExtractable().getPureExtractable();
                }
            }
            return null;
        }
        return this.getInventory().getExtractable().getPureExtractable();
    }

    public final @Nullable ItemInsertable getItemInsertable(@NotNull BlockState state, @Nullable Direction direction) {
        if (direction != null) {
            ConfiguredMachineFace cso = this.getIOConfig().get(BlockFace.toFace(state.get(Properties.HORIZONTAL_FACING), direction.getOpposite()));
            if (cso.getAutomationType().isItem()) {
                if (cso.getAutomationType().isInput()) {
                    return this.getInventory().getMappedInv(cso.getMatching(this.getInventory())).getInsertable().getPureInsertable();
                }
            }
            return null;
        }
        return this.getInventory().getInsertable().getPureInsertable();
    }

    public final @Nullable FluidInsertable getFluidInsertable(@NotNull BlockState state, @Nullable Direction direction) {
        if (direction != null) {
            ConfiguredMachineFace cso = this.getIOConfig().get(BlockFace.toFace(state.get(Properties.HORIZONTAL_FACING), direction.getOpposite()));
            if (cso.getAutomationType().isFluid()) {
                if (cso.getAutomationType().isInput()) {
                    return this.getFluidInv().getMappedInv(cso.getMatching(this.getFluidInv())).getInsertable().getPureInsertable();
                }
            }
            return null;
        }
        return this.getFluidInv().getInsertable().getPureInsertable();
    }

    public final @Nullable FluidExtractable getFluidExtractable(@NotNull BlockState state, @Nullable Direction direction) {
        if (direction != null) {
            ConfiguredMachineFace cso = this.getIOConfig().get(BlockFace.toFace(state.get(Properties.HORIZONTAL_FACING), direction.getOpposite()));
            if (cso.getAutomationType().isFluid()) {
                if (cso.getAutomationType().isOutput()) {
                    return this.getFluidInv().getMappedInv(cso.getMatching(this.getFluidInv())).getExtractable().getPureExtractable();
                }
            }
            return null;
        }
        return this.getFluidInv().getExtractable();
    }

    public final @NotNull SecurityInfo getSecurity() {
        return this.configuration.getSecurity();
    }

    public final @NotNull RedstoneInteractionType getRedstoneInteraction() {
        return this.configuration.getRedstoneInteraction();
    }

    public final @NotNull MachineIOConfig getIOConfig() {
        return this.configuration.getSideConfiguration();
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
        switch (this.getRedstoneInteraction()) {
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
        return this.getFluidInv().getInvFluid(tank).amount().isGreaterThanOrEqual(this.getFluidInv().getMaxAmount_F(tank));
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
        if (this.getInventory().getSlotCount()> 0) this.getInventory().toTag(tag);
        if (this.getFluidInv().getTankCount() > 0) this.getFluidInv().toTag(tag);
        this.configuration.toTag(tag);
        tag.putBoolean(Constant.Nbt.NO_DROP, this.noDrop);
        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        if (this.getEnergyCapacity() > 0) this.getCapacitor().fromTag(tag);
        if (this.getInventory().getSlotCount() > 0) this.getInventory().fromTag(tag);
        if (this.getFluidInv().getTankCount() > 0) this.getFluidInv().fromTag(tag);
        this.configuration.fromTag(tag);
        this.noDrop = tag.getBoolean(Constant.Nbt.NO_DROP);
        if (loaded && !world.isClient) {
            this.sync();
        } else {
            loaded = true;
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void fromClientTag(CompoundTag tag) {
        this.getIOConfig().fromTag(tag);
        ((WorldRendererAccessor) MinecraftClient.getInstance().worldRenderer).addChunkToRebuild(pos);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        this.getIOConfig().toTag(tag);
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
            for (BlockFace face : Constant.Misc.BLOCK_FACES) {
                ConfiguredMachineFace option = this.getIOConfig().get(face);
                if (option.getAutomationType().isEnergy() && option.getAutomationType().isOutput()) {
                    Direction dir = face.toDirection(this.getCachedState().get(Properties.HORIZONTAL_FACING));
                    EnergyInsertable insertable = GalacticraftEnergy.INSERTABLE.getFirst(world, pos.offset(dir), SearchOptions.inDirection(dir.getOpposite()));
                    if (insertable != RejectingEnergyInsertable.NULL) {
                        this.getCapacitor().insert(insertable.tryInsert(DefaultEnergyType.INSTANCE, this.getCapacitor().extract(2048), Simulation.ACTION));
                    }
                }
            }
        }
    }

    public void trySpreadFluids(int tank) {
        if (this.getFluidInv().getTypes()[tank].getType().isOutput() && !this.getFluidInv().getInvFluid(tank).isEmpty()) {
            for (BlockFace face : Constant.Misc.BLOCK_FACES) {
                ConfiguredMachineFace option = this.getIOConfig().get(face);
                if (option.getAutomationType().isFluid() && option.getAutomationType().isOutput()) {
                    Direction dir = face.toDirection(this.getCachedState().get(Properties.HORIZONTAL_FACING));
                    FluidInsertable insertable = FluidAttributes.INSERTABLE.getFromNeighbour(this, dir);
                    this.getFluidInv().insertFluid(tank, insertable.attemptInsertion(this.getFluidInv().extractFluid(tank, ConstantFluidFilter.ANYTHING, null, FluidAmount.ONE, Simulation.ACTION), Simulation.ACTION), Simulation.ACTION);
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
        if (EnergyUtil.isEnergyExtractable(stack)) {
            this.getCapacitor().insert(EnergyUtil.extractEnergy(stack, neededEnergy));
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
        if (EnergyUtil.isEnergyInsertable(stack)) {
            this.getCapacitor().insert(EnergyUtil.insert(stack, this.getCapacitor().extract(available)));
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
        BlockState state = this.getCachedState();
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

    @Override
    public void addAllAttributes(AttributeList<?> to) {
        Direction direction = to.getSearchDirection();
        BlockState state = this.getCachedState();
        MachineBlockEntity machine = (MachineBlockEntity) world.getBlockEntity(pos);
        assert machine != null;
        if (direction == null) {
            to.offer(machine.getFluidInv());
            to.offer(machine.getInventory()); //expose everything if not given a direction
            to.offer(machine.getCapacitor());
        } else {
            to.offer(machine.getFluidInvView());
            to.offer(machine.getInvView());
            to.offer(machine.getCapacitorView());
            to.offer(machine.getItemInsertable(state, direction));
            to.offer(machine.getItemExtractable(state, direction));
            to.offer(machine.getFluidInsertable(state, direction));
            to.offer(machine.getFluidExtractable(state, direction));
            to.offer(machine.getEnergyExtractable(state, direction));
            to.offer(machine.getEnergyInsertable(state, direction));
        }
    }

    public MachineConfiguration getConfiguration() {
        return this.configuration;
    }
}
