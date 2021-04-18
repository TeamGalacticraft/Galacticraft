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

package dev.galacticraft.mod.entity;

import dev.galacticraft.mod.Constants;
import dev.galacticraft.mod.api.attribute.GalacticraftEntityAttributes;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftEntityTypes {

    public static final EntityType<RocketEntity> ROCKET = Registry.register(Registry.ENTITY_TYPE, new Identifier(Constants.MOD_ID, Constants.Entities.ROCKET), FabricEntityTypeBuilder.create(SpawnGroup.MISC, RocketEntity::new).trackable(32, 2, false).dimensions(EntityDimensions.fixed(2.3F, 5.25F)).build()); //PLAYER VALUES
    public static final EntityType<MoonVillagerEntity> MOON_VILLAGER = Registry.register(Registry.ENTITY_TYPE, new Identifier(Constants.MOD_ID, Constants.Entities.MOON_VILLAGER), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, (EntityType.EntityFactory<MoonVillagerEntity>) MoonVillagerEntity::new).dimensions(EntityDimensions.fixed(0.6F, 2.4F)).build());
    public static final EntityType<EvolvedZombieEntity> EVOLVED_ZOMBIE = Registry.register(Registry.ENTITY_TYPE, new Identifier(Constants.MOD_ID, Constants.Entities.EVOLVED_ZOMBIE), FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, EvolvedZombieEntity::new).dimensions(EntityDimensions.fixed(0.6F, 1.95F)).build());
    public static final EntityType<EvolvedCreeperEntity> EVOLVED_CREEPER = Registry.register(Registry.ENTITY_TYPE, new Identifier(Constants.MOD_ID, Constants.Entities.EVOLVED_CREEPER), FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, EvolvedCreeperEntity::new).dimensions(EntityDimensions.changing(0.65F, 1.8F)).build());
    public static final EntityType<EvolvedSkeletonEntity> EVOLVED_SKELETON = Registry.register(Registry.ENTITY_TYPE, new Identifier(Constants.MOD_ID, Constants.Entities.EVOLVED_SKELETON), FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, EvolvedSkeletonEntity::new).dimensions(EntityDimensions.fixed(0.6F, 1.99F)).build());
    public static final EntityType<EvolvedSpiderEntity> EVOLVED_SPIDER = Registry.register(Registry.ENTITY_TYPE, new Identifier(Constants.MOD_ID, Constants.Entities.EVOLVED_SPIDER), FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, EvolvedSpiderEntity::new).dimensions(EntityDimensions.fixed(1.4F, 0.9F)).build());
    public static final EntityType<EvolvedPillagerEntity> EVOLVED_PILLAGER = Registry.register(Registry.ENTITY_TYPE, new Identifier(Constants.MOD_ID, Constants.Entities.EVOLVED_PILLAGER), FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, EvolvedPillagerEntity::new).dimensions(EntityDimensions.fixed(0.6F, 1.95F)).build());
    public static final EntityType<EvolvedEvokerEntity> EVOLVED_EVOKER = Registry.register(Registry.ENTITY_TYPE, new Identifier(Constants.MOD_ID, Constants.Entities.EVOLVED_EVOKER), FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, EvolvedEvokerEntity::new).dimensions(EntityDimensions.fixed(0.6F, 1.95F)).build());
    public static final EntityType<EvolvedVindicatorEntity> EVOLVED_VINDICATOR = Registry.register(Registry.ENTITY_TYPE, new Identifier(Constants.MOD_ID, Constants.Entities.EVOLVED_VINDICATOR), FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, EvolvedVindicatorEntity::new).dimensions(EntityDimensions.fixed(0.6F, 1.95F)).build());
    public static final EntityType<BubbleEntity> BUBBLE = Registry.register(Registry.ENTITY_TYPE, new Identifier(Constants.MOD_ID, Constants.Entities.BUBBLE), FabricEntityTypeBuilder.create(SpawnGroup.MISC, BubbleEntity::new).fireImmune().dimensions(EntityDimensions.fixed(0, 0)).disableSaving().disableSummon().build());

    public static void register() {
        FabricDefaultAttributeRegistry.register(MOON_VILLAGER, MoonVillagerEntity.createMoonVillagerAttributes());
        FabricDefaultAttributeRegistry.register(EVOLVED_ZOMBIE, EvolvedZombieEntity.createZombieAttributes().add(GalacticraftEntityAttributes.CAN_BREATHE_IN_SPACE, 1.0D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35D).add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0D));
        FabricDefaultAttributeRegistry.register(EVOLVED_CREEPER, EvolvedCreeperEntity.createCreeperAttributes().add(GalacticraftEntityAttributes.CAN_BREATHE_IN_SPACE, 1.0D));
        FabricDefaultAttributeRegistry.register(EVOLVED_SKELETON, EvolvedSkeletonEntity.createAbstractSkeletonAttributes().add(GalacticraftEntityAttributes.CAN_BREATHE_IN_SPACE, 1.0D).add(EntityAttributes.GENERIC_MAX_HEALTH, 25.0D));
        FabricDefaultAttributeRegistry.register(EVOLVED_SPIDER, EvolvedSpiderEntity.createSpiderAttributes().add(GalacticraftEntityAttributes.CAN_BREATHE_IN_SPACE, 1.0D).add(EntityAttributes.GENERIC_MAX_HEALTH, 22.0D));
        FabricDefaultAttributeRegistry.register(EVOLVED_PILLAGER, EvolvedPillagerEntity.createPillagerAttributes().add(GalacticraftEntityAttributes.CAN_BREATHE_IN_SPACE, 1.0D).add(EntityAttributes.GENERIC_MAX_HEALTH, 25.0D));
        FabricDefaultAttributeRegistry.register(EVOLVED_EVOKER, EvolvedEvokerEntity.createEvokerAttributes().add(GalacticraftEntityAttributes.CAN_BREATHE_IN_SPACE, 1.0D).add(EntityAttributes.GENERIC_MAX_HEALTH, 25.0D));
        FabricDefaultAttributeRegistry.register(EVOLVED_VINDICATOR, EvolvedVindicatorEntity.createVindicatorAttributes().add(GalacticraftEntityAttributes.CAN_BREATHE_IN_SPACE, 1.0D).add(EntityAttributes.GENERIC_MAX_HEALTH, 25.0D));
    }
}
