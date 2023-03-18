package dev.galacticraft.mod.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(MultiNoiseBiomeSourceParameterList.Preset.class)
public interface MultiNoiseBiomeSourceParameterListPresetAccessor {
    @Accessor("BY_NAME")
    static Map<ResourceLocation, MultiNoiseBiomeSourceParameterList.Preset> getByName() {
        throw new UnsupportedOperationException();
    }
}
