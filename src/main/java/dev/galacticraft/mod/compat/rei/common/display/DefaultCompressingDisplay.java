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

package dev.galacticraft.mod.compat.rei.common.display;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.compat.rei.common.GalacticraftREIServerPlugin;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.display.SimpleGridMenuDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
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
        public NbtCompound save(NbtCompound tag, DefaultCompressingDisplay display) {
            tag.putBoolean(Constant.Nbt.SHAPED, display instanceof DefaultShapedCompressingDisplay);
            NbtList list = new NbtList();
            for (EntryIngredient inputEntry : display.getInputEntries()) {
                list.add(inputEntry.save());
            }
            tag.put(Constant.Nbt.INPUTS, list);
            list = new NbtList();
            for (EntryIngredient outputEntry : display.getOutputEntries()) {
                list.add(outputEntry.save());
            }
            tag.put(Constant.Nbt.OUTPUTS, list);

            return tag;
        }

        @Override
        public DefaultCompressingDisplay read(NbtCompound tag) {
            NbtList list = tag.getList(Constant.Nbt.INPUTS, NbtElement.LIST_TYPE);
            List<EntryIngredient> inputs = new LinkedList<>();
            List<EntryIngredient> outputs = new LinkedList<>();
            for (NbtElement element : list) {
                inputs.add(EntryIngredient.read(((NbtList)element)));
            }
            list = tag.getList(Constant.Nbt.OUTPUTS, NbtElement.LIST_TYPE);
            for (NbtElement element : list) {
                outputs.add(EntryIngredient.read(((NbtList)element)));
            }
            if (tag.getBoolean(Constant.Nbt.SHAPED)) {
                return new DefaultShapedCompressingDisplay(inputs, outputs);
            } else {
                return new DefaultShapelessCompressingDisplay(inputs, outputs);
            }
        }
    }
}
