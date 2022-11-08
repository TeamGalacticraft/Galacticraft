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

import dev.galacticraft.machinelib.api.screen.MachineMenu;
import dev.galacticraft.mod.registries.block.entity.CoalGeneratorBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class CoalGeneratorScreenHandler extends MachineMenu<CoalGeneratorBlockEntity> {
    public final DataSlot fuelTime = new DataSlot() {
        @Override
        public int get() {
            return CoalGeneratorScreenHandler.this.machine.getFuelTime();
        }

        @Override
        public void set(int value) {
            CoalGeneratorScreenHandler.this.machine.setFuelTime(value);
        }
    };

    public final DataSlot fuelLength = new DataSlot() {
        @Override
        public int get() {
            return CoalGeneratorScreenHandler.this.machine.getFuelLength();
        }

        @Override
        public void set(int value) {
            CoalGeneratorScreenHandler.this.machine.setFuelLength(value);
        }
    };

    public CoalGeneratorScreenHandler(int syncId, Player player, CoalGeneratorBlockEntity machine) {
        super(syncId, player, machine, null);
        this.addDataSlot(this.fuelTime);
        this.addDataSlot(this.fuelLength);
        this.addPlayerInventorySlots(8, 84);
    }

    public CoalGeneratorScreenHandler(int syncId, Inventory inv, FriendlyByteBuf buf) {
        this(syncId, inv.player, (CoalGeneratorBlockEntity) inv.player.level.getBlockEntity(buf.readBlockPos()));
    }

    @Override
    public MenuType<?> getType() {
        return GCScreenHandlerType.COAL_GENERATOR_HANDLER;
    }
}
