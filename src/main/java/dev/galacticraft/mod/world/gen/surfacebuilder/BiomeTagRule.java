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

package dev.galacticraft.mod.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.fabricmc.fabric.impl.tag.extension.TagFactoryImpl;
import net.minecraft.tag.RequiredTagList;
import net.minecraft.tag.Tag;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;
import org.jetbrains.annotations.NotNull;

public record BiomeTagRule(@NotNull Tag<Biome> tag) implements MaterialRules.MaterialCondition {
    private static final Codec<BiomeTagRule> CODEC;

    static {
        TagFactory<Biome> biome = TagFactory.BIOME; //classload (we directly access impl) TODO: better way of getting tag group?
        CODEC = RecordCodecBuilder.create(instance -> instance.group(Tag.codec(((RequiredTagList<Biome>) TagFactoryImpl.TAG_LISTS.get(Registry.BIOME_KEY))::getGroup).fieldOf("tag").forGetter(rule -> rule.tag)).apply(instance, BiomeTagRule::new));
    }

    @Override
    public Codec<? extends MaterialRules.MaterialCondition> codec() {
        return CODEC;
    }

    @Override
    public MaterialRules.BooleanSupplier apply(MaterialRules.MaterialRuleContext context) {
        return new Predicate(context);
    }

    private class Predicate extends MaterialRules.FullLazyAbstractPredicate {
        protected Predicate(MaterialRules.MaterialRuleContext context) {
            super(context);
        }

        @Override
        protected boolean test() {
            return BiomeTagRule.this.tag.contains(this.context.biomeSupplier.get());
        }
    }
}
