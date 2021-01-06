/*
 * Copyright (c) 2020 HRZN LTD
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

import com.google.common.collect.ImmutableList;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.BubbleEntity;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.entity.GalacticraftEntityTypes;
import com.hrznstudio.galacticraft.fluids.GalacticraftFluids;
import com.hrznstudio.galacticraft.util.OxygenUtils;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.fluid.TankComponent;
import io.github.cottonmc.component.fluid.TankComponentHelper;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
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

import java.util.List;
import java.util.function.Predicate;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class BubbleDistributorBlockEntity extends ConfigurableMachineBlockEntity implements Tickable {
    public static final Fraction MAX_OXYGEN = Fraction.of(1, 100).multiply(Fraction.ofWhole(5000));
    public static final int BATTERY_SLOT = 0;
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
        return ImmutableList.of(SideOption.DEFAULT, SideOption.POWER_INPUT);
    }

    @Override
    protected MachineStatus getStatus(int index) {
        return Status.values()[index];
    }

    @Override
    public int getFluidTankSize() {
        return 1;
    }

    @Override
    public Fraction getFluidTankMaxCapacity() {
        return MAX_OXYGEN;
    }

    @Override
    public boolean canExtractEnergy() {
        return false;
    }

    @Override
    public boolean canInsertEnergy() {
        return true;
    }

    @Override
    public Predicate<ItemStack> getFilterForSlot(int slot) {
        if (slot == 0) {
            return GalacticraftEnergy.ENERGY_HOLDER_ITEM_FILTER;
        } else if (slot == 1) {
            return OxygenUtils::isOxygenItem;
        } else {
            return itemStack -> false;
        }
    }

    @Override
    public void tick() {
        if (world.isClient || disabled()) {
            if (disabled()) {
                idleEnergyDecrement(true);
                if (size > 0) {
                    setSize(size - 0.2D);
                }
            }
            return;
        }

        if (this.size > this.targetSize) {
            setSize(Math.max(size - 0.1F, targetSize));
        }

        attemptChargeFromStack(BATTERY_SLOT);
        drainOxygenFromStack(1);
        trySpreadEnergy();

        if (this.getCapacitor().getCurrentEnergy() > 0 && this.getFluidTank().getContents(0).getAmount().doubleValue() > 0) {
            this.setStatus(Status.DISTRIBUTING);
        } else {
            this.setStatus(Status.OFF);
        }

        if (getStatus() == Status.OFF) {
            idleEnergyDecrement(false);
            if (size > 0) {
                setSize(size - 0.2D);
            }

            if (size < 0) {
                setSize(0);
            }
        }

        if (getStatus() == Status.DISTRIBUTING) {
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

            Fraction amount = this.getFluidTank().takeFluid(0, Fraction.ofWhole((int) ((1.3333333333D * Math.PI * (size * size * size)) / 2D)), ActionType.PERFORM).getAmount();
            if (!world.isClient()) {
                if (size < targetSize) {
                    setSize(size + 0.05D);
                }
            }
        } else {
            if (size > 0) {
                setSize(size - 0.2D);
            }
        }
        if (size < 0) {
            setSize(0);
        }
        if (size == 0 && getStatus() != Status.DISTRIBUTING) {
            if (this.bubbleId != -1) {
                world.getEntityById(bubbleId).remove();
                this.bubbleId = -1;
            }
        }

        if (prevSize != size || players != world.getPlayers().size()) {
            for (ServerPlayerEntity player : ((ServerWorld) world).getPlayers()) {
                ServerPlayNetworking.send(player, new Identifier(Constants.MOD_ID, "bubble_size"), new PacketByteBuf(new PacketByteBuf(Unpooled.buffer()).writeBlockPos(this.pos).writeDouble(this.size)));
            }
        }
        this.players = world.getPlayers().size();
        this.prevSize = this.size;
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
        tag.putByte("MaxSize", targetSize);
        tag.putDouble("Size", size);
        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.size = tag.getDouble("Size");
        if (size < 0) size = 0;
        this.targetSize = tag.getByte("MaxSize");
        if (targetSize < 1) targetSize = 1;
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        this.fromTag(null, tag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        return this.toTag(tag);
    }

    @Override
    public int getEnergyUsagePerTick() {
        return Galacticraft.configManager.get().oxygenCollectorEnergyConsumptionRate();
    }

    @Override
    public boolean canHopperExtractItems(int slot) {
        return false;
    }

    @Override
    public boolean canHopperInsertItems(int slot) {
        return false;
    }

    @Override
    public boolean canExtractFluid(int tank) {
        return false;
    }

    @Override
    public boolean canInsertFluid(int tank) {
        return true;
    }

    @Override
    public boolean isAcceptableFluid(int tank, FluidVolume volume) {
        return false;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    protected void drainOxygenFromStack(int slot) {
        if (this.getFluidTank().getContents(0).getAmount().compareTo(this.getFluidTank().getMaxCapacity(0)) >= 0) {
            return;
        }
        ItemStack stack = getInventory().getStack(slot).copy();
        if (OxygenUtils.isOxygenItem(stack)) {
            TankComponent component = TankComponentHelper.INSTANCE.getComponent(stack, "oxy-drain-bubble");
            for (int i = 0; i < component.getTanks(); i++) {
                if (component.getContents(i).getFluid().equals(GalacticraftFluids.OXYGEN)) {
                    this.getFluidTank().insertFluid(component.takeFluid(i, this.getFluidTank().getMaxCapacity(0).subtract(this.getFluidTank().getContents(0).getAmount()), ActionType.PERFORM), ActionType.PERFORM);
                }
            }
        }
    }

    /**
     * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
     */
    private enum Status implements MachineStatus {
        OFF(new TranslatableText("ui.galacticraft-rewoven.machinestatus.off"), Formatting.RED, StatusType.OFF),
        NOT_ENOUGH_ENERGY(new TranslatableText("ui.galacticraft-rewoven.machinestatus.not_enough_energy"), Formatting.RED, StatusType.MISSING_ENERGY),
        DISTRIBUTING(new TranslatableText("ui.galacticraft-rewoven.machinestatus.distributing"), Formatting.GREEN, StatusType.WORKING),
        NOT_ENOUGH_OXYGEN(new TranslatableText("ui.galacticraft-rewoven.machinestatus.not_enough_oxygen"), Formatting.AQUA, StatusType.MISSING_FLUIDS);

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