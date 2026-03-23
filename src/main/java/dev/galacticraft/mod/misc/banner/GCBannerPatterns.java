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
    public static final ResourceKey<BannerPattern> CREEPER_ROTATED = Constant.key(Registries.BANNER_PATTERN, "creeper_rotated");
    public static final ResourceKey<BannerPattern> FLOW_ROTATED = Constant.key(Registries.BANNER_PATTERN, "flow_rotated");
    public static final ResourceKey<BannerPattern> FLOWER_ROTATED = Constant.key(Registries.BANNER_PATTERN, "flower_rotated");
    public static final ResourceKey<BannerPattern> GLOBE_ROTATED = Constant.key(Registries.BANNER_PATTERN, "globe_rotated");
    public static final ResourceKey<BannerPattern> GUSTER_ROTATED = Constant.key(Registries.BANNER_PATTERN, "guster_rotated");
    public static final ResourceKey<BannerPattern> MOJANG_ROTATED = Constant.key(Registries.BANNER_PATTERN, "mojang_rotated");
    public static final ResourceKey<BannerPattern> PIGLIN_ROTATED = Constant.key(Registries.BANNER_PATTERN, "piglin_rotated");
    public static final ResourceKey<BannerPattern> SKULL_ROTATED = Constant.key(Registries.BANNER_PATTERN, "skull_rotated");

    public static void bootstrapRegistries(BootstrapContext<BannerPattern> context) {
        context.register(ROCKET, new BannerPattern(Constant.id("rocket"), Translations.BannerPattern.ROCKET));
        context.register(CREEPER_ROTATED, new BannerPattern(Constant.id("creeper_rotated"), Translations.BannerPattern.CREEPER_ROTATED));
        context.register(FLOW_ROTATED, new BannerPattern(Constant.id("flow_rotated"), Translations.BannerPattern.FLOW_ROTATED));
        context.register(FLOWER_ROTATED, new BannerPattern(Constant.id("flower_rotated"), Translations.BannerPattern.FLOWER_ROTATED));
        context.register(GLOBE_ROTATED, new BannerPattern(Constant.id("globe_rotated"), Translations.BannerPattern.GLOBE_ROTATED));
        context.register(GUSTER_ROTATED, new BannerPattern(Constant.id("guster_rotated"), Translations.BannerPattern.GUSTER_ROTATED));
        context.register(MOJANG_ROTATED, new BannerPattern(Constant.id("mojang_rotated"), Translations.BannerPattern.MOJANG_ROTATED));
        context.register(PIGLIN_ROTATED, new BannerPattern(Constant.id("piglin_rotated"), Translations.BannerPattern.PIGLIN_ROTATED));
        context.register(SKULL_ROTATED, new BannerPattern(Constant.id("skull_rotated"), Translations.BannerPattern.SKULL_ROTATED));
    }
}
