package com.hrznstudio.galacticraft.world.gen.carver;

import com.hrznstudio.galacticraft.Constants;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.Carver;

public class GalacticraftCarvers {
    public static final Carver<ProbabilityConfig> LUNAR_CAVE = Registry.register(Registry.CARVER, new Identifier(Constants.MOD_ID, "lunar_cave"), new LunarCaveCarver(ProbabilityConfig.CODEC, 128));

    public static void register() {

    }
}
