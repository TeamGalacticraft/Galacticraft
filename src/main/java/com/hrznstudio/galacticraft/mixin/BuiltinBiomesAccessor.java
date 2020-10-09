package com.hrznstudio.galacticraft.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BuiltinBiomes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BuiltinBiomes.class)
public interface BuiltinBiomesAccessor {
	@Accessor
	static Int2ObjectMap<RegistryKey<Biome>> getBY_RAW_ID() {
		throw new UnsupportedOperationException("Accessor was not transformed");
	}
}