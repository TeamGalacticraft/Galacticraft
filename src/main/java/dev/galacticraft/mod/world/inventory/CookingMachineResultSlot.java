/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

package dev.galacticraft.mod.world.inventory;

import dev.galacticraft.machinelib.api.storage.slot.ItemResourceSlot;
import dev.galacticraft.machinelib.api.storage.slot.display.ItemSlotDisplay;
import dev.galacticraft.machinelib.impl.compat.vanilla.StorageSlot;
import dev.galacticraft.mod.content.block.entity.machine.CookingMachineBlockEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * An output slot of a {@link CookingMachineBlockEntity} that awards the stored experience
 * to the player who takes items from it, like the result slot of a vanilla furnace.
 */
public class CookingMachineResultSlot extends StorageSlot {
    private final CookingMachineBlockEntity<?> machine;
    private final Player player;

    public CookingMachineResultSlot(Container group, @NotNull ItemResourceSlot slot, @NotNull ItemSlotDisplay display, int index, @NotNull Player player, CookingMachineBlockEntity<?> machine) {
        super(group, slot, display, index, player);
        this.machine = machine;
        this.player = player;
    }

    @Override
    protected void checkTakeAchievements(ItemStack stack) {
        super.checkTakeAchievements(stack);
        if (this.player instanceof ServerPlayer serverPlayer) {
            this.machine.popExperience(serverPlayer.serverLevel(), serverPlayer.position());
        }
    }
}
