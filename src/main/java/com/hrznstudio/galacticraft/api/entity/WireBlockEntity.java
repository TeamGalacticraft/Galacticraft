///*
// * Copyright (c) 2019 HRZN LTD
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in all
// * copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// * SOFTWARE.
// */
//
//package com.hrznstudio.galacticraft.api.entity;
//
//import com.hrznstudio.galacticraft.api.wire.NetworkManager;
//import com.hrznstudio.galacticraft.api.wire.WireNetwork;
//import com.hrznstudio.galacticraft.api.wire.WireUtils;
//import net.minecraft.block.entity.BlockEntity;
//import net.minecraft.block.entity.BlockEntityType;
//import net.minecraft.util.math.BlockPos;
//
//import java.util.List;
//import java.util.UUID;
//
///**
// * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
// */
//public class WireBlockEntity extends BlockEntity {
//
//    /**
//     * The ID of the WireNetwork that this BlockEntity belongs to.
//     *
//     * @see WireNetwork
//     */
//    public UUID networkId;
//
//    public WireBlockEntity(BlockEntityType<? extends WireBlockEntity> blockEntityType) {
//        super(blockEntityType);
//    }
//
//    public void onNetworkUpdate() {
//        assert this.world != null;
//        if (!this.world.isClient && !this.isInvalid()) {
//            List<BlockPos> list = WireUtils.getAdjacentWires(this.pos, this.world);
//
//            while (!list.isEmpty()) {
//                BlockPos pos = list.remove(list.size() - 1);
//                BlockEntity entity = world.getBlockEntity(pos);
//                if (entity instanceof WireBlockEntity && !entity.isInvalid() && entity.hasWorld() && entity.getPos() != null) {
//                    WireNetwork myNet = NetworkManager.getManagerForDimension(this.world.dimension.getType()).getNetwork(getOrCreateNetworkId());
//                    if (((WireBlockEntity) entity).networkId == null) {
//                        myNet.wires.add(entity.getPos());
//                        ((WireBlockEntity) entity).networkId = this.getNetworkId();
//                        for (BlockPos p : WireUtils.getAdjacentWires(pos, this.world)) {
//                            BlockEntity e = world.getBlockEntity(p);
//                            if (e instanceof WireBlockEntity && !e.isInvalid()) {
//                                list.add(p);
//                            }
//                        }
//                        continue;
//                    }
//                    WireNetwork newNet = NetworkManager.getManagerForDimension(this.world.dimension.getType()).getNetwork(((WireBlockEntity) entity).networkId);
//
//                    if (this.networkId == ((WireBlockEntity) entity).networkId) continue;
//
//                    if (newNet == null) {
//                        myNet.wires.add(entity.getPos());
//                        ((WireBlockEntity) entity).networkId = this.getNetworkId();
//                        for (BlockPos p : WireUtils.getAdjacentWires(pos, this.world)) {
//                            BlockEntity e = world.getBlockEntity(p);
//                            if (e instanceof WireBlockEntity && !e.isInvalid()) {
//                                list.add(p);
//                            }
//                        }
//                        continue;
//                    }
//
//                    this.networkId = myNet.join(newNet).getId();
//                }
//            }
//
//            getOrCreateNetworkId();
//        }
//    }
//
//    public void onRemoved() {
//        assert this.world != null;
//        if (!this.world.isClient()) {
//            this.invalidate();
//            this.world.removeBlockEntity(pos);
//            NetworkManager.getManagerForDimension(this.world.dimension.getType()).getNetwork(this.networkId).wires.remove(this.pos);
//            NetworkManager.getManagerForDimension(this.world.dimension.getType()).getNetwork(this.networkId).wireRemoved();
//        }
//    }
//
//    @Override
//    public boolean isInvalid() {
//        return this.invalid;
//    }
//
//    private UUID getOrCreateNetworkId() {
//        assert this.world != null;
//        if (!this.world.isClient && this.networkId == null) {
//            this.networkId = new WireNetwork(this).getId();
//        }
//        return this.networkId;
//    }
//
//    public UUID getNetworkId() {
//        return networkId;
//    }
//
//    public void resetNetworkId() {
//        this.networkId = null;
//    }
//}
