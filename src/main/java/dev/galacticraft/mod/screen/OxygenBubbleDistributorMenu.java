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
import dev.galacticraft.mod.content.block.entity.machine.OxygenBubbleDistributorBlockEntity;
import dev.galacticraft.mod.screen.sync.BooleanMenuSyncHandler;
import dev.galacticraft.mod.screen.sync.DoubleMenuSyncHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class OxygenBubbleDistributorMenu extends MachineMenu<OxygenBubbleDistributorBlockEntity> {
    public boolean bubbleVisible;
    public byte targetSize;
    public double size;

    public OxygenBubbleDistributorMenu(int syncId, ServerPlayer player, OxygenBubbleDistributorBlockEntity machine) {
        super(syncId, player, machine);
        this.bubbleVisible = machine.isBubbleVisible();
        this.size = machine.getSize();
        this.targetSize = machine.getTargetSize();
    }

    public OxygenBubbleDistributorMenu(int syncId, Inventory inv, FriendlyByteBuf buf) {
        super(syncId, inv, buf, 8, 84, GCMachineTypes.OXYGEN_BUBBLE_DISTRIBUTOR);
    }

    @Override
    public void registerSyncHandlers(Consumer<MenuSyncHandler> consumer) {
        super.registerSyncHandlers(consumer);
        consumer.accept(new MenuSyncHandler() {
            private byte value;

            @Override
            public boolean needsSyncing() {
                return this.value != OxygenBubbleDistributorMenu.this.targetSize;
            }

            @Override
            public void sync(@NotNull FriendlyByteBuf buf) {
                this.value = OxygenBubbleDistributorMenu.this.targetSize;
                buf.writeByte(this.value);
            }

            @Override
            public void read(@NotNull FriendlyByteBuf buf) {
                this.value = buf.readByte();
                OxygenBubbleDistributorMenu.this.targetSize = this.value;
            }
        });
        consumer.accept(new DoubleMenuSyncHandler(this.machine::getSize, value -> this.size = value));
        consumer.accept(new BooleanMenuSyncHandler(this.machine::isBubbleVisible, t -> this.bubbleVisible = t));
    }
}
