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

package dev.galacticraft.mod.client.gui.screen.ingame.spacerace;

import dev.galacticraft.mod.util.Translations;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public enum GlobalItemsColumn {
    MINED(Translations.SpaceRace.ITEM_COLUMN_MINED, new ItemStack(Items.DIAMOND_PICKAXE)),
    CRAFTED(Translations.SpaceRace.ITEM_COLUMN_CRAFTED, new ItemStack(Items.CRAFTING_TABLE)),
    USED(Translations.SpaceRace.ITEM_COLUMN_USED, new ItemStack(Items.IRON_SWORD)),
    BROKEN(Translations.SpaceRace.ITEM_COLUMN_BROKEN, new ItemStack(Items.CHIPPED_ANVIL)),
    PICKED_UP(Translations.SpaceRace.ITEM_COLUMN_PICKED_UP, new ItemStack(Items.CHEST)),
    DROPPED(Translations.SpaceRace.ITEM_COLUMN_DROPPED, new ItemStack(Items.DROPPER));

    private final Component tooltip;
    private final ItemStack icon;

    GlobalItemsColumn(String tooltipKey, ItemStack icon) {
        this.tooltip = Component.translatable(tooltipKey);
        this.icon = icon;
    }

    public Component tooltip() {
        return this.tooltip;
    }

    public ItemStack icon() {
        return this.icon;
    }
}
