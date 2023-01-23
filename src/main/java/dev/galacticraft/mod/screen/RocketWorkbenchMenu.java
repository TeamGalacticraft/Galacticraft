/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

import dev.galacticraft.mod.recipe.GalacticraftRecipe;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class RocketWorkbenchMenu extends AbstractNasaWorkbenchMenu {
    public RocketWorkbenchMenu(int syncId, Inventory inv) {
        super(syncId, inv, GCMenuTypes.ROCKET_WORKBENCH_MENU, 220, 17, GalacticraftRecipe.ROCKETEERING_TYPE);

        this.addSlot(new ResultSlot(inv.player, this.craftSlots, this.resultSlots, 0, 142, 96)); // first or last?
        // Top Row
        this.addSlot(new Slot(this.craftSlots, 0, 48, 19));
        // Main Body
        for (int i = 0; i < 3; i++) {
            this.addSlot(new Slot(this.craftSlots, (i*2)+1, 39, 37+(i*18)));
            this.addSlot(new Slot(this.craftSlots, (i*2)+2, 57, 37+(i*18)));
        }
        // 4 piece row
        for (int i = 0; i < 4; i++) {
            this.addSlot(new Slot(this.craftSlots, i+7, 21+(i*18), 91));
        }
        // Bottom row
        for (int i = 0; i < 3; i++) {
            this.addSlot(new Slot(this.craftSlots, i+11, 21+(i*27), 109));
        }
        // Chest addons
        for (int i = 0; i < 3; i++) {
            this.addSlot(new Slot(this.craftSlots, i+14, 93+(i*26), 12));
        }
    }

    @Override
    protected boolean validForBlueprintSpot(int index, ItemStack stack) { // TODO: Implement menu
        return true;
    }
}