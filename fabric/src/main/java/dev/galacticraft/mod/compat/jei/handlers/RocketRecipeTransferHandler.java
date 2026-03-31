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

package dev.galacticraft.mod.compat.jei.handlers;

import dev.galacticraft.mod.compat.jei.GCJEIRecipeTypes;
import dev.galacticraft.mod.recipe.RocketRecipe;
import dev.galacticraft.mod.screen.GCMenuTypes;
import dev.galacticraft.mod.screen.RocketWorkbenchMenu;
import dev.galacticraft.mod.util.Translations;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class RocketRecipeTransferHandler implements IRecipeTransferHandler<RocketWorkbenchMenu, RocketRecipe> {
    private final IRecipeTransferHandlerHelper handlerHelper;

    public RocketRecipeTransferHandler(IRecipeTransferHandlerHelper handlerHelper) {
        this.handlerHelper = handlerHelper;
    }

    @Override
    public Class<? extends RocketWorkbenchMenu> getContainerClass() {
        return RocketWorkbenchMenu.class;
    }

    @Override
    public Optional<MenuType<RocketWorkbenchMenu>> getMenuType() {
        return Optional.of(GCMenuTypes.ROCKET_WORKBENCH);
    }

    @Override
    public RecipeType<RocketRecipe> getRecipeType() {
        return GCJEIRecipeTypes.ROCKET;
    }

    @Nullable
    @Override
    public IRecipeTransferError transferRecipe(RocketWorkbenchMenu menu, RocketRecipe recipe, IRecipeSlotsView recipeSlotsView, Player player, boolean maxTransfer, boolean doTransfer) {
        if (!this.handlerHelper.recipeTransferHasServerSupport()) {
            return this.handlerHelper.createUserErrorWithTooltip(Component.translatable("jei.tooltip.error.recipe.transfer.no.server"));
        }

        int ingredientsSize = recipe.getIngredients().size();
        int recipeSize = menu.getRecipeSize();
        if (ingredientsSize != recipeSize && ingredientsSize != recipeSize + 1) {
            return this.handlerHelper.createUserErrorWithTooltip(Component.translatable(Translations.Tooltip.INCORRECT_NUMBER_OF_SLOTS));
        }

        IRecipeTransferInfo info = this.handlerHelper.createBasicRecipeTransferInfo(
                this.getContainerClass(),
                this.getMenuType().get(),
                this.getRecipeType(),
                0, recipeSize + 1, // Input slots (plus one for chest slot)
                recipeSize + 2, 36 // Player inventory + hotbar slots
        );
        IRecipeTransferHandler<RocketWorkbenchMenu, RocketRecipe> handler = this.handlerHelper.createUnregisteredRecipeTransferHandler(info);
        return handler.transferRecipe(menu, recipe, recipeSlotsView, player, maxTransfer, doTransfer);
    }
}
