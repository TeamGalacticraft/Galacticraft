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

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class NasaWorkbenchSchematicMenu extends AbstractContainerMenu {
	private final int menuHeight = 177;

	public NasaWorkbenchSchematicMenu(int syncId, Inventory inv) {
		super(GCMenuTypes.NASA_WORKBENCH_SCHEMATIC_MENU, syncId);
		
		for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inv, i, 0 + 8 + i * 18, this.menuHeight - 24));
        }
        for (int i = 2; i >= 0; --i) { // we are gonna make this work irregardless of size
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inv, j + (i + 1) * 9, 0 + 8 + j * 18, this.menuHeight - 82 + i * 18));
            }
        }

		this.addSlot(new Slot(inv, 36, 80, 28)); // TODO: make this a blueprint slot
	}

	@Override
	public ItemStack quickMoveStack(Player var1, int var2) {
		// TODO Auto-generated method stub
		return ItemStack.EMPTY; // TODO: allow for shifting on/to blueprint slot
	}

	@Override
	public boolean stillValid(Player var1) {
		// TODO Auto-generated method stub
		return true; // TODO: should use a better return here than just true
	}
	
}
