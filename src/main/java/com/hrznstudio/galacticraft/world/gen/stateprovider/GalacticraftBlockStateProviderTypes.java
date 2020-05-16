package com.hrznstudio.galacticraft.world.gen.stateprovider;

import com.hrznstudio.galacticraft.Constants;
import com.mojang.datafixers.Dynamic;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

public class GalacticraftBlockStateProviderTypes {
    public static final BlockStateProviderType<MoonFlowerBlockStateProvider> MOON_FLOWER_PROVIDER = Registry.register(Registry.BLOCK_STATE_PROVIDER_TYPE, new Identifier(Constants.MOD_ID, "moon_flower_provider"), create(MoonFlowerBlockStateProvider::new));

    public static void register() {
    }

    private static <P extends BlockStateProvider> BlockStateProviderType<P> create(Function<Dynamic<?>, P> function) {
        try {
            Constructor<BlockStateProviderType> constructor = BlockStateProviderType.class.getDeclaredConstructor(Function.class);
            constructor.setAccessible(true);
            return constructor.newInstance(function);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to Instantiate BlockStateProvider!");
        }
    }
}
