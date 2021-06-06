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

package dev.galacticraft.mod.screen.factory;

import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.api.screen.MachineScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class MachineScreenHandlerFactory<B extends MachineBlockEntity, T extends MachineScreenHandler<B>> implements ScreenHandlerRegistry.ExtendedClientHandlerFactory<T> {
    private final MachineScreenHandlerSupplier<B, T> supplier;

    public MachineScreenHandlerFactory(MachineScreenHandlerSupplier<B, T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T create(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        return this.create(syncId, inventory, buf.readBlockPos());
    }

    public T create(int syncId, PlayerInventory inventory, B machine) {
        return this.supplier.create(syncId, inventory.player, machine);
    }

    public T create(int syncId, PlayerInventory inventory, BlockPos pos) {
        return this.create(syncId, inventory, (B)inventory.player.world.getBlockEntity(pos));
    }

    @FunctionalInterface
    public interface MachineScreenHandlerSupplier<B extends MachineBlockEntity, T extends MachineScreenHandler<B>> {
        T create(int syncId, PlayerEntity player, B machine);
    }
}
