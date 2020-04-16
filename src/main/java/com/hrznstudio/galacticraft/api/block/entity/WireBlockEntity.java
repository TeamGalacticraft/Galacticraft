package com.hrznstudio.galacticraft.api.block.entity;

import com.hrznstudio.galacticraft.api.block.WireBlock;
import com.hrznstudio.galacticraft.api.wire.NetworkManager;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Tickable;

public class WireBlockEntity extends BlockEntity implements Tickable {
    public WireBlockEntity() {
        super(GalacticraftBlockEntities.WIRE_TYPE);
    }


    @Override
    public void tick() {
        assert world != null;
        if (NetworkManager.getManagerForWorld(world) == null)
            NetworkManager.createManagerForWorld(((ServerWorld) world));
        if (NetworkManager.getManagerForWorld(world).getNetwork(pos) == null && world.getBlockState(pos).getBlock() instanceof WireBlock && !world.isClient) {
            this.world.getBlockState(pos).onBlockAdded(world, pos, Blocks.AIR.getDefaultState(), false);
        }
    }
}
