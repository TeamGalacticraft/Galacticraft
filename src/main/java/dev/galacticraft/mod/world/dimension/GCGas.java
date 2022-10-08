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

package dev.galacticraft.mod.world.dimension;

import dev.galacticraft.api.gas.GasFluid;
import dev.galacticraft.mod.Constant;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GCGas {
    public static final ResourceLocation NITROGEN_OXIDE_ID = new ResourceLocation(Constant.MOD_ID, "nitrogen_oxide");
    public static final Fluid NITROGEN_OXIDE =
            GasFluid.create(
                    Component.translatable("ui.galacticraft.nitrogen_oxide"),
                    new ResourceLocation(Constant.MOD_ID, "gas/nitrogen_oxide"),
                    "NO"
            );

    public static final ResourceLocation HYDROGEN_DEUTERIUM_OXYGEN_ID = new ResourceLocation(Constant.MOD_ID, "hydrogen_deuterium_oxygen");
    public static final Fluid HYDROGEN_DEUTERIUM_OXYGEN =
            GasFluid.create(
                    Component.translatable("ui.galacticraft.hydrogen_deuterium_oxygen"),
                    new ResourceLocation(Constant.MOD_ID, "gas/hydrogen_deuterium_oxygen"),
                    "HDO"
            );

    public static void register() {
        Registry.register(Registry.FLUID, HYDROGEN_DEUTERIUM_OXYGEN_ID, HYDROGEN_DEUTERIUM_OXYGEN);
        Registry.register(Registry.FLUID, NITROGEN_OXIDE_ID, NITROGEN_OXIDE);
    }
}
