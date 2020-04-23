/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.recipes.rei;

import com.google.common.collect.Lists;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.mixin.client.HandledScreenHooks;
import com.hrznstudio.galacticraft.api.screen.MachineContainerScreen;
import com.hrznstudio.galacticraft.recipes.FabricationRecipe;
import com.hrznstudio.galacticraft.recipes.ShapedCompressingRecipe;
import com.hrznstudio.galacticraft.recipes.ShapelessCompressingRecipe;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.BaseBoundsHandler;
import me.shedaniel.rei.api.DisplayHelper;
import me.shedaniel.rei.api.REIHelper;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Environment(EnvType.CLIENT)
public class GalacticraftREIPlugin implements REIPluginV0 {
    public static final Identifier CIRCUIT_FABRICATION = new Identifier(Constants.MOD_ID, "plugins/circuit_fabricator");
    public static final Identifier COMPRESSING = new Identifier(Constants.MOD_ID, "plugins/compressing");


    public void registerPluginCategories(RecipeHelper recipeHelper) {
        recipeHelper.registerCategory(new DefaultFabricationCategory());
        recipeHelper.registerCategory(new DefaultCompressingCategory());
    }

    /*@Override
    public void registerOthers(RecipeHelper recipeHelper) {
        recipeHelper.registerAutoCraftingHandler(new AutoTransferHandler() {
            @Override
            public Result handle(Context context) {
                return null;
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
    }*/

    @Override
    public Identifier getPluginIdentifier() {
        return new Identifier(Constants.MOD_ID, Constants.MOD_ID);
    }

    @Override
    public void registerRecipeDisplays(RecipeHelper recipeHelper) {
        for (Recipe<?> value : recipeHelper.getRecipeManager().values()) {
            if (value instanceof FabricationRecipe) {
                recipeHelper.registerDisplay(new DefaultFabricationDisplay((FabricationRecipe) value));
            } else if (value instanceof ShapelessCompressingRecipe) {
                recipeHelper.registerDisplay(new DefaultShapelessCompressingDisplay((ShapelessCompressingRecipe) value));
            } else if (value instanceof ShapedCompressingRecipe) {
                recipeHelper.registerDisplay(new DefaultShapedCompressingDisplay((ShapedCompressingRecipe) value));
            }
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void registerBounds(DisplayHelper displayHelper) {
        //noinspection UnstableApiUsage
        BaseBoundsHandler.getInstance().registerExclusionZones(MachineContainerScreen.class, () -> {
            HandledScreen screen = REIHelper.getInstance().getPreviousHandledScreen();
            MachineContainerScreen machineScreen = (MachineContainerScreen) screen;
            HandledScreenHooks screenHooks = (HandledScreenHooks)machineScreen;
            List<Rectangle> l = Lists.newArrayList();

            if (machineScreen.IS_SECURITY_OPEN) {
                l.add(new Rectangle(screenHooks.gc_getX() + screenHooks.gc_getBackgroundWidth(), screenHooks.gc_getY(), MachineContainerScreen.SECURITY_PANEL_WIDTH, MachineContainerScreen.SECURITY_PANEL_HEIGHT));
            } else {
                l.add(new Rectangle(screenHooks.gc_getX() + screenHooks.gc_getBackgroundWidth(), screenHooks.gc_getY(), 20, 20));
            }

            return l;
        });
    }
}