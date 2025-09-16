/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

package dev.galacticraft.mod.client.model;

import dev.galacticraft.api.component.GCDataComponents;
import dev.galacticraft.mod.content.block.decoration.CannedFoodBlock;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.SpriteFinder;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Supplier;

public class CannedFoodBakedModel extends ForwardingBakedModel {
    public CannedFoodBakedModel(BakedModel bakedModel) {
        this.wrapped = bakedModel;
    }

    // Define relative positions for each can layout
    public static float[][][] POSITIONS = {
            {{8, 0, 8}}, // 1 can
            {{4, 0, 8}, {12, 0, 8}}, // 2 cans
            {{4, 0, 4}, {12, 0, 6}, {6, 0, 12}}, // 3 cans
            {{4, 0, 4}, {12, 0, 4}, {4, 0, 12}, {12, 0, 12}}, // 4 cans
            {{4, 0, 4}, {12, 0, 4}, {4, 0, 12}, {12, 0, 12}, {8, 8, 8}}, // 5 cans (layer 2 starts)
            {{4, 0, 4}, {12, 0, 4}, {4, 0, 12}, {12, 0, 12}, {4, 8, 8}, {12, 8, 8}}, // 6 cans
            {{4, 0, 4}, {12, 0, 4}, {4, 0, 12}, {12, 0, 12}, {4, 8, 4}, {12, 8, 6}, {6, 8, 12}}, // 7 cans
            {{4, 0, 4}, {12, 0, 4}, {4, 0, 12}, {12, 0, 12}, {4, 8, 4}, {12, 8, 4}, {4, 8, 12}, {12, 8, 12}} // 8 cans (full)
    };

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        List<ItemStack> contents = (List<ItemStack>) blockView.getBlockEntityRenderData(pos);
        int canCount = contents.size();

        TextureAtlas atlas = Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS);
        SpriteFinder spriteFinder = SpriteFinder.get(atlas);

        for (int i = 0; i < canCount; i++) {
            float[] position = POSITIONS[canCount - 1][i];
            float x = (position[0] - 8) / 16.0f; // Convert pixel coords to block space
            float y = position[1] / 16.0f;
            float z = (position[2] - 8) / 16.0f;

            ItemStack stack = contents.get(i);
            int canColor = stack.getOrDefault(GCDataComponents.COLOR, 0);

            Direction facing = state.getValue(CannedFoodBlock.FACING);

            context.pushTransform(quad -> {
                quad.pos(0, quad.x(0) + x, quad.y(0) + y, quad.z(0) + z);
                quad.pos(1, quad.x(1) + x, quad.y(1) + y, quad.z(1) + z);
                quad.pos(2, quad.x(2) + x, quad.y(2) + y, quad.z(2) + z);
                quad.pos(3, quad.x(3) + x, quad.y(3) + y, quad.z(3) + z);

                if (spriteFinder.find(quad).contents().name().toString().contains("canned_food_label_texture")) {
                    quad.color(0, canColor);
                    quad.color(1, canColor);
                    quad.color(2, canColor);
                    quad.color(3, canColor);
                }
                return true;
            });
            this.wrapped.emitBlockQuads(blockView, state, pos, randomSupplier, context);
            context.popTransform();
        }
    }
}
