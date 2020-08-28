package com.hrznstudio.galacticraft.misc.banner;

import com.hrznstudio.galacticraft.Constants;
import io.github.fablabsmc.fablabs.api.bannerpattern.v1.LoomPattern;
import io.github.fablabsmc.fablabs.api.bannerpattern.v1.LoomPatterns;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class GalacticraftBannerPatterns {
    public static final LoomPattern ROCKET = Registry.register(LoomPatterns.REGISTRY, new Identifier(Constants.MOD_ID, "rocket"), new LoomPattern(false));

    public static void register() {
    }
}
