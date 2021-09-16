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

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.client.screen.MachineHandledScreen;
import dev.galacticraft.mod.block.GalacticraftBlock;
import dev.galacticraft.mod.compat.rei.client.category.DefaultCompressingCategory;
import dev.galacticraft.mod.compat.rei.client.category.DefaultFabricationCategory;
import dev.galacticraft.mod.compat.rei.client.display.DefaultFabricationDisplay;
import dev.galacticraft.mod.compat.rei.client.display.DefaultShapedCompressingDisplay;
import dev.galacticraft.mod.compat.rei.client.display.DefaultShapelessCompressingDisplay;
import dev.galacticraft.mod.compat.rei.client.transfer.DefaultTransferHandler;
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
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftREIClientPlugin implements REIClientPlugin {
    public static final CategoryIdentifier CIRCUIT_FABRICATION = CategoryIdentifier.of(Constant.MOD_ID, "plugins/circuit_fabricator");
    public static final CategoryIdentifier COMPRESSING = CategoryIdentifier.of(Constant.MOD_ID, "plugins/compressing");

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new DefaultFabricationCategory());
        registry.add(new DefaultCompressingCategory());

        registry.addWorkstations(CIRCUIT_FABRICATION, EntryStacks.of(GalacticraftBlock.CIRCUIT_FABRICATOR));
        registry.addWorkstations(COMPRESSING, EntryStacks.of(GalacticraftBlock.COMPRESSOR), EntryStacks.of(GalacticraftBlock.ELECTRIC_COMPRESSOR));

        registry.setPlusButtonArea(CIRCUIT_FABRICATION, bounds -> new Rectangle(bounds.getMinX() + 8, bounds.getMaxY() - 16, 10, 10));
        registry.setPlusButtonArea(COMPRESSING,bounds -> new Rectangle(bounds.getMaxX() - 16, bounds.getMaxY() - 16, 10, 10));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(FabricationRecipe.class, GalacticraftRecipe.FABRICATION_TYPE, DefaultFabricationDisplay::new);
        registry.registerRecipeFiller(ShapedCompressingRecipe.class, GalacticraftRecipe.COMPRESSING_TYPE, DefaultShapedCompressingDisplay::new);
        registry.registerRecipeFiller(ShapelessCompressingRecipe.class, GalacticraftRecipe.COMPRESSING_TYPE, DefaultShapelessCompressingDisplay::new);
    }

    @Override
    public void registerEntries(EntryRegistry registry) {
        for (Item item : GalacticraftItem.HIDDEN_ITEMS) {
            registry.removeEntry(EntryStacks.of(item));
        }
    }

    @Override
    public void registerExclusionZones(ExclusionZones zones) {
        zones.register(MachineHandledScreen.class, provider -> {
            MachineHandledScreen<?,?> machineScreen = provider;
            List<Rectangle> rects = new ArrayList<>();

            if (MachineHandledScreen.Tab.STATS.isOpen()) {
                rects.add(new Rectangle(machineScreen.getX() + machineScreen.width, machineScreen.getY(), MachineHandledScreen.PANEL_WIDTH, MachineHandledScreen.PANEL_HEIGHT));
            } else {
                rects.add(new Rectangle(machineScreen.getX() + machineScreen.width, machineScreen.getY(), MachineHandledScreen.TAB_WIDTH, MachineHandledScreen.TAB_HEIGHT));
            }

            return rects;
        });
    }

    @Override
    public void registerTransferHandlers(TransferHandlerRegistry registry) {
        registry.register(new DefaultTransferHandler());
    }
}