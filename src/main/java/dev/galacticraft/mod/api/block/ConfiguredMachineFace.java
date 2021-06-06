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

package dev.galacticraft.mod.api.block;

import com.mojang.datafixers.util.Either;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.attribute.Automatable;
import dev.galacticraft.mod.screen.slot.SlotType;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.IntStream;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class ConfiguredMachineFace {
    private static final Set<AutomationType> CACHED_AUTOMATION_TYPE_SET = new HashSet<>();
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
        List<AutomationType> list = new ArrayList<>();
        CACHED_AUTOMATION_TYPE_SET.clear();
        CACHED_AUTOMATION_TYPE_SET.add(AutomationType.NONE);
        list.add(AutomationType.NONE);

        if (machine.canInsertEnergy()) if (CACHED_AUTOMATION_TYPE_SET.add(AutomationType.POWER_INPUT)) list.add(AutomationType.POWER_INPUT);
        if (machine.canExtractEnergy()) if (CACHED_AUTOMATION_TYPE_SET.add(AutomationType.POWER_OUTPUT)) list.add(AutomationType.POWER_OUTPUT);

        for (SlotType type : machine.fluidInv().getTypes()) {
            if (type.getType().isBidirectional()) {
                if (CACHED_AUTOMATION_TYPE_SET.add(AutomationType.FLUID_INPUT)) list.add(AutomationType.FLUID_INPUT);
                if (CACHED_AUTOMATION_TYPE_SET.add(AutomationType.FLUID_OUTPUT)) list.add(AutomationType.FLUID_OUTPUT);
            } else {
                if (CACHED_AUTOMATION_TYPE_SET.add(type.getType())) list.add(type.getType());
            }
        }

        for (SlotType type : machine.itemInv().getTypes()) {
            if (type.getType().isBidirectional()) {
                if (CACHED_AUTOMATION_TYPE_SET.add(AutomationType.ITEM_INPUT)) list.add(AutomationType.ITEM_INPUT);
                if (CACHED_AUTOMATION_TYPE_SET.add(AutomationType.ITEM_OUTPUT)) list.add(AutomationType.ITEM_OUTPUT);
            } else {
                if (CACHED_AUTOMATION_TYPE_SET.add(type.getType())) list.add(type.getType());
            }
        }
        return list;
    }

    public void setMatching(@Nullable Either<Integer, SlotType> matching) {
        this.matching = matching;
    }

    public @NotNull AutomationType getAutomationType() {
        return automationType;
    }

    public @Nullable Either<Integer, SlotType> getMatching() {
        return matching;
    }

    public int[] getMatching(Automatable automatable) {
        if (Arrays.stream(automatable.getTypes()).anyMatch(type -> !type.getType().canPassAsIgnoreFlow(this.automationType))) {
            return new int[0];
        }
        if (matching != null) {
            if (matching.left().isPresent()) {
                return new int[]{matching.left().get()};
            }
            SlotType type = matching.right().orElseThrow(RuntimeException::new);
            if (type == null) {
                return IntStream.range(0, automatable.getTypes().length - 1).toArray();
            }
            if (type.getType() == AutomationType.NONE) return new int[0];

            IntList intList = new IntArrayList(1);
            for (int i = 0; i < automatable.getTypes().length; i++) {
                if (automatable.getTypes()[i] == type) intList.add(i);
            }
            return intList.toIntArray();
        }
        IntList intList = new IntArrayList(1);
        for (int i = 0; i < automatable.getTypes().length; i++) {
            if (automatable.getTypes()[i].getType().canPassAs(this.automationType)) intList.add(i);
        }
        return intList.toIntArray();
    }

    public NbtCompound toTag(NbtCompound tag) {
        tag.putString(Constant.Nbt.AUTOMATION_TYPE, automationType.name());
        tag.putBoolean(Constant.Nbt.MATCH, this.matching != null);
        if (this.matching != null) {
            tag.putBoolean(Constant.Nbt.INTEGER, this.matching.left().isPresent());
            if (this.matching.left().isPresent()) {
                tag.putInt(Constant.Nbt.VALUE, this.matching.left().get());
            } else {
                tag.putString(Constant.Nbt.VALUE, this.matching.right().orElseThrow(RuntimeException::new).getId().toString());
            }
        }
        return tag;
    }

    public void fromTag(NbtCompound tag) {
        this.automationType = AutomationType.valueOf(tag.getString(Constant.Nbt.AUTOMATION_TYPE));
        if (tag.getBoolean(Constant.Nbt.MATCH)) {
            if (tag.getBoolean(Constant.Nbt.INTEGER)) {
                this.matching = Either.left(tag.getInt(Constant.Nbt.VALUE));
            } else {
                this.matching = Either.right(SlotType.SLOT_TYPES.get(new Identifier(tag.getString(Constant.Nbt.VALUE))));
            }
        } else {
            this.matching = null;
        }
    }
}
