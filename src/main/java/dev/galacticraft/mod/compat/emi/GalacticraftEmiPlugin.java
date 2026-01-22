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

package dev.galacticraft.mod.compat.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.EmiWorldInteractionRecipe;
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.compat.emi.handler.MachineRecipeHandler;
import dev.galacticraft.mod.compat.emi.handler.RocketRecipeHandler;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.entity.machine.*;
import dev.galacticraft.mod.content.item.CannedFoodItem;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.content.item.EmergencyKitItem;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.content.item.ParachuteItem;
import dev.galacticraft.mod.recipe.CompressingRecipe;
import dev.galacticraft.mod.recipe.FabricationRecipe;
import dev.galacticraft.mod.recipe.RocketRecipe;
import dev.galacticraft.mod.recipe.GCRecipes;
import dev.galacticraft.mod.screen.GCMenuTypes;
import dev.galacticraft.mod.tag.GCItemTags;
import dev.galacticraft.mod.util.Translations.RecipeCategory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GalacticraftEmiPlugin implements EmiPlugin {
    private static final ResourceLocation SIMPLIFIED_ICONS = Constant.id("textures/gui/simplified_icons_emi.png");

    // Workstations
    public static final EmiStack CIRCUIT_FABRICATOR = EmiStack.of(GCBlocks.CIRCUIT_FABRICATOR);
    public static final EmiStack COMPRESSOR = EmiStack.of(GCBlocks.COMPRESSOR);
    public static final EmiStack ELECTRIC_COMPRESSOR = EmiStack.of(GCBlocks.ELECTRIC_COMPRESSOR);
    public static final EmiStack ELECTRIC_FURNACE = EmiStack.of(GCBlocks.ELECTRIC_FURNACE);
    public static final EmiStack ELECTRIC_ARC_FURNACE = EmiStack.of(GCBlocks.ELECTRIC_ARC_FURNACE);
    public static final EmiStack FOOD_CANNER = EmiStack.of(GCBlocks.FOOD_CANNER);
    public static final EmiStack ROCKET_WORKBENCH = EmiStack.of(GCBlocks.ROCKET_WORKBENCH);

    // Simplified Icons
    public static final EmiRenderable FABRICATION_ICON = new EmiTexture(SIMPLIFIED_ICONS, 0, 16, 16, 16, 16, 16, 64, 64);
    public static final EmiRenderable COMPRESSING_ICON = new EmiTexture(SIMPLIFIED_ICONS, 0, 0, 16, 16, 16, 16, 64, 64);
    public static final EmiRenderable ELECTRIC_COMPRESSING_ICON = new EmiTexture(SIMPLIFIED_ICONS, 16, 0, 16, 16, 16, 16, 64, 64);
    public static final EmiRenderable ELECTRIC_SMELTING_ICON = new EmiTexture(SIMPLIFIED_ICONS, 32, 0, 16, 16, 16, 16, 64, 64);
    public static final EmiRenderable ELECTRIC_BLASTING_ICON = new EmiTexture(SIMPLIFIED_ICONS, 48, 0, 16, 16, 16, 16, 64, 64);
    public static final EmiRenderable CANNING_ICON = new EmiTexture(SIMPLIFIED_ICONS, 16, 16, 16, 16, 16, 16, 64, 64);
    public static final EmiRenderable ROCKET_ICON = new EmiTexture(SIMPLIFIED_ICONS, 32, 16, 16, 16, 16, 16, 64, 64);

    // Categories
    public static final EmiRecipeCategory FABRICATION = new GalacticraftEmiRecipeCategory(Constant.id(Constant.Recipe.FABRICATION), CIRCUIT_FABRICATOR, FABRICATION_ICON);
    public static final EmiRecipeCategory COMPRESSING = new GalacticraftEmiRecipeCategory(Constant.id(Constant.Recipe.COMPRESSING), COMPRESSOR, COMPRESSING_ICON);
    public static final EmiRecipeCategory ELECTRIC_COMPRESSING = new GalacticraftEmiRecipeCategory(Constant.id(Constant.Recipe.ELECTRIC_COMPRESSING), ELECTRIC_COMPRESSOR, ELECTRIC_COMPRESSING_ICON, RecipeCategory.ELECTRIC_COMPRESSOR);
    public static final EmiRecipeCategory ELECTRIC_SMELTING = new GalacticraftEmiRecipeCategory(Constant.id(Constant.Recipe.ELECTRIC_SMELTING), ELECTRIC_FURNACE, ELECTRIC_SMELTING_ICON, RecipeCategory.ELECTRIC_FURNACE);
    public static final EmiRecipeCategory ELECTRIC_BLASTING = new GalacticraftEmiRecipeCategory(Constant.id(Constant.Recipe.ELECTRIC_BLASTING), ELECTRIC_ARC_FURNACE, ELECTRIC_BLASTING_ICON, RecipeCategory.ELECTRIC_ARC_FURNACE);
    public static final EmiRecipeCategory CANNING = new GalacticraftEmiRecipeCategory(Constant.id(Constant.Recipe.CANNING), FOOD_CANNER, CANNING_ICON);
    public static final EmiRecipeCategory ROCKET = new GalacticraftEmiRecipeCategory(Constant.id(Constant.Recipe.ROCKET), ROCKET_WORKBENCH, ROCKET_ICON);

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(FABRICATION);
        registry.addCategory(COMPRESSING);
        registry.addCategory(ELECTRIC_COMPRESSING);
        registry.addCategory(ELECTRIC_SMELTING);
        registry.addCategory(ELECTRIC_BLASTING);
        registry.addCategory(CANNING);
        registry.addCategory(ROCKET);

        registry.addWorkstation(FABRICATION, CIRCUIT_FABRICATOR);
        registry.addWorkstation(COMPRESSING, COMPRESSOR);
        registry.addWorkstation(ELECTRIC_COMPRESSING, ELECTRIC_COMPRESSOR);
        registry.addWorkstation(ELECTRIC_SMELTING, ELECTRIC_FURNACE);
        registry.addWorkstation(ELECTRIC_BLASTING, ELECTRIC_ARC_FURNACE);
        registry.addWorkstation(CANNING, FOOD_CANNER);
        registry.addWorkstation(ROCKET, ROCKET_WORKBENCH);

        registry.addRecipeHandler(GCMenuTypes.CIRCUIT_FABRICATOR,
                new MachineRecipeHandler<>(
                        FABRICATION,
                        new int[] {
                                CircuitFabricatorBlockEntity.DIAMOND_SLOT,
                                CircuitFabricatorBlockEntity.SILICON_SLOT_1,
                                CircuitFabricatorBlockEntity.SILICON_SLOT_2,
                                CircuitFabricatorBlockEntity.REDSTONE_SLOT,
                                CircuitFabricatorBlockEntity.INPUT_SLOT
                        },
                        CircuitFabricatorBlockEntity.OUTPUT_SLOT + 1
                )
        );
        registry.addRecipeHandler(GCMenuTypes.COMPRESSOR,
                new MachineRecipeHandler<>(
                        COMPRESSING,
                        IntStream.range(
                                CompressorBlockEntity.INPUT_SLOTS,
                                CompressorBlockEntity.INPUT_SLOTS + CompressorBlockEntity.INPUT_LENGTH
                        ).toArray(),
                        CompressorBlockEntity.OUTPUT_SLOT + 1
                )
        );
        registry.addRecipeHandler(GCMenuTypes.ELECTRIC_COMPRESSOR,
                new MachineRecipeHandler<>(
                        ELECTRIC_COMPRESSING,
                        IntStream.range(
                                ElectricCompressorBlockEntity.INPUT_SLOTS,
                                ElectricCompressorBlockEntity.INPUT_SLOTS + ElectricCompressorBlockEntity.INPUT_LENGTH
                        ).toArray(),
                        ElectricCompressorBlockEntity.OUTPUT_SLOTS + ElectricCompressorBlockEntity.OUTPUT_LENGTH
                )
        );
        registry.addRecipeHandler(GCMenuTypes.ELECTRIC_FURNACE,
                new MachineRecipeHandler<>(
                        ELECTRIC_SMELTING,
                        new int[] {ElectricFurnaceBlockEntity.INPUT_SLOT},
                        ElectricFurnaceBlockEntity.OUTPUT_SLOT + 1
                )
        );
        registry.addRecipeHandler(GCMenuTypes.ELECTRIC_ARC_FURNACE,
                new MachineRecipeHandler<>(
                        ELECTRIC_BLASTING,
                        new int[] {ElectricArcFurnaceBlockEntity.INPUT_SLOT},
                        ElectricArcFurnaceBlockEntity.OUTPUT_SLOTS + ElectricArcFurnaceBlockEntity.OUTPUT_LENGTH
                )
        );
        registry.addRecipeHandler(GCMenuTypes.FOOD_CANNER,
                new MachineRecipeHandler<>(
                        CANNING,
                        IntStream.range(
                                FoodCannerBlockEntity.INPUT_SLOT,
                                FoodCannerBlockEntity.INPUT_SLOT + FoodCannerBlockEntity.INPUT_LENGTH
                        ).toArray(),
                        FoodCannerBlockEntity.OUTPUT_SLOT + 1
                )
        );
        registry.addRecipeHandler(GCMenuTypes.ROCKET_WORKBENCH, new RocketRecipeHandler(ROCKET));

        RecipeManager manager = registry.getRecipeManager();

        for (RecipeHolder<FabricationRecipe> recipe : manager.getAllRecipesFor(GCRecipes.FABRICATION_TYPE)) {
            registry.addRecipe(new FabricationEmiRecipe(recipe));
        }

        for (RecipeHolder<CompressingRecipe> recipe : manager.getAllRecipesFor(GCRecipes.COMPRESSING_TYPE)) {
            boolean nonEmpty = true;
            for (Ingredient ingredient : recipe.value().getIngredients()) {
                if (ingredient.isEmpty()) {
                    nonEmpty = false;
                    break;
                }
            }

            // Prevents recipes such as compressed steel from steel ingots
            // from being displayed when the #c:ingots/steel item tag is empty
            if (nonEmpty) {
                registry.addRecipe(new CompressingEmiRecipe(recipe));
                registry.addRecipe(new ElectricCompressingEmiRecipe(recipe));
            }
        }

        for (RecipeHolder<SmeltingRecipe> recipe : manager.getAllRecipesFor(RecipeType.SMELTING)) {
            registry.addRecipe(new ElectricSmeltingEmiRecipe(recipe));
        }

        for (RecipeHolder<BlastingRecipe> recipe : manager.getAllRecipesFor(RecipeType.BLASTING)) {
            registry.addRecipe(new ElectricBlastingEmiRecipe(recipe));
        }

        for (RecipeHolder<RocketRecipe> recipe : manager.getAllRecipesFor(GCRecipes.ROCKET_TYPE)) {
            registry.addRecipe(new RocketEmiRecipe(recipe));
        }

        for (ItemStack cannedFood : CannedFoodItem.getDefaultCannedFoods()) {
            registry.addRecipe(new CanningEmiRecipe(cannedFood));
        }

        registry.addRecipe(new EmiCraftingRecipe(
                EmergencyKitItem.getContents(DyeColor.RED).stream()
                        .map(itemStack -> itemStack.getItem() instanceof ParachuteItem
                                ? EmiIngredient.of(GCItemTags.PARACHUTES)
                                : EmiStack.of(itemStack))
                        .collect(Collectors.toList()),
                EmiStack.of(GCItems.EMERGENCY_KIT),
                Constant.id("/" + Constant.Recipe.Serializer.EMERGENCY_KIT),
                true
        ));

        addWorldInteraction(registry);
    }

    private static void addWorldInteraction(EmiRegistry registry) {
        List<ItemStack> glassFluidPipes = new ArrayList<>();
        for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(GCItemTags.GLASS_FLUID_PIPES)) {
            glassFluidPipes.add(new ItemStack(holder));
        }

        ResourceLocation pipeInteraction = Constant.id("/world/glass_fluid_pipe/");

        GCBlocks.GLASS_FLUID_PIPES.forEach((pipeColor, block) -> {
                Ingredient dye = pipeColor.dye();
                Item pipe = block.asItem();
                registry.addRecipe(
                        EmiWorldInteractionRecipe.builder()
                                .id(pipeInteraction.withSuffix(pipeColor.getName()))
                                .leftInput(EmiIngredient.of(Ingredient.of(glassFluidPipes.stream().filter(stack -> !stack.is(pipe)))))
                                .rightInput(dye != null ? EmiIngredient.of(dye) : EmiStack.of(Items.WET_SPONGE), dye == null)
                                .output(EmiStack.of(pipe))
                                .supportsRecipeTree(false)
                                .build()
                );
        });

        registry.addRecipe(
                EmiWorldInteractionRecipe.builder()
                        .id(Constant.id("/world/cauldron_washing/parachute"))
                        .leftInput(EmiIngredient.of(Ingredient.of(GCItems.DYED_PARACHUTES.colorMap().values().stream().map(Item::getDefaultInstance))))
                        .rightInput(EmiStack.of(Items.CAULDRON), true)
                        .rightInput(EmiStack.of(Fluids.WATER, FluidConstants.BOTTLE), false)
                        .output(EmiStack.of(GCItems.PARACHUTE))
                        .supportsRecipeTree(false)
                        .build()
        );
    }
}