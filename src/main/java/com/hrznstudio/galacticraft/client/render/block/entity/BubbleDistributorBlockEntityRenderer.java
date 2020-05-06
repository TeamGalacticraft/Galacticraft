package com.hrznstudio.galacticraft.client.render.block.entity;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.blocks.machines.bubbledistributor.BubbleDistributorBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.container.PlayerContainer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Environment(EnvType.CLIENT)
public class BubbleDistributorBlockEntityRenderer extends ConfigurableElectricMachineBlockEntityRenderer<BubbleDistributorBlockEntity> {
    public BubbleDistributorBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(BubbleDistributorBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider consumers, int light, int overlay) {
        super.render(blockEntity, tickDelta, matrices, consumers, light, overlay);
    }

    @Nonnull
    @Override
    public SpriteIdentifier getDefaultSpriteId(@Nonnull BubbleDistributorBlockEntity entity, @Nullable Direction direction) {
        return direction != null ? new SpriteIdentifier(PlayerContainer.BLOCK_ATLAS_TEXTURE, new Identifier(Constants.MOD_ID, "block/oxygen_bubble_distributor")) : super.getDefaultSpriteId(entity, null);
    }
}
