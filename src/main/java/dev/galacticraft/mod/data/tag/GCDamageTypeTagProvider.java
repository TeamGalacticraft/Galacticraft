/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

package dev.galacticraft.mod.data.tag;

import dev.galacticraft.mod.content.entity.damage.GCDamageTypes;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;

import java.util.concurrent.CompletableFuture;

public class GCDamageTypeTagProvider extends FabricTagProvider<DamageType> {
    public GCDamageTypeTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, Registries.DAMAGE_TYPE, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        this.tag(DamageTypeTags.BYPASSES_ARMOR)
                .add(GCDamageTypes.SUFFOCATION);

        this.tag(DamageTypeTags.BYPASSES_WOLF_ARMOR)
                .add(GCDamageTypes.SUFFOCATION);

        this.tag(DamageTypeTags.IS_DROWNING)
                .add(GCDamageTypes.SUFFOCATION);

        this.tag(DamageTypeTags.NO_IMPACT)
                .add(GCDamageTypes.VINE_POISON)
                .add(GCDamageTypes.SUFFOCATION)
                .add(GCDamageTypes.SULFURIC_ACID);

        this.tag(DamageTypeTags.NO_KNOCKBACK)
                .add(GCDamageTypes.VINE_POISON)
                .add(GCDamageTypes.SUFFOCATION)
                .add(GCDamageTypes.SULFURIC_ACID);

        this.tag(DamageTypeTags.PANIC_ENVIRONMENTAL_CAUSES)
                .add(GCDamageTypes.VINE_POISON)
                .add(GCDamageTypes.SULFURIC_ACID);

        this.tag(DamageTypeTags.WITHER_IMMUNE_TO)
                .add(GCDamageTypes.VINE_POISON)
                .add(GCDamageTypes.SUFFOCATION)
                .add(GCDamageTypes.SULFURIC_ACID);
    }
}