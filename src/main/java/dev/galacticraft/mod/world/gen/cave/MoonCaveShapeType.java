package dev.galacticraft.mod.world.gen.cave;

/**
 * Logical cave structure type.
 *
 * <p>This is intentionally separate from visual style. Two caves can have the
 * same structure type but different styles, which allows controlled transitions
 * only between compatible registrations.</p>
 */
public enum MoonCaveShapeType {
    BRANCHING,
    LAVA_TUBE,
    LAYERED_DISC
}