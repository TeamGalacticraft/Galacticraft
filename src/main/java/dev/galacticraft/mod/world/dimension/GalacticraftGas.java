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

package dev.galacticraft.mod.world.dimension;

import dev.galacticraft.api.gas.Gas;
import dev.galacticraft.api.registry.AddonRegistry;
import dev.galacticraft.mod.Constant;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftGas {
    public static final Identifier NITROGEN_OXIDE_ID = new Identifier(Constant.MOD_ID, "nitrogen_oxide");
    public static final Gas NITROGEN_OXIDE =
            new Gas(
                    new TranslatableText("ui.galacticraft.nitrogen_oxide"),
                    "NO"
            );

    public static final Identifier HYDROGEN_DEUTERIUM_OXYGEN_ID = new Identifier(Constant.MOD_ID, "hydrogen_deuterium_oxygen");
    public static final Gas HYDROGEN_DEUTERIUM_OXYGEN =
            new Gas(
                    new TranslatableText("ui.galacticraft.hydrogen_deuterium_oxygen"),
                    "HDO"
            );

    public static void register() {
        Registry.register(AddonRegistry.GAS, HYDROGEN_DEUTERIUM_OXYGEN_ID, HYDROGEN_DEUTERIUM_OXYGEN);
        Registry.register(AddonRegistry.GAS, NITROGEN_OXIDE_ID, NITROGEN_OXIDE);
    }
}
