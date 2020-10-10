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
import com.hrznstudio.galacticraft.api.wire.Wire;
import com.hrznstudio.galacticraft.api.wire.WireConnectionType;
import com.hrznstudio.galacticraft.api.wire.WireNetwork;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import io.github.cottonmc.component.UniversalComponents;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.energy.CapacitorComponent;
import io.github.cottonmc.component.energy.CapacitorComponentHelper;
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
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class WireBlockEntity extends BlockEntity implements BlockEntityClientSerializable, BlockComponentProvider, Wire {
    private WireNetwork network;
    private static final int MAX_TRANSFER_RATE = 120;

    private final CapacitorComponent capacitor = new SimpleCapacitorComponent(MAX_TRANSFER_RATE, GalacticraftEnergy.GALACTICRAFT_JOULES) {
        @Override
        public int getCurrentEnergy() {
            return 0;
        }

        @Override
        public int insertEnergy(EnergyType type, int amount, ActionType actionType) {
            if (type.isCompatibleWith(getPreferredType())) {
                return getNetwork().insertEnergy(pos, null, type, amount, actionType);
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
                    if (entity instanceof Wire) {
                        //noinspection ConstantConditions
                        if (((Wire) entity).getNetwork() != null) {
                            ((Wire) entity).getNetwork().addWire(pos, this);
                            break;
                        }
                    }
                }
                if (this.network == null) {
                    this.network = WireNetwork.create((ServerWorld)world);
                    this.network.addWire(pos, this);
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

    @Override
    public void setNetwork(WireNetwork network) {
        this.network = network;
    }

    @Override
    public @NotNull WireNetwork getNetwork() {
        return network;
    }

    @Override
    public @NotNull WireConnectionType getConnection(Direction direction, @Nullable BlockEntity entity) {
        if (entity == null || !canConnect(direction)) return WireConnectionType.NONE;
        if (entity instanceof Wire && ((Wire) entity).canConnect(direction.getOpposite())) return WireConnectionType.WIRE;
        CapacitorComponent component = CapacitorComponentHelper.INSTANCE.getComponent(world, entity.getPos(), direction.getOpposite());
        if (component != null) {
            if (component.canInsertEnergy() && component.canExtractEnergy()) {
                return WireConnectionType.ENERGY_IO;
            } else if (component.canInsertEnergy()) {
                return WireConnectionType.ENERGY_INPUT;
            } else if (component.canExtractEnergy()) {
                return WireConnectionType.ENERGY_OUTPUT;
            }
        }
        return WireConnectionType.NONE;
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
