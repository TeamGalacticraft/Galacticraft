/*
 * Copyright (c) 2019-2025 Team Galacticraft
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
import dev.galacticraft.mod.content.block.entity.machine.FoodCannerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class FoodCannerMenu extends MachineMenu<FoodCannerBlockEntity> {
    private int progress;

    private boolean firstRowConsumed;
    private boolean secondRowConsumed;
    private boolean thirdRowConsumed;
    private boolean forthRowConsumed;

    public FoodCannerMenu(int syncId, @NotNull ServerPlayer player, @NotNull FoodCannerBlockEntity machine) {
        super(GCMenuTypes.FOOD_CANNER, syncId, player, machine);
    }

    public FoodCannerMenu(int syncId, Inventory inventory, BlockPos pos) {
        super(GCMenuTypes.FOOD_CANNER, syncId, inventory, pos, 8, 89);
    }

    @Override
    public void registerData(@NotNull MenuData data) {
        super.registerData(data);

        data.registerInt(this.be::getProgress, this::setProgress);
        data.registerBoolean(this.be::getFirstRowConsumed, this::setFirstRowConsumed);
        data.registerBoolean(this.be::getSecondRowConsumed, this::setSecondRowConsumed);
        data.registerBoolean(this.be::getThirdRowConsumed, this::setThirdRowConsumed);
        data.registerBoolean(this.be::getForthRowConsumed, this::setForthRowConsumed);

    }


    public int getProgress() {
        return progress;
    }

    public void setProgress(int value) {
        this.progress = value;
    }

    public boolean getFirstRowConsumed() {
        return firstRowConsumed;
    }

    public void setFirstRowConsumed(boolean value) {
        this.firstRowConsumed = value;
    }

    public boolean getSecondRowConsumed() {
        return secondRowConsumed;
    }

    public void setSecondRowConsumed(boolean value) {
        this.secondRowConsumed = value;
    }

    public boolean getThirdRowConsumed() {
        return thirdRowConsumed;
    }

    public void setThirdRowConsumed(boolean value) {
        this.thirdRowConsumed = value;
    }

    public boolean getForthRowConsumed() {
        return forthRowConsumed;
    }

    public void setForthRowConsumed(boolean value) {
        this.forthRowConsumed = value;
    }

}
