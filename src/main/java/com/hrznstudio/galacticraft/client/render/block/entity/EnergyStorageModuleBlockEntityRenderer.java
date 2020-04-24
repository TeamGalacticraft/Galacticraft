package com.hrznstudio.galacticraft.client.render.block.entity;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.blocks.machines.energystoragemodule.EnergyStorageModuleBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EnergyStorageModuleBlockEntityRenderer extends ConfigurableElectricMachineBlockEntityRenderer<EnergyStorageModuleBlockEntity> {
    public EnergyStorageModuleBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Nonnull
    @Override
    public SpriteIdentifier getDefaultSpriteId(@Nonnull EnergyStorageModuleBlockEntity entity, @Nullable Direction direction) {
        int level = (int) (((float) entity.getEnergyAttribute().getCurrentEnergy() / (float) entity.getMaxEnergy()) * 8F);
        if (direction == Direction.NORTH || direction == Direction.SOUTH) {
            return new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier(Constants.MOD_ID, "block/energy_storage_module_" + level));
        }
        return super.getDefaultSpriteId(entity, direction);
    }
}
