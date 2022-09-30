package dev.galacticraft.mod.data;

import dev.galacticraft.mod.misc.banner.GCBannerPattern;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.Registry;
import net.minecraft.tags.BannerPatternTags;
import net.minecraft.world.level.block.entity.BannerPattern;

public class GCBannerTagProvider extends FabricTagProvider<BannerPattern> {

    public GCBannerTagProvider(FabricDataGenerator dataGenerator) {
        super(dataGenerator, Registry.BANNER_PATTERN);
    }

    @Override
    protected void generateTags() {
        getOrCreateTagBuilder(BannerPatternTags.NO_ITEM_REQUIRED).add(GCBannerPattern.ROCKET);
    }
}
