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

package dev.galacticraft.mod.screen.slot;

import dev.galacticraft.api.gas.Gas;
import dev.galacticraft.mod.Constant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public final class ResourceType<T> {
    public static final ResourceType<?> ANY = new ResourceType(new TranslatableText("ui.galacticraft.side_option.any").setStyle(Constant.Text.AQUA_STYLE));
    public static final ResourceType<?> NONE = new ResourceType(new TranslatableText("ui.galacticraft.side_option.none").setStyle(Constant.Text.DARK_GRAY_STYLE));
    public static final ResourceType<Long> ENERGY = new ResourceType(new TranslatableText("ui.galacticraft.side_option.energy").setStyle(Constant.Text.LIGHT_PURPLE_STYLE));
    public static final ResourceType<FluidVariant> FLUID = new ResourceType(new TranslatableText("ui.galacticraft.side_option.fluid").setStyle(Constant.Text.GREEN_STYLE));
    public static final ResourceType<Gas> GAS = new ResourceType(new TranslatableText("ui.galacticraft.side_option.gas").setStyle(Constant.Text.GRAY_STYLE));
    public static final ResourceType<ItemVariant> ITEM = new ResourceType(new TranslatableText("ui.galacticraft.side_option.item").setStyle(Constant.Text.GOLD_STYLE));

    private final Text name;

    private ResourceType(Text name) {
        this.name = name;
    }

    public Text getName() {
        return this.name;
    }

    public <OT> boolean willAcceptResource(ResourceType<OT> other) {
        return this != NONE && (this == other || this == ANY);
    }
}
