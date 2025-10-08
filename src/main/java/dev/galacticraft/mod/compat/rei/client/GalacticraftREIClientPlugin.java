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

package dev.galacticraft.mod.compat.rei.client;

import dev.galacticraft.machinelib.api.menu.RecipeMachineMenu;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.compat.rei.client.category.DefaultCompressingCategory;
import dev.galacticraft.mod.compat.rei.client.category.DefaultFabricationCategory;
import dev.galacticraft.mod.compat.rei.client.category.DefaultRocketCategory;
import dev.galacticraft.mod.compat.rei.client.filler.EmergencyKitRecipeFiller;
import dev.galacticraft.mod.compat.rei.common.GalacticraftREIServerPlugin;
import dev.galacticraft.mod.compat.rei.common.display.DefaultFabricationDisplay;
import dev.galacticraft.mod.compat.rei.common.display.DefaultShapedCompressingDisplay;
import dev.galacticraft.mod.compat.rei.common.display.DefaultShapelessCompressingDisplay;
import dev.galacticraft.mod.compat.rei.common.display.DefaultRocketDisplay;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.recipe.FabricationRecipe;
import dev.galacticraft.mod.recipe.GCRecipes;
import dev.galacticraft.mod.recipe.ShapedCompressingRecipe;
import dev.galacticraft.mod.recipe.ShapelessCompressingRecipe;
import dev.galacticraft.mod.recipe.RocketRecipe;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.CollapsibleEntryRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.api.client.registry.transfer.simple.SimpleTransferHandler;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.client.categories.crafting.filler.CraftingRecipeFiller;
import me.shedaniel.rei.plugin.common.BuiltinPlugin;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class GalacticraftREIClientPlugin implements REIClientPlugin {
    private static final CraftingRecipeFiller<?>[] CRAFTING_RECIPE_FILLERS = new CraftingRecipeFiller[]{
            new EmergencyKitRecipeFiller()
    };

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new DefaultFabricationCategory());
        registry.add(new DefaultCompressingCategory());
        registry.add(new DefaultRocketCategory());

        registry.addWorkstations(GalacticraftREIServerPlugin.CIRCUIT_FABRICATION, EntryStacks.of(GCBlocks.CIRCUIT_FABRICATOR));
        registry.addWorkstations(GalacticraftREIServerPlugin.COMPRESSING, EntryStacks.of(GCBlocks.COMPRESSOR), EntryStacks.of(GCBlocks.ELECTRIC_COMPRESSOR));
        registry.addWorkstations(GalacticraftREIServerPlugin.ROCKET, EntryStacks.of(GCBlocks.ROCKET_WORKBENCH));
        registry.addWorkstations(BuiltinPlugin.BLASTING, EntryStacks.of(GCBlocks.ELECTRIC_ARC_FURNACE));
        registry.addWorkstations(BuiltinPlugin.SMELTING, EntryStacks.of(GCBlocks.ELECTRIC_FURNACE));
        registry.addWorkstations(BuiltinPlugin.FUEL, EntryStacks.of(GCBlocks.COMPRESSOR));

        for (CraftingRecipeFiller<?> filler : CRAFTING_RECIPE_FILLERS) {
            filler.registerCategories(registry);
        }
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(FabricationRecipe.class, GCRecipes.FABRICATION_TYPE, DefaultFabricationDisplay::new);
        registry.registerRecipeFiller(ShapedCompressingRecipe.class, GCRecipes.COMPRESSING_TYPE, DefaultShapedCompressingDisplay::new);
        registry.registerRecipeFiller(ShapelessCompressingRecipe.class, GCRecipes.COMPRESSING_TYPE, DefaultShapelessCompressingDisplay::new);
        registry.registerRecipeFiller(RocketRecipe.class, GCRecipes.ROCKET_TYPE, DefaultRocketDisplay::new);

        for (CraftingRecipeFiller<?> filler : CRAFTING_RECIPE_FILLERS) {
            filler.registerDisplays(registry);
        }
    }

    @Override
    public void registerEntries(EntryRegistry registry) {
        for (ItemLike item : GCItems.HIDDEN_ITEMS) {
            registry.removeEntry(EntryStacks.of(item));
        }
    }

    @Override
    public void registerCollapsibleEntries(CollapsibleEntryRegistry registry) {
        registry.group(Constant.id(Constant.Item.CANNED_FOOD), Component.translatable(GCItems.CANNED_FOOD.getDescriptionId()),
                stack -> stack.getType() == VanillaEntryTypes.ITEM && stack.<ItemStack>castValue().is(GCItems.CANNED_FOOD));
    }

    @Override
    public void registerTransferHandlers(TransferHandlerRegistry registry) {
        registry.register(SimpleTransferHandler.create(RecipeMachineMenu.class, GalacticraftREIServerPlugin.COMPRESSING,
                new SimpleTransferHandler.IntRange(1, 10)));
        registry.register(SimpleTransferHandler.create(RecipeMachineMenu.class, GalacticraftREIServerPlugin.CIRCUIT_FABRICATION,
                new SimpleTransferHandler.IntRange(1, 6)));
        registry.register(SimpleTransferHandler.create(RecipeMachineMenu.class, GalacticraftREIServerPlugin.ROCKET,
                new SimpleTransferHandler.IntRange(1, 15)));
    }
}