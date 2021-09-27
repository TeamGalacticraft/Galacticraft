/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

import dev.galacticraft.mod.api.client.screen.MachineHandledScreen;
import dev.galacticraft.mod.block.GalacticraftBlock;
import dev.galacticraft.mod.compat.rei.client.category.DefaultCompressingCategory;
import dev.galacticraft.mod.compat.rei.client.category.DefaultFabricationCategory;
import dev.galacticraft.mod.compat.rei.common.GalacticraftREIServerPlugin;
import dev.galacticraft.mod.compat.rei.common.display.DefaultFabricationDisplay;
import dev.galacticraft.mod.compat.rei.common.display.DefaultShapedCompressingDisplay;
import dev.galacticraft.mod.compat.rei.common.display.DefaultShapelessCompressingDisplay;
import dev.galacticraft.mod.item.GalacticraftItem;
import dev.galacticraft.mod.recipe.FabricationRecipe;
import dev.galacticraft.mod.recipe.GalacticraftRecipe;
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
import net.minecraft.item.ItemConvertible;

import java.util.Collections;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftREIClientPlugin implements REIClientPlugin {
    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new DefaultFabricationCategory());
        registry.add(new DefaultCompressingCategory());

        registry.addWorkstations(GalacticraftREIServerPlugin.CIRCUIT_FABRICATION, EntryStacks.of(GalacticraftBlock.CIRCUIT_FABRICATOR));
        registry.addWorkstations(GalacticraftREIServerPlugin.COMPRESSING, EntryStacks.of(GalacticraftBlock.COMPRESSOR), EntryStacks.of(GalacticraftBlock.ELECTRIC_COMPRESSOR));
        registry.addWorkstations(BuiltinPlugin.BLASTING, EntryStacks.of(GalacticraftBlock.ELECTRIC_ARC_FURNACE));
        registry.addWorkstations(BuiltinPlugin.SMELTING, EntryStacks.of(GalacticraftBlock.ELECTRIC_FURNACE));
        registry.addWorkstations(BuiltinPlugin.FUEL, EntryStacks.of(GalacticraftBlock.COMPRESSOR));

        registry.setPlusButtonArea(GalacticraftREIServerPlugin.CIRCUIT_FABRICATION, bounds -> new Rectangle(bounds.getMinX() + 8, bounds.getMaxY() - 16, 10, 10));
        registry.setPlusButtonArea(GalacticraftREIServerPlugin.COMPRESSING, bounds -> new Rectangle(bounds.getMaxX() - 16, bounds.getMaxY() - 16, 10, 10));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(FabricationRecipe.class, GalacticraftRecipe.FABRICATION_TYPE, DefaultFabricationDisplay::new);
        registry.registerRecipeFiller(ShapedCompressingRecipe.class, GalacticraftRecipe.COMPRESSING_TYPE, DefaultShapedCompressingDisplay::new);
        registry.registerRecipeFiller(ShapelessCompressingRecipe.class, GalacticraftRecipe.COMPRESSING_TYPE, DefaultShapelessCompressingDisplay::new);
    }

    @Override
    public void registerEntries(EntryRegistry registry) {
        for (ItemConvertible item : GalacticraftItem.HIDDEN_ITEMS) {
            registry.removeEntry(EntryStacks.of(item));
        }
    }

    @Override
    public void registerExclusionZones(ExclusionZones zones) {
        zones.register(MachineHandledScreen.class, provider -> {
            if (MachineHandledScreen.Tab.STATS.isOpen()) {
                return Collections.singletonList(new Rectangle(provider.getX() + provider.width, provider.getY(), MachineHandledScreen.PANEL_WIDTH, MachineHandledScreen.PANEL_HEIGHT));
            } else {
                return Collections.singletonList(new Rectangle(provider.getX() + provider.width, provider.getY(), MachineHandledScreen.TAB_WIDTH, MachineHandledScreen.TAB_HEIGHT));
            }
        });
    }

    @Override
    public void registerTransferHandlers(TransferHandlerRegistry registry) {
    }
}