/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

package dev.galacticraft.mod.compat.waila.provider;

import dev.galacticraft.mod.compat.waila.config.Options;
import dev.galacticraft.mod.content.item.OxygenTankItem;
import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.WailaHelper;
import mcp.mobius.waila.api.component.PairComponent;
import mcp.mobius.waila.api.component.SpriteBarComponent;
import mcp.mobius.waila.api.component.WrappedComponent;
import mcp.mobius.waila.api.data.FluidData;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import static dev.galacticraft.mod.util.Translations.Waila.*;

public enum OxygenLevelProvider implements IEntityComponentProvider {

    INSTANCE;

    private static final String INFINITE = "∞";

    @Override
    public void appendBody(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        if (!config.getBoolean(Options.OXYGEN_LEVEL)) return;

        LivingEntity entity = accessor.getEntity();
        Container tankInv = entity.galacticraft$getOxygenTanks();

        final int n = tankInv.getContainerSize();
        for (int i = 0; i < n; i++) {
            StorageView<FluidVariant> storage = OxygenTankItem.getStorage(tankInv.getItem(i));
            if (storage != null) {
                FluidVariant variant = storage.getResource();
                Fluid fluid = variant.getFluid();
                long amount = storage.getAmount();
                long capacity = storage.getCapacity();
                float ratio = capacity == Long.MAX_VALUE ? 1.0F : ((float) amount) / ((float) capacity);

                FluidData.Unit storedUnit = FluidData.Unit.DROPLETS;
                FluidData.Unit displayUnit = config.getEnum(FluidData.CONFIG_DISPLAY_UNIT);

                String text;
                if (amount == Long.MAX_VALUE) {
                    text = INFINITE;
                } else {
                    text = WailaHelper.suffix((long) FluidData.Unit.convert(storedUnit, displayUnit, amount));
                    if (capacity != Long.MAX_VALUE) {
                        text += "/" + WailaHelper.suffix((long) FluidData.Unit.convert(storedUnit, displayUnit, capacity));
                    }
                }
                text += " " + displayUnit.symbol;

                TextureAtlasSprite sprite = FluidVariantRendering.getSprite(variant);
                if (sprite == null) {
                    sprite = FluidVariantRendering.getSprite(FluidVariant.of(Fluids.WATER));
                    assert sprite != null;
                }

                int fluidColor = FluidVariantRendering.getColor(variant);
                if (fluidColor == -1) {
                    fluidColor = 0xFFFFFFFF;
                }

                MutableComponent label = Component.translatable(OXYGEN_TANK_LABEL, i + 1);
                tooltip.setLine(FluidData.ID.withSuffix("." + BuiltInRegistries.FLUID.getKey(fluid).toLanguageKey() + "." + String.valueOf(i)),
                        new PairComponent(
                                new WrappedComponent(label),
                                new SpriteBarComponent(ratio, sprite, 16, 16, fluidColor, Component.literal(text))
                        )
                );
            }
        }
    }
}