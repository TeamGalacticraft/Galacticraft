/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

package dev.galacticraft.mod.content.block.special.launchpad;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.entity.FuelDock;
import dev.galacticraft.mod.api.entity.Dockable;
import dev.galacticraft.mod.content.GCBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.IntFunction;

public class LaunchPadBlockEntity extends BlockEntity implements FuelDock {
    private UUID entityUUID = null;
    private @Nullable Dockable docked;
    private Type type;

    public LaunchPadBlockEntity(BlockPos pos, BlockState state, Type type) {
        super(GCBlockEntityTypes.LAUNCH_PAD, pos, state);
        this.type = type;
    }

    public LaunchPadBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.LAUNCH_PAD, pos, state);
    }

    public void setDockedEntity(@Nullable Dockable dockable) {
        if (dockable == null) {
            this.entityUUID = null;
            this.docked = null;
        } else {
            this.entityUUID = dockable.asEntity().getUUID();
            this.docked = dockable;
        }
    }

    @Override
    public BlockPos getDockPos() {
        return this.getBlockPos();
    }

    public Dockable getDockedEntity() {
        if (this.entityUUID != null) {
            if (this.docked == null && this.level instanceof ServerLevel) {
                this.docked = (Dockable) ((ServerLevel) this.level).getEntity(this.entityUUID);
            }
        } else {
            this.docked = null;
        }
        return this.docked;
    }

    public boolean hasDockedEntity() {
        return this.getDockedEntity() != null;
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);
        this.entityUUID = null;
        this.docked = null;
        this.type = Type.byName(nbt.getString("Type"));

        if (nbt.hasUUID(Constant.Nbt.DOCKED_UUID)) {
            this.entityUUID = nbt.getUUID(Constant.Nbt.DOCKED_UUID);
            if (this.level instanceof ServerLevel) {
                this.docked = (Dockable) ((ServerLevel) this.level).getEntity(this.entityUUID);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);
        if (this.entityUUID != null) nbt.putUUID(Constant.Nbt.DOCKED_UUID, this.entityUUID);
        nbt.putString("Type", type.getSerializedName());
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        return this.saveWithoutMetadata(registryLookup);
    }

    public UUID getDockedUUID() {
        return this.entityUUID;
    }

    public Type getPadType() {
        return this.type;
    }

    public enum Type implements StringRepresentable {
        ROCKET(0, "rocket"),
        FUEL(1, "fuel");

        public static final StringRepresentable.EnumCodec<Type> CODEC = StringRepresentable.fromEnum(Type::values);
        private static final IntFunction<Type> BY_ID = ByIdMap.continuous(Type::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);

        private final int id;
        private final String name;

        Type(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public int getId() {
            return this.id;
        }

        public static Type byName(String name) {
            return CODEC.byName(name, ROCKET);
        }

        public static Type byId(int id) {
            return BY_ID.apply(id);
        }
    }
}