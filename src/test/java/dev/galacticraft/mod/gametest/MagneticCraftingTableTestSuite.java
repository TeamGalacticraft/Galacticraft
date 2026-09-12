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

package dev.galacticraft.mod.gametest;

import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.entity.MagneticCraftingTableBlockEntity;
import dev.galacticraft.mod.screen.MagneticCraftingTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.HopperBlockEntity;

public class MagneticCraftingTableTestSuite implements GalacticraftGameTest {
    private static final BlockPos TABLE_POS = new BlockPos(1, 1, 1);

    @SuppressWarnings("removal")
    @GameTest(template = EMPTY_STRUCTURE)
    public void inventoryPersistsWhenMenuCloses(GameTestHelper context) {
        MagneticCraftingTableBlockEntity table = this.placeTable(context);
        table.setItem(0, new ItemStack(Items.OAK_LOG, 3));

        ServerPlayer player = context.makeMockServerPlayerInLevel();
        MagneticCraftingTableMenu menu = (MagneticCraftingTableMenu) table.createMenu(1, player.getInventory(), player);
        context.assertTrue(menu != null, "Expected the magnetic crafting table to create a menu");
        context.assertTrue(menu.getSlot(MagneticCraftingTableMenu.RESULT_SLOT).getItem().is(Items.OAK_PLANKS), "Expected the table to craft like a vanilla crafting table");
        context.assertValueEqual(menu.getSlot(MagneticCraftingTableMenu.RESULT_SLOT).getItem().getCount(), 4, "The crafting result had the wrong count");
        menu.removed(player);

        context.assertTrue(table.getItem(0).is(Items.OAK_LOG), "Closing the menu removed the crafting ingredient");
        context.assertValueEqual(table.getItem(0).getCount(), 3, "Closing the menu changed the crafting ingredient count");
        context.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void inventoryPersistsThroughSerialization(GameTestHelper context) {
        MagneticCraftingTableBlockEntity table = this.placeTable(context);
        table.setItem(0, new ItemStack(Items.IRON_INGOT, 7));
        table.setItem(8, new ItemStack(Items.REDSTONE, 12));

        CompoundTag tag = table.saveWithoutMetadata(context.getLevel().registryAccess());
        MagneticCraftingTableBlockEntity loaded = new MagneticCraftingTableBlockEntity(table.getBlockPos(), table.getBlockState());
        loaded.loadWithComponents(tag, context.getLevel().registryAccess());

        context.assertTrue(loaded.getItem(0).is(Items.IRON_INGOT), "The first crafting slot was not serialized");
        context.assertValueEqual(loaded.getItem(0).getCount(), 7, "The first crafting slot count was not serialized");
        context.assertTrue(loaded.getItem(8).is(Items.REDSTONE), "The last crafting slot was not serialized");
        context.assertValueEqual(loaded.getItem(8).getCount(), 12, "The last crafting slot count was not serialized");
        context.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void hopperCanInsertButNotExtractIngredients(GameTestHelper context) {
        MagneticCraftingTableBlockEntity table = this.placeTable(context);
        BlockPos hopperPos = TABLE_POS.above();
        context.setBlock(hopperPos, Blocks.HOPPER.defaultBlockState().setValue(HopperBlock.FACING, Direction.DOWN));
        HopperBlockEntity hopper = context.getBlockEntity(hopperPos);
        hopper.setItem(0, new ItemStack(Items.DIAMOND));

        context.runAtTickTime(20, () -> {
            context.assertTrue(table.hasAnyMatching(stack -> stack.is(Items.DIAMOND)), "The hopper did not insert its ingredient");

            BlockPos extractionHopperPos = TABLE_POS.below();
            context.setBlock(extractionHopperPos, Blocks.HOPPER);
            HopperBlockEntity extractionHopper = context.getBlockEntity(extractionHopperPos);
            context.runAtTickTime(40, () -> {
                context.assertTrue(table.hasAnyMatching(stack -> stack.is(Items.DIAMOND)), "A hopper extracted an ingredient from the table");
                context.assertTrue(extractionHopper.isEmpty(), "A hopper extracted an item from the table");
                context.succeed();
            });
        });
    }

    private MagneticCraftingTableBlockEntity placeTable(GameTestHelper context) {
        context.setBlock(TABLE_POS, GCBlocks.MAGNETIC_CRAFTING_TABLE);
        return context.getBlockEntity(TABLE_POS);
    }
}
