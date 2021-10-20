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

package dev.galacticraft.mod.screen.slot;

import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import com.mojang.datafixers.util.Pair;
import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class IconSlot extends FilteredSlot {
    private final Pair<Identifier, Identifier> icon;

    public IconSlot(MachineBlockEntity machine, int index, int x, int y, Identifier icon) {
        this(machine, index, x, y, PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, icon);
    }

    public IconSlot(MachineBlockEntity machine, int index, int x, int y, ItemFilter filter, Identifier icon) {
        this(machine, index, x, y, filter, PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, icon);
    }

    public IconSlot(MachineBlockEntity machine, int index, int x, int y, Identifier atlas, Identifier icon) {
        super(machine, index, x, y);
        this.icon = new Pair<>(atlas, icon);
    }

    public IconSlot(MachineBlockEntity machine, int index, int x, int y, ItemFilter filter, Identifier atlas, Identifier icon) {
        super(machine, index, x, y, filter);
        this.icon = new Pair<>(atlas, icon);
    }

    @Nullable
    @Override
    public Pair<Identifier, Identifier> getBackgroundSprite() {
        return this.icon;
    }
}
