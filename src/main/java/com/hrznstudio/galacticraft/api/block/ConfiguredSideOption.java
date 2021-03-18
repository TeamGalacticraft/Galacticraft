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

package com.hrznstudio.galacticraft.api.block;

import com.hrznstudio.galacticraft.screen.MachineScreenHandler;
import com.hrznstudio.galacticraft.screen.slot.SlotType;
import com.mojang.datafixers.util.Either;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.IntStream;

public class ConfiguredSideOption {
    private AutomationType automationType;
    private Either<Integer, SlotType> matching;

    public ConfiguredSideOption(@NotNull AutomationType automationType) {
        this.automationType = automationType;
        this.matching = Either.right(SlotType.WILDCARD);
    }

    public void setOption(@NotNull AutomationType option, @Nullable Either<Integer, SlotType> matching) {
        this.automationType = option;
        this.matching = matching == null ? Either.right(SlotType.WILDCARD) : matching;
    }

    public void setMatching(@Nullable Either<Integer, SlotType> matching) {
        this.matching = matching == null ? Either.right(SlotType.WILDCARD) : matching;
    }

    public @NotNull AutomationType getAutomationType() {
        return automationType;
    }

    public int[] getMatching(MachineScreenHandler<?> screenHandler) {
        if (matching.left().isPresent()) {
            return new int[]{matching.left().get()};
        }
        SlotType type = matching.right().orElseThrow(RuntimeException::new);
        if (type == SlotType.WILDCARD) {
            return IntStream.range(0, screenHandler.slots.size() - 1).toArray();
        }

        if (automationType.isItem()) {
            return IntStream.range(0, screenHandler.getMachineSlots().get(type).size() - 1).toArray();
        }

        if (automationType.isFluid()) {
            return IntStream.range(0, screenHandler.getMachineTanks().get(type).size() - 1).toArray();
        }

        if (automationType.isEnergy()) {
            return IntStream.range(0, screenHandler.getMachineCapacitors().get(type).size() - 1).toArray();
        }
        return new int[0];
    }

    public CompoundTag toTag(CompoundTag tag) {
        tag.putString("option", automationType.name());
        tag.putBoolean("left", this.matching.left().isPresent());
        if (this.matching.left().isPresent()) {
            tag.putInt("value", this.matching.left().get());
        } else {
            tag.putString("value", this.matching.right().orElseThrow(RuntimeException::new).getId().toString());
        }
        return tag;
    }

    public void fromTag(CompoundTag tag) {
        this.automationType = AutomationType.valueOf(tag.getString("option"));
        if (tag.getBoolean("left")) {
            this.matching = Either.left(tag.getInt("value"));
        } else {
            this.matching = Either.right(SlotType.SLOT_TYPES.get(new Identifier(tag.getString("value"))));
        }
    }

}
