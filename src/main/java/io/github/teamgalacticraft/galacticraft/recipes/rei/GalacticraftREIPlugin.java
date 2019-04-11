package io.github.teamgalacticraft.galacticraft.recipes.rei;

import io.github.teamgalacticraft.galacticraft.Constants;
import io.github.teamgalacticraft.galacticraft.blocks.machines.circuitfabricator.CircuitFabricatorScreen;
import io.github.teamgalacticraft.galacticraft.recipes.FabricationRecipe;
import me.shedaniel.rei.api.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Screen;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;


public class GalacticraftREIPlugin implements REIPlugin {
    public static final Identifier CIRCUIT_FABRICATION = new Identifier(Constants.MOD_ID, "plugins/circuit_fabricator");

    @Override
    public void registerItems(ItemRegistry itemRegistry) {
        Registry.ITEM.stream().forEach((item) -> {
            itemRegistry.registerItemStack(item.getDefaultStack());

            try {
                itemRegistry.registerItemStack(itemRegistry.getAllStacksFromItem(item));
            } catch (Exception var3) {
            }
        });
    }

    @Override
    public void registerPluginCategories(RecipeHelper recipeHelper) {
        recipeHelper.registerCategory(new DefaultFabricationCategory());
    }

    @Override
    public void registerOthers(RecipeHelper recipeHelper) {
        recipeHelper.registerRecipeVisibilityHandler(new DisplayVisibilityHandler() {
            public DisplayVisibility handleDisplay(RecipeCategory category, RecipeDisplay display) {
                return DisplayVisibility.ALWAYS_VISIBLE;
            }

            public float getPriority() {
                return -1.0F;
            }
        });

        recipeHelper.registerSpeedCraftFunctional(CIRCUIT_FABRICATION, new SpeedCraftFunctional<DefaultFabricationDisplay>() {
            public Class[] getFunctioningFor() {
                return new Class[]{CircuitFabricatorScreen.class};
            }

            public boolean performAutoCraft(Screen screen, DefaultFabricationDisplay recipe) {
                if (!recipe.getRecipe().isPresent()) {
                    return false;
                } else if (screen instanceof CircuitFabricatorScreen) {
                    MinecraftClient.getInstance().interactionManager.clickRecipe(MinecraftClient.getInstance().player.container.syncId, (Recipe) recipe.getRecipe().get(), Screen.hasShiftDown());
                    return true;
                } else {
                    return false;
                }
            }

            public boolean acceptRecipe(Screen screen, DefaultFabricationDisplay recipe) {
                return screen instanceof CircuitFabricatorScreen;
            }
        });

        recipeHelper.registerDefaultSpeedCraftButtonArea(GalacticraftREIPlugin.CIRCUIT_FABRICATION);
    }

    @Override
    public void registerRecipeDisplays(RecipeHelper recipeHelper) {
        for (Recipe<?> value : recipeHelper.getRecipeManager().values()) {
            if (value instanceof FabricationRecipe) {
                recipeHelper.registerDisplay(CIRCUIT_FABRICATION, new DefaultFabricationDisplay((FabricationRecipe) value));
            }
        }
    }
}