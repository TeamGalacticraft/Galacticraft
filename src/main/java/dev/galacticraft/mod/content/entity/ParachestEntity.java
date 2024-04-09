/*
 * Copyright (c) 2019-2024 Team Galacticraft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.galacticraft.mod.content.entity;

import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.GCFluids;
import dev.galacticraft.mod.content.block.special.ParaChestBlock;
import dev.galacticraft.mod.content.block.special.ParaChestBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;

public class ParachestEntity extends Entity {
    private static final EntityDataAccessor<Byte> COLOR = SynchedEntityData.defineId(ParachestEntity.class, EntityDataSerializers.BYTE);
    public NonNullList<ItemStack> cargo;
    public long fuelLevel;
    private boolean placedChest;
    public DyeColor color = DyeColor.WHITE;

    public ParachestEntity(EntityType<?> entityType, Level level, NonNullList<ItemStack> cargo, long fuelLevel) {
        this(entityType, level);
        this.cargo = NonNullList.withSize(cargo.size(), ItemStack.EMPTY);
        Collections.copy(this.cargo, cargo);
        this.placedChest = false;
        this.fuelLevel = fuelLevel;
    }

    public ParachestEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(COLOR, (byte) DyeColor.WHITE.getId());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        int size = 56;
        if (tag.contains("CargoLength")) {
            size = tag.getInt("CargoLength");
        }
        this.cargo = NonNullList.withSize(size, ItemStack.EMPTY);

        ContainerHelper.loadAllItems(tag, this.cargo);

        this.placedChest = tag.getBoolean("placedChest");
        this.fuelLevel = tag.getLong("FuelLevel");

        if (tag.contains("color")) {
            this.color = DyeColor.byId(tag.getInt("color"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("CargoLength", this.cargo.size());
        ContainerHelper.saveAllItems(tag, this.cargo);

        tag.putBoolean("placedChest", this.placedChest);
        tag.putLong("FuelLevel", this.fuelLevel);
        tag.putInt("color", this.color.getId());
    }

    @Override
    public void tick() {
        if (!this.placedChest) {
            if (this.onGround() && !this.level().isClientSide) {
                for (int i = 0; i < 100; i++) {
                    final int x = Mth.floor(this.getX());
                    final int y = Mth.floor(this.getY());
                    final int z = Mth.floor(this.getZ());

                    if (tryPlaceAtPos(new BlockPos(x, y + i, z))) {
                        return;
                    }
                }

                for (int size = 1; size < 5; ++size) {
                    for (int xOff = -size; xOff <= size; xOff++) {
                        for (int yOff = -size; yOff <= size; yOff++) {
                            for (int zOff = -size; zOff <= size; zOff++) {
                                final int x = Mth.floor(this.getX()) + xOff;
                                final int y = Mth.floor(this.getY()) + yOff;
                                final int z = Mth.floor(this.getZ()) + zOff;

                                if (tryPlaceAtPos(new BlockPos(x, y, z))) {
                                    return;
                                }
                            }
                        }
                    }
                }

                if (this.cargo != null) {
                    for (final ItemStack stack : this.cargo) {
                        final ItemEntity e = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), stack);
                        this.level().addFreshEntity(e);
                    }
                }

                this.placedChest = true;
                this.discard();
            } else {
                this.setDeltaMovement(new Vec3(0, -0.35, 0));
            }

            this.move(MoverType.SELF, getDeltaMovement());
        }

        if (!this.level().isClientSide && this.tickCount % 5 == 0) {
            this.entityData.set(COLOR, (byte) this.color.getId());
        }
    }

    private boolean tryPlaceAtPos(BlockPos pos) {
        BlockState state = this.level().getBlockState(pos);

        if (state.canBeReplaced()) {
            if (this.placeChest(pos)) {
                this.placedChest = true;
                this.discard();
                return true;
            }
        }
        return false;
    }

    private boolean placeChest(BlockPos pos) {
        if (this.level().setBlock(pos, GCBlocks.PARACHEST.defaultBlockState().setValue(ParaChestBlock.COLOR, DyeColor.byId(this.entityData.get(COLOR))), Block.UPDATE_ALL)) {
            if (this.cargo != null) {
                final BlockEntity te = this.level().getBlockEntity(pos);

                if (te instanceof ParaChestBlockEntity chest) {
                    chest.setItems(NonNullList.withSize(this.cargo.size() + 1, ItemStack.EMPTY));

                    Collections.copy(chest.getItems(), this.cargo);

                    try (Transaction tx = Transaction.openOuter()) {
                        chest.tank.insert(FluidVariant.of(GCFluids.FUEL), this.fuelLevel, tx);
                        tx.commit();
                    }
                } else {
                    for (ItemStack stack : this.cargo) {
                        final ItemEntity e = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), stack);
                        this.level().addFreshEntity(e);
                    }
                }
            }
            return true;
        }

        return false;
    }
}
