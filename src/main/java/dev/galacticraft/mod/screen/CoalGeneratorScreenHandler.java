/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

package dev.galacticraft.mod.screen;

import dev.galacticraft.api.screen.MachineScreenHandler;
import dev.galacticraft.mod.block.entity.CoalGeneratorBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandlerType;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class CoalGeneratorScreenHandler extends MachineScreenHandler<CoalGeneratorBlockEntity> {
    public final Property fuelTime = new Property() {
        @Override
        public int get() {
            return CoalGeneratorScreenHandler.this.machine.fuelTime;
        }

        @Override
        public void set(int value) {
            CoalGeneratorScreenHandler.this.machine.fuelTime = value;
        }
    };

    public final Property fuelLength = new Property() {
        @Override
        public int get() {
            return CoalGeneratorScreenHandler.this.machine.fuelLength;
        }

        @Override
        public void set(int value) {
            CoalGeneratorScreenHandler.this.machine.fuelLength = value;
        }
    };

    public CoalGeneratorScreenHandler(int syncId, PlayerEntity player, CoalGeneratorBlockEntity machine) {
        super(syncId, player, machine, null);
        this.addProperty(this.fuelTime);
        this.addProperty(this.fuelLength);
        this.addPlayerInventorySlots(8, 84);
    }

    public CoalGeneratorScreenHandler(int syncId, PlayerInventory inv, PacketByteBuf buf) {
        this(syncId, inv.player, (CoalGeneratorBlockEntity) inv.player.world.getBlockEntity(buf.readBlockPos()));
    }

    @Override
    public ScreenHandlerType<?> getType() {
        return GalacticraftScreenHandlerType.COAL_GENERATOR_HANDLER;
    }
}
