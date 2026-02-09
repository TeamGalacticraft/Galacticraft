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

package dev.galacticraft.api.rocket.part;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record RocketUpgradeData(Optional<ResourceLocation> explosiveBlock) {
    public static final RocketUpgradeData EMPTY = new RocketUpgradeData(Optional.empty());

    public static final Codec<RocketUpgradeData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("explosive_block").forGetter(RocketUpgradeData::explosiveBlock)
    ).apply(instance, RocketUpgradeData::new));

    public boolean hasExplosive() {
        return this.explosiveBlock.isPresent();
    }

    public static @NotNull RocketUpgradeData forExplosiveBlock(@NotNull Block block) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        return new RocketUpgradeData(Optional.ofNullable(id));
    }

    public static @Nullable RocketUpgradeData fromStackIfBlock(@NotNull ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem bi)) return null;
        return forExplosiveBlock(bi.getBlock());
    }
}