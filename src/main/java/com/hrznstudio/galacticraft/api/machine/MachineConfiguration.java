/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.api.machine;

import alexiil.mc.lib.attributes.misc.Saveable;
import net.minecraft.nbt.CompoundTag;

public class MachineConfiguration implements Saveable {
    private MachineStatus status = MachineStatus.NULL;
    private RedstoneState redstone = RedstoneState.IGNORE;
    private final SideConfiguration configuration = new SideConfiguration();
    private final SecurityInfo security = new SecurityInfo();

    public MachineConfiguration() {

    }

    public void setStatus(MachineStatus status) {
        this.status = status;
    }

    public void setRedstone(RedstoneState redstone) {
        this.redstone = redstone;
    }

    public SideConfiguration getConfiguration() {
        return configuration;
    }

    public SecurityInfo getSecurity() {
        return security;
    }

    public MachineStatus getStatus() {
        return status;
    }

    public RedstoneState getRedstone() {
        return redstone;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.put("security", this.getSecurity().toTag(new CompoundTag()));
        tag.put("configuration", this.getConfiguration().toTag(new CompoundTag()));
        this.redstone.toTag(tag);
        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        this.getSecurity().fromTag(tag.getCompound("security"));
        this.getConfiguration().fromTag(tag.getCompound("configuration"));
        this.redstone = RedstoneState.fromTag(tag);
    }
}
