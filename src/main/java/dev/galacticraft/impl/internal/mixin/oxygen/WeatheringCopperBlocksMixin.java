package dev.galacticraft.impl.internal.mixin.oxygen;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({WeatheringCopperBulbBlock.class, WeatheringCopperDoorBlock.class, WeatheringCopperFullBlock.class,
        WeatheringCopperGrateBlock.class, WeatheringCopperSlabBlock.class, WeatheringCopperStairBlock.class,
        WeatheringCopperTrapDoorBlock.class})
public class WeatheringCopperBlocksMixin {
    @WrapMethod(method = "randomTick")
    private void checkOxidizable(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource, Operation<Void> original) {
        if (level.galacticraft$isBreathable(pos)) original.call(state, level, pos, randomSource);
    }
}
