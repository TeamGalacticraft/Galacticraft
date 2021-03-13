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

package com.hrznstudio.galacticraft.client.render.rocket;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.client.rocket.part.BakedModelRocketPartRenderer;
import com.hrznstudio.galacticraft.api.client.rocket.part.RocketPartRendererRegistry;
import com.hrznstudio.galacticraft.api.rocket.part.GalacticraftRocketParts;
import dev.monarkhes.myron.api.Myron;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class GalacticraftRocketPartRenderers {
    private static final Identifier DEFAULT_CONE = new Identifier(Constants.MOD_ID, "models/misc/rocket_cone_basic");
    private static final Identifier ADVANCED_CONE = new Identifier(Constants.MOD_ID, "models/misc/rocket_cone_advanced");
    private static final Identifier SLOPED_CONE = new Identifier(Constants.MOD_ID, "models/misc/rocket_cone_sloped");
    private static final Identifier DEFAULT_BODY= new Identifier(Constants.MOD_ID, "models/misc/rocket_body");
    private static final Identifier DEFAULT_FIN = new Identifier(Constants.MOD_ID, "models/misc/rocket_fins");
    private static final Identifier DEFAULT_BOTTOM = new Identifier(Constants.MOD_ID, "models/misc/rocket_bottom");
    private static final Identifier BOOSTER_TIER_1 = new Identifier(Constants.MOD_ID, "models/misc/rocket_thruster_tier_1");
    private static final Identifier BOOSTER_TIER_2 = new Identifier(Constants.MOD_ID, "models/misc/rocket_thruster_tier_2");

    public static void register() {
        RocketPartRendererRegistry.register(GalacticraftRocketParts.DEFAULT_CONE, new BakedModelRocketPartRenderer(Objects.requireNonNull(Myron.getModel(DEFAULT_CONE))));
        RocketPartRendererRegistry.register(GalacticraftRocketParts.ADVANCED_CONE, new BakedModelRocketPartRenderer(Objects.requireNonNull(Myron.getModel(ADVANCED_CONE))));
        RocketPartRendererRegistry.register(GalacticraftRocketParts.SLOPED_CONE, new BakedModelRocketPartRenderer(Objects.requireNonNull(Myron.getModel(SLOPED_CONE))));
        RocketPartRendererRegistry.register(GalacticraftRocketParts.DEFAULT_BODY, new BakedModelRocketPartRenderer(Objects.requireNonNull(Myron.getModel(DEFAULT_BODY))));
        RocketPartRendererRegistry.register(GalacticraftRocketParts.DEFAULT_FIN, new BakedModelRocketPartRenderer(Objects.requireNonNull(Myron.getModel(DEFAULT_FIN))));
        RocketPartRendererRegistry.register(GalacticraftRocketParts.NO_BOOSTER, new BakedModelItemRocketPartRenderer(new ItemStack(Items.BARRIER), null));
        RocketPartRendererRegistry.register(GalacticraftRocketParts.DEFAULT_BOTTOM, new BakedModelRocketPartRenderer(Objects.requireNonNull(Myron.getModel(DEFAULT_BOTTOM))));
        RocketPartRendererRegistry.register(GalacticraftRocketParts.NO_UPGRADE, new BakedModelItemRocketPartRenderer(new ItemStack(Items.BARRIER), null));
        RocketPartRendererRegistry.register(GalacticraftRocketParts.STORAGE_UPGRADE, new BakedModelItemRocketPartRenderer(new ItemStack(Items.CHEST), null));
        RocketPartRendererRegistry.register(GalacticraftRocketParts.BOOSTER_TIER_1, new BakedModelRocketPartRenderer(Objects.requireNonNull(Myron.getModel(BOOSTER_TIER_1))));
        RocketPartRendererRegistry.register(GalacticraftRocketParts.BOOSTER_TIER_2, new BakedModelRocketPartRenderer(Objects.requireNonNull(Myron.getModel(BOOSTER_TIER_2))));
    }
}
