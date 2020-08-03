/*
 * Copyright (c) 2020 HRZN LTD
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
 *
 */

package com.hrznstudio.galacticraft.entity;

import com.hrznstudio.galacticraft.Constants;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftEntityTypes {

    public static final EntityType<MoonVillagerEntity> MOON_VILLAGER = Registry.register(Registry.ENTITY_TYPE, new Identifier(Constants.MOD_ID, Constants.Entities.MOON_VILLAGER), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, (EntityType.EntityFactory<MoonVillagerEntity>) MoonVillagerEntity::new).dimensions(EntityDimensions.fixed(0.6F, 2.4F)).build());
    public static final EntityType<EvolvedZombieEntity> EVOLVED_ZOMBIE = Registry.register(Registry.ENTITY_TYPE, new Identifier(Constants.MOD_ID, Constants.Entities.EVOLVED_ZOMBIE), FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, EvolvedZombieEntity::new).dimensions(EntityDimensions.fixed(0.6F, 1.95F)).build());
    public static final EntityType<EvolvedCreeperEntity> EVOLVED_CREEPER = Registry.register(Registry.ENTITY_TYPE, new Identifier(Constants.MOD_ID, Constants.Entities.EVOLVED_CREEPER), FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, EvolvedCreeperEntity::new).dimensions(EntityDimensions.changing(0.65F, 1.8F)).build());
    public static final EntityType<BubbleEntity> BUBBLE = Registry.register(Registry.ENTITY_TYPE, new Identifier(Constants.MOD_ID, Constants.Entities.BUBBLE), FabricEntityTypeBuilder.create(SpawnGroup.MISC, BubbleEntity::new).fireImmune().dimensions(EntityDimensions.fixed(0, 0)).disableSaving().disableSummon().build());

    public static void register() {
        FabricDefaultAttributeRegistry.register(MOON_VILLAGER, MoonVillagerEntity.createMoonVillagerAttributes());
        FabricDefaultAttributeRegistry.register(EVOLVED_ZOMBIE, EvolvedZombieEntity.createZombieAttributes());
        FabricDefaultAttributeRegistry.register(EVOLVED_CREEPER, EvolvedCreeperEntity.createCreeperAttributes());
    }
}
