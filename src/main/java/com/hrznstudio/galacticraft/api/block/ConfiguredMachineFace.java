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

import com.google.common.collect.ImmutableList;
import com.hrznstudio.galacticraft.api.block.entity.MachineBlockEntity;
import com.hrznstudio.galacticraft.attribute.Automatable;
import com.hrznstudio.galacticraft.screen.slot.SlotType;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.IntStream;

public class ConfiguredMachineFace {
    private AutomationType automationType;
    private @Nullable Either<Integer, SlotType> matching;

    public ConfiguredMachineFace(@NotNull AutomationType automationType) {
        this.automationType = automationType;
        this.matching = null;
    }

    public void setOption(@NotNull AutomationType option) {
        this.automationType = option;
        this.matching = null;
    }


    public static List<AutomationType> getValidTypes(MachineBlockEntity machine) {
        ImmutableList.Builder<AutomationType> builder = ImmutableList.builder();
        builder.add(AutomationType.NONE);
        if (machine.canInsertEnergy()) builder.add(AutomationType.POWER_INPUT);
        if (machine.canExtractEnergy()) builder.add(AutomationType.POWER_OUTPUT);
        builder.addAll(machine.getInventory().getTypes().stream().map(SlotType::getType).distinct().iterator());
        builder.addAll(machine.getFluidTank().getTypes().stream().map(SlotType::getType).distinct().iterator());
        return builder.build();
    }

    public void setMatching(@Nullable Either<Integer, SlotType> matching) {
        this.matching = matching;
    }

    public @NotNull AutomationType getAutomationType() {
        return automationType;
    }

    public Either<Integer, SlotType> getMatching() {
        return matching;
    }

    public int[] getMatching(Automatable automatable) {
        if (matching != null) {
            if (matching.left().isPresent()) {
                return new int[]{matching.left().get()};
            }
            SlotType type = matching.right().orElseThrow(RuntimeException::new);
            if (type == null) {
                return IntStream.range(0, automatable.getTypes().size() - 1).toArray();
            }
            if (type.getType() == AutomationType.NONE) return new int[0];

            IntList intList = new IntArrayList(1);
            for (int i = 0; i < automatable.getTypes().size(); i++) {
                if (automatable.getTypes().get(i) == type) intList.add(i);
            }
            return intList.toIntArray();
        }
        IntList intList = new IntArrayList(1);
        for (int i = 0; i < automatable.getTypes().size(); i++) {
            if (automatable.getTypes().get(i).getType().canPassAs(this.automationType)) intList.add(i);
        }
        return intList.toIntArray();
    }

    public CompoundTag toTag(CompoundTag tag) {
        tag.putString("option", automationType.name());
        tag.putBoolean("match", this.matching != null);
        if (this.matching != null) {
            tag.putBoolean("left", this.matching.left().isPresent());
            if (this.matching.left().isPresent()) {
                tag.putInt("value", this.matching.left().get());
            } else {
                tag.putString("value", this.matching.right().orElseThrow(RuntimeException::new).getId().toString());
            }
        }
        return tag;
    }

    public void fromTag(CompoundTag tag) {
        this.automationType = AutomationType.valueOf(tag.getString("option"));
        if (tag.getBoolean("match")) {
            if (tag.getBoolean("left")) {
                this.matching = Either.left(tag.getInt("value"));
            } else {
                this.matching = Either.right(SlotType.SLOT_TYPES.get(new Identifier(tag.getString("value"))));
            }
        } else {
            this.matching = null;
        }
    }

}
