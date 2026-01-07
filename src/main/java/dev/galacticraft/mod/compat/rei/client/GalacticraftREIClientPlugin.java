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
import dev.galacticraft.mod.client.gui.screen.ingame.CircuitFabricatorScreen;
import dev.galacticraft.mod.client.gui.screen.ingame.CompressorScreen;
import dev.galacticraft.mod.client.gui.screen.ingame.ElectricArcFurnaceScreen;
import dev.galacticraft.mod.client.gui.screen.ingame.ElectricCompressorScreen;
import dev.galacticraft.mod.client.gui.screen.ingame.ElectricFurnaceScreen;
import dev.galacticraft.mod.compat.rei.client.category.DefaultCanningCategory;
import dev.galacticraft.mod.compat.rei.client.category.DefaultCompressingCategory;
import dev.galacticraft.mod.compat.rei.client.category.DefaultFabricationCategory;
import dev.galacticraft.mod.compat.rei.client.category.DefaultRocketCategory;
import dev.galacticraft.mod.compat.rei.client.category.ElectricArcFurnaceCategory;
import dev.galacticraft.mod.compat.rei.client.category.ElectricCompressingCategory;
import dev.galacticraft.mod.compat.rei.client.category.ElectricFurnaceCategory;
import dev.galacticraft.mod.compat.rei.client.display.CompressingDisplayVisibilityPredicate;
import dev.galacticraft.mod.compat.rei.client.filler.EmergencyKitRecipeFiller;
import dev.galacticraft.mod.compat.rei.common.GalacticraftREIServerPlugin;
import dev.galacticraft.mod.compat.rei.common.display.CanningDisplayGenerator;
import dev.galacticraft.mod.compat.rei.common.display.DefaultFabricationDisplay;
import dev.galacticraft.mod.compat.rei.common.display.DefaultShapedCompressingDisplay;
import dev.galacticraft.mod.compat.rei.common.display.DefaultShapelessCompressingDisplay;
import dev.galacticraft.mod.compat.rei.common.display.DefaultRocketDisplay;
import dev.galacticraft.mod.compat.rei.common.display.ElectricArcFurnaceDisplay;
import dev.galacticraft.mod.compat.rei.common.display.ElectricFurnaceDisplay;
import dev.galacticraft.mod.compat.rei.common.display.ElectricShapedCompressingDisplay;
import dev.galacticraft.mod.compat.rei.common.display.ElectricShapelessCompressingDisplay;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.entity.machine.*;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.recipe.FabricationRecipe;
import dev.galacticraft.mod.recipe.GCRecipes;
import dev.galacticraft.mod.recipe.ShapedCompressingRecipe;
import dev.galacticraft.mod.recipe.ShapelessCompressingRecipe;
import dev.galacticraft.mod.recipe.RocketRecipe;
import dev.galacticraft.mod.screen.FoodCannerMenu;
import dev.galacticraft.mod.screen.RocketWorkbenchMenu;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.CollapsibleEntryRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.api.client.registry.transfer.simple.SimpleTransferHandler;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.client.categories.crafting.filler.CraftingRecipeFiller;
import me.shedaniel.rei.plugin.common.BuiltinPlugin;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.ItemLike;

