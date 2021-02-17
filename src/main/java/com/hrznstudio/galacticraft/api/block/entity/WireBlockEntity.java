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

import alexiil.mc.lib.attributes.Simulation;
import com.hrznstudio.galacticraft.api.wire.Wire;
import com.hrznstudio.galacticraft.api.wire.WireConnectionType;
import com.hrznstudio.galacticraft.api.wire.WireNetwork;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.util.EnergyUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.EnergyHandler;
import team.reborn.energy.EnergySide;
import team.reborn.energy.EnergyTier;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class WireBlockEntity extends BlockEntity implements Wire {
    private WireNetwork network;
    private static final int MAX_TRANSFER_RATE = 120;

    public WireBlockEntity() {
        super(GalacticraftBlockEntities.WIRE_TYPE);
    }

    @Override
    public void setNetwork(WireNetwork network) {
        this.network = network;
    }

    @Override
    public @NotNull WireNetwork getNetwork() {
        if (this.network == null) {
            if (!this.world.isClient()) {
                for (Direction direction : Direction.values()) {
                    BlockEntity entity = world.getBlockEntity(pos.offset(direction));
                    if (entity instanceof Wire) {
                        //noinspection ConstantConditions
                        if (((Wire) entity).getNetworkNullable() != null) {
                            ((Wire) entity).getNetworkNullable().addWire(pos, this);
                            break;
                        }
                    }
                }
                if (this.network == null) {
                    this.setNetwork(WireNetwork.create((ServerWorld) world));
                    this.network.addWire(pos, this);
                }
            }
        }
        return this.network;
    }

    @Override
    public @Nullable WireNetwork getNetworkNullable() {
        return this.network;
    }

    @Override
    public @NotNull WireConnectionType getConnection(Direction direction, @Nullable BlockEntity entity) {
        if (entity == null || !canConnect(direction)) return WireConnectionType.NONE;
        if (entity instanceof Wire && ((Wire) entity).canConnect(direction.getOpposite())) return WireConnectionType.WIRE;
        EnergyHandler handler = EnergyUtils.getEnergyHandler(world, entity.getPos(), direction.getOpposite());
        if (handler.getMaxInput() > 0 && handler.getMaxOutput() > 0) {
            return WireConnectionType.ENERGY_IO;
        } else if (handler.getMaxInput() > 0) {
            return WireConnectionType.ENERGY_INPUT;
        } else if (handler.getMaxOutput() > 0) {
            return WireConnectionType.ENERGY_OUTPUT;
        }
        return WireConnectionType.NONE;
    }

    @Override
    public int getMaxTransferRate() {
        return MAX_TRANSFER_RATE;
    }

    @Override
    public double getStored(EnergySide energySide) {
        return 128.0 - this.getNetwork().insert(this.getPos(), null, 128.0, Simulation.SIMULATE);
    }

    @Override
    public void setStored(double v) {
        assert this.getNetwork().insert(this.getPos(), null, v, Simulation.SIMULATE) == 0.0;
    }

    @Override
    public double getMaxStoredPower() {
        return 128;
    }

    @Override
    public EnergyTier getTier() {
        return EnergyTier.MEDIUM;
    }

    @Override
    public double getMaxInput(EnergySide side) {
        return 128;
    }

    @Override
    public double getMaxOutput(EnergySide side) {
        return 0;
    }
}
