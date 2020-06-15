package com.hrznstudio.galacticraft.world.gen.stateprovider;

import com.hrznstudio.galacticraft.Constants;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;

public class GalacticraftBlockStateProviderTypes {
    public static final BlockStateProviderType<MoonFloraBlockStateProvider> MOON_FLOWER_PROVIDER = BlockStateProviderType.register(new Identifier(Constants.MOD_ID, "moon_flower_provider").toString(), MoonFloraBlockStateProvider.CODEC);

    public static void register() {
    }
}
