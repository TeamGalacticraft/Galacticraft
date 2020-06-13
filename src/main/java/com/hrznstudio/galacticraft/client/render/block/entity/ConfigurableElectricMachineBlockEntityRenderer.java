/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.client.render.block.entity;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.block.ConfigurableElectricMachineBlock;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import com.mojang.datafixers.util.Either;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.json.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public abstract class ConfigurableElectricMachineBlockEntityRenderer<T extends ConfigurableElectricMachineBlockEntity> extends BlockEntityRenderer<T> {
    private static final BakedQuadFactory QUAD_FACTORY = new BakedQuadFactory();

    protected static final List<ModelElement> element = generateCube();
    private static final Map<String, BakedModel> MODEL_CACHE = new HashMap<>(); //Manual rotation required when rendering the models

    public ConfigurableElectricMachineBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    private static BakedQuad createQuad(ModelElement element, ModelElementFace elementFace, Sprite sprite, Direction side, ModelBakeSettings settings, Identifier id) {
        return QUAD_FACTORY.bake(element.from, element.to, elementFace, sprite, side, settings, element.rotation, element.shade, id);
    }

    private static void renderQuad(MatrixStack.Entry entry, VertexConsumer vertexConsumer, float f, float g, float h, List<BakedQuad> list, int i, int j) {
        BakedQuad bakedQuad;
        float n;
        float o;
        float p;
        for (Iterator<BakedQuad> var8 = list.iterator(); var8.hasNext(); vertexConsumer.quad(entry, bakedQuad, n, o, p, i, j)) {
            bakedQuad = var8.next();
            if (bakedQuad.hasColor()) {
                n = MathHelper.clamp(f, 0.0F, 1.0F);
                o = MathHelper.clamp(g, 0.0F, 1.0F);
                p = MathHelper.clamp(h, 0.0F, 1.0F);
            } else {
                n = 1.0F;
                o = 1.0F;
                p = 1.0F;
            }
        }

    }

    public int getId(Direction dir) {
        switch (dir) {
            case NORTH:
                return 0;
            case SOUTH:
                return 1;
            case EAST:
                return 2;
            case WEST:
                return 3;
            case UP:
                return 4;
            case DOWN:
                return 5;
            default:
                return -1;
        }
    }

    public BakedModel bakeModel(JsonUnbakedModel model, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings settings, Identifier id, boolean hasDepth) {
        Sprite sprite = textureGetter.apply(model.resolveSprite("particle"));
        if (model.getRootModel() == ModelLoader.BLOCK_ENTITY_MARKER) {
            return new BuiltinBakedModel(model.getTransformations(), ModelOverrideList.EMPTY, sprite, model.getGuiLight().isSide());
        } else {
            BasicBakedModel.Builder builder = (new BasicBakedModel.Builder(model, ModelOverrideList.EMPTY, hasDepth)).setParticle(sprite);

            for (ModelElement modelElement : model.getElements()) {
                for (Direction direction : modelElement.faces.keySet()) {
                    ModelElementFace modelElementFace = modelElement.faces.get(direction);
                    Sprite sprite2 = textureGetter.apply(model.resolveSprite(modelElementFace.textureId));
                    if (modelElementFace.cullFace == null) {
                        builder.addQuad(createQuad(modelElement, modelElementFace, sprite2, direction, settings, id));
                    } else {
                        builder.addQuad(Direction.transform(settings.getRotation().getMatrix(), modelElementFace.cullFace), createQuad(modelElement, modelElementFace, sprite2, direction, settings, id));
                    }
                }
            }

            return builder.build();
        }
    }

    public float getDegrees(Direction dir) {
        switch (dir) {
            case NORTH:
                return 0;
            case SOUTH:
                return 180;
            case EAST:
                return 90;
            case WEST:
                return 270;
        }
        return 0;
    }

    private static List<ModelElement> generateCube() {
        Map<Direction, ModelElementFace> faces = new HashMap<>();
        for (Direction direction : Direction.values()) {
            faces.put(direction, new ModelElementFace(null, -1, direction.getName(), new ModelElementTexture(null, 0)));
        }
        return Collections.singletonList(new ModelElement(new Vector3f(0, 0, 0), new Vector3f(16, 16, 16), faces, null, true));
    }

    public BakedModel getModelForState(T entity, BlockState state) {
        StringBuilder builder = new StringBuilder();
        for (ConfigurableElectricMachineBlock.BlockFace face : ConfigurableElectricMachineBlock.BlockFace.values()) {
            builder.append(((ConfigurableElectricMachineBlock) state.getBlock()).getOption(state, face).asString());
        }
        if (!MODEL_CACHE.containsKey(builder.toString())) {
            Map<String, Either<SpriteIdentifier, String>> texMap = new HashMap<>();
            texMap.put("particle", Either.left(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier(Constants.MOD_ID, "block/empty"))));
            for (Direction direction : Direction.values()) {
                switch (((ConfigurableElectricMachineBlock) state.getBlock()).getOption(state, ConfigurableElectricMachineBlock.BlockFace.toFace(Direction.NORTH, direction))) { //north so that the model doesn't rotate
                    case DEFAULT:
                        texMap.put(direction.getName(), Either.left(getDefaultSpriteId(entity, direction)));
                        break;
                    case POWER_INPUT:
                        texMap.put(direction.getName(), Either.left(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier(Constants.MOD_ID, "block/machine_power_input"))));
                        break;
                    case POWER_OUTPUT:
                        texMap.put(direction.getName(), Either.left(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier(Constants.MOD_ID, "block/machine_power_output"))));
                        break;
                    case OXYGEN_INPUT:
                        texMap.put(direction.getName(), Either.left(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier(Constants.MOD_ID, "block/machine_oxygen_input"))));
                        break;
                    case OXYGEN_OUTPUT:
                        texMap.put(direction.getName(), Either.left(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier(Constants.MOD_ID, "block/machine_oxygen_output"))));
                        break;
                    case FLUID_INPUT:
                        texMap.put(direction.getName(), Either.left(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier(Constants.MOD_ID, "block/machine_fluid_input"))));
                        break;
                    case FLUID_OUTPUT:
                        texMap.put(direction.getName(), Either.left(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier(Constants.MOD_ID, "block/machine_fluid_output"))));
                        break;
                }
            }

            BakedModel model = this.bakeModel(new JsonUnbakedModel(null, element, texMap, true,
                            JsonUnbakedModel.GuiLight.field_21859, ModelTransformation.NONE, new ArrayList<>()),
                    spriteIdentifier -> MinecraftClient.getInstance().getSpriteAtlas(spriteIdentifier.getAtlasId()).apply(spriteIdentifier.getTextureId()),
                    ModelRotation.X0_Y0, new Identifier(Constants.MOD_ID, builder.toString()), true);
            MODEL_CACHE.put(builder.toString(), model);

            return model;
        } else {
            return MODEL_CACHE.get(builder.toString());
        }
    }

    public void render(MatrixStack.Entry entry, VertexConsumer vertexConsumer, @Nullable BlockState blockState, BakedModel bakedModel, float red, float green, float blue, int[] light, int overlay) {
        Random random = new Random();
        Direction[] var13 = Direction.values();

        for (Direction direction : var13) {
            random.setSeed(42L);
            renderQuad(entry, vertexConsumer, red, green, blue, bakedModel.getQuads(blockState, direction, random), light[getId(direction)], overlay);
        }

        random.setSeed(42L);
        renderQuad(entry, vertexConsumer, red, green, blue, bakedModel.getQuads(blockState, null, random), light[getId(Direction.UP)], overlay);
    }

    @Override
    public void render(T blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int ignored, int overlay) {
        int[] light = new int[Direction.values().length];
        for (Direction direction : Direction.values()) {
            light[getId(direction)] = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().offset(direction));
        }
        matrices.push();
        Direction direction = blockEntity.getWorld().getBlockState(blockEntity.getPos()).get(Properties.HORIZONTAL_FACING);
        matrices.translate(0.5F, 0.0F, 0.5F);
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(getDegrees(direction)));
        matrices.translate(-0.5F, 0.0F, -0.5F);
        MinecraftClient.getInstance().getTextureManager().bindTexture(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
        render(matrices.peek(), vertexConsumers.getBuffer(RenderLayers.getEntityBlockLayer(blockEntity.getWorld().getBlockState(blockEntity.getPos()), true)), null, this.getModelForState(blockEntity, blockEntity.getWorld().getBlockState(blockEntity.getPos())), 1.0F, 1.0F, 1.0F, light, overlay);
        matrices.pop();
    }

    @Nonnull
    public SpriteIdentifier getDefaultSpriteId(@Nonnull T entity, @Nonnull Direction direction) {
        switch (direction) {
            case NORTH:
            case SOUTH:
            case EAST:
            case WEST:
                return new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier(Constants.MOD_ID, "block/machine_side"));
            case UP:
            case DOWN:
                return new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier(Constants.MOD_ID, "block/machine"));
        }
        return new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier(Constants.MOD_ID, "block/machine"));
    }
}
