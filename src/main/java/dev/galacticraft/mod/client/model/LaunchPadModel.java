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

package dev.galacticraft.mod.client.model;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.block.special.rocketlaunchpad.RocketLaunchPadBlock;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.SpriteFinder;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class LaunchPadModel extends ForwardingBakedModel {
    private static final int TEXTURE_SCALE = 3; // 48 / 16 = 3 todo support higher res textures
    public LaunchPadModel(BakedModel model) {
        this.wrapped = model;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        context.pushTransform(quad -> {
            TextureAtlas atlas = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS);
            TextureAtlasSprite connectedSprite = atlas.getSprite(Constant.id("connected/launch_pad"));

            TextureAtlasSprite original = SpriteFinder.get(atlas).find(quad, 0);
            for (int vertex = 0; vertex < 4; vertex++) {
                int tileX = 0, tileY = 0;
                RocketLaunchPadBlock.Part part = state.getValue(RocketLaunchPadBlock.PART);
                switch (part) {
                    case CENTER, NONE -> {
                        tileX = 1;
                        tileY = 1;
                    }
                    case EAST -> {
                        tileX = 2;
                        tileY = 1;
                    }
                    case SOUTH -> {
                        tileX = 1;
                        tileY = 2;
                    }
                    case SOUTH_EAST -> {
                        tileX = 2;
                        tileY = 2;
                    }
                    case SOUTH_WEST -> tileY = 2;
                    case WEST -> tileY = 1;
                    case NORTH -> tileX = 1;
                    case NORTH_EAST -> tileX = 2;
                }
                int index = tileX + TEXTURE_SCALE * tileY;
                quad.sprite(vertex, 0, getTargetU(original, connectedSprite, quad.spriteU(vertex, 0), index), getTargetV(original, connectedSprite, quad.spriteV(vertex, 0), index));
            }
            return true;
        });
        super.emitBlockQuads(blockView, state, pos, randomSupplier, context);
        context.popTransform();
    }

    public float getTargetU(TextureAtlasSprite original, TextureAtlasSprite connected, float localU, int index) {
        float uOffset = (index % TEXTURE_SCALE);
        return connected.getU(
                (getUnInterpolatedU(original, localU) + (uOffset * 16)) / ((float) TEXTURE_SCALE));
    }

    public float getTargetV(TextureAtlasSprite original, TextureAtlasSprite connected, float localV, int index) {
        float vOffset = (index / TEXTURE_SCALE);
        return connected.getV(
                (getUnInterpolatedV(original, localV) + (vOffset * 16)) / ((float) TEXTURE_SCALE));
    }

    public static float getUnInterpolatedU(TextureAtlasSprite sprite, float u) {
        float f = sprite.getU1() - sprite.getU0();
        return (u - sprite.getU0()) / f * 16.0F;
    }

    public static float getUnInterpolatedV(TextureAtlasSprite sprite, float v) {
        float f = sprite.getV1() - sprite.getV0();
        return (v - sprite.getV0()) / f * 16.0F;
    }
}
