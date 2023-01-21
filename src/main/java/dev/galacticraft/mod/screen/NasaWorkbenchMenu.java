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

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class NasaWorkbenchMenu extends AbstractContainerMenu { // I think this is the right superclass? // statics?

	public NasaWorkbenchMenu(int syncId, Player player) { // THIS ONLY EXECUTES ON SERVER WE CAN SAFELY CAST PLAYER TO SERVERPLAYER
		// I think we need to communicate with the server here?d
		super(GCMenuTypes.NASA_WORKBENCH_HANDLER, syncId); // should be GCMenuType.NASA_WORKBENCH
	}

	public NasaWorkbenchMenu(int syncId, Inventory inv) { //, FriendlyByteBuf buf)
		// can get inv.player.level.getBlockEntity(buf.readBlockPos()) the block data here
		this(syncId, inv.player);
	}

	@Override
	public ItemStack quickMoveStack(Player var1, int var2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean stillValid(Player var1) {
		// TODO Auto-generated method stub
		return true;
	}
}
