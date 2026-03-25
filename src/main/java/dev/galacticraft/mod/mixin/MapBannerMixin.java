package dev.galacticraft.mod.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.galacticraft.mod.content.block.decoration.FlagBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.saveddata.maps.MapBanner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MapBanner.class)
public abstract class MapBannerMixin {
    @ModifyVariable(method = "fromWorld", at = @At("HEAD"), argsOnly = true)
    private static BlockPos galacticraft$findFlagBase(BlockPos pos, @Local(argsOnly = true) BlockGetter level) {
        if (level.getBlockState(pos).getBlock() instanceof FlagBlock) {
            return FlagBlock.getBaseBlockPos(level, pos);
        }
        return pos;
    }
}
