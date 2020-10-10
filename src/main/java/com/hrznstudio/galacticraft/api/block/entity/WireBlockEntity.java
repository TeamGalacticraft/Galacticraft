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

package com.hrznstudio.galacticraft.api.block.entity;

import com.google.common.collect.ImmutableSet;
import com.hrznstudio.galacticraft.api.wire.WireNetwork;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import io.github.cottonmc.component.UniversalComponents;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.energy.CapacitorComponent;
import io.github.cottonmc.component.energy.impl.SimpleCapacitorComponent;
import io.github.cottonmc.component.energy.type.EnergyType;
import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.component.BlockComponentProvider;
import nerdhub.cardinal.components.api.component.Component;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class WireBlockEntity extends BlockEntity implements BlockEntityClientSerializable, BlockComponentProvider {
    private WireNetwork network;
    private static final int MAX_TRANSFER_RATE = 120;

    private final CapacitorComponent capacitor = new SimpleCapacitorComponent(MAX_TRANSFER_RATE * 2, GalacticraftEnergy.GALACTICRAFT_JOULES) {
        @Override
        public int getCurrentEnergy() {
            return MAX_TRANSFER_RATE;
        }

        @Override
        public int insertEnergy(EnergyType type, int amount, ActionType actionType) {
            if (getNetwork() != null && !getNetwork().isInvalid()) {
                return getNetwork().spreadEnergy(pos, amount, actionType);
            } else {
                return amount;
            }
        }

        @Override
        public int extractEnergy(EnergyType type, int amount, ActionType actionType) {
            return 0;
        }

        @Override
        public int generateEnergy(World world, BlockPos pos, int amount) {
            return amount;
        }

        @Override
        public List<Runnable> getListeners() {
            return Collections.emptyList();
        }

        @Override
        public void listen(@NotNull Runnable listener) {
        }

        @Override
        public SimpleCapacitorComponent setCurrentEnergy(int amount) {
            return this;
        }

        @Override
        public SimpleCapacitorComponent setMaxEnergy(int amount) {
            return this;
        }

        @Override
        public boolean canExtractEnergy() {
            return true;
        }

        @Override
        public boolean canInsertEnergy() {
            return true;
        }
    };

    public WireBlockEntity() {
        super(GalacticraftBlockEntities.WIRE_TYPE);
    }

    @Override
    public void setLocation(World world, BlockPos pos) {
        super.setLocation(world, pos);

        if (!world.isClient()) {
            if (this.network == null) {
                for (Direction direction : Direction.values()) {
                    BlockEntity entity = world.getBlockEntity(pos.offset(direction));
                    if (entity instanceof WireBlockEntity) {
                        if (((WireBlockEntity) entity).network != null && !((WireBlockEntity) entity).network.isInvalid()) {
                            ((WireBlockEntity) entity).network.addWire(pos, this);
                            break;
                        }
                    }
                }
                if (this.network == null) {
                    this.network = new WireNetwork(pos, (ServerWorld)world, this);
                }
            }
        }
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        fromTag(this.getCachedState(), compoundTag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        return this.toTag(compoundTag);
    }

    public void setNetwork(WireNetwork network) {
        this.network = network;
    }

    public WireNetwork getNetwork() {
        return network;
    }

    @Override
    public <T extends Component> boolean hasComponent(BlockView blockView, BlockPos pos, ComponentType<T> type, Direction side) {
        return type == UniversalComponents.CAPACITOR_COMPONENT;
    }

    @Override
    public <T extends Component> T getComponent(BlockView blockView, BlockPos pos, ComponentType<T> type, Direction side) {
        return type == UniversalComponents.CAPACITOR_COMPONENT ? (T) capacitor : null;
    }

    @Override
    public Set<ComponentType<?>> getComponentTypes(BlockView blockView, BlockPos pos, Direction side) {
        return ImmutableSet.of(UniversalComponents.CAPACITOR_COMPONENT);
    }
}
