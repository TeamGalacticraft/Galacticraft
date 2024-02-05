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

package dev.galacticraft.mod.compat.rei.common.display;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.compat.rei.common.GalacticraftREIServerPlugin;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.display.SimpleGridMenuDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public interface DefaultCompressingDisplay extends SimpleGridMenuDisplay {
    default @NotNull CategoryIdentifier<?> getCategoryIdentifier() {
        return GalacticraftREIServerPlugin.COMPRESSING;
    }

    default int getWidth() {
        return 3;
    }

    default int getHeight() {
        return 3;
    }

    enum Serializer implements DisplaySerializer<DefaultCompressingDisplay> {
        INSTANCE;

        @Override
        public CompoundTag save(CompoundTag tag, DefaultCompressingDisplay display) {
            tag.putBoolean(Constant.Nbt.SHAPED, display instanceof DefaultShapedCompressingDisplay);
            ListTag list = new ListTag();
            for (EntryIngredient inputEntry : display.getInputEntries()) {
                list.add(inputEntry.saveIngredient());
            }
            tag.put(Constant.Nbt.INPUTS, list);
            list = new ListTag();
            for (EntryIngredient outputEntry : display.getOutputEntries()) {
                list.add(outputEntry.saveIngredient());
            }
            tag.put(Constant.Nbt.OUTPUTS, list);

            return tag;
        }

        @Override
        public DefaultCompressingDisplay read(CompoundTag tag) {
            ListTag list = tag.getList(Constant.Nbt.INPUTS, Tag.TAG_LIST);
            List<EntryIngredient> inputs = new LinkedList<>();
            List<EntryIngredient> outputs = new LinkedList<>();
            for (Tag element : list) {
                inputs.add(EntryIngredient.read(((ListTag)element)));
            }
            list = tag.getList(Constant.Nbt.OUTPUTS, Tag.TAG_LIST);
            for (Tag element : list) {
                outputs.add(EntryIngredient.read(((ListTag)element)));
            }
            if (tag.getBoolean(Constant.Nbt.SHAPED)) {
                return new DefaultShapedCompressingDisplay(inputs, outputs);
            } else {
                return new DefaultShapelessCompressingDisplay(inputs, outputs);
            }
        }
    }
}
