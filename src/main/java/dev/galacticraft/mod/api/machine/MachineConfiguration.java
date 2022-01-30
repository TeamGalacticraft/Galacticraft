/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

package dev.galacticraft.mod.api.machine;

import dev.galacticraft.mod.Constant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class MachineConfiguration {
    private MachineStatus status = MachineStatus.NULL;
    private RedstoneInteractionType redstone = RedstoneInteractionType.IGNORE;
    private final MachineIOConfig configuration = new MachineIOConfig();
    private final SecurityInfo security = new SecurityInfo();

    public void setStatus(MachineStatus status) {
        this.status = status;
    }

    public void setRedstone(RedstoneInteractionType redstone) {
        this.redstone = redstone;
    }

    public MachineIOConfig getSideConfiguration() {
        return this.configuration;
    }

    public SecurityInfo getSecurity() {
        return this.security;
    }

    public MachineStatus getStatus() {
        return this.status;
    }

    public RedstoneInteractionType getRedstoneInteraction() {
        return this.redstone;
    }

    public NbtCompound toNbt(NbtCompound nbt) {
        nbt.put(Constant.Nbt.SECURITY, this.getSecurity().toTag(new NbtCompound()));
        nbt.put(Constant.Nbt.CONFIGURATION, this.getSideConfiguration().toTag(new NbtCompound()));
        this.redstone.toTag(nbt);
        return nbt;
    }

    public NbtCompound toClientNbt(NbtCompound nbt, PlayerEntity player) {
        if (security.hasAccess(player)) {
            nbt.put(Constant.Nbt.SECURITY, this.getSecurity().toTag(new NbtCompound()));
            nbt.put(Constant.Nbt.CONFIGURATION, this.getSideConfiguration().toTag(new NbtCompound()));
            this.redstone.toTag(nbt);
        }
        return nbt;
    }

    public void fromNbt(NbtCompound nbt) {
        this.getSecurity().fromTag(nbt.getCompound(Constant.Nbt.SECURITY));
        this.getSideConfiguration().fromTag(nbt.getCompound(Constant.Nbt.CONFIGURATION));
        this.redstone = RedstoneInteractionType.fromTag(nbt);
    }

    public static MachineConfiguration fromClientTag(NbtCompound tag) {
        MachineConfiguration configuration = new MachineConfiguration();
        if (tag.contains(Constant.Nbt.REDSTONE_INTERACTION_TYPE)) {
            configuration.setRedstone(RedstoneInteractionType.fromTag(tag));
        }
        if (tag.contains(Constant.Nbt.CONFIGURATION)) {
            configuration.getSideConfiguration().fromTag(tag.getCompound(Constant.Nbt.CONFIGURATION));
        }
        if (tag.contains(Constant.Nbt.SECURITY)) {
            configuration.getSecurity().fromTag(tag.getCompound(Constant.Nbt.SECURITY));
        }
        return configuration;
    }
}
