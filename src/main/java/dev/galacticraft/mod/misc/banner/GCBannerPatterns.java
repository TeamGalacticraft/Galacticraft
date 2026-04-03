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

package dev.galacticraft.mod.misc.banner;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.util.Translations;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BannerPattern;

public class GCBannerPatterns {
    public static final ResourceKey<BannerPattern> ROCKET = Constant.key(Registries.BANNER_PATTERN, "rocket");
    public static final ResourceKey<BannerPattern> CREEPER_SIDEWAYS = Constant.key(Registries.BANNER_PATTERN, "creeper_sideways");
    public static final ResourceKey<BannerPattern> FLOW_SIDEWAYS = Constant.key(Registries.BANNER_PATTERN, "flow_sideways");
    public static final ResourceKey<BannerPattern> FLOWER_SIDEWAYS = Constant.key(Registries.BANNER_PATTERN, "flower_sideways");
    public static final ResourceKey<BannerPattern> GLOBE_SIDEWAYS = Constant.key(Registries.BANNER_PATTERN, "globe_sideways");
    public static final ResourceKey<BannerPattern> GUSTER_SIDEWAYS = Constant.key(Registries.BANNER_PATTERN, "guster_sideways");
    public static final ResourceKey<BannerPattern> MOJANG_SIDEWAYS = Constant.key(Registries.BANNER_PATTERN, "mojang_sideways");
    public static final ResourceKey<BannerPattern> PIGLIN_SIDEWAYS = Constant.key(Registries.BANNER_PATTERN, "piglin_sideways");
    public static final ResourceKey<BannerPattern> SKULL_SIDEWAYS = Constant.key(Registries.BANNER_PATTERN, "skull_sideways");

    public static void bootstrapRegistries(BootstrapContext<BannerPattern> context) {
        context.register(ROCKET, new BannerPattern(Constant.id("rocket"), Translations.BannerPattern.ROCKET));
        context.register(CREEPER_SIDEWAYS, new BannerPattern(Constant.id("creeper_sideways"), Translations.BannerPattern.CREEPER_SIDEWAYS));
        context.register(FLOW_SIDEWAYS, new BannerPattern(Constant.id("flow_sideways"), Translations.BannerPattern.FLOW_SIDEWAYS));
        context.register(FLOWER_SIDEWAYS, new BannerPattern(Constant.id("flower_sideways"), Translations.BannerPattern.FLOWER_SIDEWAYS));
        context.register(GLOBE_SIDEWAYS, new BannerPattern(Constant.id("globe_sideways"), Translations.BannerPattern.GLOBE_SIDEWAYS));
        context.register(GUSTER_SIDEWAYS, new BannerPattern(Constant.id("guster_sideways"), Translations.BannerPattern.GUSTER_SIDEWAYS));
        context.register(MOJANG_SIDEWAYS, new BannerPattern(Constant.id("mojang_sideways"), Translations.BannerPattern.MOJANG_SIDEWAYS));
        context.register(PIGLIN_SIDEWAYS, new BannerPattern(Constant.id("piglin_sideways"), Translations.BannerPattern.PIGLIN_SIDEWAYS));
        context.register(SKULL_SIDEWAYS, new BannerPattern(Constant.id("skull_sideways"), Translations.BannerPattern.SKULL_SIDEWAYS));
    }
}
