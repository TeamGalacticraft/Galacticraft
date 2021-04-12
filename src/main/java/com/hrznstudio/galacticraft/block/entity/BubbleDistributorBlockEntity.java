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

package com.hrznstudio.galacticraft.block.entity;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.FluidExtractable;
import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.item.filter.ConstantItemFilter;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import com.google.common.collect.ImmutableList;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.entity.BubbleEntity;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.entity.GalacticraftEntityTypes;
import com.hrznstudio.galacticraft.fluids.GalacticraftFluids;
import com.hrznstudio.galacticraft.screen.BubbleDistributorScreenHandler;
import com.hrznstudio.galacticraft.util.EnergyUtils;
import com.hrznstudio.galacticraft.util.FluidUtils;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class BubbleDistributorBlockEntity extends ConfigurableMachineBlockEntity implements TickableBlockEntity {
    public static final FluidAmount MAX_OXYGEN = FluidAmount.ofWhole(50);
    public static final int BATTERY_SLOT = 0;
    public static final int OXYGEN_TANK_SLOT = 1;
    public boolean bubbleVisible = true;
    private double size = 0;
    private byte targetSize = 1;
    private int players = 0;
    private int bubbleId = -1;
    private double prevSize;

    public BubbleDistributorBlockEntity() {
        super(GalacticraftBlockEntities.BUBBLE_DISTRIBUTOR_TYPE);
    }

    @Override
    public int getInventorySize() {
        return 2;
    }

    @Override
    public List<SideOption> validSideOptions() {
        return ImmutableList.of(SideOption.DEFAULT, SideOption.POWER_INPUT, SideOption.FLUID_INPUT);
    }

    @Override
    protected MachineStatus getStatusById(int index) {
        return Status.values()[index];
    }

    @Override
    public int getFluidTankSize() {
        return 1;
    }

    @Override
    public FluidAmount getFluidTankCapacity() {
        return MAX_OXYGEN;
    }

    @Override
    public boolean canInsertEnergy() {
        return true;
    }

    @Override
    public ItemFilter getFilterForSlot(int slot) {
        if (slot == BATTERY_SLOT) {
            return EnergyUtils.IS_EXTRACTABLE;
        } else if (slot == OXYGEN_TANK_SLOT) {
            return stack -> FluidUtils.canExtractFluids(stack, GalacticraftFluids.LIQUID_OXYGEN);
        }
        return ConstantItemFilter.NOTHING;
    }

    @Override
    public void updateComponents() {
        super.updateComponents();
        this.attemptChargeFromStack(BATTERY_SLOT);
        this.drainOxygenFromStack(1);
    }

    @Override
    public @NotNull MachineStatus updateStatus() {
        if (!this.hasEnergyToWork()) return Status.NOT_ENOUGH_ENERGY;
        FluidAmount oxygenRequired = FluidAmount.ofWhole((int) ((1.3333333333D * Math.PI * (size * size * size)) / 2D) + 1);
        if (!this.getFluidTank().extractFluid(0, null, FluidVolumeUtil.EMPTY, oxygenRequired, Simulation.SIMULATE).getAmount_F().equals(oxygenRequired)) return Status.NOT_ENOUGH_OXYGEN;
        return Status.DISTRIBUTING;
    }

    @Override
    public void tickWork() {
        this.players = level.players().size();
        this.prevSize = this.size;

        if (this.size > this.targetSize) {
            this.setSize(Math.max(size - 0.1F, targetSize));
        }
        if (size > 0.0D && bubbleVisible && bubbleId == -1 && (level instanceof ServerLevel)) {
            BubbleEntity entity = new BubbleEntity(GalacticraftEntityTypes.BUBBLE, level);
            entity.setPosRaw(this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ());
            entity.xo = this.getBlockPos().getX();
            entity.yo = this.getBlockPos().getY();
            entity.zo = this.getBlockPos().getZ();
            level.addFreshEntity(entity);
            bubbleId = entity.getId();
            for (ServerPlayer player : ((ServerLevel) level).players()) {
                player.connection.send(entity.getAddEntityPacket());
            }
        }
        if (this.getStatus().getType().isActive()) {
            this.getFluidTank().extractFluid(0, null, FluidVolumeUtil.EMPTY, FluidAmount.ofWhole((int) ((1.3333333333D * Math.PI * (size * size * size)) / 2D)), Simulation.ACTION);
            if (!level.isClientSide()) {
                if (size < targetSize) {
                    setSize(size + 0.05D);
                }
            }
        } else {
            if (this.bubbleId != -1 && size <= 0) {
                level.getEntity(bubbleId).remove();
                this.bubbleId = -1;
            }

            if (size > 0) {
                this.setSize(size - 0.2D);
            }

            if (size < 0) {
                this.setSize(0);
            }
        }
        if (prevSize != size || players != level.players().size()) {
            for (ServerPlayer player : ((ServerLevel) level).players()) {
                ServerPlayNetworking.send(player, new ResourceLocation(Constants.MOD_ID, "bubble_size"), new FriendlyByteBuf(new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(this.worldPosition).writeDouble(this.size)));
            }
        }
    }

    public byte getTargetSize() {
        return targetSize;
    }

    public void setTargetSize(byte targetSize) {
        this.targetSize = targetSize;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        super.save(tag);
        tag.putByte("MaxSize", targetSize);
        tag.putDouble("Size", size);
        return tag;
    }

    @Override
    public void load(BlockState state, CompoundTag tag) {
        super.load(state, tag);
        this.size = tag.getDouble("Size");
        if (size < 0) size = 0;
        this.targetSize = tag.getByte("MaxSize");
        if (targetSize < 1) targetSize = 1;
    }

    @Override
    public int getBaseEnergyConsumption() {
        return Galacticraft.configManager.get().oxygenCollectorEnergyConsumptionRate();
    }

    @Override
    public boolean canPipeInsertFluid(int tank) {
        return true;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    @Override
    public FluidFilter getFilterForTank(int tank) {
        return Constants.Misc.LOX_ONLY;
    }

    protected void drainOxygenFromStack(int slot) {
        if (this.getFluidTank().getInvFluid(0).getAmount_F().compareTo(this.getFluidTank().getMaxAmount_F(0)) >= 0) {
            return;
        }
        if (FluidUtils.canExtractFluids(this.getInventory().getSlot(slot))) {
            FluidExtractable extractable = FluidAttributes.EXTRACTABLE.get(this.getInventory().getSlot(slot));
            this.getFluidTank().insertFluid(0, extractable.attemptExtraction(Constants.Misc.LOX_ONLY, this.getFluidTank().getMaxAmount_F(0).sub(this.getFluidTank().getInvFluid(0).getAmount_F()), Simulation.ACTION), Simulation.ACTION);
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        if (this.getSecurity().hasAccess(player)) return new BubbleDistributorScreenHandler(syncId, player, this);
        return null;
    }

    /**
     * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
     */
    private enum Status implements MachineStatus {
        NOT_ENOUGH_ENERGY(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.not_enough_energy"), ChatFormatting.RED, StatusType.MISSING_ENERGY),
        DISTRIBUTING(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.distributing"), ChatFormatting.GREEN, StatusType.WORKING),
        NOT_ENOUGH_OXYGEN(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.not_enough_oxygen"), ChatFormatting.AQUA, StatusType.MISSING_FLUIDS);

        private final Component text;
        private final StatusType type;

        Status(MutableComponent text, ChatFormatting color, StatusType type) {
            this.text = text.setStyle(Style.EMPTY.withColor(color));
            this.type = type;
        }

        public static Status get(int index) {
            if (index < 0) return Status.values()[0];
            return Status.values()[index % Status.values().length];
        }

        @Override
        public @NotNull Component getName() {
            return text;
        }

        @Override
        public @NotNull StatusType getType() {
            return type;
        }

        @Override
        public int getIndex() {
            return ordinal();
        }
    }
}