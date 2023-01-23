/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

package dev.galacticraft.mod.screen;

import java.util.Optional;

import dev.galacticraft.mod.content.item.GCItem;
import dev.galacticraft.mod.recipe.GalacticraftRecipe;
import dev.galacticraft.mod.recipe.RocketeeringRecipe;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class RocketWorkbenchMenu extends AbstractNasaWorkbenchMenu {
    private final ResultContainer resultSlots = new ResultContainer();

    public RocketWorkbenchMenu(int syncId, Inventory inv) {
        super(syncId, inv, GCMenuTypes.ROCKET_WORKBENCH_MENU, 220, 17, GalacticraftRecipe.ROCKETEERING_TYPE);
        // this OR super ????

        this.addSlot(new ResultSlot(inv.player, this.craftSlots, this.resultSlots, 0, 142, 96)); // first or last?
        // Top Row
        this.addSlot(new Slot(this.craftSlots, 0, 48, 19));
        // Main Body
        for (int i = 0; i < 3; i++) {
            this.addSlot(new Slot(this.craftSlots, (i*2)+1, 39, 37+(i*18)));
            this.addSlot(new Slot(this.craftSlots, (i*2)+2, 57, 37+(i*18)));
        }
        // 4 piece row
        for (int i = 0; i < 4; i++) {
            this.addSlot(new Slot(this.craftSlots, i+7, 21+(i*18), 91));
        }
        // Bottom row
        for (int i = 0; i < 3; i++) {
            this.addSlot(new Slot(this.craftSlots, i+11, 21+(i*27), 109));
        }
        // Chest addons
        for (int i = 0; i < 3; i++) {
            this.addSlot(new Slot(this.craftSlots, i+14, 93+(i*26), 12));
        }
    }

    // TODO: should extract this method to a intermediate class between this and super
    @Override
    public void slotsChanged(Container container) {
        RocketeeringRecipe recipe;
        Level level = this.inventory.player.level;
        if (level.isClientSide) {
            return; // guard for safety
        }
        ServerPlayer serverPlayer = (ServerPlayer)this.inventory.player;

        ItemStack itemStack = ItemStack.EMPTY; 
        // Need to do a catch
        Optional<RocketeeringRecipe> optional = level.getServer().getRecipeManager().getRecipeFor(this.recipeType, this.craftSlots, level); // throwing error
        if (optional.isPresent() && resultSlots.setRecipeUsed(level, serverPlayer, recipe = optional.get())) {
            itemStack = recipe.assemble(craftSlots);
        }
        this.resultSlots.setItem(36, itemStack);
        this.setRemoteSlot(36, itemStack); //36
        serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(this.containerId, this.incrementStateId(), 36, itemStack));
    }

    @Override
    protected boolean validForBlueprintSpot(int index, ItemStack stack) {
        return true;
    }
}