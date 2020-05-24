package com.hrznstudio.galacticraft.block.special;

import com.hrznstudio.galacticraft.block.entity.ResearchTableBlockEntity;
import com.hrznstudio.galacticraft.screen.GalacticraftScreenHandlers;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ResearchTableBlock extends BlockWithEntity {
    public ResearchTableBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        ContainerProviderRegistry.INSTANCE.openContainer(GalacticraftScreenHandlers.RESEARCH_TABLE_SCREEN_HANDLER, player, packetByteBuf -> packetByteBuf.writeBlockPos(pos));
        return ActionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new ResearchTableBlockEntity();
    }
}
