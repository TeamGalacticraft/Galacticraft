package com.hrznstudio.galacticraft.client.render.fluid;

import com.hrznstudio.galacticraft.Constants;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.impl.client.texture.FabricSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ExtendedBlockView;

public class OilFluidRenderHandler implements FluidRenderHandler {

    public static final Sprite STILL_TEXTURE = new FabricSprite(new Identifier(Constants.MOD_ID, Constants.Fluids.CRUDE_OIL_FLUID_STILL), 16, 16);
    public static final Sprite FLOWING_TEXTURE = new FabricSprite(new Identifier(Constants.MOD_ID, Constants.Fluids.CRUDE_OIL_FLUID_FLOWING), 16, 16);

    @Override
    public Sprite[] getFluidSprites(ExtendedBlockView view, BlockPos pos, FluidState state) {
        return new Sprite[]{STILL_TEXTURE, FLOWING_TEXTURE};
    }
}
