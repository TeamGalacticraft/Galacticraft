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
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.entity.MachineBlockEntity;
import com.hrznstudio.galacticraft.api.machine.MachineStatus;
import com.hrznstudio.galacticraft.entity.BubbleEntity;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.entity.GalacticraftEntityTypes;
import com.hrznstudio.galacticraft.screen.BubbleDistributorScreenHandler;
import com.hrznstudio.galacticraft.screen.slot.SlotType;
import com.hrznstudio.galacticraft.util.EnergyUtils;
import com.hrznstudio.galacticraft.util.FluidUtils;
import com.hrznstudio.galacticraft.util.OxygenTankUtils;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class BubbleDistributorBlockEntity extends MachineBlockEntity implements Tickable {
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
        this.getInventory().addSlot(BATTERY_SLOT, SlotType.CHARGE, EnergyUtils.IS_EXTRACTABLE, 8, 62);
        this.getInventory().addSlot(OXYGEN_TANK_SLOT, SlotType.OXYGEN_TANK, OxygenTankUtils.OXYGEN_TANK_EXTRACTABLE, 31, 62);
        this.getFluidTank().addSlot(2, SlotType.OXYGEN_IN, Constants.Filter.LOX_ONLY);
    }

    @Override
    protected MachineStatus getStatusById(int index) {
        return Status.values()[index];
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
        this.players = world.getPlayers().size();
        this.prevSize = this.size;

        if (this.size > this.targetSize) {
            this.setSize(Math.max(size - 0.1F, targetSize));
        }
        if (size > 0.0D && bubbleVisible && bubbleId == -1 && (world instanceof ServerWorld)) {
            BubbleEntity entity = new BubbleEntity(GalacticraftEntityTypes.BUBBLE, world);
            entity.setPos(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
            entity.prevX = this.getPos().getX();
            entity.prevY = this.getPos().getY();
            entity.prevZ = this.getPos().getZ();
            world.spawnEntity(entity);
            bubbleId = entity.getEntityId();
            for (ServerPlayerEntity player : ((ServerWorld) world).getPlayers()) {
                player.networkHandler.sendPacket(entity.createSpawnPacket());
            }
        }
        if (this.getStatus().getType().isActive()) {
            this.getFluidTank().extractFluid(0, null, FluidVolumeUtil.EMPTY, FluidAmount.ofWhole((int) ((1.3333333333D * Math.PI * (size * size * size)) / 2D)), Simulation.ACTION);
            if (!world.isClient()) {
                if (size < targetSize) {
                    setSize(size + 0.05D);
                }
            }
        } else {
            if (this.bubbleId != -1 && size <= 0) {
                world.getEntityById(bubbleId).remove();
                this.bubbleId = -1;
            }

            if (size > 0) {
                this.setSize(size - 0.2D);
            }

            if (size < 0) {
                this.setSize(0);
            }
        }
        if (prevSize != size || players != world.getPlayers().size()) {
            for (ServerPlayerEntity player : ((ServerWorld) world).getPlayers()) {
                ServerPlayNetworking.send(player, new Identifier(Constants.MOD_ID, "bubble_size"), new PacketByteBuf(new PacketByteBuf(Unpooled.buffer()).writeBlockPos(this.pos).writeDouble(this.size)));
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
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putByte(Constants.Nbt.MAX_SIZE, targetSize);
        tag.putDouble(Constants.Nbt.SIZE, size);
        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.size = tag.getDouble(Constants.Nbt.SIZE);
        if (size < 0) size = 0;
        this.targetSize = tag.getByte(Constants.Nbt.MAX_SIZE);
        if (targetSize < 1) targetSize = 1;
    }

    @Override
    public int getBaseEnergyConsumption() {
        return Galacticraft.CONFIG_MANAGER.get().oxygenCollectorEnergyConsumptionRate();
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    protected void drainOxygenFromStack(int slot) {
        if (this.getFluidTank().getInvFluid(0).getAmount_F().compareTo(this.getFluidTank().getMaxAmount_F(0)) >= 0) {
            return;
        }
        if (FluidUtils.canExtractFluids(this.getInventory().getSlot(slot))) {
            FluidExtractable extractable = FluidAttributes.EXTRACTABLE.get(this.getInventory().getSlot(slot));
            this.getFluidTank().insertFluid(0, extractable.attemptExtraction(Constants.Filter.LOX_ONLY, this.getFluidTank().getMaxAmount_F(0).sub(this.getFluidTank().getInvFluid(0).getAmount_F()), Simulation.ACTION), Simulation.ACTION);
        }
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if (this.getSecurity().hasAccess(player)) return new BubbleDistributorScreenHandler(syncId, player, this);
        return null;
    }

    /**
     * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
     */
    private enum Status implements MachineStatus {
        NOT_ENOUGH_ENERGY(new TranslatableText("ui.galacticraft-rewoven.machine.status.not_enough_energy"), Formatting.RED, StatusType.MISSING_ENERGY),
        DISTRIBUTING(new TranslatableText("ui.galacticraft-rewoven.machine.status.distributing"), Formatting.GREEN, StatusType.WORKING),
        NOT_ENOUGH_OXYGEN(new TranslatableText("ui.galacticraft-rewoven.machine.status.not_enough_oxygen"), Formatting.AQUA, StatusType.MISSING_FLUIDS);

        private final Text text;
        private final StatusType type;

        Status(MutableText text, Formatting color, StatusType type) {
            this.text = text.setStyle(Style.EMPTY.withColor(color));
            this.type = type;
        }

        public static Status get(int index) {
            if (index < 0) return Status.values()[0];
            return Status.values()[index % Status.values().length];
        }

        @Override
        public @NotNull Text getName() {
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