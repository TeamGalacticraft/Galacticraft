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

package com.hrznstudio.galacticraft.blocks.machines.bubbledistributor;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import io.github.cottonmc.energy.api.EnergyAttribute;
import io.github.cottonmc.energy.impl.SimpleEnergyAttribute;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
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
    private byte size = 0;
    private byte maxSize = 0;
    private int players = 0;

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
    public void tick() {
        if (world.isClient || !enabled()) {
            if (!enabled()) {
                idleEnergyDecrement(true);
                if (size > 0) {
                    size--;
                }
            }
            return;
        }

        attemptChargeFromStack(BATTERY_SLOT);
        trySpreadEnergy();

        if (this.getEnergyAttribute().getCurrentEnergy() > 0 && this.oxygen.getCurrentEnergy() >= 17) {
            this.status = BubbleDistributorStatus.DISTRIBUTING;
        } else {
            this.status = BubbleDistributorStatus.OFF;
        }

        if (this.status == BubbleDistributorStatus.OFF) {
            idleEnergyDecrement(false);
            if (size > 0) {
                size--;
            }
        }
        status = BubbleDistributorStatus.DISTRIBUTING;
        if (status == BubbleDistributorStatus.DISTRIBUTING) {
            this.oxygen.extractEnergy(GalacticraftEnergy.GALACTICRAFT_OXYGEN, 20, Simulation.ACTION);
            if (world instanceof ServerWorld) {
                if (size < 660) {
                    size++;
                    for (ServerPlayerEntity player : ((ServerWorld) world).getPlayers()) {
                        player.networkHandler.sendPacket(new CustomPayloadS2CPacket(new Identifier(Constants.MOD_ID, "bubble_size"), new PacketByteBuf(new PacketByteBuf(Unpooled.buffer()).writeBlockPos(this.pos).writeByte(this.size))));
                    }
                } else if (players != world.getPlayers().size()) {
                    for (ServerPlayerEntity player : ((ServerWorld) world).getPlayers()) {
                        player.networkHandler.sendPacket(new CustomPayloadS2CPacket(new Identifier(Constants.MOD_ID, "bubble_size"), new PacketByteBuf(new PacketByteBuf(Unpooled.buffer()).writeBlockPos(this.pos).writeByte(this.size))));
                    }
                }
                players = world.getPlayers().size();
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

        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);

        this.oxygen.setCurrentEnergy(tag.getInt("Oxygen"));
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        this.fromTag(tag);
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

    public byte getSize() {
        return size;
    }

    public void setSize(byte size) {
        this.size = size;
    }
}