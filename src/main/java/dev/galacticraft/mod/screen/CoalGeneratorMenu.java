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

import dev.galacticraft.machinelib.api.menu.MachineMenu;
import dev.galacticraft.machinelib.api.menu.MenuData;
import dev.galacticraft.mod.content.block.entity.machine.CoalGeneratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class CoalGeneratorMenu extends MachineMenu<CoalGeneratorBlockEntity> {
    private int fuelTime;
    private int fuelLength;

    public CoalGeneratorMenu(int syncId, @NotNull ServerPlayer player, @NotNull CoalGeneratorBlockEntity machine) {
        super(GCMenuTypes.COAL_GENERATOR, syncId, player, machine);
    }

    public CoalGeneratorMenu(int syncId, @NotNull Inventory inventory, @NotNull BlockPos pos) {
        super(GCMenuTypes.COAL_GENERATOR, syncId, inventory, pos, 8, 84);
    }

    @Override
    public void registerData(@NotNull MenuData data) {
        super.registerData(data);
        data.registerInt(this.be::getFuelTime, this::setFuelTime);
        data.registerInt(this.be::getFuelLength, this::setFuelLength);
    }

    public int getFuelTime() {
        return fuelTime;
    }

    public int getFuelLength() {
        return fuelLength;
    }

    public void setFuelTime(int fuelTime) {
        this.fuelTime = fuelTime;
    }

    public void setFuelLength(int fuelLength) {
        this.fuelLength = fuelLength;
    }
}
