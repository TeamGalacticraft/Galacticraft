package com.hrznstudio.galacticraft.client.render.block.entity;

import com.hrznstudio.galacticraft.block.special.fluidpipe.FluidPipeBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;

public class FluidPipeBlockEntityRenderer extends BlockEntityRenderer<FluidPipeBlockEntity> {
    public FluidPipeBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(FluidPipeBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity.getData() != FluidPipeBlockEntity.FluidData.EMPTY) {
            entity.getData().getPath().getLast(); //todo
        }
    }
}
