package com.hrznstudio.galacticraft.client.render.block.entity;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.blocks.machines.circuitfabricator.CircuitFabricatorBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CircuitFabricatorBlockEntityRenderer extends ConfigurableElectricMachineBlockEntityRenderer<CircuitFabricatorBlockEntity> {
    public CircuitFabricatorBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(CircuitFabricatorBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        super.render(blockEntity, tickDelta, matrices, vertexConsumers, light, overlay);
    }

    @Nonnull
    @Override
    public SpriteIdentifier getDefaultSpriteId(@Nonnull CircuitFabricatorBlockEntity entity, @Nullable Direction direction) {
        if (direction == Direction.NORTH) {
            return new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier(Constants.MOD_ID, "block/circuit_fabricator"));
        }
        return super.getDefaultSpriteId(entity, direction);
    }
}
