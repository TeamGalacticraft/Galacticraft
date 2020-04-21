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

package com.hrznstudio.galacticraft.blocks.machines;

import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import net.fabricmc.fabric.api.container.ContainerFactory;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.Container;
import net.minecraft.container.Property;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public abstract class MachineContainer<T extends ConfigurableElectricMachineBlockEntity> extends Container {

    public final PlayerEntity playerEntity;
    public final T blockEntity;
    public final Property energy = Property.create();

    protected MachineContainer(int syncId, PlayerEntity playerEntity, T blockEntity) {
        super(null, syncId);
        this.playerEntity = playerEntity;
        this.blockEntity = blockEntity;
        addProperty(energy);
    }

    public static <T extends ConfigurableElectricMachineBlockEntity> ContainerFactory<Container> createFactory(
            Class<T> machineClass, MachineContainerConstructor<? extends Container, T> constructor) {
        return (syncId, id, player, buffer) -> {
            BlockPos pos = buffer.readBlockPos();
            BlockEntity be = player.world.getBlockEntity(pos);
            if (machineClass.isInstance(be)) {
                return constructor.create(syncId, player, machineClass.cast(be));
            } else {
                return null;
            }
        };
    }

    @Override
    public void sendContentUpdates() {
        energy.set(blockEntity.getEnergyAttribute().getCurrentEnergy());
        super.sendContentUpdates();
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return blockEntity.getSecurity().getPublicity() == ConfigurableElectricMachineBlockEntity.SecurityInfo.Publicity.PUBLIC
                || !blockEntity.getSecurity().hasOwner()
                || blockEntity.getSecurity().getOwner().equals(player.getUuid())
                || (blockEntity.getSecurity().hasTeam() && blockEntity.getSecurity().getPublicity() == ConfigurableElectricMachineBlockEntity.SecurityInfo.Publicity.SPACE_RACE && false
//        && blockEntity.getSecurity().getTeam() == player
        );
    }

    public int getMaxEnergy() {
        return blockEntity.getMaxEnergy();
    }

    @FunctionalInterface
    public interface MachineContainerConstructor<C, T> {
        C create(int syncId, PlayerEntity player, T blockEntity);
    }
}
