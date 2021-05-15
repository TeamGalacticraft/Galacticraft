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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class FallenMeteorBlockEntity extends BlockEntity implements Tickable, BlockEntityClientSerializable {
    public static final int MAX_HEAT_LEVEL = 5000;
    public int heatLevel = FallenMeteorBlockEntity.MAX_HEAT_LEVEL;

    public FallenMeteorBlockEntity() {
        super(GalacticraftBlockEntityType.FALLEN_METEOR);
    }

    @Override
    public void tick() {
        if (!this.world.isClient && this.heatLevel > 0) {
            this.heatLevel--;
            this.sync();
        }
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.heatLevel = tag.getInt("HeatLevel");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putInt("HeatLevel", this.heatLevel);
        return tag;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void fromClientTag(CompoundTag tag) {
        this.heatLevel = tag.getInt("HeatLevel");
        ((WorldRendererAccessor) MinecraftClient.getInstance().worldRenderer).addChunkToRebuild(this.pos);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
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