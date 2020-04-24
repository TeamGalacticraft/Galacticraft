package com.hrznstudio.galacticraft.client.render.block.entity;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.blocks.machines.coalgenerator.CoalGeneratorBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CoalGeneratorBlockEntityRenderer extends ConfigurableElectricMachineBlockEntityRenderer<CoalGeneratorBlockEntity> {
    public CoalGeneratorBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Nonnull
    @Override
    public SpriteIdentifier getDefaultSpriteId(@Nonnull CoalGeneratorBlockEntity entity, @Nullable Direction direction) {
        if (direction == Direction.NORTH) {
            return new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier(Constants.MOD_ID, "block/coal_generator"));
        }
        return super.getDefaultSpriteId(entity, direction);
    }
}
