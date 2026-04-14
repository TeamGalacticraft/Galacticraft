/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.api.entity;

/**
 * Implement into entities that are living, but can breath without oxygen
 */
public interface IEntityBreathable
{

    /**
     * Whether or not this entity can currently breathe without oxygen in it's
     * vicinity
     */
    boolean canBreath();
}
