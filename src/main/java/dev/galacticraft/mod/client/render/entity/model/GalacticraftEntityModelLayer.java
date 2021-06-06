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

package dev.galacticraft.mod.client.render.entity.model;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.model.entity.EvolvedCreeperEntityModel;
import dev.galacticraft.mod.client.model.entity.MoonVillagerEntityModel;
import dev.galacticraft.mod.client.render.block.entity.BasicSolarPanelBlockEntityRenderer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftEntityModelLayer {
    public static final EntityModelLayer EVOLVED_CREEPER = new EntityModelLayer(new Identifier(Constant.MOD_ID, "evolved_creeper"), "main");
    public static final EntityModelLayer EVOLVED_CREEPER_ARMOR = new EntityModelLayer(new Identifier(Constant.MOD_ID, "evolved_creeper_armor"), "armor");
    public static final EntityModelLayer MOON_VILLAGER = new EntityModelLayer(new Identifier(Constant.MOD_ID, "moon_villager"), "main");
    public static final EntityModelLayer SOLAR_PANEL = new EntityModelLayer(new Identifier(Constant.MOD_ID, "solar_panel"), "main");

    public static void register() {
        EntityModelLayerRegistry.registerModelLayer(EVOLVED_CREEPER, () -> EvolvedCreeperEntityModel.getTexturedModelData(Dilation.NONE));
        EntityModelLayerRegistry.registerModelLayer(EVOLVED_CREEPER_ARMOR, () -> EvolvedCreeperEntityModel.getTexturedModelData(new Dilation(2.0f)));
        EntityModelLayerRegistry.registerModelLayer(MOON_VILLAGER, () -> TexturedModelData.of(MoonVillagerEntityModel.getModelData(), 64, 64));
        EntityModelLayerRegistry.registerModelLayer(SOLAR_PANEL, BasicSolarPanelBlockEntityRenderer::getTexturedModelData);
    }
}
