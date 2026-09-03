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

package dev.galacticraft.mod.content.block.decoration;

import dev.galacticraft.mod.content.item.EmergencyKitItem;
import dev.galacticraft.mod.content.item.GCItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class DisplayCaseInteractions {
    private static final Map<Item, Interaction> INTERACTIONS = new HashMap<>();

    static {
        register(
                GCItems.EMERGENCY_KIT,
                Result.CONSUME_ITEM,
                DisplayCaseInteractions::openEmergencyKit
        );
    }

    private DisplayCaseInteractions() {
    }

    public static void register(Item item, Result result, Handler handler) {
        INTERACTIONS.put(item, new Interaction(result, handler));
    }

    @Nullable
    public static Interaction get(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }

        return INTERACTIONS.get(stack.getItem());
    }

    private static void openEmergencyKit(Level level, BlockPos blockPos, Player player, ItemStack itemStack) {
        if (itemStack.getItem() instanceof  EmergencyKitItem emergencyKitItem) {
            emergencyKitItem.openEmergencyKit(level, itemStack, player);
        }
    }

    public enum Result {
        CONSUME_ITEM,
        RETURN_ITEM
    }

    @FunctionalInterface
    public interface Handler {
        void run(Level level, BlockPos pos, Player player, ItemStack item);
    }

    public record Interaction(Result result, Handler handler) {
    }
}