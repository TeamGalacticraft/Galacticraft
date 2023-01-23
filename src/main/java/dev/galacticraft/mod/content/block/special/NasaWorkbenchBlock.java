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

package dev.galacticraft.mod.content.block.special;

import java.util.ArrayList;

import dev.galacticraft.mod.screen.NasaWorkbenchSchematicMenu;
import dev.galacticraft.mod.screen.RocketWorkbenchMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class NasaWorkbenchBlock extends Block {

    public NasaWorkbenchBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (level.isClientSide) { // little confused with the client server relation here, why success if client but consume if other?
            return InteractionResult.SUCCESS;
        }
        player.openMenu(new NasaWorkbenchHandler(0, true)); // not a great memory practice
        return InteractionResult.CONSUME;
    }

    public static class NasaWorkbenchHandler implements MenuProvider {
        private final int page;
        private final boolean next; 
        
        public NasaWorkbenchHandler(int page, boolean next) {
            this.page = page;
            this.next = next;
        }

        private int getNextPage(Player player) {
            ArrayList<Integer> availablePages = new ArrayList();
            availablePages.add(0);
            ServerPlayer serverPlayer = (ServerPlayer) player;
            ServerAdvancementManager manager = serverPlayer.getServer().getAdvancements();
            // TODO: Expand with advancements for new recipes in the future as rockets are added
            if (serverPlayer.getAdvancements().getOrStartProgress(manager.getAdvancement(new ResourceLocation("galacticraft:aerospace/rocket_tier_1"))).isDone()) {
                availablePages.add(1);
            }
            // if (serverPlayer.getAdvancements().getOrStartProgress(manager.getAdvancement(new ResourceLocation("galacticraft:aerospace/rocket_tier_2"))).isDone()) { }

            int index = availablePages.indexOf(page);
            int selected = (next ? index + 1 : index - 1) % availablePages.size();

            if (selected >= 0) {
                return selected;
            } else {
                return availablePages.size() + selected;
            }
        }

        @Override
        public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
            int gotoPage = getNextPage(player);
            switch (gotoPage) { // populate here when adding new recipes
                case 0:
                    return new NasaWorkbenchSchematicMenu(syncId, inv);
                case 1:
                    return new RocketWorkbenchMenu(syncId, inv);
                default:
                    return null;
            }
        }

        @Override
        public Component getDisplayName() {
            return Component.literal("Nasa Workbench"); // TODO: localize // TODO: Could add title for each schematic
        }
    }
}
