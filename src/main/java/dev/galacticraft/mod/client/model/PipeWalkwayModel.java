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

package dev.galacticraft.mod.client.model;

import dev.galacticraft.mod.api.block.entity.Connected;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

/**
 * The walkway attached to a pipe or wire: the platform on the side of the walkway and the arm from the pipe to it.
 */
@Environment(EnvType.CLIENT)
public class PipeWalkwayModel {
    /**
     * The rotations of the platform model, which faces up, to the side of the walkway.
     */
    private static final Map<Direction, BlockModelRotation> ROTATIONS = Map.of(
            Direction.UP, BlockModelRotation.X0_Y0,
            Direction.DOWN, BlockModelRotation.X180_Y0,
            Direction.NORTH, BlockModelRotation.X90_Y0,
            Direction.SOUTH, BlockModelRotation.X270_Y0,
            Direction.EAST, BlockModelRotation.X90_Y90,
            Direction.WEST, BlockModelRotation.X270_Y90
    );
    private static final Direction[] CULL_FACES = {null, Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

    private final Map<Direction, BakedModel> platforms;
    private final WalkwayCenterModel.Baked arm;
    /**
     * Cutout, also for pipes and wires that are rendered as solid or translucent.
     */
    private final RenderMaterial material = RendererAccess.INSTANCE.getRenderer().materialFinder().blendMode(BlendMode.CUTOUT).find();

    public PipeWalkwayModel(ModelBaker baker, Function<Material, TextureAtlasSprite> textureGetter, ResourceLocation platform, ResourceLocation texture, float radius, @Nullable ResourceLocation endTexture) {
        this.platforms = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.values()) {
            this.platforms.put(direction, baker.bake(platform, ROTATIONS.get(direction)));
        }
        this.arm = new WalkwayCenterModel.Baked(textureGetter, texture, radius, endTexture);
    }

    public void emit(Connected connected, Direction walkway, QuadEmitter emitter, RandomSource random) {
        this.arm.emitArm(connected, walkway, emitter);
        BakedModel platform = this.platforms.get(walkway);
        for (Direction cullFace : CULL_FACES) {
            for (BakedQuad quad : platform.getQuads(null, cullFace, random)) {
                emitter.fromVanilla(quad, this.material, cullFace).emit();
            }
        }
    }
}
