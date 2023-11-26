/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

package dev.galacticraft.mod.client.model;

import dev.galacticraft.mod.Constant;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

import java.util.HashMap;
import java.util.Map;

public class GCModelLoader implements ModelLoadingPlugin {
    public static final GCModelLoader INSTANCE = new GCModelLoader();
    private static final ResourceLocation PARACHEST_ITEM = new ResourceLocation(Constant.MOD_ID, "item/parachest");

    @Override
    public void onInitializeModelLoader(Context pluginContext) {

        pluginContext.resolveModel().register(context -> {
            if (context.id().equals(PARACHEST_ITEM)) {
                Map<DyeColor, UnbakedModel> chutes = new HashMap<>();
                for (DyeColor color : DyeColor.values()) {
                    chutes.put(color, context.getOrLoadModel(Constant.id("block/parachest/" + color.getName() + "_chute")));
                }
                return new ParachestUnbakedModel(context.getOrLoadModel(Constant.id("block/parachest/parachest")), chutes);
            }
            return null;
        });
    }
}