package dev.galacticraft.mod.content.block.environment;

import com.mojang.serialization.MapCodec;
import dev.galacticraft.mod.content.GCBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class MoonShrubsBlock extends BushBlock {
    public MoonShrubsBlock(Properties settings) {
        super(settings);
    }

    @NotNull
    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return simpleCodec(MoonShrubsBlock::new);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter world, BlockPos pos) {
        return state.is(GCBlocks.MOON_MOSS);
    }
}