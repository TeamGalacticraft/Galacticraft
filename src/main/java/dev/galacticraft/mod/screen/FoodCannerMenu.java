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
import dev.galacticraft.machinelib.api.menu.sync.MenuSyncHandler;
import dev.galacticraft.mod.content.GCMachineTypes;
import dev.galacticraft.mod.content.block.entity.machine.CoalGeneratorBlockEntity;
import dev.galacticraft.mod.content.block.entity.machine.FoodCannerBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class FoodCannerMenu extends MachineMenu<FoodCannerBlockEntity> {
    private int progress;

    private int firstRowConsumed;
    private int secondRowConsumed;
    private int thirdRowConsumed;
    private int forthRowConsumed;

    public FoodCannerMenu(int syncId, @NotNull ServerPlayer player, @NotNull FoodCannerBlockEntity machine) {
        super(syncId, player, machine);
    }

    public FoodCannerMenu(int syncId, @NotNull Inventory inventory, @NotNull FriendlyByteBuf buf) {
        super(syncId, inventory, buf, 8, 89, GCMachineTypes.FOOD_CANNER);
    }

    @Override
    public void registerSyncHandlers(Consumer<MenuSyncHandler> consumer) {
        super.registerSyncHandlers(consumer);

        consumer.accept(MenuSyncHandler.simple(this.machine::getProgress, this::setProgress));
        consumer.accept(MenuSyncHandler.simple(this.machine::getFirstRowConsumed, this::setFirstRowConsumed));
        consumer.accept(MenuSyncHandler.simple(this.machine::getSecondRowConsumed, this::setSecondRowConsumed));
        consumer.accept(MenuSyncHandler.simple(this.machine::getThirdRowConsumed, this::setThirdRowConsumed));
        consumer.accept(MenuSyncHandler.simple(this.machine::getForthRowConsumed, this::setForthRowConsumed));
    }


    public int getProgress() {
        return progress;
    }

    public void setProgress(int value) {
        this.progress = value;
    }

    public int getFirstRowConsumed() {
        return firstRowConsumed;
    }
    public void setFirstRowConsumed(int value) {
        this.firstRowConsumed = value;
    }
    public int getSecondRowConsumed() {
        return secondRowConsumed;
    }
    public void setSecondRowConsumed(int value) {
        this.secondRowConsumed = value;
    }
    public int getThirdRowConsumed() {
        return thirdRowConsumed;
    }
    public void setThirdRowConsumed(int value) {
        this.thirdRowConsumed = value;
    }
    public int getForthRowConsumed() {
        return forthRowConsumed;
    }
    public void setForthRowConsumed(int value) {
        this.forthRowConsumed = value;
    }

}
