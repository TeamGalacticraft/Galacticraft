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

package dev.galacticraft.mod.compat.rei.client;

import dev.galacticraft.machinelib.client.api.screen.MachineScreen;
import dev.galacticraft.machinelib.impl.Constant.TextureCoordinate;
import dev.galacticraft.mod.compat.rei.client.category.DefaultCompressingCategory;
import dev.galacticraft.mod.compat.rei.client.category.DefaultFabricationCategory;
import dev.galacticraft.mod.compat.rei.common.GalacticraftREIServerPlugin;
import dev.galacticraft.mod.compat.rei.common.display.DefaultFabricationDisplay;
import dev.galacticraft.mod.compat.rei.common.display.DefaultShapedCompressingDisplay;
import dev.galacticraft.mod.compat.rei.common.display.DefaultShapelessCompressingDisplay;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.recipe.FabricationRecipe;
import dev.galacticraft.mod.recipe.GCRecipes;
import dev.galacticraft.mod.recipe.ShapedCompressingRecipe;
import dev.galacticraft.mod.recipe.ShapelessCompressingRecipe;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.common.BuiltinPlugin;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;

public class GalacticraftREIClientPlugin implements REIClientPlugin {
    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new DefaultFabricationCategory());
        registry.add(new DefaultCompressingCategory());

        registry.addWorkstations(GalacticraftREIServerPlugin.CIRCUIT_FABRICATION, EntryStacks.of(GCBlocks.CIRCUIT_FABRICATOR));
        registry.addWorkstations(GalacticraftREIServerPlugin.COMPRESSING, EntryStacks.of(GCBlocks.COMPRESSOR), EntryStacks.of(GCBlocks.ELECTRIC_COMPRESSOR));
        registry.addWorkstations(BuiltinPlugin.BLASTING, EntryStacks.of(GCBlocks.ELECTRIC_ARC_FURNACE));
        registry.addWorkstations(BuiltinPlugin.SMELTING, EntryStacks.of(GCBlocks.ELECTRIC_FURNACE));
        registry.addWorkstations(BuiltinPlugin.FUEL, EntryStacks.of(GCBlocks.COMPRESSOR));

        registry.setPlusButtonArea(GalacticraftREIServerPlugin.CIRCUIT_FABRICATION, bounds -> new Rectangle(bounds.getMinX() + 8, bounds.getMaxY() - 16, 10, 10));
        registry.setPlusButtonArea(GalacticraftREIServerPlugin.COMPRESSING, bounds -> new Rectangle(bounds.getMaxX() - 16, bounds.getMaxY() - 16, 10, 10));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(FabricationRecipe.class, GCRecipes.FABRICATION_TYPE, DefaultFabricationDisplay::new);
        registry.registerRecipeFiller(ShapedCompressingRecipe.class, GCRecipes.COMPRESSING_TYPE, DefaultShapedCompressingDisplay::new);
        registry.registerRecipeFiller(ShapelessCompressingRecipe.class, GCRecipes.COMPRESSING_TYPE, DefaultShapelessCompressingDisplay::new);
    }

    @Override
    public void registerEntries(EntryRegistry registry) {
        for (ItemLike item : GCItems.HIDDEN_ITEMS) {
            registry.removeEntry(EntryStacks.of(item));
        }
    }

    @Override
    public void registerExclusionZones(ExclusionZones zones) {
        zones.register(MachineScreen.class, provider -> {
            List<Rectangle> areas = new ArrayList<>();
            if (MachineScreen.Tab.STATS.isOpen() || MachineScreen.Tab.SECURITY.isOpen()) {
                areas.add(new Rectangle(provider.getX() + provider.getImageWidth(), provider.getY() + (MachineScreen.Tab.STATS.isOpen() ? 0 : TextureCoordinate.TAB_HEIGHT), TextureCoordinate.PANEL_WIDTH, TextureCoordinate.PANEL_HEIGHT));
                areas.add(new Rectangle(provider.getX() + provider.getImageWidth(), provider.getY() + TextureCoordinate.TAB_HEIGHT, TextureCoordinate.TAB_WIDTH, TextureCoordinate.PANEL_HEIGHT));
            }
            areas.add(new Rectangle(provider.getX() + provider.getImageWidth(), provider.getY(), TextureCoordinate.TAB_WIDTH, TextureCoordinate.TAB_HEIGHT * 2));
            return areas;
        });
    }

    @Override
    public void registerTransferHandlers(TransferHandlerRegistry registry) {
    }
}