public class GalacticraftREIClientPlugin implements REIClientPlugin {
    private static final CraftingRecipeFiller<?>[] CRAFTING_RECIPE_FILLERS = new CraftingRecipeFiller[]{
            new EmergencyKitRecipeFiller()
    };

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new DefaultFabricationCategory());
        registry.add(new DefaultCompressingCategory());
        registry.add(new ElectricCompressingCategory());
        registry.add(new ElectricFurnaceCategory());
        registry.add(new ElectricArcFurnaceCategory());
        registry.add(new DefaultCanningCategory());
        registry.add(new DefaultRocketCategory());

        registry.addWorkstations(GalacticraftREIServerPlugin.FABRICATION, EntryStacks.of(GCBlocks.CIRCUIT_FABRICATOR));
        registry.addWorkstations(GalacticraftREIServerPlugin.COMPRESSING, EntryStacks.of(GCBlocks.COMPRESSOR));
        registry.addWorkstations(GalacticraftREIServerPlugin.ELECTRIC_COMPRESSING, EntryStacks.of(GCBlocks.ELECTRIC_COMPRESSOR));
        registry.addWorkstations(GalacticraftREIServerPlugin.CANNING, EntryStacks.of(GCBlocks.FOOD_CANNER));
        registry.addWorkstations(GalacticraftREIServerPlugin.ROCKET, EntryStacks.of(GCBlocks.ROCKET_WORKBENCH));
        registry.addWorkstations(GalacticraftREIServerPlugin.ELECTRIC_SMELTING, EntryStacks.of(GCBlocks.ELECTRIC_FURNACE));
        registry.addWorkstations(GalacticraftREIServerPlugin.ELECTRIC_BLASTING, EntryStacks.of(GCBlocks.ELECTRIC_ARC_FURNACE));
        registry.addWorkstations(BuiltinPlugin.FUEL, EntryStacks.of(GCBlocks.COMPRESSOR));

        for (CraftingRecipeFiller<?> filler : CRAFTING_RECIPE_FILLERS) {
            filler.registerCategories(registry);
        }
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        /*
         * This will need to be changed from 1.21.2 onwards, see the following for more details:
         * https://hackmd.io/@shedaniel/rei17_primer#Recipe-Fillers
         * https://github.com/shedaniel/RoughlyEnoughItems/commit/ddb48e2032d1986709cad973067693eec3118504
         */
        registry.registerRecipeFiller(FabricationRecipe.class, GCRecipes.FABRICATION_TYPE, DefaultFabricationDisplay::new);
        registry.registerRecipeFiller(ShapedCompressingRecipe.class, GCRecipes.COMPRESSING_TYPE, DefaultShapedCompressingDisplay::new);
        registry.registerRecipeFiller(ShapelessCompressingRecipe.class, GCRecipes.COMPRESSING_TYPE, DefaultShapelessCompressingDisplay::new);
        registry.registerRecipeFiller(ShapedCompressingRecipe.class, GCRecipes.COMPRESSING_TYPE, ElectricShapedCompressingDisplay::new);
        registry.registerRecipeFiller(ShapelessCompressingRecipe.class, GCRecipes.COMPRESSING_TYPE, ElectricShapelessCompressingDisplay::new);
        registry.registerRecipeFiller(SmeltingRecipe.class, RecipeType.SMELTING, ElectricFurnaceDisplay::new);
        registry.registerRecipeFiller(BlastingRecipe.class, RecipeType.BLASTING, ElectricArcFurnaceDisplay::new);
        registry.registerRecipeFiller(RocketRecipe.class, GCRecipes.ROCKET_TYPE, DefaultRocketDisplay::new);

        registry.registerDisplayGenerator(GalacticraftREIServerPlugin.CANNING, new CanningDisplayGenerator());

        for (CraftingRecipeFiller<?> filler : CRAFTING_RECIPE_FILLERS) {
            filler.registerDisplays(registry);
        }

        registry.registerVisibilityPredicate(new CompressingDisplayVisibilityPredicate());
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
    public void registerScreens(ScreenRegistry registry) {
        registry.registerContainerClickArea(
                new Rectangle(79, 50, 83, 20),
                CircuitFabricatorScreen.class,
                GalacticraftREIServerPlugin.FABRICATION
        );
        registry.registerContainerClickArea(
                new Rectangle(
                        Constant.Compressor.PROGRESS_X - 1,
                        Constant.Compressor.PROGRESS_Y - 3,
                        Constant.Compressor.PROGRESS_WIDTH + 2,
                        3 + Math.min(Constant.Compressor.PROGRESS_HEIGHT, Constant.Compressor.FUEL_Y - Constant.Compressor.PROGRESS_Y - 2)
                ),
                CompressorScreen.class,
                GalacticraftREIServerPlugin.COMPRESSING
        );
        registry.registerContainerClickArea(
                new Rectangle(
                        Constant.ElectricCompressor.PROGRESS_X - 1,
                        Constant.ElectricCompressor.PROGRESS_Y - 3,
                        Constant.ElectricCompressor.PROGRESS_WIDTH + 2,
                        Constant.ElectricCompressor.PROGRESS_HEIGHT + 6
                ),
                ElectricCompressorScreen.class,
                GalacticraftREIServerPlugin.ELECTRIC_COMPRESSING
        );
        registry.registerContainerClickArea(
                new Rectangle(
                        Constant.ElectricFurnace.PROGRESS_X - 1,
                        Constant.ElectricFurnace.PROGRESS_Y - 3,
                        Constant.ElectricFurnace.PROGRESS_WIDTH + 2,
                        Constant.ElectricFurnace.PROGRESS_HEIGHT + 6
                ),
                ElectricFurnaceScreen.class,
                GalacticraftREIServerPlugin.ELECTRIC_SMELTING
        );
        registry.registerContainerClickArea(
                new Rectangle(
                        Constant.ElectricArcFurnace.PROGRESS_X - 1,
                        Constant.ElectricArcFurnace.PROGRESS_Y - 3,
                        Constant.ElectricArcFurnace.PROGRESS_WIDTH + 2,
                        Constant.ElectricArcFurnace.PROGRESS_HEIGHT + 6
                ),
                ElectricArcFurnaceScreen.class,
                GalacticraftREIServerPlugin.ELECTRIC_BLASTING
        );
    }

    @Override
    public void registerTransferHandlers(TransferHandlerRegistry registry) {
        registry.register(SimpleTransferHandler.create(RecipeMachineMenu.class, GalacticraftREIServerPlugin.FABRICATION,
                new SimpleTransferHandler.IntRange(1, 6)
        ));
        registry.register(SimpleTransferHandler.create(RecipeMachineMenu.class, GalacticraftREIServerPlugin.COMPRESSING,
                new SimpleTransferHandler.IntRange(
                        CompressorBlockEntity.INPUT_SLOTS,
                        CompressorBlockEntity.INPUT_SLOTS + CompressorBlockEntity.INPUT_LENGTH
                )
        ));
        registry.register(SimpleTransferHandler.create(RecipeMachineMenu.class, GalacticraftREIServerPlugin.ELECTRIC_COMPRESSING,
                new SimpleTransferHandler.IntRange(
                        ElectricCompressorBlockEntity.INPUT_SLOTS,
                        ElectricCompressorBlockEntity.INPUT_SLOTS + ElectricCompressorBlockEntity.INPUT_LENGTH
                )
        ));
        registry.register(SimpleTransferHandler.create(RecipeMachineMenu.class, GalacticraftREIServerPlugin.ELECTRIC_SMELTING,
                new SimpleTransferHandler.IntRange(
                        ElectricFurnaceBlockEntity.INPUT_SLOT,
                        ElectricFurnaceBlockEntity.INPUT_SLOT + 1
                )
        ));
        registry.register(SimpleTransferHandler.create(RecipeMachineMenu.class, GalacticraftREIServerPlugin.ELECTRIC_BLASTING,
                new SimpleTransferHandler.IntRange(
                        ElectricArcFurnaceBlockEntity.INPUT_SLOT,
                        ElectricArcFurnaceBlockEntity.INPUT_SLOT + 1
                )
        ));
        registry.register(SimpleTransferHandler.create(FoodCannerMenu.class, GalacticraftREIServerPlugin.CANNING,
                new SimpleTransferHandler.IntRange(
                        FoodCannerBlockEntity.INPUT_SLOT,
                        FoodCannerBlockEntity.INPUT_SLOT + FoodCannerBlockEntity.INPUT_LENGTH
                )
        ));
        registry.register(SimpleTransferHandler.create(RocketWorkbenchMenu.class, GalacticraftREIServerPlugin.ROCKET,
                new SimpleTransferHandler.IntRange(0, 15)
        ));
    }
}