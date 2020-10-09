package com.hrznstudio.galacticraft.mixin;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockStateProviderType.class)
public interface BlockStateProviderTypeAccessor {
    @Invoker
    static <P extends BlockStateProvider> BlockStateProviderType<P> callRegister(String id, Codec<P> codec) {
        throw new UnsupportedOperationException("Invoker wasn't transformed.");
    }
}
