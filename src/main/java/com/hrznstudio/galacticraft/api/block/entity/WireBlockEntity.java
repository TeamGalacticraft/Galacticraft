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

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.AttributeProviderBlockEntity;
import alexiil.mc.lib.attributes.Simulation;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.wire.Wire;
import com.hrznstudio.galacticraft.api.wire.WireConnectionType;
import com.hrznstudio.galacticraft.api.wire.WireNetwork;
import com.hrznstudio.galacticraft.energy.api.EnergyExtractable;
import com.hrznstudio.galacticraft.energy.api.EnergyInsertable;
import com.hrznstudio.galacticraft.energy.impl.DefaultEnergyType;
import com.hrznstudio.galacticraft.energy.impl.EmptyEnergyExtractable;
import com.hrznstudio.galacticraft.energy.impl.RejectingEnergyInsertable;
import com.hrznstudio.galacticraft.energy.impl.SimpleCapacitor;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.util.EnergyUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class WireBlockEntity extends BlockEntity implements Wire, AttributeProviderBlockEntity {
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
        EnergyInsertable insertable = EnergyUtils.getEnergyInsertable(world, entity.getPos(), direction.getOpposite());
        EnergyExtractable extractable = EnergyUtils.getEnergyExtractable(world, entity.getPos(), direction.getOpposite());
        if (insertable != RejectingEnergyInsertable.NULL && extractable != EmptyEnergyExtractable.NULL) {
            return WireConnectionType.ENERGY_IO;
        } else if (insertable != RejectingEnergyInsertable.NULL) {
            return WireConnectionType.ENERGY_INPUT;
        } else if (extractable != EmptyEnergyExtractable.NULL) {
            return WireConnectionType.ENERGY_OUTPUT;
        }
        return WireConnectionType.NONE;
    }

    @Override
    public int getMaxTransferRate() {
        return MAX_TRANSFER_RATE;
    }

    @Override
    public void addAllAttributes(AttributeList<?> attributeList) {
        attributeList.offer(new SimpleCapacitor(DefaultEnergyType.INSTANCE, getMaxTransferRate()) {
            @Override
            public int insert(int amount, Simulation simulation) {
                return WireBlockEntity.this.getNetwork().insert(WireBlockEntity.this.getPos(), null, amount, simulation);
            }

            @Override
            public int getEnergy() {
                return 0;
            }
        });
    }
}
