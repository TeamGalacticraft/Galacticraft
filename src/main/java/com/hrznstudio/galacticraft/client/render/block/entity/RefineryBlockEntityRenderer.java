package com.hrznstudio.galacticraft.client.render.block.entity;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.blocks.machines.refinery.RefineryBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.container.PlayerContainer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RefineryBlockEntityRenderer extends ConfigurableElectricMachineBlockEntityRenderer<RefineryBlockEntity> {
    public RefineryBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Nonnull
    @Override
    public SpriteIdentifier getDefaultSpriteId(@Nonnull RefineryBlockEntity entity, @Nullable Direction direction) {
        if (direction == null)
            return new SpriteIdentifier(PlayerContainer.BLOCK_ATLAS_TEXTURE, new Identifier(Constants.MOD_ID, "block/machine")); // particle
        switch (direction) {
            case NORTH:
                return new SpriteIdentifier(PlayerContainer.BLOCK_ATLAS_TEXTURE, new Identifier(Constants.MOD_ID, "block/refinery_front"));
            case SOUTH:
                return new SpriteIdentifier(PlayerContainer.BLOCK_ATLAS_TEXTURE, new Identifier(Constants.MOD_ID, "block/refinery_side"));
            case EAST:
            case WEST:
                return new SpriteIdentifier(PlayerContainer.BLOCK_ATLAS_TEXTURE, new Identifier(Constants.MOD_ID, "block/machine_side"));
            case UP:
            case DOWN:
                return new SpriteIdentifier(PlayerContainer.BLOCK_ATLAS_TEXTURE, new Identifier(Constants.MOD_ID, "block/machine"));
        }
        return new SpriteIdentifier(PlayerContainer.BLOCK_ATLAS_TEXTURE, new Identifier(Constants.MOD_ID, "block/machine"));
    }
}
