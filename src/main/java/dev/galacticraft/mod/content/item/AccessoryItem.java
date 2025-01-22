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

package dev.galacticraft.mod.content.item;

import dev.galacticraft.api.item.Accessory;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AccessoryItem extends Item implements Accessory {
    public AccessoryType accessoryType;

    public AccessoryItem(Properties settings, AccessoryType accessoryType) {
        super(settings.stacksTo(1));
        this.accessoryType = accessoryType;
    }

    public AccessoryType getType() {
        return this.accessoryType;
    }

    public int getSlot() {
        return this.accessoryType.getSlot();
    }

    @Override //should sync with server
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        Container inv = player.galacticraft$getGearInv();
        int slot = this.getSlot();
        if (inv.getItem(slot).isEmpty()) {
            inv.setItem(slot, player.getItemInHand(hand));
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, ItemStack.EMPTY);
        }
        return super.use(world, player, hand);
    }
}
