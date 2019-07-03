package com.hrznstudio.galacticraft.recipes.rei;

import com.google.common.collect.Lists;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.screen.MachineContainerScreen;
import com.hrznstudio.galacticraft.blocks.machines.circuitfabricator.CircuitFabricatorScreen;
import com.hrznstudio.galacticraft.blocks.machines.compressor.CompressorScreen;
import com.hrznstudio.galacticraft.recipes.FabricationRecipe;
import com.hrznstudio.galacticraft.recipes.ShapedCompressingRecipe;
import com.hrznstudio.galacticraft.recipes.ShapelessCompressingRecipe;
import me.shedaniel.rei.api.DisplayHelper;
import me.shedaniel.rei.api.REIPluginEntry;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.SpeedCraftFunctional;
import me.shedaniel.rei.client.ScreenHelper;
import me.shedaniel.rei.listeners.ContainerScreenHooks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */

public class GalacticraftREIPlugin implements REIPluginEntry {
    public static final Identifier CIRCUIT_FABRICATION = new Identifier(Constants.MOD_ID, "plugins/circuit_fabricator");
    public static final Identifier COMPRESSING = new Identifier(Constants.MOD_ID, "plugins/compressing");

    @Override
    public void registerPluginCategories(RecipeHelper recipeHelper) {
        recipeHelper.registerCategory(new DefaultFabricationCategory());
        recipeHelper.registerCategory(new DefaultCompressingCategory());
    }

    @Override
    public void registerOthers(RecipeHelper recipeHelper) {
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

        recipeHelper.registerSpeedCraftFunctional(COMPRESSING, new SpeedCraftFunctional<DefaultCompressingDisplay>() {
            public Class[] getFunctioningFor() {
                return new Class[]{CompressorScreen.class};
            }

            public boolean performAutoCraft(Screen screen, DefaultCompressingDisplay recipe) {
                if (!recipe.getRecipe().isPresent()) {
                    return false;
                } else if (screen instanceof CompressorScreen) {
                    MinecraftClient.getInstance().interactionManager.clickRecipe(MinecraftClient.getInstance().player.container.syncId, (Recipe) recipe.getRecipe().get(), Screen.hasShiftDown());
                    return true;
                } else {
                    return false;
                }
            }

            public boolean acceptRecipe(Screen screen, DefaultCompressingDisplay recipe) {
                return screen instanceof CompressorScreen;
            }
        });

        recipeHelper.registerSpeedCraftButtonArea(CIRCUIT_FABRICATION, (bounds) -> new Rectangle((int) bounds.getMaxX() - 16, (int) bounds.getMinY() + 6, 10, 10));
    }

    @Override
    public Identifier getPluginIdentifier() {
        return new Identifier(Constants.MOD_ID, Constants.MOD_ID);
    }

    @Override
    public void registerRecipeDisplays(RecipeHelper recipeHelper) {
        for (Recipe<?> value : recipeHelper.getRecipeManager().values()) {
            if (value instanceof FabricationRecipe) {
                recipeHelper.registerDisplay(CIRCUIT_FABRICATION, new DefaultFabricationDisplay((FabricationRecipe) value));
            } else if (value instanceof ShapelessCompressingRecipe) {
                recipeHelper.registerDisplay(COMPRESSING, new DefaultShapelessCompressingDisplay((ShapelessCompressingRecipe) value));
            } else if (value instanceof ShapedCompressingRecipe) {
                recipeHelper.registerDisplay(COMPRESSING, new DefaultShapedCompressingDisplay((ShapedCompressingRecipe) value));
            }
        }
    }

    @Override
    public void registerBounds(DisplayHelper displayHelper) {
        displayHelper.getBaseBoundsHandler().registerExclusionZones(MachineContainerScreen.class, isOnRightSide -> {
            ContainerScreenHooks screenHooks = ScreenHelper.getLastContainerScreenHooks();
            AbstractContainerScreen screen = ScreenHelper.getLastContainerScreen();
            MachineContainerScreen machineScreen = (MachineContainerScreen) screen;
            List<Rectangle> l = Lists.newArrayList();

            if (machineScreen.IS_SECURITY_OPEN) {
                l.add(new Rectangle(screenHooks.rei_getContainerLeft() + screenHooks.rei_getContainerWidth(), screenHooks.rei_getContainerTop(), MachineContainerScreen.SECURITY_PANEL_WIDTH, MachineContainerScreen.SECURITY_PANEL_HEIGHT));
            } else {
                l.add(new Rectangle(screenHooks.rei_getContainerLeft() + screenHooks.rei_getContainerWidth(), screenHooks.rei_getContainerTop(), 20, 20));
            }

            return l;
        });
    }
}