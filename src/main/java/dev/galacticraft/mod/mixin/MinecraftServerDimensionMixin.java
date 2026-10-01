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

package dev.galacticraft.mod.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.config.GCConfigUtil;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.dimension.LevelStem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerDimensionMixin {
    /**
     * Minecraft creates every non-overworld ServerLevel by iterating the
     * LevelStem registry in MinecraftServer#createLevels.
     *
     * <p>Filtering the entry set here means disabled dimensions never reach the
     * ServerLevel constructor. Consequently, their region files are never opened,
     * chunks are never loaded, and world generation never runs.</p>
     */
    @WrapOperation(
            method = "createLevels",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/core/Registry;entrySet()Ljava/util/Set;"
            )
    )
    private Set<Map.Entry<ResourceKey<LevelStem>, LevelStem>>
    galacticraft$filterDisabledDimensions(
            Registry<LevelStem> registry,
            Operation<Set<Map.Entry<ResourceKey<LevelStem>, LevelStem>>> original
    ) {
        MinecraftServer server = (MinecraftServer) (Object) this;

        Set<Map.Entry<ResourceKey<LevelStem>, LevelStem>> entries = original.call(registry);

        Set<ResourceLocation> disabled = GCConfigUtil.disabledDimensions(server);

        if (disabled.isEmpty()) {
            return entries;
        }

        Set<Map.Entry<ResourceKey<LevelStem>, LevelStem>> filtered = new LinkedHashSet<>(entries.size());

        for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : entries) {
            ResourceKey<LevelStem> stemKey = entry.getKey();

            /*
             * Never suppress the overworld.
             */
            if (stemKey == LevelStem.OVERWORLD) {
                filtered.add(entry);
                continue;
            }

            if (disabled.contains(stemKey.location())) {
                Constant.LOGGER.info(
                        "Dimension {} is disabled; its ServerLevel will not be created.",
                        stemKey.location()
                );
                continue;
            }

            filtered.add(entry);
        }

        return filtered;
    }
}