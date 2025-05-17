/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

package dev.galacticraft.mod.data.model;

import dev.galacticraft.mod.Constant;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureSlot;

import java.util.Optional;

public class GCModelTemplates {
    public static final ModelTemplate SPAWN_EGG = ModelTemplates.createItem("template_spawn_egg");
    public static final ModelTemplate DETAILED_WALL_POST = create("block/template_detailed_wall_post", "_post", TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.SIDE);
    public static final ModelTemplate DETAILED_WALL_LOW_SIDE = create("block/template_detailed_wall_side", "_side", TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.SIDE);
    public static final ModelTemplate DETAILED_WALL_TALL_SIDE = create("block/template_detailed_wall_side_tall", "_side_tall", TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.SIDE);
    public static final ModelTemplate DETAILED_WALL_INVENTORY = create("block/detailed_wall_inventory", "_inventory", TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.SIDE);
    public static final ModelTemplate GLASS_FLUID_PIPE = create("block/template_glass_fluid_pipe", GCTextureSlot.COLOR);
    public static final ModelTemplate FLUID_PIPE_WALKWAY = create("block/template_fluid_pipe_walkway", GCTextureSlot.COLOR);
    public static final ModelTemplate ROCKET_LAUNCH_PAD_PART = create("block/rocket_launch_pad_part", TextureSlot.PARTICLE, TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.SIDE);
    public static final ModelTemplate PIPE_INVENTORY = create("item/template_pipe", TextureSlot.LAYER0);
    public static final ModelTemplate WALKWAY_INVENTORY = create("item/template_walkway_inventory", TextureSlot.TEXTURE);

    private static ModelTemplate create(String name, TextureSlot... textureSlots) {
        return new ModelTemplate(Optional.of(Constant.id(name)), Optional.empty(), textureSlots);
    }

    private static ModelTemplate create(String name, String suffix, TextureSlot... textureSlots) {
        return new ModelTemplate(Optional.of(Constant.id(name)), Optional.of(suffix), textureSlots);
    }
}