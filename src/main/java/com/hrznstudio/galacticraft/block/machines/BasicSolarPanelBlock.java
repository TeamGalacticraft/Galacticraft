/*
 * Copyright (c) 2020 HRZN LTD
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
 *
 */

package com.hrznstudio.galacticraft.block.machines;

import com.hrznstudio.galacticraft.api.block.ConfigurableMachineBlock;
import com.hrznstudio.galacticraft.api.block.MultiBlockBase;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.block.entity.BasicSolarPanelBlockEntity;
import com.hrznstudio.galacticraft.screen.BasicSolarPanelScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class BasicSolarPanelBlock extends ConfigurableMachineBlock implements MultiBlockBase {
    public BasicSolarPanelBlock(Settings settings) {
        super(settings, BasicSolarPanelScreenHandler::new);
    }

    @Override
    public ConfigurableMachineBlockEntity createBlockEntity(BlockView view) {
        return new BasicSolarPanelBlockEntity();
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);

        for (BlockPos otherPart : getOtherParts(state, pos)) {
            world.setBlockState(otherPart, Blocks.AIR.getDefaultState(), 3);
        }
    }

    @Override
    public List<BlockPos> getOtherParts(BlockState state, BlockPos pos) {
        return MultiBlockBase.genPartList(pos);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView WorldView, BlockPos pos) {
        for (BlockPos otherPart : getOtherParts(state, pos)) {
            if (!WorldView.getBlockState(otherPart).getMaterial().isReplaceable()) {
                return false;
            }
        }
        return super.canPlaceAt(state, WorldView, pos);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        onMultiblockPlaced(world, pos, state);
    }

    @Override
    public void onPartDestroyed(World world, PlayerEntity player, BlockState state, BlockPos pos, BlockState partState, BlockPos partPos) {
        world.breakBlock(pos, !player.isCreative());

        for (BlockPos otherPart : getOtherParts(state, pos)) {
            if (!world.getBlockState(otherPart).isAir()) {
                world.setBlockState(otherPart, Blocks.AIR.getDefaultState(), 3);
            }
        }
    }

    @Override
    public Text machineInfo(ItemStack stack, BlockView blockView, TooltipContext tooltipContext) {
        return new TranslatableText("tooltip.galacticraft-rewoven.basic_solar_panel").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));
    }
}