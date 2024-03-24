package dev.galacticraft.mod.client.model;

import dev.galacticraft.mod.Constant;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class VacuumGlassBakedModel implements BakedModel {
    public static final ResourceLocation VACUUM_GLASS_MODEL = Constant.id("vacuum_glass");
    public static final VacuumGlassBakedModel INSTANCE = new VacuumGlassBakedModel();

    private static final float PANE_INSET = 6.0f / 16.0f;
    private static final float INNER_FRAME_INSET = 5.0f / 16.0f;
    private static final float INNER_FRAME_THICKNESS = 3.0f / 16.0f;
    private static final float FRAME_INSET = 4.0f / 16.0f;
    private static final float FRAME_THICKNESS = 2.0f / 16.0f;

    public VacuumGlassBakedModel() {
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, RandomSource random) {
        return Collections.emptyList();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return Minecraft.getInstance().getPaintingTextures().getBackSprite();//todo
    }

    @Override
    public ItemTransforms getTransforms() {
        return ItemTransforms.NO_TRANSFORMS;
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        QuadEmitter emitter = context.getEmitter();
        boolean up = state.getValue(BlockStateProperties.UP);
        boolean down = state.getValue(BlockStateProperties.DOWN);
        boolean north = state.getValue(BlockStateProperties.NORTH);
        boolean east = state.getValue(BlockStateProperties.EAST);
        boolean south = state.getValue(BlockStateProperties.SOUTH);
        boolean west = state.getValue(BlockStateProperties.WEST);
        int horizontal = (north ? 1 : 0) + (east ? 1 : 0) + (south ? 1 : 0) + (west ? 1 : 0);
        context.pushTransform(quad -> {
            quad.color(-1, -1, -1, -1).spriteBake(Minecraft.getInstance().getPaintingTextures().getBackSprite(), QuadEmitter.BAKE_LOCK_UV);
            return true;
        });
        switch (horizontal) {
            case 0 -> {
                Direction.Axis axis = state.getValue(BlockStateProperties.HORIZONTAL_AXIS);
                switch (axis) {
                    case X -> {
                        emitPane(emitter, Direction.NORTH, false, false, down, up);
                        emitPane(emitter, Direction.SOUTH, false, false, down, up);
                        emitBasePlate(emitter, Direction.NORTH, down, up);
                        emitSides(emitter, Direction.NORTH, false, false);
                    }
                    case Z -> {
                        emitPane(emitter, Direction.EAST, false, false, down, up);
                        emitPane(emitter, Direction.WEST, false, false, down, up);
                        emitBasePlate(emitter, Direction.EAST, down, up);
                        emitSides(emitter, Direction.EAST, false, false);
                    }
                }
            }
            case 1 -> {
                if (east || west) {
                    emitPane(emitter, Direction.NORTH, east, west, down, up);
                    emitPane(emitter, Direction.SOUTH, west, east, down, up);
                    emitBasePlate(emitter, Direction.NORTH, down, up);
                    emitSides(emitter, Direction.NORTH, east, west);
                } else {
                    emitPane(emitter, Direction.EAST, south, north, down, up);
                    emitPane(emitter, Direction.WEST, north, south, down, up);
                    emitBasePlate(emitter, Direction.EAST, down, up);
                    emitSides(emitter, Direction.EAST, south, north);
                }
            }
            case 2 -> {
                if (east && west) {
                    emitPane(emitter, Direction.NORTH, true, true, down, up);
                    emitPane(emitter, Direction.SOUTH, true, true, down, up);
                    emitBasePlate(emitter, Direction.NORTH, down, up);
                } else if (north && south) {
                    emitPane(emitter, Direction.EAST, true, true, down, up);
                    emitPane(emitter, Direction.WEST, true, true, down, up);
                    emitBasePlate(emitter, Direction.EAST, down, up);
                } else {
                    emitCornerPane(emitter, east, down, north, up);
                    emitCornerBasePlate(emitter, east, north, down, up);
                }
            }
            case 3 -> {
                if (east && west) {
                    if (north) {
                        emitBrokenPane(emitter, Direction.NORTH, down, up);
                        emitPane(emitter, Direction.SOUTH, true, true, down, up);
                        emitCenterPane(emitter, Direction.EAST, down, up);
                    } else {
                        emitBrokenPane(emitter, Direction.SOUTH, down, up);
                        emitPane(emitter, Direction.NORTH, true, true, down, up);
                        emitCenterPane(emitter, Direction.WEST, down, up);
                    }
                } else {
                    if (east) {
                        emitBrokenPane(emitter, Direction.EAST, down, up);
                        emitPane(emitter, Direction.WEST, true, true, down, up);
                        emitCenterPane(emitter, Direction.SOUTH, down, up);
                    } else {
                        emitBrokenPane(emitter, Direction.WEST, down, up);
                        emitPane(emitter, Direction.EAST, true, true, down, up);
                        emitCenterPane(emitter, Direction.NORTH, down, up);
                    }
                }
            }
            case 4 -> {
                emitBrokenPane(emitter, Direction.NORTH, down, up);
                emitBrokenPane(emitter, Direction.EAST, down, up);
                emitBrokenPane(emitter, Direction.SOUTH, down, up);
                emitBrokenPane(emitter, Direction.WEST, down, up);
            }
        }
        context.popTransform();

        BakedModel.super.emitBlockQuads(blockView, state, pos, randomSupplier, context);
    }

    private static void emitSides(QuadEmitter emitter, Direction direction, boolean left, boolean right) {
        if (!left) {
            // OUTER FRAME
            emitter.square(direction.getClockWise(), FRAME_INSET, 0.0f, 1.0f - FRAME_INSET, 1.0f, 0.0f).emit();

            // INNER FRAME PANE
            emitter.square(direction, 0.0f, 0.0f, FRAME_THICKNESS, 1.0f, FRAME_INSET).emit();
            emitter.square(direction, FRAME_THICKNESS, 0.0f, INNER_FRAME_THICKNESS, 1.0f, INNER_FRAME_INSET).emit();

            // INNER FRAME PANE CONNECTOR
            emitter.square(direction.getCounterClockWise(), FRAME_INSET, FRAME_THICKNESS, INNER_FRAME_INSET, 1.0f - FRAME_THICKNESS, 1.0f - FRAME_THICKNESS).emit();

            // INNER FRAME PANE (BACK)
            emitter.square(direction.getOpposite(), 1.0f - FRAME_THICKNESS, 0.0f, 1.0f, 1.0f, FRAME_INSET).emit();
            emitter.square(direction.getOpposite(), 1.0f - INNER_FRAME_THICKNESS, 0.0f, 1.0f - FRAME_THICKNESS, 1.0f, INNER_FRAME_INSET).emit();

            // INNER FRAME PANE CONNECTOR (BACK)
            emitter.square(direction.getCounterClockWise(), 1.0f - INNER_FRAME_INSET, FRAME_THICKNESS, 1.0f - FRAME_INSET, 1.0f - FRAME_THICKNESS, 1.0f - FRAME_THICKNESS).emit();

            // INNER FRAME
            emitter.square(direction.getCounterClockWise(), INNER_FRAME_INSET, 0.0f, 1.0f - INNER_FRAME_INSET, 1.0f, 1.0f - INNER_FRAME_THICKNESS).emit();
        }
        if (!right) {
            // OUTER FRAME
            emitter.square(direction.getCounterClockWise(), FRAME_INSET, 0.0f, 1.0f - FRAME_INSET, 1.0f, 0.0f).emit();

            // INNER FRAME PANE
            emitter.square(direction.getOpposite(), 0.0f, 0.0f, FRAME_THICKNESS, 1.0f, FRAME_INSET).emit();
            emitter.square(direction.getOpposite(), FRAME_THICKNESS, 0.0f, INNER_FRAME_THICKNESS, 1.0f, INNER_FRAME_INSET).emit();

            // INNER FRAME PANE CONNECTOR
            emitter.square(direction.getClockWise(), FRAME_INSET, FRAME_THICKNESS, INNER_FRAME_INSET, 1.0f - FRAME_THICKNESS, 1.0f - FRAME_THICKNESS).emit();

            // INNER FRAME PANE (BACK)
            emitter.square(direction, 1.0f - FRAME_THICKNESS, 0.0f, 1.0f, 1.0f, FRAME_INSET).emit();
            emitter.square(direction, 1.0f - INNER_FRAME_THICKNESS, 0.0f, 1.0f - FRAME_THICKNESS, 1.0f, INNER_FRAME_INSET).emit();

            // INNER FRAME PANE CONNECTOR (BACK)
            emitter.square(direction.getClockWise(), 1.0f - INNER_FRAME_INSET, FRAME_THICKNESS, 1.0f - FRAME_INSET, 1.0f - FRAME_THICKNESS, 1.0f - FRAME_THICKNESS).emit();

            // INNER FRAME
            emitter.square(direction.getClockWise(), INNER_FRAME_INSET, 0.0f, 1.0f - INNER_FRAME_INSET, 1.0f, 1.0f - INNER_FRAME_THICKNESS).emit();
        }
    }

    private static void emitCornerPane(QuadEmitter emitter, boolean east, boolean down, boolean north, boolean up) {
        emitter
                .pos(0, east ? PANE_INSET : 1.0f - PANE_INSET, down ? 0.0f : INNER_FRAME_THICKNESS, north ? 0.0f : 1.0f)
                .pos(1, east ? 1.0f : 0.0f, down ? 0.0f : INNER_FRAME_THICKNESS, north ? 1.0f - PANE_INSET : PANE_INSET)
                .pos(2, east ? 1.0f : 0.0f, up ? 1.0f : 1.0f - INNER_FRAME_THICKNESS, north ? 1.0f - PANE_INSET : PANE_INSET)
                .pos(3, east ? PANE_INSET : 1.0f - PANE_INSET, up ? 1.0f : 1.0f - INNER_FRAME_THICKNESS, north ? 0.0f : 1.0f)
                .emit();
        emitter
                .pos(0, east ? 1.0f - PANE_INSET : PANE_INSET, down ? 0.0f : INNER_FRAME_THICKNESS, north ? 0.0f : 1.0f)
                .pos(1, east ? 1.0f : 0.0f, down ? 0.0f : INNER_FRAME_THICKNESS, north ? PANE_INSET : 1.0f - PANE_INSET)
                .pos(2, east ? 1.0f : 0.0f, up ? 1.0f : 1.0f - INNER_FRAME_THICKNESS, north ? PANE_INSET : 1.0f - PANE_INSET)
                .pos(3, east ? 1.0f - PANE_INSET : PANE_INSET, up ? 1.0f : 1.0f - INNER_FRAME_THICKNESS, north ? 0.0f : 1.0f)
                .emit();

        emitter
                .pos(3, east ? PANE_INSET : 1.0f - PANE_INSET, down ? 0.0f : INNER_FRAME_THICKNESS, north ? 0.0f : 1.0f)
                .pos(2, east ? 1.0f : 0.0f, down ? 0.0f : INNER_FRAME_THICKNESS, north ? 1.0f - PANE_INSET : PANE_INSET)
                .pos(1, east ? 1.0f : 0.0f, up ? 1.0f : 1.0f - INNER_FRAME_THICKNESS, north ? 1.0f - PANE_INSET : PANE_INSET)
                .pos(0, east ? PANE_INSET : 1.0f - PANE_INSET, up ? 1.0f : 1.0f - INNER_FRAME_THICKNESS, north ? 0.0f : 1.0f)
                .emit();
        emitter
                .pos(3, east ? 1.0f - PANE_INSET : PANE_INSET, down ? 0.0f : INNER_FRAME_THICKNESS, north ? 0.0f : 1.0f)
                .pos(2, east ? 1.0f : 0.0f, down ? 0.0f : INNER_FRAME_THICKNESS, north ? PANE_INSET : 1.0f - PANE_INSET)
                .pos(1, east ? 1.0f : 0.0f, up ? 1.0f : 1.0f - INNER_FRAME_THICKNESS, north ? PANE_INSET : 1.0f - PANE_INSET)
                .pos(0, east ? 1.0f - PANE_INSET : PANE_INSET, up ? 1.0f : 1.0f - INNER_FRAME_THICKNESS, north ? 0.0f : 1.0f)
                .emit();
    }

    private static void emitCornerBasePlate(QuadEmitter emitter, boolean east, boolean north, boolean down, boolean up) {
        if (!up) {
            // OUTER FRAME
            emitter
                    .pos(0, east ? 1.0f : 0.0f, 1.0f, east ? 1.0f - FRAME_INSET : FRAME_INSET)
                    .pos(1, east ? 1.0f : 0.0f, 1.0f, east ? FRAME_INSET : 1.0f - FRAME_INSET)
                    .pos(2, north ? 1.0f - FRAME_INSET : FRAME_INSET, 1.0f, north ? 0.0f : 1.0f)
                    .pos(3, north ? FRAME_INSET : 1.0f - FRAME_INSET, 1.0f, north ? 0.0f : 1.0f)
                    .cullFace(Direction.UP)
                    .emit();

            // OUTER FRAME SIDE
            emitter
                    .pos(0, east ? 1.0f : 0.0f, 1.0f - FRAME_THICKNESS, east ? FRAME_INSET : 1.0f - FRAME_INSET)
                    .pos(1, north ? 1.0f - FRAME_INSET : FRAME_INSET, 1.0f - FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .pos(2, north ? 1.0f - FRAME_INSET : FRAME_INSET, 1.0f, north ? 0.0f : 1.0f)
                    .pos(3, east ? 1.0f : 0.0f, 1.0f, east ? FRAME_INSET : 1.0f - FRAME_INSET)
                    .emit();

            // OUTER FRAME IN
            emitter
                    .pos(0, east ? 1.0f : 0.0f, 1.0f - FRAME_THICKNESS, east ? FRAME_INSET : 1.0f - FRAME_INSET)
                    .pos(1, east ? 1.0f : 0.0f, 1.0f - FRAME_THICKNESS, east ? INNER_FRAME_INSET : 1.0f - INNER_FRAME_INSET)
                    .pos(2, north ? 1.0f - INNER_FRAME_INSET : INNER_FRAME_INSET, 1.0f - FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .pos(3, north ? 1.0f - FRAME_INSET : FRAME_INSET, 1.0f - FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .emit();

            // INNER FRAME CONNECTOR
            emitter
                    .pos(0, east ? 1.0f : 0.0f, 1.0f - FRAME_THICKNESS, east ? INNER_FRAME_INSET : 1.0f - INNER_FRAME_INSET)
                    .pos(1, east ? 1.0f : 0.0f, 1.0f - INNER_FRAME_THICKNESS, east ? INNER_FRAME_INSET : 1.0f - INNER_FRAME_INSET)
                    .pos(2, north ? 1.0f - INNER_FRAME_INSET : INNER_FRAME_INSET, 1.0f - INNER_FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .pos(3, north ? 1.0f - INNER_FRAME_INSET : INNER_FRAME_INSET, 1.0f - FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .emit();

            // INNER FRAME
            emitter
                    .pos(0, east ? 1.0f : 0.0f, 1.0f - INNER_FRAME_THICKNESS, east ? INNER_FRAME_INSET : 1.0f - INNER_FRAME_INSET)
                    .pos(1, east ? 1.0f : 0.0f, 1.0f - INNER_FRAME_THICKNESS, east ? 1.0f - INNER_FRAME_INSET : INNER_FRAME_INSET)
                    .pos(2, north ? INNER_FRAME_INSET : 1.0f - INNER_FRAME_INSET, 1.0f - INNER_FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .pos(3, north ? 1.0f - INNER_FRAME_INSET : INNER_FRAME_INSET, 1.0f - INNER_FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .emit();

            // OUTER FRAME SIDE (REV)
            emitter
                    .pos(3, east ? 1.0f : 0.0f, 1.0f - FRAME_THICKNESS, east ? 1.0f - FRAME_INSET : FRAME_INSET)
                    .pos(2, north ? FRAME_INSET : 1.0f - FRAME_INSET, 1.0f - FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .pos(1, north ? FRAME_INSET : 1.0f - FRAME_INSET, 1.0f, north ? 0.0f : 1.0f)
                    .pos(0, east ? 1.0f : 0.0f, 1.0f, east ? 1.0f - FRAME_INSET : FRAME_INSET)
                    .emit();

            // OUTER FRAME IN (REV)
            emitter
                    .pos(3, east ? 1.0f : 0.0f, 1.0f - FRAME_THICKNESS, east ? 1.0f - FRAME_INSET : FRAME_INSET)
                    .pos(2, east ? 1.0f : 0.0f, 1.0f - FRAME_THICKNESS, east ? 1.0f - INNER_FRAME_INSET : INNER_FRAME_INSET)
                    .pos(1, north ? INNER_FRAME_INSET : 1.0f - INNER_FRAME_INSET, 1.0f - FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .pos(0, north ? FRAME_INSET : 1.0f - FRAME_INSET, 1.0f - FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .emit();

            // INNER FRAME CONNECTOR (REV)
            emitter
                    .pos(3, east ? 1.0f : 0.0f, 1.0f - FRAME_THICKNESS, east ? 1.0f - INNER_FRAME_INSET : INNER_FRAME_INSET)
                    .pos(2, east ? 1.0f : 0.0f, 1.0f - INNER_FRAME_THICKNESS, east ? 1.0f - INNER_FRAME_INSET : INNER_FRAME_INSET)
                    .pos(1, north ? INNER_FRAME_INSET : 1.0f - INNER_FRAME_INSET, 1.0f - INNER_FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .pos(0, north ? INNER_FRAME_INSET : 1.0f - INNER_FRAME_INSET, 1.0f - FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .emit();
        }
        if (!down) {
            // OUTER FRAME
            emitter
                    .pos(3, east ? 1.0f : 0.0f, 0.0f, east ? 1.0f - FRAME_INSET : FRAME_INSET)
                    .pos(2, east ? 1.0f : 0.0f, 0.0f, east ? FRAME_INSET : 1.0f - FRAME_INSET)
                    .pos(1, north ? 1.0f - FRAME_INSET : FRAME_INSET, 0.0f, north ? 0.0f : 1.0f)
                    .pos(0, north ? FRAME_INSET : 1.0f - FRAME_INSET, 0.0f, north ? 0.0f : 1.0f)
                    .cullFace(Direction.UP)
                    .emit();

            // OUTER FRAME SIDE
            emitter
                    .pos(3, east ? 1.0f : 0.0f, FRAME_THICKNESS, east ? FRAME_INSET : 1.0f - FRAME_INSET)
                    .pos(2, north ? 1.0f - FRAME_INSET : FRAME_INSET, FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .pos(1, north ? 1.0f - FRAME_INSET : FRAME_INSET, 0.0f, north ? 0.0f : 1.0f)
                    .pos(0, east ? 1.0f : 0.0f, 0.0f, east ? FRAME_INSET : 1.0f - FRAME_INSET)
                    .emit();

            // OUTER FRAME IN
            emitter
                    .pos(3, east ? 1.0f : 0.0f, FRAME_THICKNESS, east ? FRAME_INSET : 1.0f - FRAME_INSET)
                    .pos(2, east ? 1.0f : 0.0f, FRAME_THICKNESS, east ? INNER_FRAME_INSET : 1.0f - INNER_FRAME_INSET)
                    .pos(1, north ? 1.0f - INNER_FRAME_INSET : INNER_FRAME_INSET, FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .pos(0, north ? 1.0f - FRAME_INSET : FRAME_INSET, FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .emit();

            // INNER FRAME CONNECTOR
            emitter
                    .pos(3, east ? 1.0f : 0.0f, FRAME_THICKNESS, east ? INNER_FRAME_INSET : 1.0f - INNER_FRAME_INSET)
                    .pos(2, east ? 1.0f : 0.0f, INNER_FRAME_THICKNESS, east ? INNER_FRAME_INSET : 1.0f - INNER_FRAME_INSET)
                    .pos(1, north ? 1.0f - INNER_FRAME_INSET : INNER_FRAME_INSET, INNER_FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .pos(0, north ? 1.0f - INNER_FRAME_INSET : INNER_FRAME_INSET, FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .emit();

            // INNER FRAME
            emitter
                    .pos(3, east ? 1.0f : 0.0f, INNER_FRAME_THICKNESS, east ? INNER_FRAME_INSET : 1.0f - INNER_FRAME_INSET)
                    .pos(2, east ? 1.0f : 0.0f, INNER_FRAME_THICKNESS, east ? 1.0f - INNER_FRAME_INSET : INNER_FRAME_INSET)
                    .pos(1, north ? INNER_FRAME_INSET : 1.0f - INNER_FRAME_INSET, INNER_FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .pos(0, north ? 1.0f - INNER_FRAME_INSET : INNER_FRAME_INSET, INNER_FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .emit();

            // OUTER FRAME SIDE (REV)
            emitter
                    .pos(0, east ? 1.0f : 0.0f, FRAME_THICKNESS, east ? 1.0f - FRAME_INSET : FRAME_INSET)
                    .pos(1, north ? FRAME_INSET : 1.0f - FRAME_INSET, FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .pos(2, north ? FRAME_INSET : 1.0f - FRAME_INSET, 0.0f, north ? 0.0f : 1.0f)
                    .pos(3, east ? 1.0f : 0.0f, 0.0f, east ? 1.0f - FRAME_INSET : FRAME_INSET)
                    .emit();

            // OUTER FRAME IN (REV)
            emitter
                    .pos(0, east ? 1.0f : 0.0f, FRAME_THICKNESS, east ? 1.0f - FRAME_INSET : FRAME_INSET)
                    .pos(1, east ? 1.0f : 0.0f, FRAME_THICKNESS, east ? 1.0f - INNER_FRAME_INSET : INNER_FRAME_INSET)
                    .pos(2, north ? INNER_FRAME_INSET : 1.0f - INNER_FRAME_INSET, FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .pos(3, north ? FRAME_INSET : 1.0f - FRAME_INSET, FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .emit();

            // INNER FRAME CONNECTOR (REV)
            emitter
                    .pos(0, east ? 1.0f : 0.0f, FRAME_THICKNESS, east ? 1.0f - INNER_FRAME_INSET : INNER_FRAME_INSET)
                    .pos(1, east ? 1.0f : 0.0f, INNER_FRAME_THICKNESS, east ? 1.0f - INNER_FRAME_INSET : INNER_FRAME_INSET)
                    .pos(2, north ? INNER_FRAME_INSET : 1.0f - INNER_FRAME_INSET, INNER_FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .pos(3, north ? INNER_FRAME_INSET : 1.0f - INNER_FRAME_INSET, FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .emit();
        }
    }

    private static void emitBasePlate(QuadEmitter emitter, Direction direction, boolean down, boolean up) {
        boolean side = direction.getAxis() == Direction.Axis.X;
        if (!down) {
            emitter.square(direction, 0.0f, 0.0f, 1.0f, FRAME_THICKNESS, FRAME_INSET).emit();
            emitter.square(direction, 0.0f, 0.0f, 1.0f, INNER_FRAME_THICKNESS, INNER_FRAME_INSET).emit();
            emitter.square(direction.getOpposite(), 0.0f, 0.0f, 1.0f, FRAME_THICKNESS, FRAME_INSET).emit();
            emitter.square(direction.getOpposite(), 0.0f, 0.0f, 1.0f, INNER_FRAME_THICKNESS, INNER_FRAME_INSET).emit();

            emitter.square(Direction.UP, side ? FRAME_INSET : 0.0f, side ? 0.0f : FRAME_INSET, side ? 1.0f - FRAME_INSET : 1.0f, side ? 1.0f : 1.0f - FRAME_INSET, 1.0f - FRAME_THICKNESS).emit();
            emitter.square(Direction.UP, side ? INNER_FRAME_INSET : 0.0f, side ? 0.0f : INNER_FRAME_INSET, side ? 1.0f - INNER_FRAME_INSET : 1.0f, side ? 1.0f : 1.0f - INNER_FRAME_INSET, 1.0f - INNER_FRAME_THICKNESS).emit();
            emitter.square(Direction.DOWN, side ? FRAME_INSET : 0.0f, side ? 0.0f : FRAME_INSET, side ? 1.0f - FRAME_INSET : 1.0f, side ? 1.0f : 1.0f - FRAME_INSET, 0.0f).emit();
        }
        if (!up) {
            emitter.square(direction, 0.0f, 1.0f - FRAME_THICKNESS, 1.0f, 1.0f, FRAME_INSET).emit();
            emitter.square(direction, 0.0f, 1.0f - INNER_FRAME_THICKNESS, 1.0f, 1.0f, INNER_FRAME_INSET).emit();
            emitter.square(direction.getOpposite(), 0.0f, 1.0f - FRAME_THICKNESS, 1.0f, 1.0f, FRAME_INSET).emit();
            emitter.square(direction.getOpposite(), 0.0f, 1.0f - INNER_FRAME_THICKNESS, 1.0f, 1.0f, INNER_FRAME_INSET).emit();

            emitter.square(Direction.DOWN, side ? FRAME_INSET : 0.0f, side ? 0.0f : FRAME_INSET, side ? 1.0f - FRAME_INSET : 1.0f, side ? 1.0f : 1.0f - FRAME_INSET, 1.0f - FRAME_THICKNESS).emit();
            emitter.square(Direction.DOWN, side ? INNER_FRAME_INSET : 0.0f, side ? 0.0f : INNER_FRAME_INSET, side ? 1.0f - INNER_FRAME_INSET : 1.0f, side ? 1.0f : 1.0f - INNER_FRAME_INSET, 1.0f - INNER_FRAME_THICKNESS).emit();
            emitter.square(Direction.UP, side ? FRAME_INSET : 0.0f, side ? 0.0f : FRAME_INSET, side ? 1.0f - FRAME_INSET : 1.0f, side ? 1.0f : 1.0f - FRAME_INSET, 0.0f).emit();
        }
    }

    private static void emitCenterBasePlate(QuadEmitter emitter, Direction direction, boolean down, boolean up) {
        boolean side = direction.getAxis() == Direction.Axis.X;
        if (!down) {
            emitter.square(direction, 0.0f, 0.0f, 1.0f, FRAME_THICKNESS, FRAME_INSET).emit();
            emitter.square(direction, 0.0f, 0.0f, 1.0f, INNER_FRAME_THICKNESS, INNER_FRAME_INSET).emit();
            emitter.square(direction.getOpposite(), 0.0f, 0.0f, 1.0f, FRAME_THICKNESS, FRAME_INSET).emit();
            emitter.square(direction.getOpposite(), 0.0f, 0.0f, 1.0f, INNER_FRAME_THICKNESS, INNER_FRAME_INSET).emit();

            emitter.square(Direction.UP, side ? FRAME_INSET : 0.0f, side ? 0.0f : FRAME_INSET, side ? 1.0f - FRAME_INSET : 1.0f, side ? 1.0f : 1.0f - FRAME_INSET, 1.0f - FRAME_THICKNESS).emit();
            emitter.square(Direction.UP, side ? INNER_FRAME_INSET : 0.0f, side ? 0.0f : INNER_FRAME_INSET, side ? 1.0f - INNER_FRAME_INSET : 1.0f, side ? 1.0f : 1.0f - INNER_FRAME_INSET, 1.0f - INNER_FRAME_THICKNESS).emit();
            emitter.square(Direction.DOWN, side ? FRAME_INSET : 0.0f, side ? 0.0f : FRAME_INSET, side ? 1.0f - FRAME_INSET : 1.0f, side ? 1.0f : 1.0f - FRAME_INSET, 0.0f).emit();
        }
        if (!up) {
            emitter.square(direction, 0.0f, 1.0f - FRAME_THICKNESS, 1.0f, 1.0f, FRAME_INSET).emit();
            emitter.square(direction, 0.0f, 1.0f - INNER_FRAME_THICKNESS, 1.0f, 1.0f, INNER_FRAME_INSET).emit();
            emitter.square(direction.getOpposite(), 0.0f, 1.0f - FRAME_THICKNESS, 1.0f, 1.0f, FRAME_INSET).emit();
            emitter.square(direction.getOpposite(), 0.0f, 1.0f - INNER_FRAME_THICKNESS, 1.0f, 1.0f, INNER_FRAME_INSET).emit();

            emitter.square(Direction.DOWN, side ? FRAME_INSET : 0.0f, side ? 0.0f : FRAME_INSET, side ? 1.0f - FRAME_INSET : 1.0f, side ? 1.0f : 1.0f - FRAME_INSET, 1.0f - FRAME_THICKNESS).emit();
            emitter.square(Direction.DOWN, side ? INNER_FRAME_INSET : 0.0f, side ? 0.0f : INNER_FRAME_INSET, side ? 1.0f - INNER_FRAME_INSET : 1.0f, side ? 1.0f : 1.0f - INNER_FRAME_INSET, 1.0f - INNER_FRAME_THICKNESS).emit();
            emitter.square(Direction.UP, side ? FRAME_INSET : 0.0f, side ? 0.0f : FRAME_INSET, side ? 1.0f - FRAME_INSET : 1.0f, side ? 1.0f : 1.0f - FRAME_INSET, 0.0f).emit();
        }
    }

    private static void emitCenterPane(QuadEmitter emitter, Direction direction, boolean down, boolean up) {
        emitter.square(direction, 1.0f - PANE_INSET, down ? 0.0f : INNER_FRAME_THICKNESS, 1.0f, up ? 1.0f : (1.0f - INNER_FRAME_THICKNESS), PANE_INSET).emit();
        emitter.square(direction.getOpposite(), 0.0f, down ? 0.0f : INNER_FRAME_THICKNESS, PANE_INSET, up ? 1.0f : (1.0f - INNER_FRAME_THICKNESS), PANE_INSET).emit();

        emitter.square(direction.getOpposite(), 0.0f, down ? 0.0f : INNER_FRAME_THICKNESS, PANE_INSET, up ? 1.0f : (1.0f - INNER_FRAME_THICKNESS), 1.0f - PANE_INSET).emit();
        emitter.square(direction, 1.0f - PANE_INSET, down ? 0.0f : INNER_FRAME_THICKNESS, 1.0f, up ? 1.0f : (1.0f - INNER_FRAME_THICKNESS), 1.0f - PANE_INSET).emit();
    }

    private static void emitPane(QuadEmitter emitter, Direction direction, boolean left, boolean right, boolean down, boolean up) {
        emitter.square(direction, left ? 0.0f : INNER_FRAME_THICKNESS, down ? 0.0f : INNER_FRAME_THICKNESS, right ? 1.0f : 1.0f - INNER_FRAME_THICKNESS, up ? 1.0f : (1.0f - INNER_FRAME_THICKNESS), PANE_INSET).emit();
        emitter.square(direction.getOpposite(), right ? 0.0f : INNER_FRAME_THICKNESS, down ? 0.0f : INNER_FRAME_THICKNESS, left ? 1.0f : 1.0f - INNER_FRAME_THICKNESS, up ? 1.0f : (1.0f - INNER_FRAME_THICKNESS), 1.0f - (PANE_INSET)).emit();
    }

    private static void emitBrokenPane(QuadEmitter emitter, Direction direction, boolean down, boolean up) {
        emitter.square(direction, 0.0f, down ? 0.0f : INNER_FRAME_THICKNESS, PANE_INSET, up ? 1.0f : (1.0f - INNER_FRAME_THICKNESS), PANE_INSET).emit();
        emitter.square(direction, 1.0f - PANE_INSET, down ? 0.0f : INNER_FRAME_THICKNESS, 1.0f, up ? 1.0f : (1.0f - INNER_FRAME_THICKNESS), PANE_INSET).emit();

        emitter.square(direction.getOpposite(), 0.0f, down ? 0.0f : INNER_FRAME_THICKNESS, PANE_INSET, up ? 1.0f : (1.0f - INNER_FRAME_THICKNESS), 1.0f - PANE_INSET).emit();
        emitter.square(direction.getOpposite(), 1.0f - PANE_INSET, down ? 0.0f : INNER_FRAME_THICKNESS, 1.0f, up ? 1.0f : (1.0f - INNER_FRAME_THICKNESS), 1.0f - PANE_INSET).emit();
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
        BakedModel.super.emitItemQuads(stack, randomSupplier, context);
    }
}
