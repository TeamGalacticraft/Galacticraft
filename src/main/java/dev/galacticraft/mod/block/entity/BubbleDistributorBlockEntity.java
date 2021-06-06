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

package dev.galacticraft.mod.block.entity;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.FluidExtractable;
import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.api.machine.MachineStatus;
import dev.galacticraft.mod.attribute.fluid.MachineFluidInv;
import dev.galacticraft.mod.attribute.item.MachineItemInv;
import dev.galacticraft.mod.entity.BubbleEntity;
import dev.galacticraft.mod.entity.GalacticraftEntityType;
import dev.galacticraft.mod.screen.BubbleDistributorScreenHandler;
import dev.galacticraft.mod.screen.slot.SlotType;
import dev.galacticraft.mod.util.EnergyUtil;
import dev.galacticraft.mod.util.FluidUtil;
import dev.galacticraft.mod.util.OxygenTankUtil;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
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
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class BubbleDistributorBlockEntity extends MachineBlockEntity {
    public static final FluidAmount MAX_OXYGEN = FluidAmount.ofWhole(50);
    public static final int BATTERY_SLOT = 0;
    public static final int OXYGEN_TANK_SLOT = 1;
    public static final int OXYGEN_TANK = 0;
    public boolean bubbleVisible = true;
    private double size = 0;
    private byte targetSize = 1;
    private int players = 0;
    private int bubbleId = -1;
    private double prevSize;

    public BubbleDistributorBlockEntity(BlockPos pos, BlockState state) {
        super(GalacticraftBlockEntityType.BUBBLE_DISTRIBUTOR, pos, state);
    }

    @Override
    protected MachineItemInv.Builder createInventory(MachineItemInv.Builder builder) {
        builder.addSlot(BATTERY_SLOT, SlotType.CHARGE, EnergyUtil.IS_EXTRACTABLE, 8, 62);
        builder.addSlot(OXYGEN_TANK_SLOT, SlotType.OXYGEN_TANK, OxygenTankUtil.OXYGEN_TANK_EXTRACTABLE, 31, 62);
        return builder;
    }

    @Override
    protected MachineFluidInv.Builder createFluidInv(MachineFluidInv.Builder builder) {
        builder.addLOXTank(OXYGEN_TANK, SlotType.OXYGEN_IN, 31, 8);
        return builder;
    }

    @Override
    protected MachineStatus getStatusById(int index) {
        return Status.values()[index];
    }

    @Override
    public FluidAmount fluidInvCapacity() {
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
        if (!this.fluidInv().extractFluid(OXYGEN_TANK, null, FluidVolumeUtil.EMPTY, oxygenRequired, Simulation.SIMULATE).amount().equals(oxygenRequired)) return Status.NOT_ENOUGH_OXYGEN;
        return Status.DISTRIBUTING;
    }

    @Override
    public void tickWork() {
        this.players = world.getPlayers().size();
        this.prevSize = this.size;

        if (this.size > this.targetSize) {
            this.setSize(Math.max(size - 0.1F, targetSize));
        }
        if (size > 0.0D && bubbleVisible && bubbleId == -1 && world instanceof ServerWorld serverWorld) {
            BubbleEntity entity = new BubbleEntity(GalacticraftEntityType.BUBBLE, world);
            entity.setPos(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
            entity.prevX = this.getPos().getX();
            entity.prevY = this.getPos().getY();
            entity.prevZ = this.getPos().getZ();
            world.spawnEntity(entity);
            bubbleId = entity.getId();
            for (ServerPlayerEntity player : serverWorld.getPlayers()) {
                player.networkHandler.sendPacket(entity.createSpawnPacket());
            }
        }
        if (this.getStatus().getType().isActive()) {
            this.fluidInv().extractFluid(OXYGEN_TANK, null, FluidVolumeUtil.EMPTY, FluidAmount.ofWhole((int) ((1.3333333333D * Math.PI * (size * size * size)) / 2D)), Simulation.ACTION);
            if (!world.isClient()) {
                if (size < targetSize) {
                    setSize(size + 0.05D);
                }
            }
        } else {
            if (this.bubbleId != -1 && size <= 0) {
                world.getEntityById(bubbleId).remove(Entity.RemovalReason.DISCARDED);
                world.getEntityById(bubbleId).onRemoved();
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
                ServerPlayNetworking.send(player, new Identifier(Constant.MOD_ID, "bubble_size"), new PacketByteBuf(new PacketByteBuf(Unpooled.buffer()).writeBlockPos(this.pos).writeDouble(this.size)));
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
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putByte(Constant.Nbt.MAX_SIZE, targetSize);
        tag.putDouble(Constant.Nbt.SIZE, size);
        return tag;
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        this.size = tag.getDouble(Constant.Nbt.SIZE);
        if (size < 0) size = 0;
        this.targetSize = tag.getByte(Constant.Nbt.MAX_SIZE);
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
        if (this.fluidInv().getInvFluid(0).amount().compareTo(this.fluidInv().getMaxAmount_F(0)) >= 0) {
            return;
        }
        if (FluidUtil.canExtractFluids(this.itemInv().getSlot(slot))) {
            FluidExtractable extractable = FluidAttributes.EXTRACTABLE.get(this.itemInv().getSlot(slot));
            this.fluidInv().insertFluid(OXYGEN_TANK, extractable.attemptExtraction(Constant.Filter.LOX_ONLY, this.fluidInv().getMaxAmount_F(0).sub(this.fluidInv().getInvFluid(0).amount()), Simulation.ACTION), Simulation.ACTION);
        }
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if (this.security().hasAccess(player)) return new BubbleDistributorScreenHandler(syncId, player, this);
        return null;
    }

    /**
     * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
     */
    private enum Status implements MachineStatus {
        NOT_ENOUGH_ENERGY(new TranslatableText("ui.galacticraft.machine.status.not_enough_energy"), Formatting.RED, StatusType.MISSING_ENERGY),
        DISTRIBUTING(new TranslatableText("ui.galacticraft.machine.status.distributing"), Formatting.GREEN, StatusType.WORKING),
        NOT_ENOUGH_OXYGEN(new TranslatableText("ui.galacticraft.machine.status.not_enough_oxygen"), Formatting.AQUA, StatusType.MISSING_FLUIDS);

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