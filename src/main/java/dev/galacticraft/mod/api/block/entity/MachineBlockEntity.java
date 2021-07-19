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

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.AttributeProviderBlockEntity;
import alexiil.mc.lib.attributes.SearchOptions;
import alexiil.mc.lib.attributes.Simulation;
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
import dev.galacticraft.energy.GalacticraftEnergy;
import dev.galacticraft.energy.api.CapacitorView;
import dev.galacticraft.energy.api.EnergyExtractable;
import dev.galacticraft.energy.api.EnergyInsertable;
import dev.galacticraft.energy.impl.DefaultEnergyType;
import dev.galacticraft.energy.impl.RejectingEnergyInsertable;
import dev.galacticraft.energy.impl.SimpleCapacitor;
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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public abstract class MachineBlockEntity extends BlockEntity implements BlockEntityClientSerializable, ExtendedScreenHandlerFactory, AttributeProviderBlockEntity {
    private final MachineConfiguration configuration = new MachineConfiguration();

    private boolean noDrop = false;
    private boolean loaded = false;

    private final @NotNull SimpleCapacitor capacitor = new SimpleCapacitor(DefaultEnergyType.INSTANCE, this.getEnergyCapacity());
    private final @NotNull MachineItemInv itemInv = this.createInventory(MachineItemInv.Builder.create()).build();

    private final @NotNull MachineFluidInv fluidInv = this.createFluidInv(MachineFluidInv.Builder.create(this.fluidInvCapacity())).build();

    private final @NotNull CapacitorView capacitorView = this.capacitor.createView();

    private final @NotNull FixedFluidInvView fluidInvView = this.fluidInv().getFixedView();
    private final @NotNull FixedItemInvView invView = this.itemInv().getFixedView();

    private final InventoryFixedWrapper wrappedInventory = new InventoryFixedWrapper(this.itemInv()) {
        @Override
        public boolean canPlayerUse(PlayerEntity player) {
            return MachineBlockEntity.this.security().hasAccess(player);
        }
    };

    public MachineBlockEntity(BlockEntityType<? extends MachineBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
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

    public FluidAmount fluidInvCapacity() {
        return FluidAmount.ZERO;
    }

    public void setRedstone(@NotNull RedstoneInteractionType redstone) {
        this.configuration.setRedstone(redstone);
    }

    public @NotNull MachineStatus getStatus() {
        return this.configuration.getStatus();
    }

    public void setStatus(MachineStatus status) {
        assert this.world != null;
        if (!this.world.isClient()) this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(MachineBlock.ACTIVE, status.getType().isActive()));
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
     * @return The {@link ItemFilter} for the given slot of {@link #itemInv()}.
     */
    public final ItemFilter getFilterForSlot(int slot) {
        return this.itemInv().getFilterForSlot(slot);
    }

    /**
     * @return The maximum amount of energy that can be transferred to or from a battery in this machine per call to
     * {@link #attemptChargeFromStack(int)} or {@link #attemptDrainPowerToStack(int)}
     */
    protected int getBatteryTransferRate() {
        return 500;
    }

    public final @NotNull SimpleCapacitor capacitor() {
        return this.capacitor;
    }

    public final @NotNull MachineItemInv itemInv() {
        return this.itemInv;
    }

    public final @NotNull MachineFluidInv fluidInv() {
        return this.fluidInv;
    }

    public @NotNull CapacitorView capacitorView() {
        return capacitorView;
    }

    public @NotNull FixedFluidInvView fluidInvView() {
        return fluidInvView;
    }

    public @NotNull FixedItemInvView itemInvView() {
        return invView;
    }

    public final @Nullable EnergyExtractable getEnergyExtractable(@NotNull BlockState state, @Nullable Direction direction) {
        if (direction != null) {
            ConfiguredMachineFace cso = this.getIOConfig().get(BlockFace.toFace(state.get(Properties.HORIZONTAL_FACING), direction.getOpposite()));
            if (cso.getAutomationType().isEnergy()) {
                if (cso.getAutomationType().isOutput()) {
                    return this.capacitor().getPureExtractable();
                }
            }
            return null;
        }
        return this.capacitor().getPureExtractable();
    }

    public final @Nullable EnergyInsertable getEnergyInsertable(@NotNull BlockState state, @Nullable Direction direction) {
        if (direction != null) {
            ConfiguredMachineFace cso = this.getIOConfig().get(BlockFace.toFace(state.get(Properties.HORIZONTAL_FACING), direction.getOpposite()));
            if (cso.getAutomationType().isEnergy()) {
                if (cso.getAutomationType().isInput()) {
                    return this.capacitor().getPureInsertable();
                }
            }
            return null;
        }
        return this.capacitor().getPureInsertable();
    }

    public final @Nullable ItemExtractable getItemExtractable(@NotNull BlockState state, @Nullable Direction direction) {
        if (direction != null) {
            ConfiguredMachineFace cso = this.getIOConfig().get(BlockFace.toFace(state.get(Properties.HORIZONTAL_FACING), direction.getOpposite()));
            if (cso.getAutomationType().isItem()) {
                if (cso.getAutomationType().isOutput()) {
                    return this.itemInv().getMappedInv(cso.getMatching(this.itemInv())).getExtractable().getPureExtractable();
                }
            }
            return null;
        }
        return this.itemInv().getExtractable().getPureExtractable();
    }

    public final @Nullable ItemInsertable getItemInsertable(@NotNull BlockState state, @Nullable Direction direction) {
        if (direction != null) {
            ConfiguredMachineFace cso = this.getIOConfig().get(BlockFace.toFace(state.get(Properties.HORIZONTAL_FACING), direction.getOpposite()));
            if (cso.getAutomationType().isItem()) {
                if (cso.getAutomationType().isInput()) {
                    return this.itemInv().getMappedInv(cso.getMatching(this.itemInv())).getInsertable().getPureInsertable();
                }
            }
            return null;
        }
        return this.itemInv().getInsertable().getPureInsertable();
    }

    public final @Nullable FluidInsertable getFluidInsertable(@NotNull BlockState state, @Nullable Direction direction) {
        if (direction != null) {
            ConfiguredMachineFace cso = this.getIOConfig().get(BlockFace.toFace(state.get(Properties.HORIZONTAL_FACING), direction.getOpposite()));
            if (cso.getAutomationType().isFluid()) {
                if (cso.getAutomationType().isInput()) {
                    return this.fluidInv().getMappedInv(cso.getMatching(this.fluidInv())).getInsertable().getPureInsertable();
                }
            }
            return null;
        }
        return this.fluidInv().getInsertable().getPureInsertable();
    }

    public final @Nullable FluidExtractable getFluidExtractable(@NotNull BlockState state, @Nullable Direction direction) {
        if (direction != null) {
            ConfiguredMachineFace cso = this.getIOConfig().get(BlockFace.toFace(state.get(Properties.HORIZONTAL_FACING), direction.getOpposite()));
            if (cso.getAutomationType().isFluid()) {
                if (cso.getAutomationType().isOutput()) {
                    return this.fluidInv().getMappedInv(cso.getMatching(this.fluidInv())).getExtractable().getPureExtractable();
                }
            }
            return null;
        }
        return this.fluidInv().getExtractable();
    }

    public final @NotNull SecurityInfo security() {
        return this.configuration.getSecurity();
    }

    public final @NotNull RedstoneInteractionType redstoneInteraction() {
        return this.configuration.getRedstoneInteraction();
    }

    public final @NotNull MachineIOConfig getIOConfig() {
        return this.configuration.getSideConfiguration();
    }

    protected ItemStack decrement(int slot, int amount) {
        return this.itemInv().extractStack(slot, ConstantItemFilter.ANYTHING, ItemStack.EMPTY, amount, Simulation.ACTION);
    }

    /**
     * Whether the current machine is enabled
     *
     * @return The state of the machine
     */
    public boolean disabled() {
        return switch (this.redstoneInteraction()) {
            case LOW -> this.getWorld().isReceivingRedstonePower(pos);
            case HIGH -> !this.getWorld().isReceivingRedstonePower(pos);
            default -> false;
        };
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (!world.isClient) {
            this.updateComponents();
            if (disabled()) {
                idleEnergyDecrement(true);
                return;
            }
            this.setStatus(this.updateStatus());
            this.tickWork();
            if (this.getStatus().getType().isActive()) {
                if (this.getBaseEnergyConsumption() > 0) {
                    this.capacitor().extract(getEnergyConsumption());
                } else if (this.getBaseEnergyGenerated() > 0) {
                    this.capacitor().insert(getEnergyGenerated());
                }
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
        return this.capacitor().getEnergy() >= this.getBaseEnergyConsumption();
    }

    public boolean isTankFull(int tank) {
        return this.fluidInv().getInvFluid(tank).amount().isGreaterThanOrEqual(this.fluidInv().getMaxAmount_F(tank));
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
            stack = this.itemInv().insertStack(slot, stack, Simulation.SIMULATE);
            if (stack.isEmpty()) return true;
        }
        return stack.isEmpty();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        if (this.getEnergyCapacity() > 0) this.capacitor().toTag(tag);
        if (this.itemInv().getSlotCount()> 0) this.itemInv().toTag(tag);
        if (this.fluidInv().getTankCount() > 0) this.fluidInv().toTag(tag);
        this.configuration.toTag(tag);
        tag.putBoolean(Constant.Nbt.NO_DROP, this.noDrop);
        return tag;
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        if (this.getEnergyCapacity() > 0) this.capacitor().fromTag(tag);
        if (this.itemInv().getSlotCount() > 0) this.itemInv().fromTag(tag);
        if (this.fluidInv().getTankCount() > 0) this.fluidInv().fromTag(tag);
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
    public void fromClientTag(NbtCompound tag) {
        this.getIOConfig().fromTag(tag);
        ((WorldRendererAccessor) MinecraftClient.getInstance().worldRenderer).addChunkToRebuild(pos);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        this.getIOConfig().toTag(tag);
        return tag;
    }

    public boolean canInsert(int slot, ItemStack stack) {
        return this.itemInv().insertStack(slot, stack, Simulation.SIMULATE).isEmpty();
    }

    public void insert(int slot, ItemStack stack) {
        if (this.canInsert(slot, stack)) {
            this.itemInv().insertStack(slot, stack, Simulation.ACTION);
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
                        this.capacitor().insert(insertable.attemptInsertion(DefaultEnergyType.INSTANCE, this.capacitor().extract(2048), Simulation.ACTION));
                    }
                }
            }
        }
    }

    public void trySpreadFluids(int tank) {
        if (this.fluidInv().getTypes()[tank].getType().isOutput() && !this.fluidInv().getInvFluid(tank).isEmpty()) {
            for (BlockFace face : Constant.Misc.BLOCK_FACES) {
                ConfiguredMachineFace option = this.getIOConfig().get(face);
                if (option.getAutomationType().isFluid() && option.getAutomationType().isOutput()) {
                    Direction dir = face.toDirection(this.getCachedState().get(Properties.HORIZONTAL_FACING));
                    FluidInsertable insertable = FluidAttributes.INSERTABLE.getFromNeighbour(this, dir);
                    this.fluidInv().insertFluid(tank, insertable.attemptInsertion(this.fluidInv().extractFluid(tank, ConstantFluidFilter.ANYTHING, null, FluidAmount.ONE, Simulation.ACTION), Simulation.ACTION), Simulation.ACTION);
                }
            }
        }
    }

    public void idleEnergyDecrement(boolean off) {
        if (this.world.random.nextInt(off ? 40 : 20) == 1) {
            if (this.getBaseEnergyConsumption() > 0) {
                this.capacitor().extract(this.getBaseEnergyConsumption() / 20);
            }
        }
    }

    /**
     * Tries to charge this machine from the item in the given slot in this {@link #itemInv}.
     */
    protected void attemptChargeFromStack(int slot) {
        if (this.capacitor().getEnergy() >= this.capacitor().getMaxCapacity()) return;

        Reference<ItemStack> stack = this.itemInv().getSlot(slot);
        int neededEnergy = Math.min(this.getBatteryTransferRate(), this.capacitor().getMaxCapacity() - this.capacitor().getEnergy());
        if (EnergyUtil.isEnergyExtractable(stack)) {
            this.capacitor().insert(EnergyUtil.extractEnergy(stack, neededEnergy));
        }
    }

    /**
     * Tries to drain some of this machine's power into the item in the given slot in this {@link #itemInv}.
     *
     * @param slot The slot id of the item
     */
    protected void attemptDrainPowerToStack(int slot) {
        int available = Math.min(this.getBatteryTransferRate(), this.capacitor().getEnergy());
        if (available <= 0) {
            return;
        }
        Reference<ItemStack> stack = this.itemInv().getSlot(slot);
        if (EnergyUtil.isEnergyInsertable(stack)) {
            this.capacitor().insert(EnergyUtil.insert(stack, this.capacitor().extract(available)));
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
        return this.getCachedState().getBlock().getName().copy().setStyle(Constant.Text.DARK_GRAY_STYLE);
    }

    @Override
    public void addAllAttributes(AttributeList<?> to) {
        Direction direction = to.getSearchDirection();
        BlockState state = this.getCachedState();
        MachineBlockEntity machine = (MachineBlockEntity) world.getBlockEntity(pos);
        assert machine != null;
        if (direction == null) {
            if (this.fluidInv().getTankCount() != 0) to.offer(machine.fluidInv());
            if (this.itemInv().getSlotCount() != 0) to.offer(machine.itemInv()); //expose everything if not given a direction
            if (this.getEnergyCapacity() > 0) to.offer(machine.capacitor());
        } else {
            if (this.fluidInv().getTankCount() != 0) to.offer(machine.fluidInvView());
            if (this.itemInv().getSlotCount() != 0) to.offer(machine.itemInvView());
            if (this.getEnergyCapacity() > 0) to.offer(machine.capacitorView());
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
