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

package dev.galacticraft.mod.api.machine;

import dev.galacticraft.mod.api.block.AutomationType;
import dev.galacticraft.mod.api.block.ConfiguredMachineFace;
import dev.galacticraft.mod.api.block.util.BlockFace;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class MachineIOConfig {
    private final ConfiguredMachineFace front;
    private final ConfiguredMachineFace back;
    private final ConfiguredMachineFace left;
    private final ConfiguredMachineFace right;
    private final ConfiguredMachineFace top;
    private final ConfiguredMachineFace bottom;

    public MachineIOConfig() {
        this.front = new ConfiguredMachineFace(AutomationType.NONE);
        this.back = new ConfiguredMachineFace(AutomationType.NONE);
        this.left = new ConfiguredMachineFace(AutomationType.NONE);
        this.right = new ConfiguredMachineFace(AutomationType.NONE);
        this.top = new ConfiguredMachineFace(AutomationType.NONE);
        this.bottom = new ConfiguredMachineFace(AutomationType.NONE);
    }

    public NbtCompound toTag(NbtCompound tag) {
        tag.put("Front", this.front.toTag(new NbtCompound()));
        tag.put("Back", this.back.toTag(new NbtCompound()));
        tag.put("Left", this.left.toTag(new NbtCompound()));
        tag.put("Right", this.right.toTag(new NbtCompound()));
        tag.put("Top", this.top.toTag(new NbtCompound()));
        tag.put("Bottom", this.bottom.toTag(new NbtCompound()));
        return tag;
    }

    public void fromTag(NbtCompound tag) {
        this.front.fromTag(tag.getCompound("Front"));
        this.back.fromTag(tag.getCompound("Back"));
        this.left.fromTag(tag.getCompound("Left"));
        this.right.fromTag(tag.getCompound("Right"));
        this.top.fromTag(tag.getCompound("Top"));
        this.bottom.fromTag(tag.getCompound("Bottom"));
    }

    /**
     * Please do not modify the returned {@link ConfiguredMachineFace}
     *
     * @param face the block face to pull the option from
     * @return a {@link ConfiguredMachineFace} assignd to the given face.
     */
    public ConfiguredMachineFace get(@NotNull BlockFace face) {
        return switch (face) {
            case FRONT -> this.front;
            case TOP -> this.top;
            case BACK -> this.back;
            case RIGHT -> this.right;
            case LEFT -> this.left;
            case BOTTOM -> this.bottom;
        };
    }
}
