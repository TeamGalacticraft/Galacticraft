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

package dev.galacticraft.mod.data.tag;

import dev.galacticraft.mod.content.GCEntityTypes;
import dev.galacticraft.mod.tag.GCEntityTypeTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;

import java.util.concurrent.CompletableFuture;

public class GCEntityTypeTagProvider extends FabricTagProvider.EntityTypeTagProvider {

    public GCEntityTypeTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        this.getOrCreateTagBuilder(GCEntityTypeTags.HAS_FOOTPRINTS)
                .add(EntityType.PLAYER);

        this.getOrCreateTagBuilder(GCEntityTypeTags.HAS_OXYGEN_SETUP)
                .add(GCEntityTypes.EVOLVED_ZOMBIE)
                .add(GCEntityTypes.EVOLVED_CREEPER)
                .add(GCEntityTypes.EVOLVED_SKELETON)
                .add(GCEntityTypes.EVOLVED_SPIDER)
                .add(GCEntityTypes.EVOLVED_ENDERMAN)
                .add(GCEntityTypes.EVOLVED_WITCH)
                .add(GCEntityTypes.EVOLVED_PILLAGER)
                .add(GCEntityTypes.EVOLVED_EVOKER)
                .add(GCEntityTypes.EVOLVED_VINDICATOR);
        this.getOrCreateTagBuilder(EntityTypeTags.CAN_BREATHE_UNDER_WATER)
                .addTag(GCEntityTypeTags.HAS_OXYGEN_SETUP);

        this.getOrCreateTagBuilder(GCEntityTypeTags.HAS_PET_INVENTORY)
                .add(EntityType.WOLF)
                .add(EntityType.CAT)
                .add(EntityType.PARROT);

        this.getOrCreateTagBuilder(GCEntityTypeTags.IMMUNE_TO_ACID)
                .add(EntityType.TRIDENT)
                .add(GCEntityTypes.THROWABLE_METEOR_CHUNK);
        this.getOrCreateTagBuilder(GCEntityTypeTags.SENSITIVE_TO_ACID)
                .add(EntityType.TNT);

        this.getOrCreateTagBuilder(GCEntityTypeTags.CAN_REENTER_ATMOSPHERE)
                .add(EntityType.PLAYER);

        this.getOrCreateTagBuilder(EntityTypeTags.IMPACT_PROJECTILES)
                .add(GCEntityTypes.THROWABLE_METEOR_CHUNK);
        this.getOrCreateTagBuilder(EntityTypeTags.FALL_DAMAGE_IMMUNE)
                .add(GCEntityTypes.ROCKET)
                .add(GCEntityTypes.LANDER);

        this.getOrCreateTagBuilder(EntityTypeTags.SKELETONS)
                .add(GCEntityTypes.EVOLVED_SKELETON)
                .add(GCEntityTypes.SKELETON_BOSS);
        this.getOrCreateTagBuilder(EntityTypeTags.ZOMBIES)
                .add(GCEntityTypes.EVOLVED_ZOMBIE);
        this.getOrCreateTagBuilder(EntityTypeTags.RAIDERS)
                .add(GCEntityTypes.EVOLVED_WITCH)
                .add(GCEntityTypes.EVOLVED_PILLAGER)
                .add(GCEntityTypes.EVOLVED_EVOKER)
                .add(GCEntityTypes.EVOLVED_VINDICATOR);
        this.getOrCreateTagBuilder(EntityTypeTags.ILLAGER)
                .add(GCEntityTypes.EVOLVED_PILLAGER)
                .add(GCEntityTypes.EVOLVED_EVOKER)
                .add(GCEntityTypes.EVOLVED_VINDICATOR);
        this.getOrCreateTagBuilder(EntityTypeTags.ARTHROPOD)
                .add(GCEntityTypes.EVOLVED_SPIDER);
    }
}
