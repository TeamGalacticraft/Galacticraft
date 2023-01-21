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

import dev.galacticraft.mod.screen.NasaWorkbenchMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class NasaWorkbenchBlock extends Block implements MenuProvider {
    // private static final Component CONTAINER_TITLE = Component.translatable("container.crafting");

    public NasaWorkbenchBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (level.isClientSide) { // little confused with the client server relation here, why success if client but consume if other?
            return InteractionResult.SUCCESS;
        }
        // MenuProvider menuProvider = blockState.getMenuProvider(level, blockPos);
        player.openMenu(this);
        // player.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
        return InteractionResult.CONSUME;
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        // TODO Auto-generated method stub
        return new NasaWorkbenchMenu(syncId, player);
    }

    @Override
    public Component getDisplayName() {
        // TODO Auto-generated method stub
        return Component.literal("Nasa Workbench");
    }


    // @Override
    // @Deprecated
    // public MenuProvider getMenuProvider(BlockState blockState, Level level, BlockPos blockPos) { // method needed?
    //     // return new SimpleMenuProvider((i, inventory, player) -> new CraftingMenu(i, inventory, ContainerLevelAccess.create(level, blockPos)), CONTAINER_TITLE);
    //     return new SimpleMenuProvider((i, inventory, player) -> new NasaWorkbenchMenu(i, player), CONTAINER_TITLE);
    // }

    // @Override
    // public AirlockControllerMenu createMenu(int syncId, Inventory inventory, Player player) {
    //     return new AirlockControllerMenu(syncId, inventory);
    // }
}
