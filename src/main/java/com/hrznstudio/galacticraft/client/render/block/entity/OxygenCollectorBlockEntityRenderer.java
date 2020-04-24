package com.hrznstudio.galacticraft.client.render.block.entity;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.blocks.machines.oxygencollector.OxygenCollectorBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class OxygenCollectorBlockEntityRenderer extends ConfigurableElectricMachineBlockEntityRenderer<OxygenCollectorBlockEntity> {
    public OxygenCollectorBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Nonnull
    @Override
    public SpriteIdentifier getDefaultSpriteId(@Nonnull OxygenCollectorBlockEntity entity, @Nullable Direction direction) {
        if (direction != null) {
            return new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier(Constants.MOD_ID, "block/oxygen_collector"));
        }
        return new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier(Constants.MOD_ID, "block/machine")); // particle
    }
}
