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

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.BubbleEntity;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.entity.GalacticraftEntityTypes;
import com.hrznstudio.galacticraft.items.OxygenTankItem;
import io.github.cottonmc.energy.api.EnergyAttribute;
import io.github.cottonmc.energy.impl.SimpleEnergyAttribute;
import io.netty.buffer.Unpooled;
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
import team.reborn.energy.EnergySide;
import team.reborn.energy.EnergyStorage;
import team.reborn.energy.EnergyTier;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class BubbleDistributorBlockEntity extends ConfigurableElectricMachineBlockEntity implements Tickable, EnergyStorage {
    public static final int MAX_OXYGEN = 5000;
    public static final int BATTERY_SLOT = 0;
    private final SimpleEnergyAttribute oxygen = new SimpleEnergyAttribute(MAX_OXYGEN, GalacticraftEnergy.GALACTICRAFT_OXYGEN);
    public BubbleDistributorStatus status = BubbleDistributorStatus.OFF;
    public boolean bubbleVisible = true;
    private double size = 0;
    private byte maxSize = 1;
    private int players = 0;
    private int bubbleId = -1;

    public BubbleDistributorBlockEntity() {
        super(GalacticraftBlockEntities.BUBBLE_DISTRIBUTOR_TYPE);
    }

    @Override
    protected int getInvSize() {
        return 2;
    }

    @Override
    protected ItemFilter getFilterForSlot(int slot) {
        if (slot == 0) {
            return GalacticraftEnergy.ENERGY_HOLDER_ITEM_FILTER;
        } else if (slot == 1) {
            return GalacticraftEnergy::isOxygenItem;
        } else {
            return itemStack -> false;
        }
    }

    @Override
    public void markRemoved() {
        super.markRemoved();
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

        attemptChargeFromStack(BATTERY_SLOT);
        drainOxygenFromStack(1);
        trySpreadEnergy();

        if (this.getEnergyAttribute().getCurrentEnergy() > 0 && this.oxygen.getCurrentEnergy() >= 0) {
            this.status = BubbleDistributorStatus.DISTRIBUTING;
        } else {
            this.status = BubbleDistributorStatus.OFF;
        }

//        status = BubbleDistributorStatus.DISTRIBUTING;

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

            int a = this.oxygen.extractEnergy(GalacticraftEnergy.GALACTICRAFT_OXYGEN, (int) ((1.3333333333D * Math.PI * (size * size * size)) / 2D), Simulation.ACTION); //vos
//            if (a > 0) return;
            if (!world.isClient()) {
                if (size < maxSize) {
                    setSize(size + 0.05D);
                    for (ServerPlayerEntity player : ((ServerWorld) world).getPlayers()) {
                        player.networkHandler.sendPacket(new CustomPayloadS2CPacket(new Identifier(Constants.MOD_ID, "bubble_size"), new PacketByteBuf(new PacketByteBuf(Unpooled.buffer()).writeBlockPos(this.pos).writeDouble(this.size))));
                    }
                } else if (players != world.getPlayers().size()) {
                    for (ServerPlayerEntity player : ((ServerWorld) world).getPlayers()) {
                        player.networkHandler.sendPacket(new CustomPayloadS2CPacket(new Identifier(Constants.MOD_ID, "bubble_size"), new PacketByteBuf(new PacketByteBuf(Unpooled.buffer()).writeBlockPos(this.pos).writeDouble(this.size))));
                    }
                }
                players = world.getPlayers().size();
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
    }

    public byte getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(byte maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putInt("Oxygen", oxygen.getCurrentEnergy());
        tag.putByte("MaxSize", maxSize);
        tag.putDouble("Size", size);
        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.oxygen.setCurrentEnergy(tag.getInt("Oxygen"));
        this.size = tag.getDouble("Size");
        if (size < 0) size = 0;
        this.maxSize = tag.getByte("MaxSize");
        if (maxSize < 1) maxSize = 1;
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        this.fromTag(null, tag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        return this.toTag(tag);
    }

    public EnergyAttribute getOxygen() {
        return this.oxygen;
    }

    @Override
    public double getStored(EnergySide face) {
        return GalacticraftEnergy.convertToTR(this.getEnergyAttribute().getCurrentEnergy());
    }

    @Override
    public void setStored(double amount) {
        this.getEnergyAttribute().setCurrentEnergy(GalacticraftEnergy.convertFromTR(amount));
    }

    @Override
    public double getMaxStoredPower() {
        return GalacticraftEnergy.convertToTR(getEnergyAttribute().getMaxEnergy());
    }

    @Override
    public EnergyTier getTier() {
        return EnergyTier.MEDIUM;
    }

    @Override
    public int getEnergyUsagePerTick() {
        return Galacticraft.configManager.get().oxygenCollectorEnergyConsumptionRate();
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    protected void drainOxygenFromStack(int slot) {
        if (getEnergyAttribute().getCurrentEnergy() >= getEnergyAttribute().getMaxEnergy()) {
            return;
        }
        ItemStack stack = getInventory().getStack(slot).copy();
        if (GalacticraftEnergy.isOxygenItem(stack)) {
            int leftover = this.getOxygen().insertEnergy(GalacticraftEnergy.GALACTICRAFT_OXYGEN, stack.getTag().getInt(OxygenTankItem.OXYGEN_NBT_KEY), Simulation.ACTION);
            stack.getTag().putInt(OxygenTankItem.OXYGEN_NBT_KEY, leftover);
            getInventory().forceSetInvStack(slot, stack);
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