/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

import dev.galacticraft.machinelib.api.menu.RecipeMachineMenu;
import dev.galacticraft.machinelib.api.menu.sync.MenuSyncHandler;
import dev.galacticraft.mod.content.GCMachineTypes;
import dev.galacticraft.mod.content.block.entity.machine.CompressorBlockEntity;
import dev.galacticraft.mod.recipe.CompressingRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CraftingContainer;

import java.util.function.Consumer;

public class CompressorMenu extends RecipeMachineMenu<CraftingContainer, CompressingRecipe, CompressorBlockEntity> {
    private int fuelTime = 0;
    private int fuelLength = 0;

    public CompressorMenu(int syncId, ServerPlayer player, CompressorBlockEntity machine) {
        super(syncId, player, machine);
    }

    public CompressorMenu(int syncId, Inventory inv, FriendlyByteBuf buf) {
        super(syncId, inv, buf, 8, 84, GCMachineTypes.COMPRESSOR);
    }

    @Override
    public void registerSyncHandlers(Consumer<MenuSyncHandler> consumer) {
        super.registerSyncHandlers(consumer);
        consumer.accept(MenuSyncHandler.simple(this.machine::getFuelTime, this::setFuelTime));
        consumer.accept(MenuSyncHandler.simple(this.machine::getFuelLength, this::setFuelLength));
    }

    public int getFuelLength() {
        return this.fuelLength;
    }

    public int getFuelTime() {
        return this.fuelTime;
    }

    public void setFuelLength(int fuelLength) {
        this.fuelLength = fuelLength;
    }

    public void setFuelTime(int fuelTime) {
        this.fuelTime = fuelTime;
    }
}
