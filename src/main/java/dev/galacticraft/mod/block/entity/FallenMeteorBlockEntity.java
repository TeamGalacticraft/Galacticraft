/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.block.entity;

import dev.galacticraft.mod.accessor.WorldRendererAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class FallenMeteorBlockEntity extends BlockEntity implements BlockEntityClientSerializable {
    public static final int MAX_HEAT_LEVEL = 5000;
    public int heatLevel = FallenMeteorBlockEntity.MAX_HEAT_LEVEL;

    public FallenMeteorBlockEntity(BlockPos pos, BlockState state) {
        super(GalacticraftBlockEntityType.FALLEN_METEOR, pos, state);
    }

    public static void clientTick(World world, BlockPos pos, BlockState state, FallenMeteorBlockEntity blockEntity) {
        if (!world.isClient && blockEntity.heatLevel > 0) {
            blockEntity.heatLevel--;
            blockEntity.sync();
        }
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        this.heatLevel = tag.getInt("HeatLevel");
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putInt("HeatLevel", this.heatLevel);
        return tag;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void fromClientTag(NbtCompound tag) {
        this.heatLevel = tag.getInt("HeatLevel");
        ((WorldRendererAccessor) MinecraftClient.getInstance().worldRenderer).addChunkToRebuild(this.pos);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        tag.putInt("HeatLevel", this.heatLevel);
        return tag;
    }

    public int getHeatLevel() {
        return this.heatLevel;
    }

    public void setHeatLevel(int heatLevel) {
        this.heatLevel = heatLevel;
    }

    public float getScaledHeatLevel() {
        return (float) this.heatLevel / FallenMeteorBlockEntity.MAX_HEAT_LEVEL;
    }
}