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

package dev.galacticraft.impl.internal.client.tabs;

import dev.galacticraft.api.client.tabs.InventoryTabRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class InventoryTabRegistryImpl implements InventoryTabRegistry {
    public static final InventoryTabRegistryImpl INSTANCE = new InventoryTabRegistryImpl();
    public final List<TabData> TABS = new ArrayList<>();

    static {
        INSTANCE.register(Items.CRAFTING_TABLE.getDefaultInstance(), () -> {
            assert Minecraft.getInstance().player != null;
            Minecraft.getInstance().setScreen(new InventoryScreen(Minecraft.getInstance().player));
        }, player -> true, InventoryMenu.class);
    }

    @Override
    public void register(ItemStack icon, Runnable onClick, Predicate<Player> visiblePredicate, Class<? extends AbstractContainerMenu> clazz) {
        TABS.add(new TabData(icon, onClick, visiblePredicate, clazz));
    }

    public record TabData(ItemStack icon, Runnable onClick, Predicate<Player> visiblePredicate, Class<? extends AbstractContainerMenu> clazz) {
    }
}
