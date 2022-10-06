package dev.galacticraft.mod.mixin.client;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import dev.galacticraft.mod.block.entity.CryogenicChamberBlockEntity;
import dev.galacticraft.mod.block.entity.CryogenicChamberPartBlockEntity;
import dev.galacticraft.mod.block.special.CryogenicChamberBlock;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow protected abstract void move(double d, double e, double f);

    @Shadow protected abstract void setRotation(float f, float g);

    private static float sleepDirectionToRotationCryo(Direction direction) {
        return switch (direction) {
            case NORTH -> 0.0F;
            case EAST -> 90.0F;
            case SOUTH -> 180.0F;
            case WEST -> 270.0F;
            default -> 0.0F;
        };
    }

    @Inject(method = "setup", at = @At("TAIL"))
    private void gc$rotateCamera(BlockGetter blockGetter, Entity entity, boolean bl, boolean bl2, float partialTicks, CallbackInfo ci) {
        if (entity != null)
        {
            int x = Mth.floor(entity.getX());
            int y = Mth.floor(entity.getY());
            int z = Mth.floor(entity.getZ());
            BlockEntity tile = Minecraft.getInstance().level.getBlockEntity(new BlockPos(x, y, z));

            if (tile instanceof CryogenicChamberPartBlockEntity partBlockEntity)
            {
                tile = partBlockEntity.getLevel().getBlockEntity(partBlockEntity.basePos);
            }

            if (tile instanceof CryogenicChamberBlockEntity)
            {
                entity.xRotO = 45;
                setRotation((Minecraft.getInstance().player.getSleepTimer() - 50) + sleepDirectionToRotationCryo(tile.getBlockState().getValue(CryogenicChamberBlock.FACING)), 0.0F);
                move(-4.1F, 0.3F, 0.0F);
            }
        }
    }
}
