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

package dev.galacticraft.mod.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.decoration.FlagBlock;
import dev.galacticraft.mod.content.block.entity.decoration.FlagBlockEntity;
import dev.galacticraft.mod.content.item.FlagItem;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class FlagItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    @Override
    public void render(ItemStack stack, ItemDisplayContext context, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        DyeColor color = DyeColor.WHITE;
        if (stack.getItem() instanceof FlagItem flag) {
            color = flag.getColor();
        }

        Block flagBlock = GCBlocks.FLAGS.get(color);
        FlagBlockEntity flag = new FlagBlockEntity(BlockPos.ZERO, flagBlock.defaultBlockState(), color);
        flag.applyComponentsFromItemStack(stack);

        Minecraft client = Minecraft.getInstance();
        client.getBlockEntityRenderDispatcher().renderItem(flag, matrices, vertexConsumers, light, overlay);

        BlockState base = flagBlock.defaultBlockState();
        client.getBlockRenderer().renderSingleBlock(base, matrices, vertexConsumers, light, overlay);
        matrices.pushPose();
        matrices.translate(0, 1, 0);
        client.getBlockRenderer().renderSingleBlock(base.setValue(FlagBlock.SECTION, FlagBlock.Section.MIDDLE), matrices, vertexConsumers, light, overlay);
        matrices.popPose();
        matrices.pushPose();
        matrices.translate(0, 2, 0);
        client.getBlockRenderer().renderSingleBlock(base.setValue(FlagBlock.SECTION, FlagBlock.Section.TOP), matrices, vertexConsumers, light, overlay);
        matrices.popPose();
    }
}
