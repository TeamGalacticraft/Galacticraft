package dev.galacticraft.mod.compat.waila.provider;

import dev.galacticraft.mod.content.block.decoration.FlagBlock;
import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITargetRedirector;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public enum FlagNameProvider implements IBlockComponentProvider {
    INSTANCE;

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public ITargetRedirector.@Nullable Result redirect(ITargetRedirector redirect, IBlockAccessor accessor, IPluginConfig config) {
        BlockState state = accessor.getBlockState();
        Level level = accessor.getWorld();
        if (state.getValue(FlagBlock.SECTION) != FlagBlock.Section.BOTTOM) {
            BlockPos basePos = FlagBlock.getBaseBlockPos(state, accessor.getPosition());
            if (level.getBlockState(basePos).getBlock() instanceof FlagBlock) {
                BlockHitResult hitResult = new BlockHitResult(basePos.getCenter(), accessor.getSide(), basePos, accessor.getBlockHitResult().isInside());
                return redirect.to(hitResult);
            }
        }
        return null;
    }
}
