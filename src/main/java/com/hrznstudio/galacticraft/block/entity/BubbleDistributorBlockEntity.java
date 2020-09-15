/*
 * Copyright (c) 2019 HRZN LTD
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

import com.google.common.collect.Lists;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.BubbleEntity;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.entity.GalacticraftEntityTypes;
import com.hrznstudio.galacticraft.fluids.GalacticraftFluids;
import com.hrznstudio.galacticraft.tag.GalacticraftTags;
import io.github.cottonmc.component.UniversalComponents;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.fluid.TankComponent;
import io.github.cottonmc.component.fluid.impl.SimpleTankComponent;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction;
import io.netty.buffer.Unpooled;
import nerdhub.cardinal.components.api.component.ComponentProvider;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class BubbleDistributorBlockEntity extends ConfigurableMachineBlockEntity implements Tickable {
    public static final Fraction MAX_OXYGEN = Fraction.of(1, 100).multiply(Fraction.ofWhole(5000));
    public static final int BATTERY_SLOT = 0;
    public BubbleDistributorStatus status = BubbleDistributorStatus.OFF;
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
    public int getOxygenTankSize() {
        return 1;
    }

    @Override
    public Fraction getOxygenTankMaxCapacity() {
        return MAX_OXYGEN;
    }

    @Override
    public List<SideOption> validSideOptions() {
        return Lists.asList(SideOption.DEFAULT, SideOption.POWER_INPUT, new SideOption[]{SideOption.ITEM_INPUT, SideOption.ITEM_OUTPUT});
    }

    @Override
    public int getFluidTankSize() {
        return 0;
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
            return GalacticraftEnergy::isOxygenItem;
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

        if (this.getCapacitor().getCurrentEnergy() > 0 && this.getOxygenTank().getContents(0).getAmount().doubleValue() > 0) {
            this.status = BubbleDistributorStatus.DISTRIBUTING;
        } else {
            this.status = BubbleDistributorStatus.OFF;
        }

        if (this.status == BubbleDistributorStatus.OFF) {
            idleEnergyDecrement(false);
            if (size > 0) {
                setSize(size - 0.2D);
            }

            if (size < 0) {
                setSize(0);
            }
        }

        if (status == BubbleDistributorStatus.DISTRIBUTING) {
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

            Fraction amount = this.getOxygenTank().takeFluid(0, Fraction.ofWhole((int) ((1.3333333333D * Math.PI * (size * size * size)) / 2D)), ActionType.PERFORM).getAmount();
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
        if (size == 0 && status != BubbleDistributorStatus.DISTRIBUTING) {
            if (this.bubbleId != -1) {
                world.getEntityById(bubbleId).remove();
                this.bubbleId = -1;
            }
        }

        if (prevSize != size || players != world.getPlayers().size()) {
            for (ServerPlayerEntity player : ((ServerWorld) world).getPlayers()) {
                player.networkHandler.sendPacket(new CustomPayloadS2CPacket(new Identifier(Constants.MOD_ID, "bubble_size"), new PacketByteBuf(new PacketByteBuf(Unpooled.buffer()).writeBlockPos(this.pos).writeDouble(this.size))));
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
    public boolean canExtractOxygen(int tank) {
        return false;
    }

    @Override
    public boolean canInsertOxygen(int tank) {
        return true;
    }

    @Override
    public boolean canExtractFluid(int tank) {
        return false;
    }

    @Override
    public boolean canInsertFluid(int tank) {
        return false;
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
        if (this.getOxygenTank().getContents(0).getAmount().compareTo(this.getOxygenTank().getMaxCapacity(0)) >= 0) {
            return;
        }
        ItemStack stack = getInventory().getStack(slot).copy();
        if (GalacticraftEnergy.isOxygenItem(stack)) {
            TankComponent component = ComponentProvider.fromItemStack(stack).getComponent(UniversalComponents.TANK_COMPONENT);
            for (int i = 0; i < component.getTanks(); i++) {
                if (component.getContents(i).getFluid().equals(GalacticraftFluids.OXYGEN)) {
                    this.getOxygenTank().insertFluid(component.takeFluid(i, this.getOxygenTank().getMaxCapacity(0).subtract(this.getOxygenTank().getContents(0).getAmount()), ActionType.PERFORM), ActionType.PERFORM);
                }
            }
        }
    }

    /**
     * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
     */
    public enum BubbleDistributorStatus {
        OFF(Formatting.RED),
        NOT_ENOUGH_POWER(Formatting.RED),
        DISTRIBUTING(Formatting.GREEN),
        NOT_ENOUGH_OXYGEN(Formatting.AQUA);

        private final Formatting textColor;

        BubbleDistributorStatus(Formatting color) {
            this.textColor = color;
        }

        public static BubbleDistributorStatus get(int index) {
            if (index < 0) return BubbleDistributorStatus.values()[0];
            return BubbleDistributorStatus.values()[index % BubbleDistributorStatus.values().length];
        }

        public Formatting getColor() {
            return textColor;
        }
    }
}