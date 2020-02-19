/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.entity;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.entity.moonvillager.EntityMoonVillager;
import com.hrznstudio.galacticraft.entity.rocket.RocketEntity;
import com.hrznstudio.galacticraft.api.entity.EvolvedEntity;
import com.hrznstudio.galacticraft.entity.evolvedzombie.EvolvedZombieEntity;
import com.hrznstudio.galacticraft.entity.moonvillager.MoonVillagerEntity;
import net.fabricmc.fabric.api.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftEntityTypes {

    public static final EntityType<RocketEntity> ROCKET = Registry.register(Registry.ENTITY_TYPE, new Identifier(Constants.MOD_ID, Constants.Entities.ROCKET), FabricEntityTypeBuilder.create(EntityCategory.MISC, RocketEntity::new).trackable(32, 2, false).size(EntityDimensions.fixed(2.3F, 5.25F)).build()); //PLAYER VALUES
    public static final EntityType<MoonVillagerEntity> MOON_VILLAGER = FabricEntityTypeBuilder.create(EntityCategory.CREATURE, MoonVillagerEntity::new).size(EntityDimensions.fixed(0.6F, 2.4F)).build();
    public static final EntityType<EvolvedZombieEntity> EVOLVED_ZOMBIE = FabricEntityTypeBuilder.create(EntityCategory.MONSTER, EvolvedZombieEntity::new).size(EntityDimensions.fixed(0.6F, 1.95F)).build();

    public static void register() {
        Registry.register(Registry.ENTITY_TYPE, new Identifier(Constants.Entities.MOON_VILLAGER), MOON_VILLAGER);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(Constants.Entities.EVOLVED_ZOMBIE), EVOLVED_ZOMBIE);
        Galacticraft.logger.info("Registered entity types!");
    }
}
